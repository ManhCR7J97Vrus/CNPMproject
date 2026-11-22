import {api} from "../api.js";
import {dateTime, fields, form, href, idParam, input, label, link, mount, numberValue, optionRows, panel,
    param, qty, select, stack, table, textValue} from "../components/ui.js";

mount("Truy vết lô và chứng từ", ["MANAGER"], async () => {
    const batches = await api("/batches");
    const batchId = idParam("batchId");
    const reference = param("reference");
    const query = form(fields(select("Lô hàng", "batchId", optionRows(batches, row => row.skuCode + " · " + row.batchNo),
        batchId, false, "Tất cả lô"), input("Số chứng từ", "reference", {value: reference})), "Áp dụng", element => {
        const selection = {};
        const id = numberValue(element, "batchId");
        const ref = textValue(element, "reference");
        if (id) selection.batchId = id;
        if (ref) selection.reference = ref;
        location.assign(href("batch-trace.html", selection));
    });
    let movements = batchId ? await api("/batches/" + batchId + "/trace")
        : reference ? await api("/movements", {query: {referenceNo: reference}}) : [];
    if (batchId && reference) movements = movements.filter(row => row.referenceNo.toLowerCase() === reference.toLowerCase());
    const source = row => {
        if (row.movementType === "INBOUND" || row.movementType === "RETURN") {
            return link(row.referenceNo, "inbound-list.html", {reference: row.referenceNo});
        }
        if (row.movementType === "OUTBOUND") return link(row.referenceNo, "outbound-list.html", {reference: row.referenceNo});
        return link(row.referenceNo, "batch-trace.html", {batchId: row.batchId, reference: row.referenceNo});
    };
    return stack(panel("Chọn lô hoặc chứng từ", query), panel("Sổ biến động kho", table([
        ["Thời điểm", row => dateTime(row.createdAt)], ["Loại", row => label(row.movementType)], ["Lô", "batchNo"],
        ["Ô nguồn", "fromLocation"], ["Ô đích", "toLocation"], ["Lượng", row => qty(row.changeQty)],
        ["Chứng từ nguồn", source], ["Người ghi", "createdBy"], ["Lý do", "reason"],
    ], movements, batchId || reference ? "Chưa có giao dịch phù hợp." : "Chọn lô hoặc nhập số chứng từ để tra.")));
});
