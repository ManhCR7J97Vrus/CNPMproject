import {api} from "../api.js";
import {actionLink, badge, choices, dateTime, fields, form, href, inline, input, label, link,
    mount, outboundRoles, panel, param, select, stack, table, textValue} from "../components/ui.js";

const states = ["REQUESTED", "APPROVED", "PICKED", "PACKED", "HANDED_OVER", "DELIVERED", "FAILED", "CANCEL_PENDING_RETURN", "CANCELLED"];

mount(account => account.role === "CUSTOMER" ? "Đơn của tôi" : "Đơn xuất kho", outboundRoles, async account => {
    const customer = account.role === "CUSTOMER";
    const query = Object.fromEntries(["from", "to", "status", "reference"].map(key => [key, param(key)]));
    const rows = await api("/outbound-orders", {query});
    const search = form(fields(input("Từ ngày tạo", "from", {type: "date", value: query.from}),
        input("Đến ngày tạo", "to", {type: "date", value: query.to}),
        select("Trạng thái", "status", choices(states), query.status, false, "Tất cả"),
        input(customer ? "Mã đơn" : "Mã đơn hoặc phiếu xuất", "reference", {value: query.reference})), "Lọc đơn",
    element => location.assign(href("outbound-list.html", Object.fromEntries(Object.keys(query)
        .map(key => [key, textValue(element, key)]).filter(([, value]) => value != null)))));
    return stack(customer ? inline(actionLink("Tạo yêu cầu gửi", "outbound-form.html")) : null,
        panel("Bộ lọc", search),
        panel(customer ? "Lịch sử gửi hàng" : "Hàng chờ và lịch sử", table([
            ["Mã đơn", order => link(order.orderNo, "outbound-detail.html", {id: order.id})],
            ...(!customer ? [["Khách hàng", "customerName"], ["Phiếu xuất", "dispatchNo"]] : []),
            ["Dịch vụ", order => label(order.priority)], ["Hạn giao", order => dateTime(order.deliveryDeadline)],
            ["Ngày tạo", order => dateTime(order.createdAt)], ["Trạng thái", order => badge(order.status)],
            ...(!customer ? [["Yêu cầu hủy", row => row.cancelRequestedAt && row.status !== "CANCELLED" ? "Chờ xử lý" : ""]] : []),
        ], rows, "Chưa có đơn phù hợp.")));
});
