<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { userApi } from '../api'
import PitchView from '../components/PitchView.vue'

const route = useRoute()
const user = ref(null)
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const { data } = await userApi.getById(route.params.id)
    user.value = data
  } catch {
    error.value = 'Utilizatorul nu a fost gasit.'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="max-w-2xl mx-auto py-8 px-4">
    <div v-if="loading" class="space-y-4">
      <div class="skeleton h-10 rounded-xl w-48" />
      <div class="skeleton h-64 rounded-xl" />
    </div>

    <div v-else-if="error" class="py-16 text-center" style="color: var(--danger)">
      {{ error }}
    </div>

    <template v-else-if="user">
      <!-- Header -->
      <div class="card mb-6 p-5">
        <h1 class="text-2xl font-bold mb-1" style="color: var(--text-primary)">{{ user.name }}</h1>
        <div v-if="user.favouritePlayerTeamName" class="text-sm" style="color: var(--text-muted)">
          Club favorit: <span style="color: var(--accent)">{{ user.favouritePlayerTeamName }}</span>
        </div>
      </div>

      <!-- Team info -->
      <div v-if="user.userTeam" class="card p-5">
        <div class="flex items-center justify-between mb-4">
          <div>
            <h2 class="text-lg font-semibold" style="color: var(--text-primary)">{{ user.userTeam.name }}</h2>
            <div class="flex gap-3 text-sm mt-1" style="color: var(--text-muted)">
              <span v-if="user.userTeam.formation">{{ user.userTeam.formation.name }}</span>
              <span v-if="user.userTeam.tactic">{{ user.userTeam.tactic.details }}</span>
            </div>
          </div>
          <span class="font-medium" style="color: var(--accent)">▲ {{ user.userTeam.votes }}</span>
        </div>

        <div v-if="!user.userTeam.submitted" class="py-8 text-center text-sm" style="color: var(--text-muted)">
          Acest utilizator nu a publicat o echipa
        </div>
        <div v-else class="max-w-sm mx-auto">
          <PitchView
            :formation="user.userTeam.formation"
            :players="user.userTeam.players"
          />
        </div>
      </div>

      <div v-else class="py-8 text-center text-sm" style="color: var(--text-muted)">
        Acest utilizator nu a publicat o echipa
      </div>
    </template>
  </div>
</template>
