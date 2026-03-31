<template>
  <div class="page">
    <h1 class="title">园区管理</h1>
    <p class="hint">用于授权维度管理（名称唯一）。</p>

    <div class="toolbar">
      <button type="button" class="btn primary" @click="openAdd">新增园区</button>
      <button type="button" class="btn" :disabled="reloading" @click="refreshList">
        {{ reloading ? '刷新中…' : '刷新列表' }}
      </button>
    </div>

    <div class="table-wrap">
      <table class="tbl">
        <thead>
          <tr>
            <th>ID</th>
            <th>名称</th>
            <th>描述</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in list" :key="p.id">
            <td>{{ p.id }}</td>
            <td>{{ p.name }}</td>
            <td>{{ p.description }}</td>
            <td>
              <button type="button" class="btn sm" @click="openEdit(p)">编辑</button>
              <button type="button" class="btn sm danger" @click="remove(p)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="dialog" class="modal-mask">
      <div class="modal">
        <h3>{{ dialog.mode === 'add' ? '新增园区' : '编辑园区' }}</h3>
        <div class="form-row">
          <label>园区名称</label>
          <input v-model="form.name" />
        </div>
        <div class="form-row">
          <label>园区描述</label>
          <input v-model="form.description" />
        </div>
        <div class="modal-actions">
          <button type="button" class="btn" @click="dialog = null">取消</button>
          <button type="button" class="btn primary" :disabled="saving" @click="submit">
            {{ saving ? '保存中…' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { parkApi } from '../../api/system'
import { flash } from '../../utils/flash'

const list = ref([])
const dialog = ref(null)
const saving = ref(false)
const reloading = ref(false)
const form = ref({ id: null, name: '', description: '' })

async function load() {
  try {
    const res = await parkApi.list()
    if (res.code === 200) list.value = res.data || []
    else flash(res.msg || '加载失败', 'error')
  } catch (e) {
    flash(e.message || '加载失败', 'error')
  }
}

async function refreshList() {
  if (reloading.value) return
  reloading.value = true
  try {
    await load()
    flash('列表已刷新', 'success')
  } finally {
    reloading.value = false
  }
}

function openAdd() {
  form.value = { id: null, name: '', description: '' }
  dialog.value = { mode: 'add' }
}

function openEdit(p) {
  form.value = { id: p.id, name: p.name || '', description: p.description || '' }
  dialog.value = { mode: 'edit' }
}

async function submit() {
  saving.value = true
  try {
    const body = { name: form.value.name, description: form.value.description }
    if (dialog.value.mode === 'add') {
      const res = await parkApi.add(body)
      if (res.code !== 200) throw new Error(res.msg)
    } else {
      body.id = form.value.id
      const res = await parkApi.update(body)
      if (res.code !== 200) throw new Error(res.msg)
    }
    dialog.value = null
    await load()
  } catch (e) {
    flash(e.message || '保存失败', 'error')
  } finally {
    saving.value = false
  }
}

async function remove(p) {
  if (!confirm('确定删除？')) return
  try {
    const res = await parkApi.delete({ id: p.id })
    if (res.code !== 200) throw new Error(res.msg)
    await load()
  } catch (e) {
    flash(e.message || '删除失败', 'error')
  }
}

onMounted(load)
</script>

<style scoped>
.page {
  background: #fff;
  border-radius: 10px;
  padding: 1.25rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.title {
  font-size: 1.25rem;
  margin-bottom: 0.35rem;
}
.hint {
  font-size: 0.85rem;
  color: #888;
  margin-bottom: 1rem;
}
.toolbar {
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}
.btn {
  padding: 0.45rem 1rem;
  border-radius: 6px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
}
.btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
}
.btn.sm {
  padding: 0.25rem 0.6rem;
  font-size: 0.82rem;
  margin-right: 0.35rem;
}
.btn.danger {
  color: #c0392b;
  border-color: #f5c6cb;
}
.table-wrap {
  overflow-x: auto;
}
.tbl {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}
.tbl th,
.tbl td {
  border: 1px solid #eee;
  padding: 0.5rem 0.65rem;
  text-align: left;
}
.tbl th {
  background: #f8f9fb;
}
.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}
.modal {
  background: #fff;
  padding: 1.25rem;
  border-radius: 10px;
  width: 100%;
  max-width: 420px;
}
.form-row {
  margin-bottom: 0.75rem;
}
.form-row label {
  display: block;
  font-size: 0.85rem;
  margin-bottom: 0.25rem;
}
.form-row input {
  width: 100%;
  padding: 0.45rem 0.5rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-sizing: border-box;
}
.modal-actions {
  margin-top: 1rem;
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}
</style>

