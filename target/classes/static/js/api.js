export const Api = {
  async request(path, { method = "GET", body, auth = true } = {}) {
    const headers = { "Content-Type": "application/json" };
    const token = localStorage.getItem("auth_token");
    if (auth && token) {
      headers.Authorization = `Bearer ${token}`;
    }

    const response = await fetch(path, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined
    });

    let payload = null;
    try {
      payload = await response.json();
    } catch (err) {
      payload = null;
    }

    if (!response.ok) {
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
