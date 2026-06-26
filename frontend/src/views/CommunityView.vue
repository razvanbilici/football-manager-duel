<script setup>
import { ref, onMounted } from 'vue'
import { teamApi } from '../api'
import VoteButtons from '../components/VoteButtons.vue'
import PitchView from '../components/PitchView.vue'

// Importăm videoclipul pentru fani din assets
import bgVideo from '../assets/fans.mp4'

const teams = ref([])
const loading = ref(true)
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const search = ref('')

async function load() {
  loading.value = true
  try {
    const { data } = await teamApi.getSubmitted({
      page: page.value,
      size: 10,
      sortBy: 'votes',
      sortDir: 'desc',
      search: search.value || undefined,
    })
    teams.value = data.content
    totalPages.value = data.totalPages
    totalElements.value = data.totalElements
  } finally {
    loading.value = false
  }
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
      <h1 class="text-2xl font-bold mb-2">Echipe din comunitate</h1>
      <p class="mb-6 text-sm" style="color: var(--text-muted)">Echipele publicate de utilizatori, sortate după voturi.</p>

      <div v-if="loading" class="space-y-6">
        <div v-for="i in 3" :key="i" class="skeleton h-48 rounded-xl" />
      </div>

      <div v-else-if="teams.length === 0" class="py-16 text-center">
        <div class="text-4xl mb-4">🏟️</div>
        <p class="font-semibold mb-1" style="color: var(--text-primary)">Nicio echipă publicată încă.</p>
        <p class="text-sm" style="color: var(--text-muted)">Fii primul care publică!</p>
      </div>

      <div v-else class="space-y-8 bg-black/20 backdrop-blur-sm">
        <div
          v-for="(team, index) in teams"
          :key="team.id"
          class="p-5 rounded-xl"
        >
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-4 ">
            <div>
              <div class="flex items-center gap-3">
                <span class="text-2xl font-bold" style="color: var(--accent)">#{{ index + 1 }}</span>
                <h2 class="text-lg font-semibold" style="color: var(--text-primary)">{{ team.name }}</h2>
              </div>
              <div class="mt-1 flex gap-3 text-sm" style="color: var(--text-muted)">
                <span v-if="team.formation">{{ team.formation.name }}</span>
                <span v-if="team.tactic">{{ team.tactic.details }}</span>
              </div>
              <router-link v-if="team.ownerId" :to="`/user/${team.ownerId}`"
                class="text-xs hover:underline mt-1 inline-block" style="color: var(--accent)">
                👤 {{ team.ownerName }}
              </router-link>
            </div>
            <div class="flex items-center gap-4">
              <span class="font-medium" style="color: var(--accent)">▲ {{ team.votes }}</span>
              <VoteButtons target-type="USER_TEAM" :target-id="team.id" />
            </div>
          </div>

          <div class="max-w-sm mx-auto">
            <PitchView
              :formation="team.formation"
              :players="team.players"
            />
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>
</style>