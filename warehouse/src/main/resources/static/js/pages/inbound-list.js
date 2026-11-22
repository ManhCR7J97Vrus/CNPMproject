import {api} from "../api.js";
import {actionLink, badge, dateTime, fields, form, href, inboundRoles, inline, input, link, mount, panel,
    param, select, choices, stack, table, textValue} from "../components/ui.js";

mount(account => account.role === "SUPPLIER" ? "Thông báo giao hàng" : "Nhập kho", inboundRoles, async account => {
    const supplier = account.role === "SUPPLIER";
    const reference = param("reference");
    const status = param("status");
    const all = await api("/inbound-orders", {query: {reference}});
    const rows = status ? all.filter(row => row.status === status) : all;
    const search = form(fields(input("Mã chứng từ", "reference", {value: reference}),
        select("Trạng thái", "status", choices(["EXPECTED", "DRAFT", "PENDING_CONFIRMATION", "CONFIRMED", "RECEIVED"]),
            status, false, "Tất cả")), "Tìm phiếu", form => {
        const query = {};
        const ref = textValue(form, "reference");
        const state = textValue(form, "status");
        if (ref) query.reference = ref;
        if (state) query.status = state;
        location.assign(href("inbound-list.html", query));
    });
    return stack(
        inline(actionLink(supplier ? "Gửi thông báo" : "Lập phiếu nhập", "inbound-form.html"),
            !supplier ? actionLink("Lập phiếu hoàn", "inbound-form.html", {kind: "return"}) : null),
        panel("Tra phiếu", search),
        panel("Danh sách", table([
            ["Số phiếu", row => link(row.orderNo, "inbound-detail.html", {id: row.id})],
            ["Mã ngoài", "externalReference"], ["Nguồn", row => row.returnOfId ? "Hàng hoàn" : row.supplierName],
            ["Dự kiến", row => dateTime(row.expectedAt)], ["Dòng", row => row.items.length],
            ["Trạng thái", row => badge(row.status)],
        ], rows)));
});
