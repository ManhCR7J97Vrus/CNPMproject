import {api, post} from "../api.js";
import {actionLink, badge, checked, dateTime, details, fields, form, h, idParam, inline, input, label,
    locationText, mount, notice, numberValue, optionRows, outboundRoles, panel, qty, select, stack,
    table, textValue, timeline} from "../components/ui.js";
import {optionalText, positiveAmount, positiveWhole, requiredText} from "../components/editor.js";

const stockColumns = [
    ["SKU", "skuCode"], ["Lô", "batchNo"], ["Ô", "location"],
    ["Cần lấy", row => qty(row.qtyRequested)], ["Đã lấy", row => qty(row.qtyPicked)],
];

mount("Chi tiết đơn xuất", outboundRoles, async (account, refresh) => {
    const id = idParam();
    if (!id) return actionLink("Chọn đơn xuất", "outbound-list.html");
    const path = "/outbound-orders/" + id;
    const order = await api(path);
    const customer = account.role === "CUSTOMER";
    const manager = account.role === "MANAGER";
    const sent = ["HANDED_OVER", "DELIVERED", "FAILED"].includes(order.status);
    const active = !sent && order.status !== "CANCELLED";
    const requestOpen = ["REQUESTED", "APPROVED", "PICKED", "PACKED"].includes(order.status);
    const cancelStatus = order.cancelRequestedAt
        ? "Chờ xử lý · " + order.cancelRequestReason : null;
    const header = panel(order.orderNo, stack(inline(badge(order.status),
        customer ? actionLink("Yêu cầu hỗ trợ", "support.html", {orderId: id}) : null,
        !customer && sent ? actionLink("Lập phiếu hoàn", "inbound-form.html", {kind: "return", outboundId: id}) : null),
    details([
        ["Khách hàng", order.customerName], ["Địa chỉ giao", order.shippingAddress], ["Người nhận", order.receiverName],
        ["Liên hệ", order.receiverPhone], ["Dịch vụ", label(order.priority)], ["Hạn giao", dateTime(order.deliveryDeadline)],
        ["Tạo lúc", dateTime(order.createdAt)], ["Số kiện", order.packageCount], ["Yêu cầu hủy", order.status === "CANCELLED" ? null : cancelStatus],
        ["Lý do hủy", order.cancelReason],
    ].filter(([, value]) => value != null && value !== "—"))));
    const demand = panel("Hàng trong đơn", table([
        ["SKU", "skuCode"], ["Tên hàng", "productName"], ["Yêu cầu", row => qty(row.qtyRequested)], ["Bảo quản", "storageCondition"],
    ], order.items));
    const actions = [];
    if (customer && requestOpen && !order.cancelRequestedAt) {
        actions.push(panel("Yêu cầu hủy", form(input("Lý do", "reason", requiredText), "Gửi yêu cầu hủy", async element => {
            await post(path + "/cancel-request", {reason: textValue(element, "reason")});
            await refresh("Quản lý kho đã nhận yêu cầu hủy.");
        })));
    }
    if (!customer && order.status === "REQUESTED") actions.push(suggest(path, manager, refresh));
    if (!customer && order.allocations.length) actions.push(panel("Danh sách soạn hàng", table(stockColumns, order.allocations)));
    if (!customer && order.status === "APPROVED") actions.push(await pick(path, order, refresh));
    if (manager && ["APPROVED", "PICKED"].includes(order.status)) actions.push(reallocate(path, refresh));
    if (!customer && order.status === "PICKED") {
        actions.push(panel("Đóng gói", form(input("Số kiện", "packageCount", positiveWhole), "Xác nhận đóng gói", async element => {
            await post(path + "/pack", {packageCount: numberValue(element, "packageCount")});
            await refresh("Đã lưu số kiện.");
        })));
    }
    if (!customer && order.status === "PACKED" && !order.dispatchNo) {
        actions.push(panel("Phiếu xuất", form(null, "Lập phiếu xuất chờ bàn giao", async () => {
            await post(path + "/issue-dispatch");
            await refresh("Đã lập phiếu xuất.");
        })));
    }
    if (!customer && order.dispatchNo) actions.push(await dispatch(path, order, sent, refresh));
    if (manager && active) actions.push(cancel(path, order, refresh));
    return stack(header, demand, actions, panel("Hành trình đơn", timeline(order.history)));
});

function suggest(path, manager, refresh) {
    const preview = h("div", {"aria-live": "polite"});
    const show = form(null, "Xem đề xuất FIFO / FEFO", async () => {
        const plan = await api(path + "/suggestions");
        preview.replaceChildren(stack(table(stockColumns, plan.allocations),
            plan.shortages.length ? notice(plan.shortages.map(row => row.skuCode + " thiếu " + qty(row.missing)).join(" · "), true) : null));
    });
    const approve = manager ? form(null, "Duyệt và giữ tồn", async () => {
        await post(path + "/approve");
        await refresh("Đã duyệt và giữ hàng theo đơn.");
    }) : null;
    return panel("Cấp phát hàng", stack(show, preview, approve));
}

