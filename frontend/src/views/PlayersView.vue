<script setup>
import { ref, onMounted } from 'vue'
import { playerApi, transferApi } from '../api'
import { useAuthStore } from '../stores/auth'
import PlayerCard from '../components/PlayerCard.vue'

const auth = useAuthStore()
const players = ref([])
const search = ref('')
const loading = ref(true)
const message = ref('')

async function load() {
  loading.value = true
  try {
    const { data } = await playerApi.getAll(search.value || undefined)
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
  } catch (e) {
    message.value = e.response?.data?.message || 'Transfer eșuat'
  }
}

onMounted(load)
</script>

<template>
  <div>
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
      <h1 class="text-2xl font-bold">Jucători</h1>
      <form @submit.prevent="load" class="flex gap-2">
        <input
          v-model="search"
          placeholder="Caută jucător..."
          class="px-3 py-2 rounded-lg bg-slate-900 border border-slate-700 focus:border-emerald-500 outline-none"
        />
        <button type="submit" class="px-4 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 transition-colors">
          Caută
        </button>
      </form>
    </div>

    <p v-if="message" class="mb-4 text-sm" :class="message.includes('eșuat') ? 'text-red-400' : 'text-emerald-400'">
      {{ message }}
    </p>

    <div v-if="loading" class="text-slate-400">Se încarcă...</div>
    <div v-else class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
      <PlayerCard
        v-for="player in players"
        :key="player.id"
        :player="player"
        :show-buy="auth.isLoggedIn"
        @buy="buyPlayer"
      />
    </div>
  </div>
</template>
