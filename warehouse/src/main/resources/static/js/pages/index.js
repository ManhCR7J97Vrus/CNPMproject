import {api} from "../api.js";
import {saveSession} from "../auth.js";
import {href, notice} from "../components/ui.js";

const login = document.querySelector("#login");
const feedback = document.querySelector("#feedback");

login.addEventListener("submit", async event => {
    event.preventDefault();
    const button = login.querySelector("button");
    button.disabled = true;
    feedback.replaceChildren();
    const credentials = new FormData(login);
    try {
        const account = await api("/auth/login", {
            method: "POST", authenticated: false,
            body: {username: credentials.get("username"), password: credentials.get("password")},
        });
        saveSession(account);
        location.replace(href("dashboard.html"));
    } catch (error) {
        feedback.replaceChildren(notice(error.message, true));
    } finally {
        button.disabled = false;
    }
});
