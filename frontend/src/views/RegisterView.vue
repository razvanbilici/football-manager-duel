<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()

const name = ref('')
const email = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  loading.value = true
  try {
    await auth.register(name.value, email.value, password.value)
    router.push('/my-team')
  } catch (e) {
    error.value = e.response?.data?.message || 'Înregistrare eșuată'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="max-w-md mx-auto">
    <h1 class="text-2xl font-bold mb-6">Înregistrare</h1>
    <form @submit.prevent="submit" class="space-y-4">
      <div>
        <label class="block text-sm text-slate-400 mb-1">Nume</label>
        <input
          v-model="name"
          type="text"
          required
          class="w-full px-3 py-2 rounded-lg bg-slate-900 border border-slate-700 focus:border-emerald-500 outline-none"
        />
      </div>
      <div>
        <label class="block text-sm text-slate-400 mb-1">Email</label>
        <input
          v-model="email"
          type="email"
          required
          class="w-full px-3 py-2 rounded-lg bg-slate-900 border border-slate-700 focus:border-emerald-500 outline-none"
        />
      </div>
      <div>
        <label class="block text-sm text-slate-400 mb-1">Parolă</label>
        <input
          v-model="password"
          type="password"
          required
          minlength="4"
          class="w-full px-3 py-2 rounded-lg bg-slate-900 border border-slate-700 focus:border-emerald-500 outline-none"
        />
      </div>
      <p v-if="error" class="text-red-400 text-sm">{{ error }}</p>
      <button
        type="submit"
        :disabled="loading"
        class="w-full py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 disabled:opacity-50 transition-colors"
      >
        {{ loading ? 'Se încarcă...' : 'Creează cont' }}
      </button>
    </form>
    <p class="mt-4 text-sm text-slate-400 text-center">
      Ai deja cont?
      <router-link to="/login" class="text-emerald-400 hover:underline">Login</router-link>
    </p>
  </div>
</template>
