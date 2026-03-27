import { Api, pickData } from "./api.js";
import { Auth } from "./auth.js";

const ui = {
  message: document.getElementById("message"),
  enrollForm: document.getElementById("enrollForm"),
  updateForm: document.getElementById("updateForm"),
  employeesTable: document.getElementById("employeesTableBody"),
  searchForm: document.getElementById("searchForm"),
  detailPanel: document.getElementById("detailPanel"),
  totals: {
    total: document.getElementById("totalEmployees"),
    active: document.getElementById("activeEmployees"),
    inactive: document.getElementById("inactiveEmployees")
  }
};

const state = {
  employees: []
};

const showMessage = (text, isError = false) => {
  if (!ui.message) return;
  ui.message.textContent = text;
  ui.message.classList.toggle("show", true);
  ui.message.style.background = isError ? "#ffe5e4" : "#eef1ff";
  ui.message.style.color = isError ? "#b3261e" : "#1a34ba";
};

const clearMessage = () => {
  if (!ui.message) return;
  ui.message.classList.remove("show");
};

const formatStatus = (status) => (status || "").toUpperCase() === "ACTIVE" ? "ACTIVE" : "INACTIVE";

const renderEmployees = (employees) => {
  if (!ui.employeesTable) return;
  ui.employeesTable.innerHTML = "";

  employees.forEach((emp) => {
    const row = document.createElement("tr");
    const status = formatStatus(emp.status);

    row.innerHTML = `
      <td>${emp.id ?? "-"}</td>
      <td>${emp.firstName ?? ""} ${emp.lastName ?? ""}</td>
      <td>${emp.role ?? "-"}</td>
      <td>${emp.department ?? "-"}</td>
      <td><span class="badge ${status === "ACTIVE" ? "active" : "inactive"}">${status}</span></td>
      <td>
        <button class="btn btn-outline" data-action="view" data-id="${emp.id}">View</button>
        <button class="btn btn-outline" data-action="edit" data-id="${emp.id}">Edit</button>
        <button class="btn ${status === "ACTIVE" ? "btn-danger" : "btn-primary"}" data-action="toggle" data-id="${emp.id}" data-status="${status}">
          ${status === "ACTIVE" ? "Disable" : "Activate"}
        </button>
      </td>
    `;
    ui.employeesTable.appendChild(row);
  });
};

const renderDetail = (employee) => {
  if (!ui.detailPanel) return;
  if (!employee) {
    ui.detailPanel.innerHTML = "<p class=\"muted\">Select an employee to view details.</p>";
    return;
  }

  ui.detailPanel.innerHTML = `
    <h3>${employee.firstName ?? ""} ${employee.lastName ?? ""}</h3>
    <p>Role: ${employee.role ?? "-"}</p>
    <p>Department: ${employee.department ?? "-"}</p>
    <p>Status: ${formatStatus(employee.status)}</p>
    <p>Email: ${employee.email ?? "-"}</p>
    <p>Phone: ${employee.phone ?? "-"}</p>
    <p>Address: ${employee.address ?? "-"}</p>
    <p>Salary: ${employee.salary ?? "-"}</p>
    <p>Date Of Joining: ${employee.joiningDate ?? "-"}</p>
    <p>Username: ${employee.username ?? "-"}</p>
  `;
};

const refreshSummary = () => {
  const total = state.employees.length;
  const active = state.employees.filter((emp) => formatStatus(emp.status) === "ACTIVE").length;
  const inactive = total - active;
  if (ui.totals.total) ui.totals.total.textContent = total;
  if (ui.totals.active) ui.totals.active.textContent = active;
  if (ui.totals.inactive) ui.totals.inactive.textContent = inactive;
};

const loadEmployees = async () => {
  try {
    const payload = await Api.request("/employees");
    const data = pickData(payload) || [];
    state.employees = Array.isArray(data) ? data : [];
    renderEmployees(state.employees);
    refreshSummary();
  } catch (err) {
    showMessage(err.message || "Unable to load employees", true);
  }
};

