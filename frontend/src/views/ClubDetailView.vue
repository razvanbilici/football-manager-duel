<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { clubApi, transferApi } from '../api'
import { useAuthStore } from '../stores/auth'
import VoteButtons from '../components/VoteButtons.vue'
import PlayerCard from '../components/PlayerCard.vue'

const route = useRoute()
const auth = useAuthStore()
const club = ref(null)
const loading = ref(true)
const message = ref('')

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

onMounted(load)
</script>

<template>
  <div>
    <router-link to="/clubs" class="text-sm text-emerald-400 hover:underline mb-4 inline-block">
      ← Înapoi la cluburi
    </router-link>

    <div v-if="loading" class="text-slate-400">Se încarcă...</div>
    <template v-else-if="club">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h1 class="text-2xl font-bold">{{ club.name }}</h1>
          <div class="flex gap-4 mt-2 text-sm text-slate-400">
            <span>UCL: {{ club.ucl }}</span>
            <span>Liga: {{ club.league }}</span>
            <span>Cupă: {{ club.cup }}</span>
          </div>
        </div>
        <VoteButtons target-type="PLAYER_TEAM" :target-id="club.id" />
      </div>

      <p v-if="message" class="mb-4 text-sm" :class="message.includes('eșuat') ? 'text-red-400' : 'text-emerald-400'">
        {{ message }}
      </p>

      <h2 class="font-semibold mb-4">Lot jucători</h2>
      <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <PlayerCard
          v-for="player in club.players"
          :key="player.id"
          :player="player"
          :show-buy="auth.isLoggedIn"
          @buy="buyPlayer"
        />
      </div>
    </template>
  </div>
</template>
