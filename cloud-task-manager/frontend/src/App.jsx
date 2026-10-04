import { useEffect, useState } from "react";
import { listTasks, searchTasks, getTask, createTask, updateTask, deleteTask } from "./api";

const STATUSES = ["TODO", "IN_PROGRESS", "COMPLETED"];
const EMPTY = { title: "", description: "", status: "TODO" };

export default function App() {
  const [tasks, setTasks] = useState([]);
  const [form, setForm] = useState(EMPTY);
  const [editingId, setEditingId] = useState(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState(null);
  const [error, setError] = useState("");

  const run = async (fn) => {
    try {
      setError("");
      await fn();
    } catch (e) {
      setError(e.response?.data?.error || e.message);
    }
  };

  const load = (q = query) =>
    run(async () => setTasks(q.trim() ? await searchTasks(q.trim()) : await listTasks()));

  useEffect(() => { load(""); }, []);

  const submit = (e) => {
    e.preventDefault();
    run(async () => {
      if (editingId) await updateTask(editingId, form);
      else await createTask(form);
      setForm(EMPTY);
      setEditingId(null);
      await load();
    });
  };

  const edit = (t) => {
    setEditingId(t.id);
    setForm({ title: t.title, description: t.description || "", status: t.status });
  };

  const remove = (id) =>
    run(async () => {
      await deleteTask(id);
      if (selected?.id === id) setSelected(null);
      await load();
    });

  const view = (id) => run(async () => setSelected(await getTask(id)));

  return (
    <div className="container">
      <h1>Cloud Task Manager</h1>
      {error && <div className="error">{error}</div>}

      <form className="card" onSubmit={submit}>
        <h2>{editingId ? `Edit task #${editingId}` : "New task"}</h2>
        <input
          placeholder="Title" required value={form.title}
          onChange={(e) => setForm({ ...form, title: e.target.value })}
        />
        <textarea
          placeholder="Description" rows={3} value={form.description}
          onChange={(e) => setForm({ ...form, description: e.target.value })}
        />
        <select value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}>
          {STATUSES.map((s) => <option key={s}>{s}</option>)}
        </select>
        <div className="row">
          <button type="submit">{editingId ? "Update" : "Add task"}</button>
          {editingId && (
            <button type="button" className="secondary"
              onClick={() => { setEditingId(null); setForm(EMPTY); }}>Cancel</button>
          )}
        </div>
      </form>

      <div className="row search">
        <input
          placeholder="Search by title..." value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && load()}
        />
        <button onClick={() => load()}>Search</button>
        <button className="secondary" onClick={() => { setQuery(""); load(""); }}>Clear</button>
      </div>

      {selected && (
        <div className="card detail">
          <strong>Task #{selected.id}: {selected.title}</strong>
          <p>{selected.description || "(no description)"}</p>
          <small>{selected.status} · created {new Date(selected.createdAt).toLocaleString()}</small>
          <button className="secondary" onClick={() => setSelected(null)}>Close</button>
        </div>
      )}

      {tasks.length === 0 && <p className="muted">No tasks found.</p>}
      {tasks.map((t) => (
        <div className="card task" key={t.id}>
          <div>
            <a href="#" onClick={(e) => { e.preventDefault(); view(t.id); }}>{t.title}</a>
            <span className={`badge ${t.status}`}>{t.status.replace("_", " ")}</span>
            <p className="muted">{t.description}</p>
          </div>
          <div className="row">
            <button className="secondary" onClick={() => edit(t)}>Edit</button>
            <button className="danger" onClick={() => remove(t.id)}>Delete</button>
          </div>
        </div>
      ))}
    </div>
  );
}
