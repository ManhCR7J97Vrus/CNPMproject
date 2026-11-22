import {api} from "../api.js";
import {details, fields, form, input, link, mount, panel, qty, stack, table, textValue, warehouse} from "../components/ui.js";

mount("Tồn kho theo lô và ô", warehouse, async account => {
    const result = panel("Số dư");
    const columns = [
        ["SKU", "skuCode"], ["Hàng hóa", "productName"],
        ["Lô", row => account.role === "MANAGER" ? link(row.batchNo, "batch-trace.html", {batchId: row.batchId}) : row.batchNo],
        ["Kho / ô", row => row.warehouseName + " / " + row.locationCode],
        ["Tồn thực", row => qty(row.qtyOnHand)], ["Đã giữ", row => qty(row.qtyReserved)],
        ["Khả dụng", row => qty(row.qtyAvailable)], ["Hạn dùng", "expiryDate"],
        ["Ghi chú", row => row.quarantine ? "Cách ly" : row.locked ? "Ô khóa" : row.eligible ? "" : "Không được cấp phát"],
    ];
    async function load(query) {
        const summary = await api("/inventory/summary", {query});
        result.replaceChildren(stack(details([
            ["Tổng tồn thực", qty(summary.qtyOnHand)], ["Tổng đã giữ", qty(summary.qtyReserved)],
            ["Tổng cấp được", qty(summary.qtyAvailable)],
        ]), table(columns, summary.items)));
    }
    const search = form(fields(input("SKU", "sku"), input("Mã lô", "batchNo"),
        input("Mã ô", "locationCode"), input("Kho", "warehouseName")), "Tra tồn", element =>
        load(Object.fromEntries(["sku", "batchNo", "locationCode", "warehouseName"].map(key => [key, textValue(element, key)]))));
    await load();
    return stack(panel("Bộ lọc", search), result);
});
