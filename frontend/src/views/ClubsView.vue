<script setup>
import { ref, onMounted } from 'vue'
import { clubApi } from '../api'
import VoteButtons from '../components/VoteButtons.vue'

// Importăm videoclipul pentru cluburi din assets
import bgVideo from '../assets/clubs.mp4'

const clubs = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const { data } = await clubApi.getAll()
    clubs.value = data
  } finally {
    loading.value = false
  }
})
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
      <h1 class="text-2xl font-bold mb-6">Cluburi</h1>

      <div v-if="loading" class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="i in 6" :key="i" class="skeleton h-32 rounded-xl" />
      </div>

      <div v-else-if="clubs.length === 0" class="text-center py-16" style="color: var(--text-muted)">
        <div class="text-4xl mb-3">🏟️</div>
        <p class="text-lg font-medium" style="color: var(--text-primary)">Niciun club găsit</p>
        <p class="text-sm mt-1">Momentan nu există cluburi înregistrate.</p>
      </div>

      <div v-else class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="club in clubs"
          :key="club.id"
          class="card card-hover p-5"
        >
          <div class="flex justify-between items-start">
            <router-link :to="`/clubs/${club.id}`" class="font-semibold text-lg transition-colors"
              style="color: var(--text-primary)"
              onmouseover="this.style.color='var(--accent)'"
              onmouseout="this.style.color='var(--text-primary)'">
              {{ club.name }}
            </router-link>
            <VoteButtons target-type="PLAYER_TEAM" :target-id="club.id" />
          </div>
          <div class="mt-3 flex gap-4 text-sm" style="color: var(--text-muted)">
            <span>🏆 UCL: {{ club.ucl }}</span>
            <span>📋 Liga: {{ club.league }}</span>
            <span>🥇 Cupă: {{ club.cup }}</span>
          </div>
          <div class="mt-2 flex items-center justify-between">
            <span style="color: var(--text-muted)" class="text-sm">
              ⚽ {{ club.playerCount }} jucatori
            </span>
            <router-link :to="`/clubs/${club.id}`"
              class="text-sm transition-colors"
              style="color: var(--text-muted)"
              onmouseover="this.style.color='var(--accent)'"
              onmouseout="this.style.color='var(--text-muted)'">
              Vezi club →
            </router-link>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<style scoped>
/* Păstrăm stilurile existente ale aplicației tale */
</style>