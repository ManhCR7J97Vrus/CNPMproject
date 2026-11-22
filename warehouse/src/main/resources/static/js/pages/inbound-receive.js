import {api, post} from "../api.js";
import {actionLink, choices, fields, form, h, href, idParam, input, mount, numberValue, select, stack, textValue, warehouse, panel} from "../components/ui.js";
import {amountField, optionalText} from "../components/editor.js";
import {batchField, inboundDetails, placementPicker} from "../components/inbound.js";

mount("Đối chiếu hàng thực nhận", warehouse, async () => {
    const id = idParam();
    if (!id) return actionLink("Chọn phiếu nhập", "inbound-list.html");
    const [order, locations, profiles] = await Promise.all([
        api("/inbound-orders/" + id), api("/locations"), api("/products/storage-profiles"),
    ]);
    if (!["DRAFT", "EXPECTED"].includes(order.status)) return actionLink("Mở phiếu", "inbound-detail.html", {id});
    const lines = order.items.map(line => h("fieldset", {}, h("legend", {},
        line.skuCode + " · Đặt " + line.qtyOrdered + " · " + [line.category, line.perishable && "Dễ hư hỏng", line.storageCondition]
            .filter(Boolean).join(" / ")), fields(
        input("Số thực giao", "received-" + line.id, {...amountField, required: true, value: line.qtyOrdered}),
        input("Số đạt", "accepted-" + line.id, {...amountField, required: true, value: line.qtyOrdered}),
        input("Đơn giá", "cost-" + line.id, {...amountField, required: true, value: line.unitCost}),
        select("Tình trạng", "condition-" + line.id, choices(["GOOD", "DAMAGED", "QUARANTINE"]), "GOOD"),
        batchField(line, false, false),
        input("Ngày sản xuất", "mfg-" + line.id, {type: "date", value: line.mfgDate}),
        input("Hạn dùng" + (line.hasExpiry ? " (SKU có hạn)" : ""), "expiry-" + line.id, {type: "date", value: line.expiryDate}),
        select("Hồ sơ bảo quản", "profile-" + line.id, profiles.map(p => [p.code, p.code]), line.storageCondition, false, "Theo SKU"),
        placementPicker(line, locations, {suggestions: true, required: false}),
        input("Lý do chênh lệch / đổi phân loại", "reason-" + line.id, optionalText))));
    return panel("Đối chiếu " + order.orderNo, stack(inboundDetails(order), form(lines, "Gửi quản lý xác nhận", async element => {
        const items = order.items.map(line => ({
            inboundItemId: line.id, locationId: numberValue(element, "location-" + line.id),
            qtyReceived: numberValue(element, "received-" + line.id), qtyAccepted: numberValue(element, "accepted-" + line.id),
            unitCost: numberValue(element, "cost-" + line.id), itemCondition: textValue(element, "condition-" + line.id),
            batchNo: textValue(element, "batch-" + line.id), mfgDate: textValue(element, "mfg-" + line.id),
            expiryDate: textValue(element, "expiry-" + line.id), storageCondition: textValue(element, "profile-" + line.id),
            discrepancyReason: textValue(element, "reason-" + line.id),
        }));
        await post("/inbound-orders/" + id + "/receive", {items});
        location.assign(href("inbound-detail.html", {id}));
    })));
});
