import {api, post} from "../api.js";
import {button, fields, form, h, input, link, locationText, mount, notice, numberValue, optionRows,
    panel, qty, select, stack, textValue, warehouse} from "../components/ui.js";
import {optionalText, positiveAmount} from "../components/editor.js";

mount("Chuyển ô lưu trữ", warehouse, async account => {
    const [stocks, batches] = await Promise.all([api("/inventory"), api("/batches")]);
    const movable = stocks.filter(row => row.qtyAvailable > 0);
    if (!movable.length) return panel(null, h("p", {}, "Chưa có lô có lượng khả dụng để chuyển."));
    const balances = new Map(movable.map(row => [row.id, row]));
    const source = select("Lô / ô nguồn", "stockId", optionRows(movable,
        row => row.skuCode + " · " + row.batchNo + " · " + row.warehouseName + " / " + row.locationCode + " · khả dụng " + qty(row.qtyAvailable)));
    const destination = select("Ô đích", "toLocationId", [], "", true, "Chọn ô nguồn trước");
    const feedback = h("div", {"aria-live": "polite"});
    let requestVersion = 0;
    source.querySelector("select").addEventListener("change", async event => {
        const version = ++requestVersion;
        const stock = balances.get(Number(event.target.value));
        const control = destination.querySelector("select");
        control.replaceChildren(h("option", {value: ""}, stock ? "Đang tìm ô…" : "Chọn ô nguồn trước"));
        feedback.replaceChildren();
        if (!stock) return;
        try {
            const batch = batches.find(row => row.id === stock.batchId);
            const locations = await api("/locations/suggestions", {query: {
                productId: stock.productId, storageCondition: batch?.storageCondition,
            }});
            if (version !== requestVersion) return;
            const valid = locations.filter(row => row.id !== stock.locationId);
            control.replaceChildren(h("option", {value: ""}, valid.length ? "Chọn ô…" : "Chưa có ô phù hợp"),
                ...valid.map(row => h("option", {value: row.id}, locationText(row))));
        } catch (error) {
            if (version === requestVersion) feedback.replaceChildren(notice(error.message, true));
        }
    });
    return panel("Ghi nhận chuyển thực tế", form(stack(
        fields(source, destination, input("Số lượng", "quantity", positiveAmount), input("Lý do", "reason", optionalText)),
        feedback), "Xác nhận chuyển", async element => {
            const stock = balances.get(numberValue(element, "stockId"));
            const result = await post("/movements/transfer", {batchId: stock.batchId, fromLocationId: stock.locationId,
                toLocationId: numberValue(element, "toLocationId"), quantity: numberValue(element, "quantity"),
                reason: textValue(element, "reason")});
            const reference = account.role === "MANAGER"
                ? link(result.referenceNo, "batch-trace.html", {batchId: result.batchId, reference: result.referenceNo})
                : result.referenceNo;
            element.replaceWith(stack(notice(h("span", {}, "Đã chuyển " + qty(result.changeQty) + " · Chứng từ ", reference)),
                button("Chuyển tiếp", () => location.reload())));
        }));
});
