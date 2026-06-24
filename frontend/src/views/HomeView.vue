<script setup>
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
</script>

<template>
  <div class="space-y-8">
    <!-- Hero -->
    <section class="text-center py-16">
      <h1 class="text-5xl font-extrabold mb-4 tracking-tight"
        style="background: linear-gradient(135deg, var(--accent) 0%, #e6edf3 60%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text">
        Football Manager
      </h1>
      <p class="max-w-xl mx-auto text-base" style="color: var(--text-muted)">
        Construiește-ți echipa de vis, cumpără jucători de la cluburile mari
        și concurează cu comunitatea.
      </p>
      <div v-if="!auth.isLoggedIn" class="mt-8 flex gap-4 justify-center">
        <router-link
          to="/register"
          class="px-6 py-2.5 rounded-lg text-sm font-semibold transition-colors"
          style="background: var(--accent); color: #0d1117">
          Începe acum
        </router-link>
        <router-link
          to="/players"
          class="px-6 py-2.5 rounded-lg text-sm transition-colors"
          style="border: 1px solid var(--border); color: var(--text-primary)">
          Vezi jucătorii
        </router-link>
      </div>
      <div v-else class="mt-8">
        <router-link
          to="/my-team"
          class="px-6 py-2.5 rounded-lg text-sm font-semibold transition-colors"
          style="background: var(--accent); color: #0d1117">
          Mergi la echipa mea
        </router-link>
      </div>
    </section>

    <!-- Feature cards -->
    <section class="grid sm:grid-cols-3 gap-4">
      <router-link
        to="/players"
        class="p-6 rounded-xl transition-all hover:scale-[1.02]"
        style="background: var(--bg-surface); border: 1px solid var(--border)">
        <div class="text-2xl mb-3">👤</div>
        <h2 class="font-semibold mb-1" style="color: var(--text-primary)">Jucători</h2>
        <p class="text-sm" style="color: var(--text-muted)">Explorează și cumpără jucători de top</p>
      </router-link>
      <router-link
        to="/clubs"
        class="p-6 rounded-xl transition-all hover:scale-[1.02]"
        style="background: var(--bg-surface); border: 1px solid var(--border)">
        <div class="text-2xl mb-3">🏆</div>
        <h2 class="font-semibold mb-1" style="color: var(--text-primary)">Cluburi</h2>
        <p class="text-sm" style="color: var(--text-muted)">Barcelona, Real Madrid, Arsenal și altele</p>
      </router-link>
      <router-link
        to="/community"
        class="p-6 rounded-xl transition-all hover:scale-[1.02]"
        style="background: var(--bg-surface); border: 1px solid var(--border)">
        <div class="text-2xl mb-3">🌍</div>
        <h2 class="font-semibold mb-1" style="color: var(--text-primary)">Comunitate</h2>
        <p class="text-sm" style="color: var(--text-muted)">Votează cele mai bune echipe</p>
      </router-link>
    </section>
  </div>
</template>