async function pick(path, order, refresh) {
    const [batches, locations] = await Promise.all([api("/batches"), api("/locations")]);
    const pending = order.allocations.filter(row => row.qtyPicked < row.qtyRequested);
    return panel("Xác nhận số lấy thực tế", stack(pending.map(row => h("fieldset", {},
        h("legend", {}, row.skuCode + " · " + row.batchNo + " · " + row.location + " · còn " + qty(row.qtyRequested - row.qtyPicked)),
        form(fields(
            select("Lô thực lấy", "batchId", optionRows(batches.filter(batch => batch.productId === row.productId), batch => batch.batchNo), row.batchId),
            select("Ô thực lấy", "locationId", optionRows(locations, locationText), row.locationId),
            input("Lượng lấy lần này", "qtyPicked", positiveAmount)), "Ghi số lấy", async element => {
            await post(path + "/pick", {items: [{outboundItemId: row.itemId, inventoryId: row.inventoryId,
                batchId: numberValue(element, "batchId"), locationId: numberValue(element, "locationId"),
                qtyPicked: numberValue(element, "qtyPicked")}]});
            await refresh("Đã ghi số lấy.");
        })))));
}

function reallocate(path, refresh) {
    return panel("Đổi lô đã giữ", form(input("Đã trả phần vừa lấy về ô, nếu có", "returned", {type: "checkbox"}),
        "Phân bổ lại", async element => {
            await api(path + "/reallocate", {method: "POST", query: {returnedToStorage: checked(element, "returned")}});
            await refresh("Đã cập nhật phân bổ.");
        }));
}

async function dispatch(path, order, sent, refresh) {
    const shipment = order.shipmentId ? await api("/shipments/" + order.shipmentId) : null;
    const status = sent ? "Đã xuất" : order.status === "CANCELLED" ? "Đã hủy"
        : shipment?.status === "AWAITING_DRIVER" ? "Chờ tài xế xác nhận" : "Chờ bàn giao";
    let next;
    if (order.status === "PACKED" && !shipment) {
        next = form(fields(
            input("Mã đơn vị vận chuyển", "carrierName", requiredText),
            input("Điểm nhận tại kho", "pickupLocation", requiredText),
            input("Điểm giao", "dropoffLocation", {...optionalText, value: order.shippingAddress}),
            input("Liên hệ người nhận", "contactPhone", {...optionalText, value: order.receiverPhone})),
        "Gửi yêu cầu vận chuyển", async element => {
            const values = Object.fromEntries(["carrierName", "pickupLocation", "dropoffLocation", "contactPhone"]
                .map(key => [key, textValue(element, key)]));
            await post("/shipments", {...values, outboundOrderId: order.id});
            await refresh("Đã gửi yêu cầu vận chuyển.");
        });
    } else if (order.status === "PACKED" && shipment?.status === "ASSIGNED") {
        next = form(fields(input("Số kiện thực đối chiếu", "packageCount", {...positiveWhole, min: "0"})),
            "Kho xác nhận kiện", async element => {
                await post(path + "/handover", {packageCount: numberValue(element, "packageCount")});
                await refresh("Kho đã đối chiếu. Đang chờ tài xế xác nhận.");
            });
    }
    return panel("Phiếu xuất " + order.dispatchNo, stack(details([
        ["Trạng thái", status], ["Số kiện đóng gói", qty(order.packageCount)], ["Lập lúc", dateTime(order.issuedAt)],
        ...(shipment ? [["Tài xế", shipment.driverName], ["Xe", shipment.vehicleCode], ["Giờ hẹn", dateTime(shipment.startAt)]] : []),
    ]), shipment ? actionLink("Mở chuyến", "my-shipments.html", {id: shipment.id}) : null, next));
}

function cancel(path, order, refresh) {
    const waiting = order.status === "CANCEL_PENDING_RETURN";
    return panel(waiting ? "Xác nhận trả hàng trước khi hủy" : "Duyệt hủy", form(fields(
        input("Lý do", "reason", {...requiredText, value: order.cancelReason ?? order.cancelRequestReason}),
        waiting ? input("Đã trả đủ phần đã lấy về ô", "returned", {type: "checkbox", required: true}) : null),
    waiting ? "Xác nhận và hủy đơn" : "Duyệt hủy đơn", async element => {
        const result = await post(path + "/cancel", {reason: textValue(element, "reason"), returnedToStorage: checked(element, "returned")});
        await refresh(result.status === "CANCELLED" ? "Đã hủy đơn." : "Cần trả hàng đã lấy về ô trước khi giải phóng số giữ.");
    }, {danger: true}));
}
