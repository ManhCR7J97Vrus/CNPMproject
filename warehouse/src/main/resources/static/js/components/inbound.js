import {api} from "../api.js";
import {dateTime, details, h, input, locationText, notice, optionRows, qty, select, table} from "./ui.js";
import {optionalText} from "./editor.js";

export function inboundLines(order, internal) {
    const columns = [
        ["SKU", "skuCode"], ["Hàng hóa", "productName"], ["Đặt", row => qty(row.qtyOrdered)],
        ...(internal ? [
            ["Giao", row => qty(row.qtyReceived)], ["Đạt", row => qty(row.qtyAccepted)],
            ["Loại", row => qty(row.qtyRejected)], ["Thiếu", row => qty(row.qtyMissing)],
            ["Đơn giá", row => qty(row.unitCost)], ["Lô", "batchNo"], ["Ô", "locationCode"],
            ["Hồ sơ", row => [row.category, row.perishable && "Dễ hư hỏng", row.storageCondition]
                .filter(Boolean).join(" · ")],
            ["Hạn dùng", "expiryDate"], ["Lý do", "discrepancyReason"],
        ] : []),
    ];
    return table(columns, order.items);
}

export function inboundDetails(order) {
    return details([
        ["Số phiếu", order.orderNo], ["Mã ngoài", order.externalReference],
        ["Nhà cung cấp", order.supplierName], ["Dự kiến nhận", dateTime(order.expectedAt)],
        ["Người lập", order.createdBy], ["Lập lúc", dateTime(order.createdAt)],
    ].filter(([, value]) => value != null && value !== "—"));
}

export function placementPicker(line, locations, {quarantine = false, suggestions = false, required = true} = {}) {
    const rows = suggestions ? locations.filter(row => row.active && !row.locked && row.quarantine === quarantine) : locations;
    const picker = select("Ô lưu trữ", "location-" + line.id, optionRows(rows, locationText), line.locationId, required);
    if (!suggestions) return picker;
    const status = h("div", {"aria-live": "polite"});
    const button = h("button", {type: "button", class: "secondary"}, "Gợi ý ô phù hợp");
    button.addEventListener("click", async () => {
        status.replaceChildren();
        button.disabled = true;
        try {
            const container = button.closest("fieldset");
            const condition = container?.querySelector('[name="profile-' + line.id + '"]')?.value || line.storageCondition;
            const valid = await api("/locations/suggestions", {query: {
                productId: line.productId, storageCondition: condition, quarantine,
            }});
            const control = picker.querySelector("select");
            const previous = control.value;
            control.replaceChildren(h("option", {value: ""}, valid.length ? "Chọn ô…" : "Chưa có ô phù hợp"),
                ...valid.map(row => h("option", {value: row.id}, locationText(row))));
            if (valid.some(row => String(row.id) === previous)) control.value = previous;
            if (!valid.length) status.replaceChildren(notice("Chưa có ô phù hợp với điều kiện của dòng hàng."));
        } catch (error) {
            status.replaceChildren(notice(error.message, true));
        } finally {
            button.disabled = false;
        }
    });
    return h("div", {class: "stack"}, picker, button, status);
}

export function batchField(line, locked = false, required = true) {
    return input("Mã lô", "batch-" + line.id, {value: line.batchNo, readonly: locked,
        required, ...optionalText});
}
