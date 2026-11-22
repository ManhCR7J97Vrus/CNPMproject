import {logout, requireRole, roleName} from "../auth.js";

export const warehouse = ["STAFF", "MANAGER"];
export const inboundRoles = [...warehouse, "SUPPLIER"];
export const outboundRoles = [...warehouse, "CUSTOMER"];
export const shippingRoles = [...warehouse, "DISPATCHER", "DRIVER"];

const navigation = [
    ["dashboard.html", "Tổng quan", null],
    ["inventory.html", "Tồn kho", warehouse],
    ["inbound-list.html", "Nhập kho", inboundRoles],
    ["outbound-list.html", "Đơn xuất", outboundRoles],
    ["my-shipments.html", "Vận chuyển", shippingRoles],
    ["locations.html", "Vị trí kho", warehouse],
    ["transfer.html", "Chuyển ô", warehouse],
    ["products.html", "Hàng hóa", warehouse],
    ["suppliers.html", "Nhà cung cấp", warehouse],
    ["stocktake.html", "Kiểm kê và cách ly", warehouse],
    ["reports.html", "Báo cáo và cảnh báo", ["MANAGER"]],
    ["batch-trace.html", "Truy vết lô", ["MANAGER"]],
    ["support.html", "Hỗ trợ đơn hàng", ["CUSTOMER", "MANAGER"]],
    ["users.html", "Tài khoản", ["ADMIN"]],
];

export const statuses = {
    DRAFT: "Nháp", EXPECTED: "Dự kiến nhận", PENDING_CONFIRMATION: "Chờ xác nhận", CONFIRMED: "Đã xác nhận",
    RECEIVED: "Đã nhập", REQUESTED: "Chờ duyệt", APPROVED: "Đã duyệt", PICKED: "Đã lấy", PACKED: "Đã đóng gói",
    DISPATCH_ISSUED: "Lập phiếu xuất", HANDED_OVER: "Đã bàn giao", CANCEL_REQUESTED: "Yêu cầu hủy",
    CANCEL_PENDING_RETURN: "Chờ trả về ô", CANCELLED: "Đã hủy", REALLOCATED: "Đã đổi lô",
    PENDING_ASSIGNMENT: "Chờ phân công", SHIPMENT_REQUESTED: "Yêu cầu vận chuyển", ASSIGNED: "Đã phân công",
    AWAITING_DRIVER: "Chờ tài xế xác nhận", WAREHOUSE_CHECKED: "Kho đã đối chiếu kiện", DISPATCHED: "Đang giao",
    DELIVERED: "Đã giao", FAILED: "Giao thất bại", LATE_PICKUP: "Nhận trễ", NO_SHOW: "Không đến",
    REDELIVERY: "Hẹn giao lại", RETURN_REQUESTED: "Yêu cầu hoàn", COUNTED: "Chờ duyệt", NEEDS_RECOUNT: "Cần đếm lại",
    COMPLETED: "Đã cách ly", NEW: "Mới", IN_PROGRESS: "Đang xử lý", RESOLVED: "Đã giải quyết",
    NORMAL: "Thường", EXPRESS: "Nhanh", URGENT: "Hỏa tốc", AVAILABLE: "Sử dụng được",
    GOOD: "Đạt", DAMAGED: "Hỏng", QUARANTINE: "Cách ly", EXPIRED: "Hết hạn",
    INBOUND: "Nhập", RETURN: "Nhập hoàn", OUTBOUND: "Xuất", TRANSFER: "Chuyển ô", ADJUSTMENT: "Điều chỉnh",
    MISSING_ITEMS: "Thiếu hàng", DAMAGED_ITEMS: "Hàng hỏng", DELIVERY: "Giao nhận", OTHER: "Khác",
};

export function h(tag, attributes = {}, ...children) {
    const element = document.createElement(tag);
    for (const [key, value] of Object.entries(attributes)) {
        if (value == null || value === false) continue;
        if (key === "onClick") element.addEventListener("click", value);
        else element.setAttribute(key, value === true ? "" : String(value));
    }
    for (const child of children.flat(Infinity)) {
        if (child != null && child !== false) element.append(child instanceof Node ? child : String(child));
    }
    return element;
}

