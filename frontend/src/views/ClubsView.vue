<script setup>
import { ref, onMounted } from 'vue'
import { clubApi } from '../api'
import VoteButtons from '../components/VoteButtons.vue'

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
  <div>
    <h1 class="text-2xl font-bold mb-6">Cluburi</h1>

    <!-- Skeleton -->
    <div v-if="loading" class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
      <div v-for="i in 6" :key="i" class="skeleton h-32 rounded-xl" />
    </div>

    <div v-else class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
      <div
        v-for="club in clubs"
        :key="club.id"
        class="p-5 rounded-xl transition-all hover:scale-[1.01]"
        style="background: var(--bg-surface); border: 1px solid var(--border)"
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
        <router-link :to="`/clubs/${club.id}`"
          class="mt-2 text-sm inline-block transition-colors"
          style="color: var(--text-muted)"
          onmouseover="this.style.color='var(--accent)'"
          onmouseout="this.style.color='var(--text-muted)'">
          {{ club.players?.length || 0 }} jucători →
        </router-link>
      </div>
    </div>
  </div>
</template>
