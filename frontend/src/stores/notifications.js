import { defineStore } from 'pinia'
import { ref } from 'vue'
import { notificationApi } from '../api'

export const useNotificationStore = defineStore('notifications', () => {
  const unreadCount = ref(0)
  const items = ref([])
  let pollTimer = null

  async function fetchUnreadCount() {
    try {
      const { data } = await notificationApi.getUnreadCount()
      unreadCount.value = data.count
    } catch {
      unreadCount.value = 0
    }
  }

  async function fetchAll() {
    const { data } = await notificationApi.getAll()
    items.value = data
    unreadCount.value = data.filter((n) => !n.read).length
    return data
  }

  async function markRead(id) {
    await notificationApi.markRead(id)
    await fetchAll()
  }

  function startPolling(isLoggedIn) {
    stopPolling()
    if (!isLoggedIn) return
    fetchUnreadCount()
    pollTimer = setInterval(fetchUnreadCount, 30000)
  }

  function stopPolling() {
    if (pollTimer) {
      clearInterval(pollTimer)
      pollTimer = null
    }
    unreadCount.value = 0
    items.value = []
  }

  return { unreadCount, items, fetchUnreadCount, fetchAll, markRead, startPolling, stopPolling }
})