const handleEnroll = async (event) => {
  event.preventDefault();
  clearMessage();

  const formData = new FormData(ui.enrollForm);
  const payload = Object.fromEntries(formData.entries());
  if (payload.doj && !payload.joiningDate) {
    payload.joiningDate = payload.doj;
    delete payload.doj;
  }
  if (payload.role) {
    payload.role = String(payload.role).trim().toUpperCase();
  }
  payload.status = "ACTIVE";

  try {
    await Api.request("/employees/enroll", {
      method: "POST",
      body: payload
    });
    showMessage("Employee enrolled successfully.");
    ui.enrollForm.reset();
    await loadEmployees();
  } catch (err) {
    showMessage(err.message || "Enrollment failed", true);
  }
};

const handleSearch = async (event) => {
  event.preventDefault();
  clearMessage();
  const id = document.getElementById("searchId").value.trim();
  if (!id) {
    showMessage("Enter employee ID to search", true);
    return;
  }

  try {
    const payload = await Api.request(`/employees/${id}`);
    const employee = pickData(payload) || payload;
    renderDetail(employee);
  } catch (err) {
    showMessage(err.message || "Unable to find employee", true);
  }
};

const handleUpdate = async (event) => {
  event.preventDefault();
  clearMessage();
  const formData = new FormData(ui.updateForm);
  const payload = Object.fromEntries(formData.entries());
  const id = payload.id;
  if (!id) {
    showMessage("Employee ID is required for update", true);
    return;
  }
  delete payload.id;
  Object.keys(payload).forEach((key) => {
    if (payload[key] === "" || payload[key] === null) delete payload[key];
  });
  if (payload.doj && !payload.joiningDate) {
    payload.joiningDate = payload.doj;
    delete payload.doj;
  }
  if (payload.role) {
    payload.role = String(payload.role).trim().toUpperCase();
  }

  try {
    await Api.request(`/employees/${id}`, {
      method: "PUT",
      body: payload
    });
    showMessage("Employee updated successfully.");
    ui.updateForm.reset();
    await loadEmployees();
  } catch (err) {
    showMessage(err.message || "Update failed", true);
  }
};

const handleTableClick = async (event) => {
  const button = event.target.closest("button");
  if (!button) return;
  const id = button.dataset.id;
  if (!id) return;

  const action = button.dataset.action;

  if (action === "view") {
    try {
      const payload = await Api.request(`/employees/${id}`);
      const employee = pickData(payload) || payload;
      renderDetail(employee);
    } catch (err) {
      showMessage(err.message || "Unable to load details", true);
    }
  }

  if (action === "edit") {
    const employee = state.employees.find((emp) => String(emp.id) === String(id));
    if (!employee) return;
    Object.entries(employee).forEach(([key, value]) => {
      const input = ui.updateForm.querySelector(`[name="${key}"]`);
      if (input) input.value = value ?? "";
    });
    showMessage("Edit the fields and click Update Employee.");
  }

  if (action === "toggle") {
    const current = button.dataset.status;
    const nextStatus = current === "ACTIVE" ? "INACTIVE" : "ACTIVE";
    try {
      await Api.request(`/employees/${id}/status`, {
        method: "PUT",
        body: { status: nextStatus }
      });
      showMessage(`Employee status updated to ${nextStatus}.`);
      await loadEmployees();
    } catch (err) {
      showMessage(err.message || "Status update failed", true);
    }
  }
};

const bindEvents = () => {
  if (ui.enrollForm) ui.enrollForm.addEventListener("submit", handleEnroll);
  if (ui.searchForm) ui.searchForm.addEventListener("submit", handleSearch);
  if (ui.updateForm) ui.updateForm.addEventListener("submit", handleUpdate);
  if (ui.employeesTable) ui.employeesTable.addEventListener("click", handleTableClick);
  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) logoutBtn.addEventListener("click", Auth.logout);
};

const init = async () => {
  if (!Auth.guard("ADMIN")) return;
  bindEvents();
  renderDetail(null);
  await loadEmployees();
};

document.addEventListener("DOMContentLoaded", init);
