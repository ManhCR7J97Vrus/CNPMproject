import {api, post} from "../api.js";
import {actionLink, badge, choices, dateTime, details, fields, form, h, href, idParam, input, label, link,
    mount, numberValue, optionRows, panel, select, stack, table, textValue, timeline} from "../components/ui.js";

mount("Hỗ trợ đơn hàng", ["CUSTOMER", "MANAGER"], async (account, refresh) => {
    const manager = account.role === "MANAGER";
    const id = idParam();
    if (id) {
        const request = await api("/support/" + id);
        const path = "/support/" + id + "/review";
        let review;
        if (manager && request.status === "NEW") {
            review = panel("Tiếp nhận", form(null, "Tiếp nhận yêu cầu", async () => {
                await post(path, {status: "IN_PROGRESS"});
                await refresh("Đã tiếp nhận yêu cầu.");
            }));
        }
        if (manager && request.status === "IN_PROGRESS") {
            review = panel("Phản hồi cho khách", form(input("Nội dung phản hồi", "response",
                {type: "textarea", maxlength: 2000, required: true}), "Giải quyết yêu cầu", async element => {
                await post(path, {status: "RESOLVED", response: textValue(element, "response")});
                await refresh("Đã lưu phản hồi và giải quyết.");
            }));
        }
        return stack(actionLink("Tất cả yêu cầu", "support.html"), panel(request.referenceNo, stack(badge(request.status), details([
            ["Đơn hàng", link(request.orderNo, "outbound-detail.html", {id: request.outboundOrderId})],
            ["Loại yêu cầu", label(request.type)], ["Gửi lúc", dateTime(request.createdAt)],
            ...(manager ? [["Khách hàng", request.customer]] : []), ["Nội dung", request.content], ["Phản hồi", request.response],
        ]))), review, panel("Lịch sử xử lý", timeline(request.history)));
    }

    const tickets = await api("/support");
    const ticketsView = panel("Danh sách yêu cầu", table([
        ["Mã hỗ trợ", row => link(row.referenceNo, "support.html", {id: row.id})], ["Đơn hàng", "orderNo"],
        ["Loại", row => label(row.type)], ["Ngày tạo", row => dateTime(row.createdAt)], ["Trạng thái", row => badge(row.status)],
    ], tickets));
    if (manager) return ticketsView;
    const orders = await api("/outbound-orders");
    return stack(ticketsView, panel("Gửi yêu cầu mới", orders.length ? form(fields(
        select("Đơn hàng", "outboundOrderId", optionRows(orders, row => row.orderNo), idParam("orderId")),
        select("Loại yêu cầu", "type", choices(["MISSING_ITEMS", "DAMAGED_ITEMS", "DELIVERY", "OTHER"])),
        input("Nội dung", "content", {type: "textarea", maxlength: 2000, required: true})),
    "Gửi hỗ trợ", async element => {
        const ticket = await post("/support", {outboundOrderId: numberValue(element, "outboundOrderId"),
            type: textValue(element, "type"), content: textValue(element, "content")});
        location.assign(href("support.html", {id: ticket.id}));
    }) : h("p", {}, "Cần có đơn hàng trước khi gửi yêu cầu hỗ trợ.")));
});
