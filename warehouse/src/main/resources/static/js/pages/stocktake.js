import {api, post} from "../api.js";
import {actionLink, badge, checked, dateTime, details, fields, form, h, href, idParam, inline, input, link,
    locationText, mount, numberValue, optionRows, panel, param, qty, select, stack, table, textValue, warehouse} from "../components/ui.js";
import {optionalText, positiveAmount, requiredText, wholeAmount} from "../components/editor.js";

mount("Kiểm kê và cách ly", warehouse, async (account, refresh) => {
    const manager = account.role === "MANAGER";
    const nav = inline(actionLink("Phiếu kiểm kê", "stocktake.html"),
        actionLink("Đề nghị cách ly", "stocktake.html", {view: "quarantine"}));
    if (param("view") === "quarantine") return stack(nav, await quarantineView(manager, refresh));
    const id = idParam();
    if (id) return stack(nav, await countView(id, account, refresh));
    const counts = await api("/stocktakes");
    return stack(nav, panel("Danh sách phiếu", table([
        ["Số phiếu", row => link(row.referenceNo, "stocktake.html", {id: row.id})],
        ["Mốc số sổ", row => dateTime(row.snapshotAt)], ["Người đếm", "counter"],
        ["Người duyệt", "approvedBy"], ["Trạng thái", row => badge(row.status)],
    ], counts)), manager ? await startCount() : null);
});

async function startCount() {
    const [locations, counters] = await Promise.all([api("/locations"), api("/stocktakes/counters")]);
    return panel("Chụp số sổ kiểm kê", form(stack(
        select("Người đếm", "counterId", optionRows(counters, user => user.fullName + " · " + user.username),
            "", false, "Tôi sẽ kiểm kê"),
        h("fieldset", {}, h("legend", {}, "Chọn các ô cần đếm"), fields(locations.map(row =>
            h("label", {class: "field field-check"}, h("input", {type: "checkbox", name: "locationIds", value: row.id}),
                h("span", {}, locationText(row))))))), "Mở phiếu kiểm kê", async element => {
        const locationIds = new FormData(element).getAll("locationIds").map(Number);
        const count = await post("/stocktakes", {locationIds, counterId: numberValue(element, "counterId")});
        location.assign(href("stocktake.html", {id: count.id}));
    }));
}

async function countView(id, account, refresh) {
    const count = await api("/stocktakes/" + id);
    const path = "/stocktakes/" + id;
    const manager = account.role === "MANAGER";
    const canCount = count.status === "DRAFT" && (manager || count.counter === account.username);
    const forms = [];
    if (canCount) {
        forms.push(panel("Số thực đếm", form(count.items.map(row => h("fieldset", {}, h("legend", {},
            row.skuCode + " · " + row.batchNo + " · " + row.locationCode + " · sổ " + qty(row.expectedQty)), fields(
            input("Đếm tại ô", "count-" + row.id, {...wholeAmount, value: row.countedQty}),
            input("Lý do chênh lệch", "reason-" + row.id, {...optionalText, value: row.reason})))),
        "Gửi duyệt kiểm kê", async element => {
            const items = count.items.map(row => ({itemId: row.id, countedQty: numberValue(element, "count-" + row.id),
                reason: textValue(element, "reason-" + row.id)}));
            await post(path + "/count", {items});
            await refresh("Đã gửi số đếm để duyệt.");
        })));
    }
    if (manager && count.status === "COUNTED") {
        forms.push(panel("Duyệt điều chỉnh", form(input("Lý do duyệt", "reason", optionalText), "Duyệt và ghi chênh lệch", async element => {
            await post(path + "/approve", {reason: textValue(element, "reason")});
            await refresh("Đã duyệt kiểm kê.");
        })), panel("Trả phiếu để đếm lại", form(input("Ý kiến", "reason", requiredText), "Yêu cầu đếm lại", async element => {
            await post(path + "/recount", {reason: textValue(element, "reason")});
            await refresh("Đã trả phiếu để đếm lại.");
        })));
    }
    if (manager && count.status === "NEEDS_RECOUNT") {
        forms.push(panel("Chụp lại số sổ", form(null, "Lấy mốc mới", async () => {
            await post(path + "/refresh");
            await refresh("Đã chụp lại số sổ. Phiếu mở để đếm.");
        })));
    }
    return stack(panel(count.referenceNo, stack(badge(count.status), details([
        ["Mốc số sổ", dateTime(count.snapshotAt)], ["Người lập", count.createdBy], ["Người đếm", count.counter],
        ["Người duyệt", count.approvedBy], ["Duyệt lúc", dateTime(count.approvedAt)], ["Ý kiến", count.reason],
    ]))),
    panel("Theo lô và ô", table([
        ["SKU", "skuCode"], ["Lô", "batchNo"], ["Ô", "locationCode"], ["Số sổ", row => qty(row.expectedQty)],
        ["Thực đếm", row => row.countedQty == null ? "Chưa đếm" : qty(row.countedQty)],
        ["Chênh lệch", row => qty(row.difference)], ["Lý do", "reason"],
    ], count.items)), forms);
}

