<template>
  <div class="no-access">
    <h2>当前园区无菜单权限</h2>
    <p class="text">切换园区或联系管理员配置「用户-角色」「角色-菜单」（按园区）。</p>
    <p class="hint">若刚保存过绑定，请确认角色-菜单已在<strong>同一园区</strong>下配置。</p>
  </div>
</template>

<script setup></script>

<style scoped>
.no-access {
  padding: 2rem;
  max-width: 520px;
}
h2 {
  margin: 0 0 0.75rem;
  font-size: 1.15rem;
  color: #333;
}
.text {
  color: #555;
  line-height: 1.6;
  margin: 0 0 0.5rem;
}
.hint {
  font-size: 0.9rem;
  color: #888;
  line-height: 1.5;
}
</style>
