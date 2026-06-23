<script setup>
import { ref, onMounted } from 'vue'
import { teamApi } from '../api'
import VoteButtons from '../components/VoteButtons.vue'
import PitchView from '../components/PitchView.vue'

const teams = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const { data } = await teamApi.getSubmitted()
    teams.value = data
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold mb-2">Echipe din comunitate</h1>
    <p class="text-slate-400 mb-6">Echipele publicate de utilizatori, sortate după voturi.</p>

    <div v-if="loading" class="text-slate-400">Se încarcă...</div>
    <div v-else-if="teams.length === 0" class="text-slate-400">
      Nicio echipă publicată încă. Fii primul!
    </div>
    <div v-else class="space-y-8">
      <div
        v-for="(team, index) in teams"
        :key="team.id"
        class="p-5 rounded-xl bg-slate-900 border border-slate-800"
      >
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-4">
          <div>
            <div class="flex items-center gap-3">
              <span class="text-2xl font-bold text-emerald-600">#{{ index + 1 }}</span>
              <h2 class="text-lg font-semibold">{{ team.name }}</h2>
            </div>
            <div class="mt-1 flex gap-3 text-sm text-slate-400">
              <span v-if="team.formation">{{ team.formation.name }}</span>
              <span v-if="team.tactic">{{ team.tactic.details }}</span>
            </div>
          </div>
          <div class="flex items-center gap-4">
            <span class="text-emerald-400 font-medium">▲ {{ team.votes }}</span>
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
</template>
