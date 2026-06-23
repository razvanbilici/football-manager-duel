<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useNotificationStore } from '../stores/notifications'
import { formatBudget } from '../utils/player'

const auth = useAuthStore()
const notifications = useNotificationStore()
const router = useRouter()
const showNotifications = ref(false)

const links = [
  { to: '/', label: 'Acasă' },
  { to: '/players', label: 'Jucători' },
  { to: '/clubs', label: 'Cluburi' },
  { to: '/my-team', label: 'Echipa mea', auth: true },
  { to: '/community', label: 'Comunitate' },
  { to: '/transfers', label: 'Transferuri' },
]

const visibleLinks = computed(() =>
  links.filter((l) => !l.auth || auth.isLoggedIn),
)

watch(
  () => auth.isLoggedIn,
  (loggedIn) => {
    if (loggedIn) {
      auth.fetchProfile()
      notifications.startPolling(true)
    } else {
      notifications.stopPolling()
    }
  },
  { immediate: true },
)

async function toggleNotifications() {
  if (!auth.isLoggedIn) return
  showNotifications.value = !showNotifications.value
  if (showNotifications.value) {
    await notifications.fetchAll()
  }
}

function goToOffers() {
  showNotifications.value = false
  router.push({ path: '/transfers', query: { tab: 'offers' } })
}

function logout() {
  notifications.stopPolling()
  auth.logout()
  router.push('/')
}
</script>

<template>
  <nav class="border-b border-emerald-900/50 bg-slate-900/80 backdrop-blur sticky top-0 z-50">
    <div class="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between gap-4">
      <router-link to="/" class="flex items-center gap-2 font-bold text-emerald-400 text-lg shrink-0">
        <span>⚽</span>
        <span class="hidden sm:inline">Football Manager</span>
      </router-link>

      <div class="flex items-center gap-1 sm:gap-3 text-sm overflow-x-auto">
        <router-link
          v-for="link in visibleLinks"
          :key="link.to"
          :to="link.to"
          class="px-2 py-1 rounded hover:text-emerald-400 transition-colors whitespace-nowrap"
          active-class="text-emerald-400 font-medium"
        >
          {{ link.label }}
        </router-link>
      </div>

      <div class="flex items-center gap-2 sm:gap-3 text-sm shrink-0">
        <template v-if="auth.isLoggedIn">
          <div class="relative">
            <button
              type="button"
              @click="toggleNotifications"
              class="relative px-2 py-1 rounded hover:bg-slate-800 transition-colors"
              title="Notificări"
            >
              🔔
              <span
                v-if="notifications.unreadCount > 0"
                class="absolute -top-1 -right-1 min-w-4 h-4 px-1 rounded-full bg-red-500 text-[10px] font-bold flex items-center justify-center"
              >
                {{ notifications.unreadCount }}
              </span>
            </button>

            <div
              v-if="showNotifications"
              class="absolute right-0 top-full mt-2 w-80 max-h-96 overflow-y-auto rounded-xl bg-slate-900 border border-slate-700 shadow-xl z-50"
            >
              <div class="p-3 border-b border-slate-800 flex justify-between items-center">
                <span class="font-semibold">Notificări</span>
                <button @click="goToOffers" class="text-xs text-emerald-400 hover:underline">
                  Vezi toate ofertele
                </button>
              </div>
              <div v-if="notifications.items.length === 0" class="p-4 text-sm text-slate-500">
                Nicio notificare.
              </div>
              <div
                v-for="n in notifications.items.slice(0, 8)"
                :key="n.id"
                :class="[
                  'p-3 border-b border-slate-800 text-sm',
                  !n.read && 'bg-emerald-950/30',
                ]"
              >
                <p>{{ n.message }}</p>
                <div v-if="n.proposal && n.proposal.status === 'PENDING'" class="mt-2 flex gap-2">
                  <button
                    @click="goToOffers"
                    class="text-xs px-2 py-1 rounded bg-emerald-600 hover:bg-emerald-500"
                  >
                    Răspunde
                  </button>
                </div>
              </div>
            </div>
          </div>

          <span v-if="auth.user?.budget != null" class="hidden md:inline text-emerald-300">
            {{ formatBudget(auth.user.budget) }}
          </span>
          <span class="hidden sm:inline text-slate-400">{{ auth.user?.name }}</span>
          <button
            @click="logout"
            class="px-3 py-1 rounded bg-slate-800 hover:bg-slate-700 transition-colors"
          >
            Ieșire
          </button>
        </template>
        <template v-else>
          <router-link to="/login" class="px-3 py-1 rounded hover:text-emerald-400">Login</router-link>
          <router-link
            to="/register"
            class="px-3 py-1 rounded bg-emerald-600 hover:bg-emerald-500 transition-colors"
          >
            Înregistrare
          </router-link>
        </template>
      </div>
    </div>
  </nav>
</template>
