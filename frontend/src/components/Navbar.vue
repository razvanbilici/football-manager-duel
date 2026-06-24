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
  <nav class="backdrop-blur sticky top-0 z-50" style="background-color: var(--bg-surface); border-bottom: 1px solid var(--border)">
    <!-- Accent top border -->
    <div style="height: 3px; background: var(--accent)"></div>

    <div class="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between gap-4">
      <router-link to="/" class="flex items-center gap-2 text-lg shrink-0"
        style="font-weight: 700; letter-spacing: 0.08em; text-transform: uppercase; color: var(--accent)">
        <span>⚽</span>
        <span class="hidden sm:inline">Football Manager</span>
      </router-link>

      <div class="flex items-center gap-1 sm:gap-3 text-sm overflow-x-auto">
        <router-link
          v-for="link in visibleLinks"
          :key="link.to"
          :to="link.to"
          class="px-2 py-1 rounded transition-colors whitespace-nowrap"
          :style="{ color: 'var(--text-muted)' }"
          active-class="nav-link-active"
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
              class="relative px-2 py-1 rounded transition-colors"
              :style="{ color: 'var(--text-muted)' }"
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
              class="absolute right-0 top-full mt-2 w-80 max-h-96 overflow-y-auto rounded-xl shadow-xl z-50"
              style="background: var(--bg-surface); border: 1px solid var(--border)"
            >
              <div class="p-3 flex justify-between items-center" style="border-bottom: 1px solid var(--border)">
                <span class="font-semibold" style="color: var(--text-primary)">Notificări</span>
                <button @click="goToOffers" class="text-xs hover:underline" style="color: var(--accent)">
                  Vezi toate ofertele
                </button>
              </div>
              <div v-if="notifications.items.length === 0" class="p-4 text-sm" style="color: var(--text-muted)">
                Nicio notificare.
              </div>
              <div
                v-for="n in notifications.items.slice(0, 8)"
                :key="n.id"
                class="p-3 text-sm"
                :style="{ borderBottom: '1px solid var(--border)', background: !n.read ? 'rgba(0,212,170,0.05)' : 'transparent' }"
              >
                <p style="color: var(--text-primary)">{{ n.message }}</p>
                <div v-if="n.proposal && n.proposal.status === 'PENDING'" class="mt-2 flex gap-2">
                  <button
                    @click="goToOffers"
                    class="text-xs px-2 py-1 rounded"
                    style="background: var(--accent); color: #0d1117"
                  >
                    Răspunde
                  </button>
                </div>
              </div>
            </div>
          </div>

          <span v-if="auth.user?.budget != null" class="hidden md:inline" style="color: var(--accent)">
            {{ formatBudget(auth.user.budget) }}
          </span>
          <span class="hidden sm:inline" style="color: var(--text-muted)">{{ auth.user?.name }}</span>
          <button
            @click="logout"
            class="px-3 py-1 rounded transition-colors"
            style="background: var(--bg-elevated); color: var(--text-primary)"
            onmouseover="this.style.filter='brightness(0.8)'"
            onmouseout="this.style.filter=''"
          >
            Ieșire
          </button>
        </template>
        <template v-else>
          <router-link to="/login" class="px-3 py-1 rounded transition-colors" style="color: var(--text-muted)">Login</router-link>
          <router-link
            to="/register"
            class="px-3 py-1 rounded transition-colors"
            style="background: var(--accent); color: #0d1117; font-weight: 600"
          >
            Înregistrare
          </router-link>
        </template>
      </div>
    </div>
  </nav>
</template>

<style scoped>
.nav-link-active {
  color: var(--accent) !important;
  font-weight: 600;
  border-bottom: 2px solid var(--accent);
  padding-bottom: 2px;
}
</style>
