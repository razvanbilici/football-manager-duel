<script setup>
import { ref, computed, onMounted } from 'vue'
import { playerApi, transferApi } from '../api'
import { useAuthStore } from '../stores/auth'
import PlayerCard from '../components/PlayerCard.vue'
import PlayerFilters from '../components/PlayerFilters.vue'
import PlayerDetailModal from '../components/PlayerDetailModal.vue'

// Importăm videoclipul din assets
import bgVideo from '../assets/playerview.mp4'

const auth = useAuthStore()
const players = ref([])
const filters = ref({})
const loading = ref(true)
const message = ref('')
const showFilters = ref(false) // mobile toggle
const selectedPlayer = ref(null)
const showModal = ref(false)
const page = ref(0)
const totalPages = ref(1)
const totalElements = ref(0)
const sortBy = ref('name')
const sortDir = ref('asc')

const ownedPlayerIds = computed(() => {
  const squad = auth.user?.userTeam?.players || []
  return new Set(squad.map(tp => tp.player?.id))
})

let debounceTimer = null
function onFiltersChange(newFilters) {
  filters.value = newFilters
  page.value = 0
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(load, 350)
}

async function load() {
  loading.value = true
  try {
    const { data } = await playerApi.getAll({
      ...filters.value,
      page: page.value,
      size: 12,
      sortBy: sortBy.value,
      sortDir: sortDir.value,
    })
    players.value = data.content
    totalPages.value = data.totalPages
    totalElements.value = data.totalElements
  } finally {
    loading.value = false
  }
}

async function buyPlayer(player) {
  if (!auth.isLoggedIn) return
  message.value = ''
  try {
    await transferApi.buy(player.id)
    message.value = `${player.name} cumpărat și adăugat pe bancă!`
    await auth.fetchProfile()
    selectedPlayer.value = null
    showModal.value = false
  } catch (e) {
    message.value = e.response?.data?.message || 'Transfer eșuat'
  }
}

function openDetail(player) {
  selectedPlayer.value = player
  showModal.value = true
}

onMounted(load)
</script>

<template>
  <div class="relative min-h-screen">
    
    <video
      :src="bgVideo"
      autoplay
      loop
      muted
      playsinline
      style="position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; object-fit: cover; z-index: -2; pointer-events: none;"
    ></video>
    
    <div style="position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(13, 17, 23, 0.7); z-index: -1; pointer-events: none;"></div>

    <div class="relative z-10">
      
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-bold">Jucători</h1>
        <button class="lg:hidden px-3 py-1.5 rounded-lg text-sm"
          style="background: var(--bg-elevated); color: var(--text-primary)"
          @click="showFilters = !showFilters">
          {{ showFilters ? 'Ascunde filtrele ▲' : 'Filtre ▼' }}
        </button>
      </div>

      <p v-if="message" class="mb-4 text-sm"
        :style="{ color: message.includes('eșuat') ? 'var(--danger)' : 'var(--success)' }">
        {{ message }}
      </p>

      <div class="flex gap-6">
        <aside class="w-64 shrink-0 hidden lg:block">
          <PlayerFilters :model-value="filters" @update:model-value="onFiltersChange" />
        </aside>

        <div v-if="showFilters" class="lg:hidden w-full mb-4">
          <PlayerFilters :model-value="filters" @update:model-value="onFiltersChange" />
        </div>

        <div class="flex-1">
          <div class="flex items-center gap-2 mb-4 flex-wrap">
            <span class="text-sm" style="color: var(--text-muted)">Sorteaza:</span>
            <select v-model="sortBy" @change="page = 0; load()"
              class="text-sm px-2 py-1.5 rounded outline-none"
              style="background: var(--bg-elevated); border: 1px solid var(--border); color: var(--text-primary)">
              <option value="name">Nume</option>
              <option value="price">Pret</option>
              <option value="age">Varsta</option>
            </select>
            <button @click="sortDir = sortDir === 'asc' ? 'desc' : 'asc'; page = 0; load()"
              class="px-3 py-1.5 rounded text-sm transition-colors"
              style="background: var(--bg-elevated); border: 1px solid var(--border); color: var(--text-muted)">
              {{ sortDir === 'asc' ? '↑ Crescator' : '↓ Descrescator' }}
            </button>
            <span class="text-xs ml-auto" style="color: var(--text-muted)">
              {{ totalElements }} jucatori
            </span>
          </div>

          <div v-if="loading" class="grid sm:grid-cols-2 xl:grid-cols-3 gap-4">
            <div v-for="i in 9" :key="i" class="skeleton h-44 rounded-xl" />
          </div>

          <div v-else-if="players.length === 0"
            class="flex flex-col items-center justify-center py-20 text-center">
            <div class="text-5xl mb-4">🔍</div>
            <h3 class="text-lg font-semibold mb-1" style="color: var(--text-primary)">Niciun jucător găsit</h3>
            <p class="text-sm" style="color: var(--text-muted)">Încearcă să modifici filtrele.</p>
          </div>

          <div v-else class="grid sm:grid-cols-2 xl:grid-cols-3 gap-4">
            <PlayerCard
              v-for="player in players"
              :key="player.id"
              :player="player"
              :show-buy="auth.isLoggedIn"
              :is-owned="ownedPlayerIds.has(player.id)"
              @buy="buyPlayer"
              @view-detail="openDetail"
            />
          </div>

          <div v-if="totalPages > 1" class="flex items-center justify-center gap-2 mt-8 flex-wrap">
            <button @click="page = 0; load()" :disabled="page === 0"
              class="btn-secondary px-3 py-1.5 text-sm disabled:opacity-40">««</button>
            <button @click="page--; load()" :disabled="page === 0"
              class="btn-secondary px-3 py-1.5 text-sm disabled:opacity-40">‹ Anterior</button>
            <span class="text-sm px-3" style="color: var(--text-muted)">
              {{ page + 1 }} / {{ totalPages }}
            </span>
            <button @click="page++; load()" :disabled="page >= totalPages - 1"
              class="btn-secondary px-3 py-1.5 text-sm disabled:opacity-40">Urmator ›</button>
            <button @click="page = totalPages - 1; load()" :disabled="page >= totalPages - 1"
              class="btn-secondary px-3 py-1.5 text-sm disabled:opacity-40">»»</button>
          </div>
        </div>
      </div>

    </div>

    <PlayerDetailModal
      :player="selectedPlayer"
      :show="showModal"
      :is-owned="selectedPlayer ? ownedPlayerIds.has(selectedPlayer.id) : false"
      @close="showModal = false"
      @buy="buyPlayer"
    />
  </div>
</template>

<style scoped>
.feature-card { border-top: 2px solid transparent; transition: border-color 0.2s; }
.feature-card:hover { border-top-color: var(--accent); }
</style>