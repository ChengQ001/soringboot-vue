<template>
  <div class="page">
    <h1 class="title">用户管理</h1>
    <p class="hint">新增时手机号唯一；密码留空则新增用户无密码（仅测试环境建议）。</p>

    <div class="toolbar">
      <button type="button" class="btn primary" @click="openAdd">新增用户</button>
    </div>

    <div class="table-wrap">
      <table class="tbl">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>手机号</th>
            <th>描述</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="u in list" :key="u.id">
            <td>{{ u.id }}</td>
            <td>{{ u.username }}</td>
            <td>{{ u.phone }}</td>
            <td>{{ u.description }}</td>
            <td class="actions">
              <button type="button" class="btn sm" @click="openEdit(u)">编辑</button>
              <button type="button" class="btn sm danger" @click="remove(u)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="dialog" class="modal-mask">
      <div class="modal">
        <h3>{{ dialog.mode === 'add' ? '新增用户' : '编辑用户' }}</h3>
        <div class="form-row">
          <label>用户名</label>
          <input v-model="form.username" />
        </div>
        <div class="form-row">
          <label>手机号</label>
          <input v-model="form.phone" />
        </div>
        <div class="form-row">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="编辑时可留空不改" />
        </div>
        <div class="form-row">
          <label>用户描述</label>
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
import { sysUserApi } from '../../api/system'
import { flash } from '../../utils/flash'

const list = ref([])
const dialog = ref(null)
const saving = ref(false)
const form = ref({
  id: null,
  username: '',
  phone: '',
  password: '',
  description: ''
})

async function load() {
  try {
    const res = await sysUserApi.list()
    if (res.code === 200) list.value = res.data || []
    else flash(res.msg || '加载失败', 'error')
  } catch (e) {
    flash(e.message || '加载失败', 'error')
  }
}

function openAdd() {
  form.value = { id: null, username: '', phone: '', password: '', description: '' }
  dialog.value = { mode: 'add' }
}

function openEdit(u) {
  form.value = {
    id: u.id,
    username: u.username || '',
    phone: u.phone || '',
    password: '',
    description: u.description || ''
  }
  dialog.value = { mode: 'edit' }
}

async function submit() {
  saving.value = true
  try {
    if (dialog.value.mode === 'add') {
      const res = await sysUserApi.add({
        username: form.value.username,
        phone: form.value.phone,
        password: form.value.password,
        description: form.value.description
      })
      if (res.code !== 200) throw new Error(res.msg)
    } else {
      const payload = {
        id: form.value.id,
        username: form.value.username,
        phone: form.value.phone,
        description: form.value.description
      }
      if (form.value.password) payload.password = form.value.password
      const res = await sysUserApi.update(payload)
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

async function remove(u) {
  if (!confirm('确定删除该用户？')) return
  try {
    const res = await sysUserApi.delete({ id: u.id })
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
  color: #333;
}
.hint {
  font-size: 0.85rem;
  color: #888;
  margin-bottom: 1rem;
}
.toolbar {
  margin-bottom: 1rem;
}
.btn {
  padding: 0.45rem 1rem;
  border-radius: 6px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
  font-size: 0.9rem;
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
.actions {
  white-space: nowrap;
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
.modal h3 {
  margin-bottom: 1rem;
}
.form-row {
  margin-bottom: 0.75rem;
}
.form-row label {
  display: block;
  font-size: 0.85rem;
  color: #555;
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
