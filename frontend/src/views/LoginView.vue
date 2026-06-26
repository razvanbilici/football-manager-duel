<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const email = ref('')
const password = ref('')
const rememberMe = ref(false)
const showPassword = ref(false)

const error = ref('')
const loading = ref(false)

// Starea de atingere a câmpurilor (identică cu cea de la înregistrare)
const touched = reactive({
  email: false,
  password: false
})

// Validări reactive pentru feedback instantaneu în interfață
const errors = computed(() => {
  const e = {}

  if (!email.value.trim()) {
    e.email = 'Email-ul este obligatoriu'
  }

  if (!password.value) {
    e.password = 'Parola este obligatorie'
  }

  return e
})

const isFormValid = computed(() => Object.keys(errors.value).length === 0)

function touchAll() {
  touched.email = true
  touched.password = true
}

async function submit() {
  error.value = ''
  touchAll()

  if (!isFormValid.value) return

  loading.value = true
  try {
    await auth.login(email.value.trim(), password.value, rememberMe.value)
    router.push(route.query.redirect || '/my-team')
  } catch (e) {
    error.value = e.response?.data?.message || 'Email sau parolă incorectă.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="max-w-md mx-auto">
    <h1 class="text-2xl font-bold mb-6">Autentificare</h1>
    <form @submit.prevent="submit" class="space-y-4" novalidate>
      
      <!-- Câmp Email -->
      <div>
        <label class="block text-sm text-slate-400 mb-1">Email</label>
        <input
          v-model="email"
          type="email"
          autocomplete="email"
          @blur="touched.email = true"
          class="w-full px-3 py-2 rounded-lg bg-slate-900 border outline-none transition-colors"
          :class="touched.email && errors.email ? 'border-red-500 focus:border-red-500' : 'border-slate-700 focus:border-emerald-500'"
        />
        <p v-if="touched.email && errors.email" class="text-red-400 text-xs mt-1">{{ errors.email }}</p>
      </div>

      <div>
        <label class="block text-sm text-slate-400 mb-1">Parola</label>
        <div class="relative">
          <input
            v-model="password"
            :type="showPassword ? 'text' : 'password'"
            autocomplete="current-password"
            @blur="touched.password = true"
            class="w-full px-3 py-2 pr-16 rounded-lg bg-slate-900 border outline-none transition-colors "
            :class="touched.password && errors.password ? 'border-red-500 focus:border-red-500' : 'border-slate-700 focus:border-emerald-500'"
          />
          <button
            type="button"
            @click="showPassword = !showPassword"
            class="absolute right-2 top-1/2 -translate-y-1/2 text-xs text-slate-400 hover:text-emerald-400"
            tabindex="-1"
          >
            {{ showPassword ? 'Ascunde' : 'Arată' }}
          </button>
        </div>
        <p v-if="touched.password && errors.password" class="text-red-400 text-xs mt-1">{{ errors.password }}</p>
      </div>

      <!-- Ține-mă minte -->
      <div class="flex items-center">
        <label class="text-slate-400 text-xs flex items-center gap-2 cursor-pointer select-none">
          <input 
            type="checkbox" 
            v-model="rememberMe" 
            class="rounded bg-slate-900 border-slate-700 text-emerald-600 focus:ring-emerald-500" 
          />
          Ține-mă minte (30 zile)
        </label>
      </div>

      <!-- Alertă eroare globală de la server -->
      <p v-if="error" role="alert" class="text-red-400 text-sm">{{ error }}</p>

      <!-- Buton Login cu Loader Integrat -->
      <button
        type="submit"
        :disabled="loading"
        class="w-full py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center justify-center gap-2"
      >
        <svg v-if="loading" class="animate-spin h-4 w-4" viewBox="0 0 24 24" fill="none">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
        </svg>
        {{ loading ? 'Se încarcă...' : 'Login' }}
      </button>
    </form>
    
    <p class="mt-4 text-sm text-slate-400 text-center">
      Nu ai cont?
      <router-link to="/register" class="text-emerald-400 hover:underline">Înregistrează-te</router-link>
    </p>
  </div>
</template>