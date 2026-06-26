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
    <footer class="border-t border-slate-800 py-4 text-center text-slate-500">
      Proiect AWBD: Ciurea Claudiu (406), Turcu Andrei (405), Bilici Razvan (405)
    </footer>
  </div>
</template>
