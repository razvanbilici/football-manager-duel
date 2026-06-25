<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>

<template>
  <div class="flex flex-col items-center justify-center min-h-[60vh] text-center px-4">
    <div class="text-8xl font-black mb-4" style="color: var(--accent); opacity: 0.15">
      404
    </div>
    <div class="text-6xl mb-6">🏟️</div>
    <h1 class="text-2xl font-bold mb-2" style="color: var(--text-primary)">
      Pagina nu a fost gasita
    </h1>
    <p class="text-sm mb-8 max-w-sm" style="color: var(--text-muted)">
      Pagina pe care o cauti nu exista sau a fost mutata.
    </p>
    <div class="flex gap-3">
      <button @click="router.back()" class="btn-secondary">
        ← Inapoi
      </button>
      <router-link to="/" class="btn-primary" style="text-decoration: none; padding: 0.5rem 1.25rem; border-radius: 0.5rem; font-weight: 600; font-size: 0.875rem; background: var(--accent); color: #0d1117">
        Acasa
      </router-link>
    </div>
  </div>
</template>
