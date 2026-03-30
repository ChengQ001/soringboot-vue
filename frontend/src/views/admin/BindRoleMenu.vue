<template>
  <div class="page">
    <h1 class="title">角色 — 菜单绑定</h1>
    <p class="hint">选择角色与园区，勾选菜单后提交（会先清空该角色下原有关联再写入）。</p>

    <div class="form-block">
      <div class="form-row">
        <label>角色</label>
        <select v-model="roleId">
          <option value="">请选择</option>
          <option v-for="r in roles" :key="r.id" :value="r.id">{{ r.name }} ({{ r.id }})</option>
        </select>
      </div>
      <div class="form-row">
        <label>parkId（园区，可空）</label>
        <input v-model.number="parkId" type="number" placeholder="可选" />
      </div>
    </div>

    <div class="menu-box">
      <div class="menu-title">菜单（多选）</div>
      <div class="checks">
        <label v-for="m in menus" :key="m.id" class="check-line">
          <input v-model="selectedMenuIds" type="checkbox" :value="m.id" />
          <span>{{ m.name }} <small class="muted">#{{ m.id }} {{ m.code }}</small></span>
        </label>
      </div>
    </div>

    <button type="button" class="btn primary" :disabled="submitting" @click="submit">
      {{ submitting ? '提交中…' : '保存绑定' }}
    </button>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { roleApi } from '../../api/role'
import { menuApi } from '../../api/menu'
import { permissionApi } from '../../api/permission'
import { flash } from '../../utils/flash'

const roles = ref([])
const menus = ref([])
const roleId = ref('')
const parkId = ref(null)
const selectedMenuIds = ref([])
const submitting = ref(false)

async function loadRolesMenus() {
  try {
    const [rr, mr] = await Promise.all([roleApi.list({}), menuApi.list({})])
    if (rr.code === 200) roles.value = rr.data || []
    if (mr.code === 200) menus.value = mr.data || []
  } catch (e) {
    flash(e.message || '加载失败', 'error')
  }
}

watch(roleId, (id) => {
  if (!id) {
    selectedMenuIds.value = []
    return
  }
  selectedMenuIds.value = []
})

async function submit() {
  if (!roleId.value) {
    flash('请选择角色', 'error')
    return
  }
  submitting.value = true
  try {
    const res = await permissionApi.bindRoleMenus({
      roleId: Number(roleId.value),
      menuIds: selectedMenuIds.value,
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
.form-block {
  max-width: 420px;
  margin-bottom: 1rem;
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
.btn.primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
