<template>
  <div class="page">
    <h1 class="title">菜单管理</h1>
    <p class="hint">树结构：父节点 ID 为空或 0 表示根菜单；删除前需无子节点。</p>
    <div class="toolbar">
      <button type="button" class="btn primary" @click="openAdd">新增菜单</button>
    </div>
    <div class="table-wrap">
      <table class="tbl">
        <thead>
          <tr>
            <th>ID</th>
            <th>名称</th>
            <th>code</th>
            <th>路径</th>
            <th>父节点ID</th>
            <th>描述</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="m in list" :key="m.id">
            <td>{{ m.id }}</td>
            <td>{{ m.name }}</td>
            <td>{{ m.code }}</td>
            <td>{{ m.path }}</td>
            <td>{{ m.parentId }}</td>
            <td>{{ m.description }}</td>
            <td>
              <button type="button" class="btn sm" @click="openEdit(m)">编辑</button>
              <button type="button" class="btn sm danger" @click="remove(m)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="dialog" class="modal-mask">
      <div class="modal">
        <h3>{{ dialog.mode === 'add' ? '新增菜单' : '编辑菜单' }}</h3>
        <div class="form-row">
          <label>菜单名称</label>
          <input v-model="form.name" />
        </div>
        <div class="form-row">
          <label>菜单 code</label>
          <input v-model="form.code" />
        </div>
        <div class="form-row">
          <label>路径</label>
          <input v-model="form.path" />
        </div>
        <div class="form-row">
          <label>父节点 ID（空或0为根）</label>
          <input v-model.number="form.parentId" type="number" />
        </div>
        <div class="form-row">
          <label>描述</label>
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
import { menuApi } from '../../api/menu'
import { flash } from '../../utils/flash'

const list = ref([])
const dialog = ref(null)
const saving = ref(false)
const form = ref({
  id: null,
  name: '',
  code: '',
  path: '',
  parentId: null,
  description: ''
})

async function load() {
  try {
    const res = await menuApi.list({})
    if (res.code === 200) list.value = res.data || []
    else flash(res.msg || '加载失败', 'error')
  } catch (e) {
    flash(e.message || '加载失败', 'error')
  }
}

function openAdd() {
  form.value = {
    id: null,
    name: '',
    code: '',
    path: '',
    parentId: null,
    description: ''
  }
  dialog.value = { mode: 'add' }
}

function openEdit(m) {
  form.value = {
    id: m.id,
    name: m.name || '',
    code: m.code || '',
    path: m.path || '',
    parentId: m.parentId,
    description: m.description || ''
  }
  dialog.value = { mode: 'edit' }
}

async function submit() {
  saving.value = true
  try {
    const body = {
      name: form.value.name,
      code: form.value.code,
      path: form.value.path,
      parentId: form.value.parentId === '' || form.value.parentId === undefined ? null : form.value.parentId,
      description: form.value.description
    }
    if (dialog.value.mode === 'add') {
      const res = await menuApi.create(body)
      if (res.code !== 200) throw new Error(res.msg)
    } else {
      body.id = form.value.id
      const res = await menuApi.update(body)
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

async function remove(m) {
  if (!confirm('确定删除？')) return
  try {
    const res = await menuApi.remove({ id: m.id })
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
  max-width: 460px;
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
