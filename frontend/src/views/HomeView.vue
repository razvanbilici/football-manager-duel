<script setup>
import { useAuthStore } from '../stores/auth'
import { ref, onMounted } from 'vue'
import { playerApi } from '../api'
import PlayerCard from '../components/PlayerCard.vue'
import homeVideo from '../assets/homevideo.mp4'

const auth = useAuthStore()
const recent = ref([])
const loadingRecent = ref(true)

async function loadRecent() {
  loadingRecent.value = true
  try {
    const { data } = await playerApi.getRecent()
    recent.value = data
  } catch (e) {
    recent.value = []
  } finally {
    loadingRecent.value = false
  }
}

onMounted(loadRecent)
</script>

<template>
  <div class="space-y-8">
    <!-- Hero -->
    <section class="relative text-center py-16 overflow-hidden rounded-2xl isolate">
      <!-- Video background -->
      <video
        :src="homeVideo"
        autoplay
        muted
        loop
        playsinline
        class="absolute inset-0 w-full h-full object-cover -z-20"
      ></video>

      <!-- Overlay -->
      <div class="absolute inset-0 bg-black/60 -z-10"></div>

      <!-- Football icon -->
      <span
        style="
          position: absolute;
          font-size: 12rem;
          opacity: 0.04;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          pointer-events: none;
          user-select: none;
          line-height: 1;
        "
      >
        ⚽
      </span>

      <h1
        class="text-5xl font-extrabold mb-4 tracking-tight"
        style="
          background: linear-gradient(135deg, var(--accent) 0%, #e6edf3 60%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        "
      >
        Football Manager
      </h1>

      <p class="max-w-xl mx-auto text-base text-white">
        Construiește-ți echipa de vis, cumpără jucători de la cluburile mari și
        concurează cu comunitatea.
      </p>

      <div
        v-if="!auth.isLoggedIn"
        class="mt-8 flex gap-4 justify-center flex-wrap"
      >
        <router-link
          to="/register"
          class="px-6 py-2.5 rounded-lg text-sm font-semibold transition-colors"
          style="background: var(--accent); color: #0d1117"
        >
          Începe acum
        </router-link>

        <router-link
          to="/players"
          class="px-6 py-2.5 rounded-lg text-sm border border-white/40 text-white hover:bg-white/10 transition-colors"
        >
          Vezi jucătorii
        </router-link>
      </div>

      <div v-else class="mt-8">
        <router-link
          to="/my-team"
          class="px-6 py-2.5 rounded-lg text-sm font-semibold transition-colors"
          style="background: var(--accent); color: #0d1117"
        >
          Mergi la echipa mea
        </router-link>
      </div>
    </section>

    <div
      style="
        height: 1px;
        background: linear-gradient(
          90deg,
          transparent,
          var(--accent),
          transparent
        );
        margin: 2rem 0;
      "
    ></div>

    <!-- Feature cards -->
    <section class="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <router-link
        to="/players"
        class="feature-card card card-hover p-6"
      >
        <div class="text-2xl mb-3">👤</div>
        <h2
          class="font-semibold mb-1"
          style="color: var(--text-primary)"
        >
          Jucători
        </h2>
        <p class="text-sm" style="color: var(--text-muted)">
          Explorează și cumpără jucători de top
        </p>
      </router-link>

      <router-link
        to="/clubs"
        class="feature-card card card-hover p-6"
      >
        <div class="text-2xl mb-3">🏆</div>
        <h2
          class="font-semibold mb-1"
          style="color: var(--text-primary)"
        >
          Cluburi
        </h2>
        <p class="text-sm" style="color: var(--text-muted)">
          Barcelona, Real Madrid, Arsenal și altele
        </p>
      </router-link>

      <router-link
        to="/community"
        class="feature-card card card-hover p-6"
      >
        <div class="text-2xl mb-3">🌍</div>
        <h2
          class="font-semibold mb-1"
          style="color: var(--text-primary)"
        >
          Comunitate
        </h2>
        <p class="text-sm" style="color: var(--text-muted)">
          Votează cele mai bune echipe
        </p>
      </router-link>

      <router-link
        to="/transfers"
        class="feature-card card card-hover p-6"
      >
        <div class="text-2xl mb-3">🔄</div>
        <h2
          class="font-semibold mb-1"
          style="color: var(--text-primary)"
        >
          Transferuri
        </h2>
        <p class="text-sm" style="color: var(--text-muted)">
          Cumpără, vinde și negociază jucători
        </p>
      </router-link>
    </section>

    <!-- Recent players -->
    <section
      v-if="!loadingRecent && recent.length"
      class="mt-8"
    >
      <div class="flex items-center justify-between mb-4">
        <div>
          <h2 class="text-2xl font-semibold">Jucători noi</h2>
          <p
            class="text-sm"
            style="color: var(--text-muted)"
          >
            Ultimii jucători adăugați
          </p>
        </div>

        <router-link
          to="/players"
          class="text-sm"
          style="color: var(--accent)"
        >
          Vezi toți
        </router-link>
      </div>

      <div class="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <PlayerCard
          v-for="p in recent"
          :key="p.id"
          :player="p"
        />
      </div>
    </section>
  </div>
</template>

<style scoped>
.feature-card {
  border-top: 2px solid transparent;
  transition:
    border-color 0.2s,
    transform 0.2s;
}

.feature-card:hover {
  border-top-color: var(--accent);
  transform: translateY(-4px);
}

video {
  filter: brightness(0.45);
}
</style>