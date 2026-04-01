import { Api, pickData } from "./api.js";

const ROLE_ADMIN = "ADMIN";

export const Auth = {
  async login(username, password) {
    const payload = await Api.request("/auth/login", {
      method: "POST",
      body: { username, password },
      auth: false
    });

    const data = pickData(payload) || payload;
    const token = data?.token || data?.accessToken || payload?.token;
    const role = (data?.role || payload?.role || "").toUpperCase();
    const employeeId = data?.employeeId || data?.id || payload?.employeeId;

    if (token) {
      localStorage.setItem("auth_token", token);
    }
    if (role) {
      localStorage.setItem("auth_role", role);
    }
    if (employeeId) {
      localStorage.setItem("auth_employee_id", employeeId);
    }

    return { token, role, employeeId, raw: payload };
  },
  logout() {
    localStorage.removeItem("auth_token");
    localStorage.removeItem("auth_role");
    localStorage.removeItem("auth_employee_id");
    window.location.href = "/pages/login.html";
  },
  token() {
    return localStorage.getItem("auth_token") || "";
  },
  role() {
    return (localStorage.getItem("auth_role") || "").toUpperCase();
  },
  isAdmin() {
    return Auth.role() === ROLE_ADMIN;
  },
  guard(expectedRole) {
    const role = Auth.role();
    const token = Auth.token();
    if (!role || !token) {
      Auth.logout();
      return false;
    }
    if (!role) {
      window.location.href = "/pages/login.html";
      return false;
    }
    if (expectedRole && role !== expectedRole) {
      window.location.href = "/pages/employee-dashboard.html";
      return false;
    }
    return true;
  }
};
