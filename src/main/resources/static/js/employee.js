import { Api, pickData } from "./api.js";
import { Auth } from "./auth.js";

const profilePanel = document.getElementById("profilePanel");
const logoutBtn = document.getElementById("logoutBtn");

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

document.addEventListener("DOMContentLoaded", init);
