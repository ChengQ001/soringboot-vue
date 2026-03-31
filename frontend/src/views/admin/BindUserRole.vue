<template>
  <div class="page">
    <h1 class="title">用户 — 角色绑定</h1>
    <p class="hint">选择用户与园区，勾选角色后提交。</p>

    <div class="form-block">
      <div class="toolbar-inline">
        <button type="button" class="btn" :disabled="reloading" @click="refreshList">
          {{ reloading ? '刷新中…' : '刷新列表' }}
        </button>
      </div>
      <div class="form-row">
        <label>用户</label>
        <select v-model="userId">
          <option value="">请选择</option>
          <option v-for="u in users" :key="u.id" :value="u.id">
            {{ u.username }} / {{ u.phone }} ({{ u.id }})
          </option>
        </select>
      </div>
      <div class="form-row">
        <label>园区（可空）</label>
        <select v-model.number="parkId">
          <option :value="null">全部园区（空）</option>
          <option v-for="p in parks" :key="p.id" :value="p.id">
            {{ p.name }} ({{ p.id }})
          </option>
        </select>
      </div>
    </div>

    <div class="box">
      <div class="sub">角色（多选）</div>
      <label v-for="r in roles" :key="r.id" class="check-line">
        <input v-model="selectedRoleIds" type="checkbox" :value="r.id" />
        <span>{{ r.name }} <small class="muted">#{{ r.id }}</small></span>
      </label>
    </div>

    <button type="button" class="btn primary" :disabled="submitting" @click="submit">
      {{ submitting ? '提交中…' : '保存绑定' }}
    </button>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { parkApi, sysUserApi } from '../../api/system'
import { roleApi } from '../../api/role'
import { permissionApi } from '../../api/permission'
import { flash } from '../../utils/flash'

const users = ref([])
const roles = ref([])
const parks = ref([])
const userId = ref('')
const parkId = ref(null)
const selectedRoleIds = ref([])
const submitting = ref(false)
const reloading = ref(false)

async function load() {
  try {
    const [ur, rr, pr] = await Promise.all([sysUserApi.list(), roleApi.list({}), parkApi.list()])
    if (ur.code === 200) users.value = ur.data || []
    if (rr.code === 200) roles.value = rr.data || []
    if (pr.code === 200) parks.value = pr.data || []
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

watch(userId, (id) => {
  if (!id) {
    selectedRoleIds.value = []
    return
  }
  loadBoundRoles(id)
})

async function loadBoundRoles(id) {
  try {
    const res = await permissionApi.getUserRoleIds({ id: Number(id) })
    if (res.code === 200) {
      selectedRoleIds.value = Array.isArray(res.data) ? res.data : []
    } else {
      selectedRoleIds.value = []
      flash(res.msg || '加载已绑定角色失败', 'error')
    }
  } catch (e) {
    selectedRoleIds.value = []
    flash(e.message || '加载已绑定角色失败', 'error')
  }
}

async function submit() {
  if (!userId.value) {
    flash('请选择用户', 'error')
    return
  }
  submitting.value = true
  try {
    const res = await permissionApi.bindUserRoles({
      userId: Number(userId.value),
      roleIds: selectedRoleIds.value,
      parkId: parkId.value || null
    })
    if (res.code !== 200) throw new Error(res.msg)
    flash('绑定成功', 'success')
  } catch (e) {
    flash(e.message || '绑定失败', 'error')
  } finally {
    submitting.value = false
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
.box {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 0.75rem;
  margin-bottom: 1rem;
  max-height: 280px;
  overflow-y: auto;
}
.sub {
  font-weight: 600;
  margin-bottom: 0.5rem;
}
.check-line {
  display: flex;
  align-items: flex-start;
  gap: 0.5rem;
  margin-bottom: 0.35rem;
  font-size: 0.9rem;
}
.muted {
  color: #999;
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
