import {api, post} from "../api.js";
import {actionLink, badge, fields, form, h, idParam, inboundRoles, inline, mount, numberValue, panel,
    qty, stack, textValue} from "../components/ui.js";
import {batchField, inboundDetails, inboundLines, placementPicker} from "../components/inbound.js";

mount("Chi tiết phiếu nhập", inboundRoles, async (account, refresh) => {
    const id = idParam();
    if (!id) return actionLink("Chọn phiếu nhập", "inbound-list.html");
    const order = await api("/inbound-orders/" + id);
    const internal = account.role !== "SUPPLIER";
    const editable = ["DRAFT", "EXPECTED"].includes(order.status);
    const manager = account.role === "MANAGER";
    const placements = ["PENDING_CONFIRMATION", "CONFIRMED"].includes(order.status) && internal;
    const locations = placements ? await api("/locations") : [];
    const actions = [
        editable ? actionLink("Sửa phiếu", "inbound-form.html", {id}) : null,
        editable && internal ? actionLink("Đối chiếu hàng thực nhận", "inbound-receive.html", {id}) : null,
        order.returnOfId ? actionLink("Phiếu xuất gốc", "outbound-detail.html", {id: order.returnOfId}) : null,
    ];
    return stack(
        panel(null, stack(inline(badge(order.status), actions), inboundDetails(order))),
        panel("Dòng hàng", inboundLines(order, internal)),
        manager && placements ? placementForm(order, locations, true, refresh) : null,
        order.status === "CONFIRMED" && internal ? placementForm(order, locations, false, refresh) : null);
});

function placementForm(order, locations, approval, refresh) {
    const accepted = order.items.filter(line => line.qtyAccepted > 0);
    const title = approval ? (order.status === "CONFIRMED" ? "Duyệt lại vị trí" : "Duyệt lô và vị trí") : "Xác nhận hàng đã vào ô";
    const controls = accepted.map(line => h("fieldset", {}, h("legend", {}, line.skuCode + " · " + qty(line.qtyAccepted)), fields(
        batchField(line, approval && Boolean(order.returnOfId)),
        placementPicker(line, locations, {suggestions: approval, quarantine: Boolean(order.returnOfId) && line.itemCondition !== "GOOD"}),
    )));
    return panel(title, form(controls, approval ? "Xác nhận duyệt" : "Cập nhật tồn kho", async element => {
        const items = accepted.map(line => ({inboundItemId: line.id, locationId: numberValue(element, "location-" + line.id),
            batchNo: textValue(element, "batch-" + line.id)}));
        await post("/inbound-orders/" + order.id + (approval ? "/confirm" : "/post"), {items});
        await refresh(approval ? "Đã duyệt lô và vị trí." : "Đã ghi nhập kho.");
    }));
}
