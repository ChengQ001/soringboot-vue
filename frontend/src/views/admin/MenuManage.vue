<template>
  <div class="page">
    <h1 class="title">菜单管理</h1>
    <p class="hint">
      仅支持两级：一级（父）+ 二级（子）。可勾选一个父节点后，快速新增其子菜单。排序越小越靠前。
    </p>
    <div class="toolbar">
      <button type="button" class="btn primary" @click="openAdd()">
        新增菜单
      </button>
      <button type="button" class="btn" :disabled="reloading" @click="refreshList">
        {{ reloading ? '刷新中…' : '刷新列表' }}
      </button>
      <span class="toolbar-tip" v-if="selectedRootId">
        已选父节点：#{{ selectedRootId }}
        <button type="button" class="link" @click="selectedRootId = null">清除</button>
      </span>
    </div>
    <div class="table-wrap">
      <table class="tbl">
        <thead>
          <tr>
            <th style="width: 42px">选</th>
            <th style="width: 42px"></th>
            <th>ID</th>
            <th>名称</th>
            <th>code</th>
            <th>路径</th>
            <th>排序</th>
            <th>描述</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <template v-for="root in tree" :key="root.id">
            <tr class="row-root">
              <td>
                <input
                  type="checkbox"
                  :checked="selectedRootId === root.id"
                  @change="toggleSelectRoot(root)"
                />
              </td>
              <td>
                <button
                  type="button"
                  class="icon-btn"
                  :disabled="!root.children || root.children.length === 0"
                  @click="toggleExpand(root.id)"
                  :title="expandedRootIds.has(root.id) ? '收起' : '展开'"
                >
                  {{ expandedRootIds.has(root.id) ? '▾' : '▸' }}
                </button>
              </td>
              <td>{{ root.id }}</td>
              <td class="name-cell">
                <span class="tag">父</span>
                {{ root.name }}
              </td>
              <td>{{ root.code }}</td>
              <td>{{ root.path }}</td>
              <td>{{ root.sortOrder ?? 0 }}</td>
              <td>{{ root.description }}</td>
              <td>
                <button type="button" class="btn sm" @click="openEdit(root)">编辑</button>
                <button type="button" class="btn sm" @click="openAdd('child', root)">添加子菜单</button>
                <button type="button" class="btn sm danger" @click="remove(root)">删除</button>
              </td>
            </tr>

            <tr
              v-for="child in (expandedRootIds.has(root.id) ? (root.children || []) : [])"
              :key="child.id"
              class="row-child"
            >
              <td></td>
              <td></td>
              <td>{{ child.id }}</td>
              <td class="name-cell child-indent">
                <span class="tag sub">子</span>
                {{ child.name }}
              </td>
              <td>{{ child.code }}</td>
              <td>{{ child.path }}</td>
              <td>{{ child.sortOrder ?? 0 }}</td>
              <td>{{ child.description }}</td>
              <td>
                <button type="button" class="btn sm" @click="openEdit(child)">编辑</button>
                <button type="button" class="btn sm danger" @click="remove(child)">删除</button>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>

    <div v-if="dialog" class="modal-mask">
      <div class="modal">
        <h3>{{ dialog.mode === 'add' ? '新增菜单' : '编辑菜单' }}</h3>
        <div class="form-row">
          <label>菜单层级</label>
          <div class="radio-row">
            <label class="radio">
              <input type="radio" value="root" v-model="form.nodeType" :disabled="dialog.mode === 'edit' && hasChildren" />
              父节点（一级）
            </label>
            <label class="radio">
              <input type="radio" value="child" v-model="form.nodeType" />
              子节点（二级）
            </label>
          </div>
          <div v-if="dialog.mode === 'edit' && hasChildren" class="help">
            当前菜单下有子节点，不能改为子节点（仅支持两级）。
          </div>
        </div>
        <div class="form-row" v-if="form.nodeType === 'child'">
          <label>父节点（一级）</label>
          <select v-model.number="form.parentId">
            <option :value="null" disabled>请选择父节点</option>
            <option v-for="r in roots" :key="r.id" :value="r.id">
              #{{ r.id }} - {{ r.name }} ({{ r.code }})
            </option>
          </select>
        </div>
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
          <label>排序（越小越靠前）</label>
          <input v-model.number="form.sortOrder" type="number" />
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
import { computed, onMounted, ref } from 'vue'
import { menuApi } from '../../api/menu'
import { flash } from '../../utils/flash'

const list = ref([])
const selectedRootId = ref(null)
const expandedRootIds = ref(new Set())
const dialog = ref(null)
const saving = ref(false)
const reloading = ref(false)
const form = ref({
  id: null,
  nodeType: 'root', // root | child
  name: '',
  code: '',
  path: '',
  parentId: null,
  sortOrder: 0,
  description: ''
})

