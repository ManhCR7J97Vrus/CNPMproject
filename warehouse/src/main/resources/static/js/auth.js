const KEY = "warehouse.session";

export const roleName = {
    ADMIN: "Quản trị hệ thống", MANAGER: "Quản lý kho", STAFF: "Nhân viên kho",
    CUSTOMER: "Khách hàng", SUPPLIER: "Nhà cung cấp", DRIVER: "Tài xế", DISPATCHER: "Điều phối viên",
};

export function session() {
    try {
        const account = JSON.parse(sessionStorage.getItem(KEY));
        return account?.accessToken && roleName[account.role] ? account : null;
    } catch {
        return null;
    }
}

export function saveSession(account) {
    sessionStorage.setItem(KEY, JSON.stringify(account));
}

export function logout() {
    sessionStorage.removeItem(KEY);
    location.replace("/");
}

export function requireRole(roles) {
    const account = session();
    if (!account) {
        location.replace("/");
        return null;
    }
    if (roles && !roles.includes(account.role)) {
        location.replace("/pages/dashboard.html");
        return null;
    }
    return account;
}
