<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { clubApi, transferApi } from '../api'
import { useAuthStore } from '../stores/auth'
import { formatPrice } from '../utils/player'
import VoteButtons from '../components/VoteButtons.vue'
import PlayerDetailModal from '../components/PlayerDetailModal.vue'

const route = useRoute()
const auth = useAuthStore()
const club = ref(null)
const loading = ref(true)
const message = ref('')
const selectedPlayer = ref(null)
const showModal = ref(false)

const positionOrder = { GK:0, CB:1, LB:1, RB:1, LWB:1, RWB:1, CDM:2, CM:2, LM:2, RM:2, CAM:3, LW:4, RW:4, ST:4, CF:4 }

const sortedPlayers = computed(() => {
  if (!club.value?.players) return []
  return [...club.value.players].sort((a, b) =>
    (positionOrder[a.position] ?? 5) - (positionOrder[b.position] ?? 5)
  )
})

async function load() {
  loading.value = true
  try {
    const { data } = await clubApi.getById(route.params.id)
    club.value = data
  } finally {
    loading.value = false
  }
}

async function buyPlayer(player) {
  message.value = ''
  try {
    await transferApi.buy(player.id)
    message.value = `${player.name} cumpărat și adăugat pe bancă!`
    await auth.fetchProfile()
    await load()
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
    <router-link to="/clubs" class="text-sm hover:underline mb-4 inline-block" style="color: var(--accent)">
      ← Înapoi la cluburi
    </router-link>

    <div v-if="loading" class="text-sm" style="color: var(--text-muted)">Se încarcă...</div>
    <template v-else-if="club">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h1 class="text-2xl font-bold">{{ club.name }}</h1>
          <div class="flex gap-4 mt-2 text-sm" style="color: var(--text-muted)">
            <span>UCL: {{ club.ucl }}</span>
            <span>Liga: {{ club.league }}</span>
            <span>Cupă: {{ club.cup }}</span>
          </div>
        </div>
        <VoteButtons target-type="PLAYER_TEAM" :target-id="club.id" />
      </div>

      <p v-if="message" class="mb-4 text-sm"
        :style="{ color: message.includes('eșuat') ? 'var(--danger)' : 'var(--success)' }">
        {{ message }}
      </p>

      <h2 class="font-semibold mb-4">Lot jucători ({{ club.players?.length || 0 }})</h2>
      <div class="rounded-xl overflow-hidden" style="border: 1px solid var(--border)">
        <table class="w-full text-sm">
          <thead>
            <tr style="background: var(--bg-elevated); color: var(--text-muted)">
              <th class="text-left px-4 py-3">#</th>
              <th class="text-left px-4 py-3">Jucător</th>
              <th class="text-left px-4 py-3 hidden sm:table-cell">Vârstă</th>
              <th class="text-left px-4 py-3 hidden md:table-cell">Naționalitate</th>
              <th class="text-right px-4 py-3">Valoare</th>
              <th class="text-center px-4 py-3">Status</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(player, i) in sortedPlayers" :key="player.id"
              @click="openDetail(player)"
              class="cursor-pointer transition-colors"
              style="border-top: 1px solid var(--border)"
              :style="{ background: i % 2 === 0 ? 'var(--bg-surface)' : 'var(--bg-base)' }"
              onmouseover="this.style.background='var(--bg-elevated)'"
              onmouseout="this.style.background=''"
            >
              <td class="px-4 py-3" style="color: var(--text-muted)">{{ player.shirtNumber || '—' }}</td>
              <td class="px-4 py-3">
                <div class="flex items-center gap-2">
                  <span v-if="!player.available" class="w-2 h-2 rounded-full shrink-0" style="background: var(--danger)"></span>
                  <span :style="{ color: player.available !== false ? 'var(--text-primary)' : 'var(--text-muted)', fontStyle: player.available !== false ? 'normal' : 'italic' }">
                    {{ player.name }}
                  </span>
                  <span class="text-xs px-1.5 py-0.5 rounded font-mono" style="background: var(--bg-elevated); color: var(--text-muted)">{{ player.position }}</span>
                </div>
              </td>
              <td class="px-4 py-3 hidden sm:table-cell" style="color: var(--text-muted)">{{ player.age || '—' }}</td>
              <td class="px-4 py-3 hidden md:table-cell" style="color: var(--text-muted)">{{ player.nationality || '—' }}</td>
              <td class="px-4 py-3 text-right font-semibold" style="color: var(--accent)">{{ formatPrice(player.price) }}</td>
              <td class="px-4 py-3 text-center">
                <span v-if="player.available === false" class="text-xs px-2 py-0.5 rounded" style="background: rgba(248,81,73,0.15); color: var(--danger)">Indisponibil</span>
                <span v-else class="text-xs px-2 py-0.5 rounded" style="background: rgba(63,185,80,0.15); color: var(--success)">Activ</span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <PlayerDetailModal
        :player="selectedPlayer"
        :show="showModal"
        :is-owned="false"
        @close="showModal = false"
        @buy="buyPlayer"
      />
    </template>
  </div>
</template>