async function quarantineView(manager, refresh) {
    const [entries, stocks] = await Promise.all([api("/stocktakes/quarantine"), api("/inventory")]);
    const selected = entries.find(row => row.id === idParam());
    const stock = stocks.filter(row => !row.quarantine && row.qtyOnHand > 0);
    const listed = panel("Đề nghị cách ly", table([
        ["Số đề nghị", row => link(row.referenceNo, "stocktake.html", {view: "quarantine", id: row.id})],
        ["Lô", "batchNo"], ["Ô nguồn", "fromLocation"], ["Ô cách ly", "toLocation"], ["Số hỏng", row => qty(row.quantity)],
        ["Lý do", "reason"], ["Trạng thái", row => badge(row.status)],
    ], entries), selected ? h("div", {class: "top-gap"}, actionLink("Tạo đề nghị", "stocktake.html", {view: "quarantine"})) : null);
    if (!selected) {
        return stack(listed, panel("Báo hàng hỏng", form(fields(
            select("Lô và ô đang tồn", "stock", optionRows(stock, row => row.skuCode + " · " + row.batchNo + " · " + row.locationCode)),
            input("Lượng hỏng", "quantity", positiveAmount), input("Lý do", "reason", requiredText)), "Gửi đề nghị", async element => {
            const row = stock.find(item => item.id === numberValue(element, "stock"));
            const request = await post("/stocktakes/quarantine", {batchId: row.batchId, fromLocationId: row.locationId,
                quantity: numberValue(element, "quantity"), reason: textValue(element, "reason")});
            location.assign(href("stocktake.html", {view: "quarantine", id: request.id}));
        })));
    }
    const path = "/stocktakes/quarantine/" + selected.id;
    let approval;
    if (manager && selected.status !== "COMPLETED") {
        const locations = await api("/locations");
        const origin = locations.find(row => row.id === selected.fromLocationId);
        const available = locations.filter(row => row.quarantine && row.active && !row.locked && row.warehouseName === origin?.warehouseName);
        approval = panel("Duyệt vị trí cách ly", form(select("Ô cách ly", "toLocationId", optionRows(available, locationText),
            selected.toLocationId), "Duyệt cách ly", async element => {
            await post(path + "/approve", {toLocationId: numberValue(element, "toLocationId")});
            await refresh("Đã duyệt. Hãy xác nhận sau khi chuyển thực tế.");
        }));
    }
    const completion = selected.status === "APPROVED" ? panel("Chuyển thực tế", form(
        input("Đã chuyển hàng đến " + selected.toLocation, "moved", {type: "checkbox", required: true}), "Ghi cách ly", async element => {
            if (!checked(element, "moved")) return;
            await post(path + "/post");
            await refresh("Đã chuyển hàng sang ô cách ly.");
        })) : null;
    return stack(listed, panel(selected.referenceNo, details([
        ["Người báo", selected.createdBy], ["Báo lúc", dateTime(selected.createdAt)],
        ["Người duyệt", selected.approvedBy], ["Duyệt lúc", dateTime(selected.approvedAt)], ["Ghi sổ lúc", dateTime(selected.completedAt)],
    ])), approval, completion);
}
