<template>
  <div class="page">
    <h1 class="title">角色 — 菜单绑定</h1>
    <p class="hint">先选角色与园区，勾选菜单后保存。仅覆盖<strong>当前园区</strong>下该角色的菜单，其它园区配置不受影响。</p>

    <div class="form-block">
      <div class="toolbar-inline">
        <button type="button" class="btn" :disabled="reloading" @click="refreshList">
          {{ reloading ? '刷新中…' : '刷新列表' }}
        </button>
      </div>
      <div class="form-row">
        <label>角色 <span class="req">*</span></label>
        <select v-model="roleId">
          <option value="">请选择</option>
          <option v-for="r in roles" :key="r.id" :value="String(r.id)">{{ r.name }} ({{ r.id }})</option>
        </select>
      </div>
      <div class="form-row">
        <label>园区 <span class="req">*</span></label>
        <select v-model="parkIdStr">
          <option value="">请选择园区</option>
          <option v-for="p in parks" :key="p.id" :value="String(p.id)">
            {{ p.name }} ({{ p.id }})
          </option>
        </select>
      </div>
    </div>

    <div class="menu-box">
      <div class="menu-title">菜单树（多选）<span class="req"> *</span></div>
      <div class="checks tree">
        <div v-for="root in menuRoots" :key="root.id" class="tree-node">
          <label class="check-line root">
            <input
              type="checkbox"
              :checked="isSelected(root.id)"
              @change="onToggle(root, $event.target.checked)"
            />
            <span>{{ root.name }} <small class="muted">#{{ root.id }} {{ root.code }}</small></span>
          </label>
          <div v-if="root.children && root.children.length" class="tree-children">
            <label v-for="child in root.children" :key="child.id" class="check-line child">
              <input
                type="checkbox"
                :checked="isSelected(child.id)"
                @change="onToggle(child, $event.target.checked)"
              />
              <span>{{ child.name }} <small class="muted">#{{ child.id }} {{ child.code }}</small></span>
            </label>
          </div>
        </div>
      </div>
    </div>

    <button type="button" class="btn primary" :disabled="submitting" @click="submit">
      {{ submitting ? '提交中…' : '保存当前园区绑定' }}
    </button>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { roleApi } from '../../api/role'
import { menuApi } from '../../api/menu'
import { permissionApi } from '../../api/permission'
import { parkApi } from '../../api/system'
import { flash } from '../../utils/flash'

const roles = ref([])
const menus = ref([])
const menuRoots = ref([])
const parks = ref([])
const roleId = ref('')
const parkIdStr = ref('')
const selectedMenuIds = ref([])
const submitting = ref(false)
const reloading = ref(false)

function parseParkId() {
  const s = (parkIdStr.value || '').trim()
  if (!s) return null
  const n = Number(s)
  return Number.isFinite(n) ? n : null
}

async function loadRolesMenus() {
  try {
    const [rr, mr, pr] = await Promise.all([roleApi.list({}), menuApi.list({}), parkApi.list()])
    if (rr.code === 200) {
      roles.value = (rr.data || []).filter(r => String(r.name || '').toUpperCase() !== 'ADMIN')
    }
    if (mr.code === 200) {
      menus.value = mr.data || []
      menuRoots.value = buildTree(menus.value)
    }
    if (pr.code === 200) parks.value = pr.data || []
  } catch (e) {
    flash(e.message || '加载失败', 'error')
  }
}

function buildTree(flat) {
  const nodes = (flat || []).map(m => ({ ...m, children: [] }))
  const byId = new Map(nodes.map(n => [n.id, n]))
  const roots = []
  for (const n of nodes) {
    const pid = n.parentId
    if (pid == null || pid === 0 || !byId.has(pid)) roots.push(n)
    else byId.get(pid).children.push(n)
  }
  return roots
}

function collectIds(node) {
  const ids = []
  const stack = [node]
  while (stack.length) {
    const cur = stack.pop()
    if (!cur || cur.id == null) continue
    ids.push(cur.id)
    if (Array.isArray(cur.children)) {
      for (const c of cur.children) stack.push(c)
    }
  }
  return ids
}

