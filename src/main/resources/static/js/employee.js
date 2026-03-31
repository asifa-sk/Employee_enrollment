import { Api, pickData } from "./api.js";
import { Auth } from "./auth.js";

const profilePanel = document.getElementById("profilePanel");
const logoutBtn = document.getElementById("logoutBtn");
const changePasswordForm = document.getElementById("changePasswordForm");
const passwordMessage = document.getElementById("passwordMessage");

const showPasswordMessage = (text, isError = false) => {
  if (!passwordMessage) return;
  passwordMessage.textContent = text;
  passwordMessage.classList.add("show");
  passwordMessage.style.background = isError ? "#ffe5e4" : "#eef1ff";
  passwordMessage.style.color = isError ? "#b3261e" : "#1a34ba";
};

const handleChangePassword = async (event) => {
  event.preventDefault();
  if (!changePasswordForm) return;

  const formData = new FormData(changePasswordForm);
  const currentPassword = String(formData.get("currentPassword") || "").trim();
  const newPassword = String(formData.get("newPassword") || "").trim();
  const confirmPassword = String(formData.get("confirmPassword") || "").trim();

  if (!currentPassword || !newPassword || !confirmPassword) {
    showPasswordMessage("All password fields are required.", true);
    return;
  }
  if (newPassword.length < 8) {
    showPasswordMessage("New password must be at least 8 characters.", true);
    return;
  }
  if (newPassword !== confirmPassword) {
    showPasswordMessage("New password and confirm password do not match.", true);
    return;
  }

  try {
    await Api.request("/auth/change-password", {
      method: "POST",
      body: { currentPassword, newPassword }
    });
    changePasswordForm.reset();
    showPasswordMessage("Password updated successfully.");
  } catch (err) {
    showPasswordMessage(err.message || "Unable to change password.", true);
  }
};

const renderProfile = (employee) => {
  if (!profilePanel) return;
  if (!employee) {
    profilePanel.innerHTML = "<p class=\"muted\">Profile not available.</p>";
    return;
  }
  const statusValue = (employee.status || "").toUpperCase() === "ACTIVE"
    ? "Active Employee"
    : (employee.status || "-");
  profilePanel.innerHTML = `
    <h2>${employee.firstName ?? ""} ${employee.lastName ?? ""}</h2>
    <p>Role: ${employee.role ?? "-"}</p>
    <p>Department: ${employee.department ?? "-"}</p>
    <p>Status: ${statusValue}</p>
    <p>Email: ${employee.email ?? "-"}</p>
    <p>Phone: ${employee.phone ?? "-"}</p>
    <p>Address: ${employee.address ?? "-"}</p>
    <p>Salary: ${employee.salary ?? "-"}</p>
    <p>Date Of Joining: ${employee.joiningDate ?? "-"}</p>
  `;
};

const init = async () => {
  if (!Auth.guard()) return;
  const id = localStorage.getItem("auth_employee_id");
  if (!id) {
    renderProfile(null);
    return;
  }

  try {
    const payload = await Api.request(`/employees/${id}`);
    const employee = pickData(payload) || payload;
    renderProfile(employee);
  } catch (err) {
    renderProfile(null);
  }
};

if (logoutBtn) logoutBtn.addEventListener("click", Auth.logout);
if (changePasswordForm) changePasswordForm.addEventListener("submit", handleChangePassword);

document.addEventListener("DOMContentLoaded", init);
