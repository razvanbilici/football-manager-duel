<script setup>
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
</script>

<template>
  <div class="space-y-8">
    <section class="text-center py-12">
      <h1 class="text-4xl font-bold text-emerald-400 mb-4">Football Manager</h1>
      <p class="text-slate-400 max-w-xl mx-auto">
        Construiește-ți echipa de vis, cumpără jucători de la cluburile mari
        și concurează cu comunitatea.
      </p>
      <div v-if="!auth.isLoggedIn" class="mt-6 flex gap-4 justify-center">
        <router-link
          to="/register"
          class="px-6 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 transition-colors"
        >
          Începe acum
        </router-link>
        <router-link
          to="/players"
          class="px-6 py-2 rounded-lg border border-slate-700 hover:border-emerald-700 transition-colors"
        >
          Vezi jucătorii
        </router-link>
      </div>
      <div v-else class="mt-6">
        <router-link
          to="/my-team"
          class="px-6 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 transition-colors"
        >
          Mergi la echipa mea
        </router-link>
      </div>
    </section>

    <section class="grid sm:grid-cols-3 gap-4">
      <router-link
        to="/players"
        class="p-6 rounded-xl bg-slate-900 border border-slate-800 hover:border-emerald-800 transition-colors"
      >
        <div class="text-2xl mb-2">👤</div>
        <h2 class="font-semibold mb-1">Jucători</h2>
        <p class="text-sm text-slate-400">Explorează și cumpără jucători de top</p>
      </router-link>
      <router-link
        to="/clubs"
        class="p-6 rounded-xl bg-slate-900 border border-slate-800 hover:border-emerald-800 transition-colors"
      >
        <div class="text-2xl mb-2">🏆</div>
        <h2 class="font-semibold mb-1">Cluburi</h2>
        <p class="text-sm text-slate-400">Barcelona, Real Madrid, Arsenal și altele</p>
      </router-link>
      <router-link
        to="/community"
        class="p-6 rounded-xl bg-slate-900 border border-slate-800 hover:border-emerald-800 transition-colors"
      >
        <div class="text-2xl mb-2">🌍</div>
        <h2 class="font-semibold mb-1">Comunitate</h2>
        <p class="text-sm text-slate-400">Votează cele mai bune echipe</p>
      </router-link>
    </section>
  </div>
</template>
