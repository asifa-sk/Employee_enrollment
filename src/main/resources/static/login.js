const loginForm = document.getElementById("loginForm");
const message = document.getElementById("message");
const passwordInput = document.getElementById("password");
const togglePasswordBtn = document.getElementById("togglePasswordBtn");
const forgotBtn = document.getElementById("forgotBtn");
const modal = document.getElementById("modal");
const cancelReset = document.getElementById("cancelReset");
const submitReset = document.getElementById("submitReset");
const resetMessage = document.getElementById("resetMessage");

if (togglePasswordBtn && passwordInput) {
    togglePasswordBtn.addEventListener("click", () => {
        const isHidden = passwordInput.type === "password";
        passwordInput.type = isHidden ? "text" : "password";
        togglePasswordBtn.textContent = isHidden ? "Hide" : "Show";
        togglePasswordBtn.setAttribute("aria-label", isHidden ? "Hide password" : "Show password");
    });
}

function showMessage(target, text, isError = false) {
    target.textContent = text;
    target.classList.add("show");
    target.style.background = isError ? "#ffe8e8" : "#f5f7ff";
    target.style.color = isError ? "#8a1f1f" : "#2c3140";
}

function hideMessage(target) {
    target.textContent = "";
    target.classList.remove("show");
}

function extractRole(payload) {
    if (!payload) return "";
    if (payload.role) return payload.role;
    if (payload.data && payload.data.role) return payload.data.role;
    return "";
}

function extractToken(payload) {
    if (!payload) return "";
    return payload.token || payload.accessToken || (payload.data && (payload.data.token || payload.data.accessToken)) || "";
}

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    hideMessage(message);

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();

    try {
        const response = await fetch("/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });
        const payload = await response.json();

        if (!response.ok) {
            showMessage(message, payload.message || "Login failed", true);
            return;
        }

        const role = (extractRole(payload) || "").toUpperCase();
        const token = extractToken(payload);

        if (token) {
            localStorage.setItem("auth_token", token);
        }
        if (role) {
            localStorage.setItem("auth_role", role);
        }

        if (role === "ADMIN") {
            showMessage(message, "Admin login successful. Redirecting...");
            setTimeout(() => {
                window.location.href = "/pages/admin-dashboard.html";
            }, 400);
            return;
        }

        if (role === "EMPLOYEE") {
            showMessage(message, "Employee login successful. Redirecting...");
            setTimeout(() => {
                window.location.href = "/pages/employee-dashboard.html";
            }, 400);
            return;
        }

        showMessage(message, "Login successful but role not recognized.", true);
    } catch (err) {
        showMessage(message, "Unable to connect to server", true);
    }
});

forgotBtn.addEventListener("click", () => {
    modal.classList.remove("hidden");
    hideMessage(resetMessage);
});

cancelReset.addEventListener("click", () => {
    modal.classList.add("hidden");
});

submitReset.addEventListener("click", async () => {
    hideMessage(resetMessage);
    const usernameOrEmail = document.getElementById("resetKey").value.trim();
    if (!usernameOrEmail) {
        showMessage(resetMessage, "Please enter username or email", true);
        return;
    }

    try {
        const response = await fetch("/auth/forgot-password", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ usernameOrEmail })
        });
        const payload = await response.json();

        if (!response.ok) {
            showMessage(resetMessage, payload.message || "Request failed", true);
            return;
        }

        showMessage(
            resetMessage,
            "Temporary password: " + payload.data.temporaryPassword
        );
    } catch (err) {
        showMessage(resetMessage, "Unable to connect to server", true);
    }
});
