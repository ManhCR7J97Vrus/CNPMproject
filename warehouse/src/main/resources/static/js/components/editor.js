import {checked, fields, form, input, numberValue, panel, textValue} from "./ui.js";

export function editor(title, specs, values, save, submitLabel = "Lưu") {
    const controls = fields(specs.map(([key, label, options = {}]) =>
        input(label, key, {...options, value: values[key] ?? options.value})));
    return panel(title, form(controls, submitLabel, element => {
        const data = Object.fromEntries(specs.filter(([, , options]) => !options?.disabled)
            .map(([key, , options = {}]) => {
                let value;
                if (options.type === "checkbox") value = checked(element, key);
                else if (options.type === "number" || options.numeric) value = numberValue(element, key);
                else if (options.type === "password") value = element.elements.namedItem(key).value;
                else value = textValue(element, key);
                return [key, value];
            }));
        return save(data);
    }));
}

export const requiredText = {required: true, maxlength: 255};
export const optionalText = {maxlength: 255};
export const amountField = {type: "number", min: "0", step: "0.001"};
export const positiveAmount = {...amountField, min: "0.001", required: true};
export const wholeAmount = {type: "number", min: "0", step: "1"};
export const positiveWhole = {...wholeAmount, min: "1", required: true};
