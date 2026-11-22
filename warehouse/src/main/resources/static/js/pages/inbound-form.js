import {api, post, put} from "../api.js";
import {actionLink, choices, fields, form, h, href, idParam, inboundRoles, input, locationText, mount, numberValue,
    optionRows, panel, param, qty, records, select, skuText, stack, textValue} from "../components/ui.js";
import {amountField, optionalText, positiveAmount, requiredText} from "../components/editor.js";

mount(account => account.role === "SUPPLIER" ? "Gửi thông báo giao hàng" : "Lập phiếu nhập", inboundRoles,
    async account => {
        if (param("kind") === "return" && account.role !== "SUPPLIER") return returnView();
        const id = idParam();
        const partner = account.role === "SUPPLIER";
        const [products, suppliers, existing] = await Promise.all([
            api(partner ? "/products/catalog" : "/products"), partner ? [] : api("/suppliers/options"),
            id ? api("/inbound-orders/" + id) : null,
        ]);
        if (existing && !["DRAFT", "EXPECTED"].includes(existing.status)) {
            return panel(null, actionLink("Mở phiếu", "inbound-detail.html", {id}));
        }
        const stock = products.filter(product => product.active !== false);
        const lines = records((key, line) => [
            select("SKU", "product-" + key, optionRows(stock, product => skuText(product)
                + (!partner ? " · " + [product.category, product.perishable && "Dễ hư hỏng", product.storageCondition].filter(Boolean).join(" / ") : "")),
                line.productId),
            input("Số lượng dự kiến", "qty-" + key, {...positiveAmount, value: line.qtyOrdered}),
            partner ? null : input("Đơn giá", "cost-" + key, {...amountField, value: line.unitCost ?? 0}),
        ], existing?.items, (element, key) => ({
            productId: numberValue(element, "product-" + key),
            qtyOrdered: numberValue(element, "qty-" + key), unitCost: numberValue(element, "cost-" + key),
        }));
        return panel(existing ? "Sửa phiếu " + existing.orderNo : partner ? "Thông báo dự kiến nhận" : "Chứng từ giao đến kho",
            form(stack(fields(
                !partner ? select("Nhà cung cấp", "supplierId",
                    optionRows(suppliers.filter(row => row.active), row => row.code + " · " + row.name), existing?.supplierId) : null,
                input("Mã chứng từ / thông báo", "externalReference", {...requiredText, value: existing?.externalReference}),
                input("Ngày giờ dự kiến", "expectedAt", {type: "datetime-local", required: partner,
                    value: existing?.expectedAt?.slice(0, 16)})), lines.element),
            partner ? "Lưu thông báo" : "Lưu phiếu nháp", async element => {
                const data = {supplierId: numberValue(element, "supplierId"), expectedAt: textValue(element, "expectedAt"),
                    externalReference: textValue(element, "externalReference"), items: lines.data(element)};
                const saved = id ? await put("/inbound-orders/" + id, data) : await post("/inbound-orders", data);
                location.assign(href("inbound-detail.html", {id: saved.id}));
            }));
    });

async function returnView() {
    const outboundId = idParam("outboundId");
    if (!outboundId) {
        const orders = (await api("/outbound-orders")).filter(row => ["HANDED_OVER", "DELIVERED", "FAILED"].includes(row.status));
        return panel("Lập phiếu hoàn", form(select("Phiếu xuất gốc", "outboundId",
            optionRows(orders, row => row.dispatchNo + " · " + row.orderNo)), "Chọn phiếu xuất", element => {
                location.assign(href("inbound-form.html", {kind: "return", outboundId: numberValue(element, "outboundId")}));
            }));
    }
    const [order, locations] = await Promise.all([api("/outbound-orders/" + outboundId), api("/locations")]);
    if (!["HANDED_OVER", "DELIVERED", "FAILED"].includes(order.status)) {
        return panel(null, h("p", {}, "Chỉ lập phiếu hoàn sau khi đã ghi xuất."));
    }
    const rows = records((key, line) => [
        select("Dòng phiếu xuất", "item-" + key, order.allocations.map(row => [row.itemId,
            row.skuCode + " · " + row.batchNo + " · " + row.location + " · đã xuất " + qty(row.qtyPicked)]), line.itemId),
        input("Số lượng hoàn", "quantity-" + key, positiveAmount),
        select("Tình trạng", "condition-" + key, choices(["GOOD", "DAMAGED", "QUARANTINE"]), "GOOD"),
        select("Ô nhận hoàn", "location-" + key, optionRows(locations.filter(row => row.active && !row.locked),
            row => locationText(row) + (row.quarantine ? " · Cách ly" : ""))),
    ], [], (element, key) => ({
        outboundItemId: numberValue(element, "item-" + key), quantity: numberValue(element, "quantity-" + key),
        itemCondition: textValue(element, "condition-" + key), locationId: numberValue(element, "location-" + key),
    }));
    return panel("Lập phiếu hoàn từ " + order.dispatchNo, form(stack(rows.element, input("Lý do", "reason", optionalText)),
        "Gửi duyệt hoàn", async element => {
            const receipt = await post("/inbound-orders/returns", {outboundOrderId: outboundId,
                reason: textValue(element, "reason"), items: rows.data(element)});
            location.assign(href("inbound-detail.html", {id: receipt.id}));
        }));
}
