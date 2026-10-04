import axios from "axios";

const baseURL = `${import.meta.env.VITE_API_BASE_URL ?? ""}/api`;
const api = axios.create({ baseURL });

export const listTasks = () => api.get("/tasks").then((r) => r.data);
export const searchTasks = (title) =>
  api.get("/tasks/search", { params: { title } }).then((r) => r.data);
export const getTask = (id) => api.get(`/tasks/${id}`).then((r) => r.data);
export const createTask = (task) => api.post("/tasks", task).then((r) => r.data);
export const updateTask = (id, task) => api.put(`/tasks/${id}`, task).then((r) => r.data);
export const deleteTask = (id) => api.delete(`/tasks/${id}`);
