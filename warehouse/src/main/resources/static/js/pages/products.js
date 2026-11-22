import {api, post, put} from "../api.js";
import {actionLink, h, idParam, label, link, mount, panel, param, qty, stack, table, warehouse} from "../components/ui.js";
import {amountField, editor, optionalText, requiredText, wholeAmount} from "../components/editor.js";

const temperature = {type: "number", step: "0.01"};
const humidity = {...temperature, min: "0", max: "100"};
const ranks = ["URGENT", "EXPRESS", "NORMAL"];

mount("Hàng hóa và quy tắc", warehouse, async (account, refresh) => {
    const [products, profiles, priorities] = await Promise.all([
        api("/products"), api("/products/storage-profiles"), api("/products/priorities"),
    ]);
    const manager = account.role === "MANAGER";
    const picked = products.find(row => row.id === idParam());
    const profile = profiles.find(row => row.code === param("profile"));
    const itemFields = [
        ["skuCode", "Mã SKU", requiredText], ["name", "Tên hàng", requiredText],
        ["category", "Danh mục", requiredText], ["unit", "Đơn vị", requiredText],
        ["storageCondition", "Hồ sơ bảo quản", {opts: [["", "Không quy định"], ...profiles.map(p => [p.code, p.code])]}],
        ["isolationGroup", "Nhóm hàng cách ly", optionalText], ["perishable", "Dễ hư hỏng", {type: "checkbox"}],
        ["hasExpiry", "Theo dõi hạn dùng", {type: "checkbox"}],
        ["expiryAlertDays", "Số ngày cảnh báo trước", {...wholeAmount, required: true, value: 0}],
        ["minStock", "Tồn tối thiểu", {...amountField, required: true, value: 0}],
        ["active", "Hoạt động", {type: "checkbox", value: true}],
    ];
    const rules = [
        ["code", "Mã hồ sơ", requiredText], ["temperatureMin", "Nhiệt độ từ (°C)", temperature],
        ["temperatureMax", "Nhiệt độ đến (°C)", temperature], ["humidityMin", "Độ ẩm từ (%)", humidity],
        ["humidityMax", "Độ ẩm đến (%)", humidity], ["avoidLight", "Tránh sáng", {type: "checkbox"}],
        ["fireProtection", "Chống cháy", {type: "checkbox"}],
        ["incompatibleGroups", "Nhóm xung khắc, cách nhau bằng dấu phẩy", optionalText],
    ];
    return stack(
        panel("Danh mục SKU", table([
            ["Mã SKU", row => manager ? link(row.skuCode, "products.html", {id: row.id}) : row.skuCode],
            ["Hàng hóa", "name"], ["Danh mục", "category"], ["Đơn vị", "unit"], ["Bảo quản", "storageCondition"],
            ["Cảnh báo hạn", row => row.hasExpiry ? qty(row.expiryAlertDays) + " ngày" : "—"],
            ["Trạng thái", row => row.active ? "Hoạt động" : "Ngừng"],
        ], products), picked ? h("div", {class: "top-gap"}, actionLink("Thêm SKU", "products.html")) : null),
        manager ? editor(picked ? "Sửa " + picked.skuCode : "Thêm SKU", itemFields, picked ?? {}, async data => {
            if (picked) await put("/products/" + picked.id, data);
            else await post("/products", data);
            await refresh("Đã lưu SKU.");
        }) : null,
        panel("Hồ sơ bảo quản", table([
            ["Mã", row => manager ? link(row.code, "products.html", {profile: row.code}) : row.code],
            ["Nhiệt độ", row => range(row.temperatureMin, row.temperatureMax, "°C")],
            ["Độ ẩm", row => range(row.humidityMin, row.humidityMax, "%")],
            ["Điều kiện", row => [row.avoidLight && "Tránh sáng", row.fireProtection && "Chống cháy"].filter(Boolean).join(", ")],
            ["Nhóm xung khắc", row => row.incompatibleGroups.join(", ")],
        ], profiles), profile ? h("div", {class: "top-gap"}, actionLink("Thêm hồ sơ", "products.html")) : null),
        manager ? editor(profile ? "Sửa hồ sơ " + profile.code : "Lưu hồ sơ bảo quản", rules,
            {...profile, incompatibleGroups: profile?.incompatibleGroups.join(", ")}, async data => {
                const {code, incompatibleGroups, ...conditions} = data;
                await put("/products/storage-profiles/" + encodeURIComponent(code), {
                    ...conditions, incompatibleGroups: incompatibleGroups?.split(",").map(value => value.trim()).filter(Boolean) ?? [],
                });
                await refresh("Đã lưu hồ sơ bảo quản.");
            }) : null,
        manager ? editor("Thứ hạng dịch vụ", ranks.map(rank => [rank, label(rank), {...wholeAmount, min: "1", required: true}]),
            priorities, async data => {
                await put("/products/priorities", {ranks: data});
                await refresh("Đã lưu thứ hạng dịch vụ.");
            }) : panel("Thứ hạng dịch vụ", table([["Dịch vụ", "service"], ["Hạng", "rank"]],
                ranks.map(rank => ({service: label(rank), rank: priorities[rank]})))));
});

function range(from, to, unit) {
    return from == null || to == null ? "—" : qty(from) + "–" + qty(to) + " " + unit;
}