const numeral = new Intl.NumberFormat("vi-VN", {maximumFractionDigits: 3});
export const qty = value => value == null ? "—" : numeral.format(value);
export const dateTime = value => value ? String(value).replace("T", " ").slice(0, 16) : "—";
export const label = value => statuses[value] ?? value ?? "—";
export const choices = values => values.map(value => [value, label(value)]);
export const locationText = location => [location.warehouseName, location.zoneName, location.rackCode, location.code]
    .filter(Boolean).join(" / ");
export const skuText = product => product.skuCode + " · " + product.name;
export const optionRows = (rows, name) => rows.map(row => [row.id, name(row)]);

export function badge(value) {
    const waiting = ["DRAFT", "EXPECTED", "PENDING_CONFIRMATION", "REQUESTED", "COUNTED", "PENDING_ASSIGNMENT", "AWAITING_DRIVER", "NEW"];
    const blocked = ["CANCEL_PENDING_RETURN", "CANCELLED", "FAILED", "NEEDS_RECOUNT", "EXPIRED"];
    return h("span", {class: "status", "data-tone": blocked.includes(value) ? "blocked" : waiting.includes(value) ? "waiting" : null}, label(value));
}

export function href(page, query = {}) {
    const search = new URLSearchParams(query).toString();
    return "/pages/" + page + (search ? "?" + search : "");
}

export function param(name) {
    return new URLSearchParams(location.search).get(name);
}

export function idParam(name = "id") {
    const raw = param(name);
    const result = Number(raw);
    return Number.isSafeInteger(result) && result > 0 ? result : null;
}

export function link(text, page, query) {
    return h("a", {href: href(page, query)}, text);
}

export function actionLink(text, page, query) {
    return h("a", {href: href(page, query), class: "button secondary"}, text);
}

export function button(text, onClick, kind = "secondary") {
    return h("button", {type: "button", class: kind, onClick}, text);
}

export function mount(title, roles, render) {
    const account = requireRole(roles);
    if (!account) return;
    const main = document.querySelector("main");
    const current = location.pathname.split("/").pop();
    const active = ({
        "inbound-form.html": "inbound-list.html", "inbound-detail.html": "inbound-list.html",
        "inbound-receive.html": "inbound-list.html", "outbound-form.html": "outbound-list.html",
        "outbound-detail.html": "outbound-list.html",
    })[current] ?? current;
    const nav = h("nav", {class: "side-nav", "aria-label": "Phân hệ"});
    for (const [page, text, visible] of navigation) {
        if (visible && !visible.includes(account.role)) continue;
        nav.append(h("a", {href: href(page), "aria-current": page === active ? "page" : null}, text));
    }
    const header = h("header", {class: "app-header"},
        h("a", {class: "brand", href: href("dashboard.html")}, "Kho trung chuyển"),
        h("div", {class: "header-account"},
            (account.fullName ?? account.username) + " · " + roleName[account.role], button("Đăng xuất", logout)));
    const shell = h("div", {class: "shell"}, nav);
    main.before(header, shell);
    shell.append(main);
    main.classList.add("page-content");
    const heading = typeof title === "function" ? title(account) : title;
    document.title = heading + " | Kho trung chuyển";
    const content = h("div", {class: "page-content"});
    main.replaceChildren(h("h1", {}, heading), content);
    async function refresh(message) {
        content.replaceChildren(h("p", {class: "muted", role: "status"}, "Đang tải…"));
        try {
            const view = await render(account, refresh);
            content.replaceChildren(message ? notice(message) : nullNotice(), view);
        } catch (error) {
            content.replaceChildren(notice(error.message || "Không tải được dữ liệu", true),
                button("Thử lại", () => refresh()));
        }
    }
    refresh();
}

function nullNotice() {
    return document.createDocumentFragment();
}

export function notice(text, error = false) {
    return h("div", {class: "notice", "data-error": error ? "true" : null, role: error ? "alert" : "status"}, text);
}

export function panel(title, ...content) {
    return h("section", {class: "panel"}, title ? h("h2", {}, title) : null, content);
}

export function stack(...content) {
    return h("div", {class: "stack"}, content);
}

