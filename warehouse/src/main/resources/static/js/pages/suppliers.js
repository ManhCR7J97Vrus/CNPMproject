import {api, post, put} from "../api.js";
import {actionLink, h, idParam, link, mount, panel, stack, table, warehouse} from "../components/ui.js";
import {editor, optionalText, requiredText} from "../components/editor.js";

mount("Nhà cung cấp", warehouse, async (account, refresh) => {
    const suppliers = await api("/suppliers");
    const manager = account.role === "MANAGER";
    const selected = suppliers.find(row => row.id === idParam());
    const specs = [
        ["code", "Mã nhà cung cấp", requiredText], ["name", "Tên", requiredText],
        ["phone", "Số liên hệ", optionalText], ["address", "Địa chỉ", optionalText],
        ["licenseNo", "Giấy phép", optionalText], ["active", "Hoạt động", {type: "checkbox", value: true}],
    ];
    return stack(
        panel("Danh mục nhà cung cấp", table([
            ["Mã", row => manager ? link(row.code, "suppliers.html", {id: row.id}) : row.code],
            ["Nhà cung cấp", "name"], ["Liên hệ", "phone"], ["Địa chỉ", "address"], ["Giấy phép", "licenseNo"],
            ["Trạng thái", row => row.active ? "Hoạt động" : "Ngừng hoạt động"],
        ], suppliers), selected ? h("div", {class: "top-gap"}, actionLink("Thêm nhà cung cấp", "suppliers.html")) : null),
        manager ? editor(selected ? "Sửa " + selected.code : "Thêm nhà cung cấp", specs, selected ?? {}, async data => {
            if (selected) await put("/suppliers/" + selected.id, data);
            else await post("/suppliers", data);
            await refresh("Đã lưu nhà cung cấp.");
        }) : null);
});
