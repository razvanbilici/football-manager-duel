<script setup>
import { ref, computed, onMounted } from 'vue'
import { playerApi, transferApi } from '../api'
import { useAuthStore } from '../stores/auth'
import PlayerCard from '../components/PlayerCard.vue'
import PlayerFilters from '../components/PlayerFilters.vue'
import PlayerDetailModal from '../components/PlayerDetailModal.vue'

const auth = useAuthStore()
const players = ref([])
const filters = ref({})
const loading = ref(true)
const message = ref('')
const showFilters = ref(false) // mobile toggle
const selectedPlayer = ref(null)
const showModal = ref(false)

const ownedPlayerIds = computed(() => {
  const squad = auth.user?.userTeam?.players || []
  return new Set(squad.map(tp => tp.player?.id))
})

let debounceTimer = null
function onFiltersChange(newFilters) {
  filters.value = newFilters
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(load, 350)
}

async function load() {
  loading.value = true
  try {
    const { data } = await playerApi.getAll(filters.value)
    players.value = data
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
  <div>
    <div class="flex items-center justify-between mb-6">
      <h1 class="text-2xl font-bold">Jucători</h1>
      <!-- Mobile filter toggle -->
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
      <!-- Filter sidebar -->
      <aside class="w-64 shrink-0 hidden lg:block">
        <PlayerFilters :model-value="filters" @update:model-value="onFiltersChange" />
      </aside>

      <!-- Mobile filter panel -->
      <div v-if="showFilters" class="lg:hidden w-full mb-4">
        <PlayerFilters :model-value="filters" @update:model-value="onFiltersChange" />
      </div>

      <!-- Player grid -->
      <div class="flex-1">
        <!-- Skeleton -->
        <div v-if="loading" class="grid sm:grid-cols-2 xl:grid-cols-3 gap-4">
          <div v-for="i in 9" :key="i" class="skeleton h-44 rounded-xl" />
        </div>

        <!-- Empty state -->
        <div v-else-if="players.length === 0"
          class="flex flex-col items-center justify-center py-20 text-center">
          <div class="text-5xl mb-4">🔍</div>
          <h3 class="text-lg font-semibold mb-1" style="color: var(--text-primary)">Niciun jucător găsit</h3>
          <p class="text-sm" style="color: var(--text-muted)">Încearcă să modifici filtrele.</p>
        </div>

        <!-- Cards -->
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
      </div>
    </div>

    <!-- Detail modal -->
    <PlayerDetailModal
      :player="selectedPlayer"
      :show="showModal"
      :is-owned="selectedPlayer ? ownedPlayerIds.has(selectedPlayer.id) : false"
      @close="showModal = false"
      @buy="buyPlayer"
    />
  </div>
</template>
