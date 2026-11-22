import {API_BASE_URL} from "./config.js";
import {logout, session} from "./auth.js";

export async function api(path, {method = "GET", body, query, authenticated = true} = {}) {
    const url = new URL(API_BASE_URL + path, location.origin);
    for (const [key, value] of Object.entries(query ?? {})) {
        if (value !== "" && value != null) url.searchParams.set(key, value);
    }
    const headers = {Accept: "application/json"};
    const token = authenticated && session()?.accessToken;
    if (token) headers.Authorization = "Bearer " + token;
    if (body !== undefined) headers["Content-Type"] = "application/json";
    const response = await fetch(url, {method, headers, body: body === undefined ? undefined : JSON.stringify(body)});
    const data = await response.json().catch(() => null);
    if (!response.ok) {
        if (response.status === 401 && authenticated) logout();
        const error = data?.error;
        if (error?.code === "VALIDATION_ERROR" && Object.keys(error.fields ?? {}).length) {
            throw new Error(Object.entries(error.fields).map(([key, message]) => key + " — " + message).join(" · "));
        }
        throw new Error(error?.message ?? "Yêu cầu thất bại (HTTP " + response.status + ")");
    }
    return data;
}

export function post(path, body) {
    return api(path, {method: "POST", body});
}

export function put(path, body) {
    return api(path, {method: "PUT", body});
}