function isSelected(id) {
  return selectedMenuIds.value.includes(id)
}

function onToggle(node, checked) {
  const allIds = collectIds(node)
  const set = new Set(selectedMenuIds.value)
  if (checked) allIds.forEach(id => set.add(id))
  else allIds.forEach(id => set.delete(id))
  selectedMenuIds.value = Array.from(set)
}

async function refreshList() {
  if (reloading.value) return
  reloading.value = true
  try {
    await loadRolesMenus()
    flash('列表已刷新', 'success')
  } finally {
    reloading.value = false
  }
}

watch([roleId, parkIdStr], () => {
  if (!roleId.value) {
    selectedMenuIds.value = []
    return
  }
  const pid = parseParkId()
  if (pid == null) {
    selectedMenuIds.value = []
    return
  }
  loadBoundMenus(Number(roleId.value), pid)
})

async function loadBoundMenus(rid, parkId) {
  try {
    const res = await permissionApi.getRoleMenuIds({ id: rid, parkId })
    if (res.code === 200) {
      selectedMenuIds.value = Array.isArray(res.data) ? res.data : []
    } else {
      selectedMenuIds.value = []
      flash(res.msg || '加载已绑定菜单失败', 'error')
    }
  } catch (e) {
    selectedMenuIds.value = []
    flash(e.message || '加载已绑定菜单失败', 'error')
  }
}

async function submit() {
  if (!roleId.value) {
    flash('请选择角色', 'error')
    return
  }
  const parkId = parseParkId()
  if (parkId == null) {
    flash('请选择园区后再保存（各园区独立）', 'error')
    return
  }
  if (!selectedMenuIds.value.length) {
    flash('请选择至少一个菜单后再保存', 'error')
    return
  }
  submitting.value = true
  try {
    const res = await permissionApi.bindRoleMenus({
      roleId: Number(roleId.value),
      menuIds: selectedMenuIds.value,
      parkId
    })
    if (res.code !== 200) throw new Error(res.msg)
    flash('当前园区绑定已保存', 'success')
  } catch (e) {
    flash(e.message || '绑定失败', 'error')
  } finally {
    submitting.value = false
  }
}

onMounted(loadRolesMenus)
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
.req {
  color: #c0392b;
}
.form-block {
  max-width: 420px;
  margin-bottom: 1rem;
}
.toolbar-inline {
  margin-bottom: 0.75rem;
}
.form-row {
  margin-bottom: 0.75rem;
}
.form-row label {
  display: block;
  font-size: 0.85rem;
  margin-bottom: 0.25rem;
}
.form-row select,
.form-row input {
  width: 100%;
  padding: 0.45rem 0.5rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-sizing: border-box;
}
.menu-box {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 0.75rem;
  margin-bottom: 1rem;
  max-height: 320px;
  overflow-y: auto;
}
.menu-title {
  font-weight: 600;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}
.checks.tree {
  display: flex;
  flex-direction: column;
  gap: 0.55rem;
}
.tree-node {
  border: 1px solid #e6ebfb;
  border-radius: 8px;
  background: #fafbff;
  padding: 0.5rem 0.6rem;
}
.tree-children {
  margin-top: 0.35rem;
  margin-left: 1.2rem;
  padding-left: 0.7rem;
  border-left: 2px dashed #c7d2fe;
}
.check-line {
  display: flex;
  align-items: flex-start;
  gap: 0.5rem;
  margin-bottom: 0.3rem;
  font-size: 0.9rem;
}
.check-line.root {
  font-weight: 600;
  color: #2e3a63;
}
.check-line.child {
  font-size: 0.88rem;
  color: #4a5674;
}
.check-line input[type='checkbox'] {
  margin-top: 0.1rem;
}
.check-line:last-child {
  margin-bottom: 0;
}
.muted {
  color: #8f97ab;
  margin-left: 0.2rem;
}
.btn.primary {
  padding: 0.5rem 1.2rem;
  border: none;
  border-radius: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  cursor: pointer;
}
.btn {
  padding: 0.45rem 1rem;
  border-radius: 6px;
  border: 1px solid #ddd;
  background: #fff;
  cursor: pointer;
}
.btn.primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
