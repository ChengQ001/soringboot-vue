import { ref } from 'vue'

/** 全局轻量提示文案（供 FlashToast 绑定） */
export const flashText = ref('')
/** 'error' | 'success' | 'info' */
export const flashType = ref('info')

let timer = null

/**
 * 顶部自动消失提示
 * @param {string} message
 * @param {'error'|'success'|'info'} type
 * @param {number} durationMs 默认约 3.2 秒
 */
export function flash(message, type = 'info', durationMs = 3200) {
  if (message == null || message === '') {
    return
  }
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
  flashText.value = String(message)
  flashType.value = type
  timer = setTimeout(() => {
    flashText.value = ''
    flashType.value = 'info'
    timer = null
  }, durationMs)
}

export function clearFlash() {
  if (timer) {
    clearTimeout(timer)
    timer = null
  }
  flashText.value = ''
  flashType.value = 'info'
}
