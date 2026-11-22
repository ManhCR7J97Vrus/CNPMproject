import {api, post} from "../api.js";
import {choices, fields, form, href, input, mount, numberValue, optionRows, panel, records, select,
    skuText, stack, textValue} from "../components/ui.js";
import {optionalText, positiveAmount, requiredText} from "../components/editor.js";

mount("Tạo yêu cầu gửi hàng", ["CUSTOMER"], async () => {
    const catalog = await api("/products/catalog");
    const rows = records(key => [
        select("SKU", "product-" + key, optionRows(catalog, product => skuText(product)
            + (product.storageCondition ? " · " + product.storageCondition : ""))),
        input("Số lượng", "qty-" + key, positiveAmount),
    ], [], (element, key) => ({
        productId: numberValue(element, "product-" + key), qtyRequested: numberValue(element, "qty-" + key),
    }));
    return panel("Thông tin giao hàng", form(stack(fields(
        input("Địa chỉ giao", "shippingAddress", requiredText),
        input("Người nhận", "receiverName", optionalText), input("Số liên hệ", "receiverPhone", optionalText),
        select("Dịch vụ", "priority", choices(["NORMAL", "EXPRESS", "URGENT"]), "NORMAL"),
        input("Hạn giao", "deliveryDeadline", {type: "datetime-local"})), rows.element),
    "Gửi yêu cầu", async element => {
        const values = Object.fromEntries(["shippingAddress", "receiverName", "receiverPhone", "priority", "deliveryDeadline"]
            .map(key => [key, textValue(element, key)]));
        const order = await post("/outbound-orders", {...values, items: rows.data(element)});
        location.assign(href("outbound-detail.html", {id: order.id}));
    }));
});
