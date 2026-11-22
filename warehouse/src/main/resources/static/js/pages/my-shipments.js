import {api, post, put} from "../api.js";
import {actionLink, badge, checked, choices, dateTime, details, fields, form, h, idParam, inline, input,
    link, mount, numberValue, optionRows, panel, param, qty, select, shippingRoles, stack, table, textValue, timeline} from "../components/ui.js";
import {editor, optionalText, positiveWhole, requiredText} from "../components/editor.js";

mount(account => account.role === "DRIVER" ? "Chuyến của tôi" : "Điều phối vận chuyển", shippingRoles,
    async (account, refresh) => {
        const canDispatch = ["MANAGER", "DISPATCHER"].includes(account.role);
        const view = param("view");
        const nav = canDispatch ? inline(actionLink("Danh sách chuyến", "my-shipments.html"),
            actionLink("Hồ sơ tài xế", "my-shipments.html", {view: "drivers"}),
            actionLink("Phương tiện", "my-shipments.html", {view: "vehicles"})) : null;
        if (canDispatch && view === "drivers") return stack(nav, await driverView(account, refresh));
        if (canDispatch && view === "vehicles") return stack(nav, await vehicleView(account, refresh));
        const id = idParam();
        if (id) {
            const shipment = await api("/shipments/" + id);
            return stack(nav, await tripView(shipment, account, refresh));
        }
        const list = await api(account.role === "DRIVER" ? "/shipments/mine" : "/shipments");
        return stack(nav, panel("Chuyến vận chuyển", table([
            ["Phiếu xuất", row => link(row.dispatchNo, "my-shipments.html", {id: row.id})],
            ["Đơn vị", "carrierName"], ["Tài xế", "driverName"], ["Xe", "plateNumber"],
            ["Hẹn", row => dateTime(row.startAt)], ["Nơi nhận", "pickupLocation"],
            ["Điểm giao", "dropoffLocation"], ["Trạng thái", row => badge(row.status)],
        ], list)));
    });

async function driverView(account, refresh) {
    const drivers = await api("/shipments/drivers");
    const selected = drivers.find(row => row.id === idParam());
    return stack(panel("Hồ sơ tài xế", table([
        ["Tài khoản", row => link(row.username, "my-shipments.html", {view: "drivers", id: row.id})],
        ["Tên", "fullName"], ["Liên hệ", "phone"], ["Đơn vị", "carrierName"],
        ["Phân công", row => row.available ? "Sẵn sàng" : "Ngừng"],
    ], drivers)), selected ? editor("Sửa liên hệ và khả năng nhận chuyến", [
        ["carrierName", "Mã đơn vị", {...requiredText, readonly: account.role === "DISPATCHER"}],
        ["phone", "Điện thoại tài xế", optionalText], ["available", "Sẵn sàng nhận chuyến", {type: "checkbox"}],
    ], selected, async data => {
        await put("/shipments/drivers/" + selected.id, data);
        await refresh("Đã lưu hồ sơ tài xế.");
    }) : null);
}

async function vehicleView(account, refresh) {
    const list = await api("/shipments/vehicles");
    const selected = list.find(row => row.id === idParam());
    return stack(
        panel("Đội xe", table([
            ["Mã xe", row => link(row.code, "my-shipments.html", {view: "vehicles", id: row.id})],
            ["Biển số", "plateNumber"], ["Đơn vị", "carrierName"], ["Trạng thái", row => row.active ? "Hoạt động" : "Ngừng hoạt động"],
        ], list), selected ? h("div", {class: "top-gap"}, actionLink("Thêm xe", "my-shipments.html", {view: "vehicles"})) : null),
        editor(selected ? "Sửa xe " + selected.code : "Thêm phương tiện", [
            ["carrierName", "Mã đơn vị", {...requiredText, readonly: Boolean(account.carrierName && account.role === "DISPATCHER")}],
            ["code", "Mã xe", requiredText], ["plateNumber", "Biển số", requiredText],
            ["active", "Hoạt động", {type: "checkbox", value: true}],
        ], selected ?? {carrierName: account.carrierName}, async data => {
            if (selected) await put("/shipments/vehicles/" + selected.id, data);
            else await post("/shipments/vehicles", data);
            await refresh("Đã lưu phương tiện.");
        }));
}

