<script setup>
import { onMounted } from 'vue'
import Navbar from './components/Navbar.vue'
import { useAuthStore } from './stores/auth'

const auth = useAuthStore()

onMounted(() => {
  if (auth.isLoggedIn) auth.fetchProfile()
})
</script>

<template>
  <div class="min-h-screen flex flex-col">
    <Navbar />
    <main class="flex-1 max-w-6xl w-full mx-auto px-4 py-8">
      <router-view />
    </main>
    <footer class="border-t border-slate-800 py-4 text-center text-sm text-slate-500">
      Football Manager — Vue + Spring Boot
    </footer>
  </div>
</template>
