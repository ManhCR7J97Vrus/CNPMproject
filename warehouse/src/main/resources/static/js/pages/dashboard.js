import {api} from "../api.js";
import {actionLink, h, mount, panel, qty} from "../components/ui.js";

mount("Tổng quan", null, async account => {
    let cards;
    switch (account.role) {
        case "MANAGER":
        case "STAFF": {
            const [stock, incoming, queue] = await Promise.all([
                api("/inventory/summary"), api("/inbound-orders"), api("/outbound-orders", {query: {status: "REQUESTED"}}),
            ]);
            cards = [
                panel("Tồn khả dụng", h("p", {}, qty(stock.qtyAvailable)), actionLink("Tra tồn", "inventory.html")),
                panel("Phiếu chưa ghi nhập", h("p", {}, incoming.filter(row => row.status !== "RECEIVED").length),
                    actionLink("Mở nhập kho", "inbound-list.html")),
                panel("Đơn chờ duyệt", h("p", {}, queue.length), actionLink("Mở hàng chờ", "outbound-list.html")),
            ];
            break;
        }
        case "CUSTOMER":
            cards = [
                panel("Gửi hàng", actionLink("Tạo yêu cầu gửi", "outbound-form.html")),
                panel("Theo dõi đơn", actionLink("Đơn của tôi", "outbound-list.html")),
                panel("Yêu cầu hỗ trợ", actionLink("Mở hỗ trợ", "support.html")),
            ];
            break;
        case "SUPPLIER":
            cards = [panel("Thông báo giao hàng", actionLink("Gửi thông báo", "inbound-form.html"),
                h("div", {class: "top-gap"}, actionLink("Thông báo của tôi", "inbound-list.html")))];
            break;
        case "DRIVER":
        case "DISPATCHER":
            cards = [panel("Vận chuyển", actionLink(account.role === "DRIVER" ? "Chuyến của tôi" : "Điều phối chuyến", "my-shipments.html"))];
            break;
        default:
            cards = [panel("Quản trị tài khoản", actionLink("Danh sách người dùng", "users.html"))];
    }
    return h("div", {class: "dashboard-grid"}, cards);
});
