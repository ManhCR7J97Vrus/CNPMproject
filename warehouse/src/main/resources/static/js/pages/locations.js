import {api, post, put} from "../api.js";
import {actionLink, h, idParam, link, mount, panel, stack, table, warehouse} from "../components/ui.js";
import {editor, optionalText, requiredText} from "../components/editor.js";

mount("Vị trí kho", warehouse, async (account, refresh) => {
    const [locations, profiles] = await Promise.all([api("/locations"), api("/products/storage-profiles")]);
    const manager = account.role === "MANAGER";
    const picked = locations.find(row => row.id === idParam());
    const specs = [
        ["warehouseName", "Kho", requiredText], ["zoneName", "Khu", requiredText],
        ["rackCode", "Dãy", optionalText], ["code", "Mã ô", requiredText],
        ["storageCondition", "Hồ sơ bảo quản", {opts: [["", "Không quy định"], ...profiles.map(p => [p.code, p.code])]}],
        ["active", "Hoạt động", {type: "checkbox", value: true}],
        ["locked", "Khóa ô", {type: "checkbox"}], ["quarantine", "Ô cách ly", {type: "checkbox"}],
    ];
    return stack(
        panel("Địa chỉ lưu trữ", table([
            ["Kho", "warehouseName"], ["Khu", "zoneName"], ["Dãy", "rackCode"],
            ["Ô", row => manager ? link(row.code, "locations.html", {id: row.id}) : row.code],
            ["Hồ sơ", "storageCondition"], ["Loại", row => row.quarantine ? "Cách ly" : "Lưu trữ"],
            ["Trạng thái", row => row.locked ? "Khóa" : row.active ? "Mở" : "Ngừng hoạt động"],
        ], locations), picked ? h("div", {class: "top-gap"}, actionLink("Thêm ô mới", "locations.html")) : null),
        manager ? editor(picked ? "Sửa ô " + picked.code : "Thêm ô", specs, picked ?? {}, async data => {
            if (picked) await put("/locations/" + picked.id, data);
            else await post("/locations", data);
            await refresh("Đã lưu vị trí.");
        }) : null);
});
