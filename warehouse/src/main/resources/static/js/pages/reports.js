import {api} from "../api.js";
import {fields, form, input, link, localDate, mount, panel, qty, stack, table, textValue} from "../components/ui.js";

mount("Báo cáo và cảnh báo", ["MANAGER"], async () => {
    const alerts = await api("/batches/alerts");
    const today = alerts.asOf || localDate();
    const output = panel(null);
    const alertColumns = [
        ["SKU", "skuCode"], ["Lô", row => link(row.batchNo, "batch-trace.html", {batchId: row.id})],
        ["Hàng hóa", "productName"], ["Hạn dùng", "expiryDate"],
    ];
    async function load(query) {
        const report = await api("/reports/stock-summary", {query});
        output.replaceChildren(table([
            ["SKU", "skuCode"], ["Tồn đầu", row => qty(row.openingQty)], ["Nhập và hoàn", row => qty(row.inboundQty)],
            ["Xuất", row => qty(row.outboundQty)], ["Điều chỉnh ròng", row => qty(row.netAdjustment)], ["Tồn cuối", row => qty(row.closingQty)],
        ], report));
    }
    const initial = {from: today.slice(0, 7) + "-01", to: today};
    const search = form(fields(input("Từ ngày", "from", {type: "date", value: initial.from, required: true}),
        input("Đến ngày", "to", {type: "date", value: initial.to, required: true}),
        input("Kho", "warehouseName"), input("SKU", "sku")), "Lập báo cáo", element =>
        load(Object.fromEntries(["from", "to", "warehouseName", "sku"].map(key => [key, textValue(element, key)]))));
    await load(initial);
    return stack(panel("Nhập xuất tồn theo kỳ", search), output,
        panel("Đã hết hạn · " + today, table(alertColumns, alerts.expired)),
        panel("Sắp hết hạn · " + today, table(alertColumns, alerts.expiring)));
});