const roots = computed(() => {
  return (list.value || []).filter((m) => !m.parentId || m.parentId === 0)
})

const tree = computed(() => {
  const all = Array.isArray(list.value) ? list.value : []
  const byId = new Map()
  all.forEach((m) => {
    if (m && m.id != null) byId.set(m.id, { ...m, children: [] })
  })
  const rootNodes = []
  byId.forEach((m) => {
    const pid = m.parentId
    if (!pid || pid === 0) {
      rootNodes.push(m)
    } else {
      const p = byId.get(pid)
      if (p) p.children.push(m)
    }
  })
  // 仅两级：这里不再递归挂第三层
  return rootNodes
})

const hasChildren = computed(() => {
  if (!dialog.value || dialog.value.mode !== 'edit') return false
  const id = form.value.id
  if (!id) return false
  const root = tree.value.find((r) => r.id === id)
  return !!(root && root.children && root.children.length)
})

async function load() {
  try {
    const res = await menuApi.list({})
    if (res.code === 200) {
      list.value = res.data || []
      // 默认展开所有父节点
      const rootIds = new Set(
        (list.value || [])
          .filter((m) => !m.parentId || m.parentId === 0)
          .map((m) => m.id)
          .filter(Boolean)
      )
      expandedRootIds.value = rootIds
    } else flash(res.msg || '加载失败', 'error')
  } catch (e) {
    flash(e.message || '加载失败', 'error')
  }
}

async function refreshList() {
  if (reloading.value) return
  reloading.value = true
  try {
    await load()
    flash('菜单已刷新', 'success')
  } finally {
    reloading.value = false
  }
}

function openAdd(type = null, root = null) {
  const preferChildRootId = root?.id || selectedRootId.value || null
  const nodeType = type || (preferChildRootId ? 'child' : 'root')
  form.value = {
    id: null,
    nodeType,
    name: '',
    code: '',
    path: '',
    parentId: nodeType === 'child' ? preferChildRootId : null,
    sortOrder: 0,
    description: ''
  }
  dialog.value = { mode: 'add' }
}

function openEdit(m) {
  form.value = {
    id: m.id,
    nodeType: m.parentId ? 'child' : 'root',
    name: m.name || '',
    code: m.code || '',
    path: m.path || '',
    parentId: m.parentId,
    sortOrder: m.sortOrder ?? 0,
    description: m.description || ''
  }
  dialog.value = { mode: 'edit' }
}

function toggleSelectRoot(root) {
  if (!root || !root.id) return
  selectedRootId.value = selectedRootId.value === root.id ? null : root.id
}

function toggleExpand(rootId) {
  const next = new Set(expandedRootIds.value)
  if (next.has(rootId)) next.delete(rootId)
  else next.add(rootId)
  expandedRootIds.value = next
}

async function submit() {
  saving.value = true
  try {
    const nodeType = form.value.nodeType || 'root'
    const parentId = nodeType === 'child' ? form.value.parentId : null
    if (nodeType === 'child' && !parentId) {
      throw new Error('请选择父节点')
    }
    const body = {
      name: form.value.name,
      code: form.value.code,
      path: form.value.path,
      parentId,
      sortOrder: form.value.sortOrder == null ? 0 : form.value.sortOrder,
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
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}
.toolbar-tip {
  font-size: 0.85rem;
  color: #666;
}
.link {
  border: none;
  background: transparent;
  color: #667eea;
  cursor: pointer;
  padding: 0;
  margin-left: 0.25rem;
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
.icon-btn {
  width: 28px;
  height: 28px;
  border-radius: 6px;
  border: 1px solid #e6e6e6;
  background: #fff;
  cursor: pointer;
  color: #333;
}
.icon-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}
.row-root {
  background: #fcfcff;
}
.row-child td {
  background: #fff;
}
.name-cell {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.child-indent {
  padding-left: 1.3rem;
}
.tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 26px;
  height: 18px;
  font-size: 0.75rem;
  border-radius: 999px;
  padding: 0 6px;
  border: 1px solid rgba(102, 126, 234, 0.25);
  background: rgba(102, 126, 234, 0.08);
  color: #3d4a8f;
}
.tag.sub {
  border-color: rgba(46, 204, 113, 0.25);
  background: rgba(46, 204, 113, 0.08);
  color: #1e7e34;
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
.form-row input,
.form-row select {
  width: 100%;
  padding: 0.45rem 0.5rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-sizing: border-box;
}
.radio-row {
  display: flex;
  gap: 1rem;
  align-items: center;
  flex-wrap: wrap;
}
.radio {
  display: inline-flex;
  gap: 0.4rem;
  align-items: center;
  font-size: 0.9rem;
  color: #333;
}
.help {
  margin-top: 0.35rem;
  font-size: 0.82rem;
  color: #888;
}
.modal-actions {
  margin-top: 1rem;
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}
</style>
