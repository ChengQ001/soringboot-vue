<template>
  <Teleport to="body">
    <Transition name="flash-fade">
      <div v-if="flashText" class="flash-toast" :class="flashType" role="alert">
        {{ flashText }}
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { flashText, flashType } from '../utils/flash'
</script>

<style scoped>
.flash-toast {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10000;
  padding: 0.65rem 1.25rem;
  border-radius: 8px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  font-size: 0.92rem;
  max-width: min(420px, calc(100vw - 32px));
  line-height: 1.45;
  pointer-events: none;
}

.flash-toast.error {
  background: #fdecea;
  color: #c0392b;
  border: 1px solid #f5c6cb;
}

.flash-toast.success {
  background: #e8f8f0;
  color: #1e8449;
  border: 1px solid #a9dfbf;
}

.flash-toast.info {
  background: #e8f0fe;
  color: #1a56a8;
  border: 1px solid #aecbfa;
}
</style>

<style>
.flash-fade-enter-active,
.flash-fade-leave-active {
  transition: opacity 0.22s ease, transform 0.22s ease;
}
.flash-fade-enter-from,
.flash-fade-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(-8px);
}
</style>
