import {api, post, put} from "../api.js";
import {roleName} from "../auth.js";
import {actionLink, h, idParam, link, mount, optionRows, panel, stack, table} from "../components/ui.js";
import {editor, optionalText, requiredText} from "../components/editor.js";

mount("Tài khoản", ["ADMIN"], async (account, refresh) => {
    const id = idParam();
    const [users, suppliers] = await Promise.all([api("/users"), api("/suppliers/options")]);
    const selected = users.find(user => user.id === id);
    const roles = Object.entries(roleName);
    const fields = [
        ["username", "Tên đăng nhập", {...requiredText, disabled: Boolean(selected)}],
        ...(!selected ? [["password", "Mật khẩu", {type: "password", required: true, maxlength: 72}]] : []),
        ["fullName", "Họ và tên", requiredText],
        ["email", "Email", {...optionalText, type: "email"}],
        ["phone", "Số điện thoại", optionalText],
        ["addr", "Địa chỉ", optionalText],
        ["role", "Vai trò", {opts: roles, required: true, value: "STAFF"}],
        ["supplierId", "Nhà cung cấp của vai trò SUPPLIER", {numeric: true, opts: [["", "Không liên kết"],
            ...optionRows(suppliers, row => row.code + " · " + row.name + (row.active ? "" : " (ngừng)"))]}],
        ["carrierName", "Đơn vị của tài xế / điều phối viên", optionalText],
        ...(selected ? [["active", "Hoạt động", {type: "checkbox"}]] : []),
    ];
    return stack(
        panel("Danh sách người dùng", table([
            ["Tên đăng nhập", row => link(row.username, "users.html", {id: row.id})], ["Họ và tên", "fullName"],
            ["Vai trò", row => roleName[row.role]], ["Trạng thái", row => row.active ? "Hoạt động" : "Đã khóa"],
        ], users), selected ? h("div", {class: "top-gap"}, actionLink("Tạo tài khoản", "users.html")) : null),
        editor(selected ? "Sửa tài khoản " + selected.username : "Tạo tài khoản", fields, selected ?? {}, async data => {
            if (selected) await put("/users/" + selected.id, data);
            else await post("/users", data);
            await refresh(selected ? "Đã lưu tài khoản." : "Đã tạo tài khoản.");
        }));
});