export function inline(...content) {
    return h("div", {class: "inline"}, content);
}

export function fields(...content) {
    return h("div", {class: "fields"}, content);
}

export function details(entries) {
    return h("dl", {class: "details"}, entries.map(([name, value]) =>
        h("div", {}, h("dt", {}, name), h("dd", {}, value == null || value === "" ? "—" : value))));
}

export function table(columns, rows, empty = "Chưa có dữ liệu phù hợp.") {
    if (!rows.length) return h("p", {class: "muted", role: "status"}, empty);
    const head = h("tr", {}, columns.map(([title]) => h("th", {scope: "col"}, title)));
    const body = rows.map(row => h("tr", {}, columns.map(([, get]) => {
        const value = typeof get === "function" ? get(row) : row[get];
        return h("td", {}, value == null || value === "" ? "—" : value);
    })));
    return h("div", {class: "table-wrap"}, h("table", {}, h("thead", {}, head), h("tbody", {}, body)));
}

export function input(title, name, {type = "text", value, opts, ...attrs} = {}) {
    const control = opts ? h("select", {name, ...attrs}, opts.map(([key, option]) => h("option", {value: key}, option)))
        : type === "textarea" ? h("textarea", {name, ...attrs}) : h("input", {name, type, ...attrs});
    if (type === "checkbox") control.checked = Boolean(value);
    else if (value != null) control.value = String(value);
    return h("label", {class: type === "checkbox" ? "field field-check" : "field"},
        type === "checkbox" ? [control, h("span", {}, title)] : [h("span", {}, title), control]);
}

export function select(title, name, options, value = "", required = true, prompt = "Chọn…") {
    return input(title, name, {opts: [["", prompt], ...options], value, required});
}

export function textValue(form, key) {
    const raw = form.elements.namedItem(key)?.value;
    return raw?.trim() || null;
}

export function numberValue(form, key) {
    const value = textValue(form, key);
    return value === null ? null : Number(value);
}

export function checked(form, key) {
    return form.elements.namedItem(key)?.checked ?? false;
}

export function form(content, submitLabel, submit, extra = {}) {
    const feedback = h("div", {"aria-live": "polite"});
    const go = h("button", {type: "submit", class: extra.danger ? "danger-action" : null}, submitLabel);
    const element = h("form", {class: extra.className}, content, go, feedback);
    element.addEventListener("submit", async event => {
        event.preventDefault();
        go.disabled = true;
        feedback.replaceChildren();
        try {
            await submit(element);
        } catch (error) {
            feedback.replaceChildren(notice(error.message || "Không thực hiện được yêu cầu", true));
        } finally {
            go.disabled = false;
        }
    });
    return element;
}

export function records(specs, initial, makeRow) {
    let next = 0;
    const list = h("div", {class: "stack"});
    function add(value = {}) {
        const key = next++;
        const row = h("fieldset", {},
            h("legend", {}, "Dòng hàng"), fields(specs(key, value)));
        const remove = button("Bỏ dòng", () => {
            if (list.children.length > 1) row.remove();
        });
        remove.classList.add("top-gap");
        row.append(remove);
        row.dataset.key = key;
        list.append(row);
    }
    (initial?.length ? initial : [{}]).forEach(add);
    return {
        element: stack(list, button("Thêm dòng", () => add())),
        data: element => Array.from(list.children, child => makeRow(element, Number(child.dataset.key))),
    };
}

export function timeline(events, eventLabel = label) {
    if (!events?.length) return h("p", {class: "muted"}, "Chưa có mốc.");
    return h("ol", {class: "timeline"}, events.map(event => h("li", {},
        h("strong", {}, eventLabel(event.type ?? event.status)), " · ", dateTime(event.at),
        event.actor ? " · " + event.actor : null,
        event.note || event.response ? h("div", {}, event.note ?? event.response) : null,
        event.appointment ? h("div", {}, (event.previousAppointment ? dateTime(event.previousAppointment) + " → " : "")
            + dateTime(event.appointment)) : null)));
}

export function localDate() {
    const now = new Date();
    return [now.getFullYear(), now.getMonth() + 1, now.getDate()].map(value => String(value).padStart(2, "0")).join("-");
}
