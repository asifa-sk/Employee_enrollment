export const Api = {
  async request(path, { method = "GET", body, auth = true } = {}) {
    const headers = { "Content-Type": "application/json" };
    const token = localStorage.getItem("auth_token");
    if (auth && token) {
      headers.Authorization = `Bearer ${token}`;
    }

    console.log(`[API] ${method} ${path}`, body || "");

    const response = await fetch(path, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined
    });

    console.log(`[API Response] ${response.status} ${response.statusText}`);

    let payload = null;
    try {
      payload = await response.json();
      console.log(`[API Payload]`, payload);
    } catch (err) {
      console.error("[API JSON Parse Error]", err);
      payload = null;
    }

    if (!response.ok) {
      if (response.status === 401 || response.status === 403) {
        console.log("[API] Clearing auth due to 401/403");
        localStorage.removeItem("auth_token");
        localStorage.removeItem("auth_role");
        localStorage.removeItem("auth_employee_id");
        if (auth) {
          window.location.href = "/pages/login.html";
        }
      }

      let message = payload?.message || `Request failed (${response.status})`;
      if (payload?.data && typeof payload.data === "object" && !Array.isArray(payload.data)) {
        const details = Object.entries(payload.data)
          .map(([key, value]) => `${key}: ${value}`)
          .join("; ");
        if (details) {
          message = `${message} (${details})`;
        }
      }
      throw new Error(message);
    }

    return payload;
  }
};

export const pickData = (payload) => {
  if (!payload) return null;
  if (Array.isArray(payload)) return payload;
  if (Array.isArray(payload.data)) return payload.data;
  if (payload.data) return payload.data;
  return payload;
};