async function tripView(trip, account, refresh) {
    const path = "/shipments/" + trip.id;
    const driver = account.role === "DRIVER";
    const dispatch = ["MANAGER", "DISPATCHER"].includes(account.role);
    const warehouse = ["MANAGER", "STAFF"].includes(account.role);
    const before = ["ASSIGNED", "AWAITING_DRIVER"].includes(trip.status);
    const actions = [];
    if (dispatch && (before || trip.status === "PENDING_ASSIGNMENT")) {
        actions.push(await assignment(path, trip, refresh));
    }
    if (driver && trip.status === "AWAITING_DRIVER") {
        actions.push(panel("Xác nhận thực nhận", form(fields(
            input("Số kiện nhận", "packageCount", {...positiveWhole, value: trip.checkedPackages}),
            input("Ghi chú bàn giao", "note", optionalText)), "Nhận hàng từ kho", async element => {
                await post(path + "/accept", {packageCount: numberValue(element, "packageCount"), note: textValue(element, "note")});
                await refresh("Đã xác nhận bàn giao.");
            })));
    }
    if (driver && trip.status === "DISPATCHED") {
        actions.push(panel("Kết quả giao hàng", form(fields(
            select("Kết quả", "outcome", [["ok", "Đã giao"], ["failed", "Giao thất bại"]]),
            input("Ghi chú / lý do thất bại", "note", optionalText),
            input("Yêu cầu hoàn hàng nếu thất bại", "requestReturn", {type: "checkbox"})), "Lưu kết quả", async element => {
                await post(path + "/delivery", {delivered: textValue(element, "outcome") === "ok", note: textValue(element, "note"),
                    requestReturn: checked(element, "requestReturn")});
                await refresh("Đã lưu kết quả giao.");
            })));
    }
    if (before && (dispatch || driver)) {
        actions.push(reschedule(path, refresh, driver ? ["LATE_PICKUP"] : ["NO_SHOW", "LATE_PICKUP"], "Đổi lịch nhận"));
    }
    if (dispatch && trip.status === "FAILED" && !trip.returnRequested) {
        actions.push(reschedule(path, refresh, ["REDELIVERY"], "Hẹn giao lại"),
            panel("Yêu cầu hàng hoàn về kho", form(input("Lý do", "reason", requiredText), "Yêu cầu hoàn", async element => {
                await post(path + "/request-return", {reason: textValue(element, "reason")});
                await refresh("Đã gửi yêu cầu hoàn cho kho.");
            })));
    }
    return stack(panel("Phiếu xuất " + trip.dispatchNo, stack(
        inline(badge(trip.status), warehouse ? actionLink("Mở đơn xuất", "outbound-detail.html", {id: trip.outboundOrderId}) : null,
            warehouse && trip.returnRequested ? actionLink("Lập phiếu hoàn", "inbound-form.html",
                {kind: "return", outboundId: trip.outboundOrderId}) : null),
        details([
            ["Đơn vị", trip.carrierName], ["Tài xế", trip.driverName], ["Điện thoại tài xế", trip.driverPhone],
            ["Biển số", trip.plateNumber], ["Mã theo dõi", trip.trackingNo], ["Nhận tại", trip.pickupLocation],
            ["Giao tại", trip.dropoffLocation], ["Người nhận", trip.contactName], ["Điện thoại giao nhận", trip.contactPhone],
            ["Giờ hẹn", dateTime(trip.startAt)], ["Đến", dateTime(trip.endAt)], ["Số kiện dự kiến", qty(trip.packageCount)],
            ["Kho đối chiếu", qty(trip.checkedPackages)], ["Tài xế đã nhận", qty(trip.receivedPackages)],
            ["Bàn giao lúc", dateTime(trip.dispatchedAt)], ["Giao lúc", dateTime(trip.deliveredAt)],
            ["Kết quả", trip.deliveryNote], ["Hàng hoàn", trip.returnRequested ? "Đã yêu cầu" : null],
        ].filter(([, value]) => value != null && value !== "—")))), actions,
        panel("Lịch sử chuyến", timeline(trip.history)));
}

async function assignment(path, trip, refresh) {
    const [drivers, vehicles] = await Promise.all([
        api("/shipments/drivers", {query: {carrier: trip.carrierName, availableOnly: true}}),
        api("/shipments/vehicles", {query: {carrier: trip.carrierName, activeOnly: true}}),
    ]);
    return panel("Phân công chuyến", form(fields(
        select("Tài xế", "driverId", optionRows(drivers, row => row.fullName + " · " + (row.phone || row.username)), trip.driverId),
        select("Xe", "vehicleId", optionRows(vehicles, row => row.code + " · " + row.plateNumber), trip.vehicleId),
        input("Từ giờ", "startAt", {type: "datetime-local", required: true, value: trip.startAt?.slice(0, 16)}),
        input("Đến giờ", "endAt", {type: "datetime-local", required: true, value: trip.endAt?.slice(0, 16)}),
        input("Mã theo dõi", "trackingNo", {...optionalText, value: trip.trackingNo})), "Lưu phân công", async element => {
        await post(path + "/assign", {
            driverId: numberValue(element, "driverId"), vehicleId: numberValue(element, "vehicleId"),
            startAt: textValue(element, "startAt"), endAt: textValue(element, "endAt"), trackingNo: textValue(element, "trackingNo"),
        });
        await refresh("Đã lưu tài xế, xe và giờ hẹn.");
    }));
}

function reschedule(path, refresh, kinds, title) {
    return panel(title, form(fields(
        select("Sự kiện", "kind", choices(kinds), kinds[0]),
        input("Giờ mới từ", "startAt", {type: "datetime-local", required: true}),
        input("Giờ mới đến", "endAt", {type: "datetime-local", required: true}),
        input("Lý do", "reason", requiredText)), "Lưu lịch mới", async element => {
        const data = Object.fromEntries(["kind", "startAt", "endAt", "reason"].map(key => [key, textValue(element, key)]));
        await post(path + "/reschedule", data);
        await refresh("Đã lưu lịch hẹn mới.");
    }));
}
