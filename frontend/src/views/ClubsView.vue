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

    <div v-if="loading" class="text-slate-400">Se încarcă...</div>
    <div v-else class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
      <div
        v-for="club in clubs"
        :key="club.id"
        class="p-5 rounded-xl bg-slate-900 border border-slate-800 hover:border-emerald-800 transition-colors"
      >
        <div class="flex justify-between items-start">
          <router-link :to="`/clubs/${club.id}`" class="font-semibold text-lg hover:text-emerald-400">
            {{ club.name }}
          </router-link>
          <VoteButtons target-type="PLAYER_TEAM" :target-id="club.id" />
        </div>
        <div class="mt-3 flex gap-4 text-sm text-slate-400">
          <span>🏆 UCL: {{ club.ucl }}</span>
          <span>📋 Liga: {{ club.league }}</span>
          <span>🥇 Cupă: {{ club.cup }}</span>
        </div>
        <router-link :to="`/clubs/${club.id}`" class="mt-2 text-sm text-slate-500 hover:text-emerald-400 inline-block">
          {{ club.players?.length || 0 }} jucători →
        </router-link>
      </div>
    </div>
  </div>
</template>
