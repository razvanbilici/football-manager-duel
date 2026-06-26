<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()

const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')

const showPassword = ref(false)
const showConfirmPassword = ref(false)

const error = ref('')
const loading = ref(false)

const touched = reactive({
  name: false,
  email: false,
  password: false,
  confirmPassword: false
})

const nameInput = ref(null)
onMounted(() => {
  nameInput.value?.focus()
})

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

const errors = computed(() => {
  const e = {}

  if (!name.value.trim()) {
    e.name = 'Numele este obligatoriu'
  } else if (name.value.trim().length < 5) {
    e.name = 'Numele trebuie să aibă cel puțin 5 caractere'
  }

  if (!email.value.trim()) {
    e.email = 'Email-ul este obligatoriu'
  } else if (!EMAIL_REGEX.test(email.value.trim())) {
    e.email = 'Adresa de email nu este validă'
  }

  if (!password.value) {
    e.password = 'Parola este obligatorie'
  } else if (password.value.length < 6) {
    e.password = 'Parola trebuie să aibă cel puțin 6 caractere'
  }

  if (!confirmPassword.value) {
    e.confirmPassword = 'Confirmă parola'
  } else if (confirmPassword.value !== password.value) {
    e.confirmPassword = 'Parolele nu coincid'
  }

  return e
})

const isFormValid = computed(() => Object.keys(errors.value).length === 0)

function touchAll() {
  touched.name = true
  touched.email = true
  touched.password = true
  touched.confirmPassword = true
}

async function submit() {
  error.value = ''
  touchAll()

  if (!isFormValid.value) return

  loading.value = true
  try {
    await auth.register(name.value.trim(), email.value.trim(), password.value)
    router.push('/my-team')
  } catch (e) {
    if (!e.response) {
      error.value = 'Nu am putut contacta serverul. Verifică conexiunea la internet.'
    } else if (e.response.status === 409) {
      error.value = e.response.data?.message || 'Există deja un cont cu acest email.'
    } else {
      error.value = e.response.data?.message || 'Înregistrare eșuată. Încearcă din nou.'
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="max-w-md mx-auto">
    <h1 class="text-2xl font-bold mb-6">Înregistrare</h1>
    <form @submit.prevent="submit" class="space-y-4" novalidate>
      <div>
        <label class="block text-sm text-slate-400 mb-1">Nume</label>
        <input
          ref="nameInput"
          v-model="name"
          type="text"
          autocomplete="name"
          @blur="touched.name = true"
          class="w-full px-3 py-2 rounded-lg bg-slate-900 border outline-none transition-colors"
          :class="touched.name && errors.name ? 'border-red-500 focus:border-red-500' : 'border-slate-700 focus:border-emerald-500'"
        />
        <p v-if="touched.name && errors.name" class="text-red-400 text-xs mt-1">{{ errors.name }}</p>
      </div>

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
        <label class="block text-sm text-slate-400 mb-1">Parolă</label>
        <div class="relative">
          <input
            v-model="password"
            :type="showPassword ? 'text' : 'password'"
            autocomplete="new-password"
            @blur="touched.password = true"
            class="w-full px-3 py-2 pr-16 rounded-lg bg-slate-900 border outline-none transition-colors"
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
        <p
          class="text-xs mt-1"
          :class="touched.password && errors.password ? 'text-red-400' : 'text-slate-500'"
        >
          {{ touched.password && errors.password ? errors.password : 'Minim 6 caractere' }}
        </p>
      </div>

      <div>
        <label class="block text-sm text-slate-400 mb-1">Confirmă parola</label>
        <div class="relative">
          <input
            v-model="confirmPassword"
            :type="showConfirmPassword ? 'text' : 'password'"
            autocomplete="new-password"
            @blur="touched.confirmPassword = true"
            class="w-full px-3 py-2 pr-16 rounded-lg bg-slate-900 border outline-none transition-colors"
            :class="touched.confirmPassword && errors.confirmPassword ? 'border-red-500 focus:border-red-500' : 'border-slate-700 focus:border-emerald-500'"
          />
          <button
            type="button"
            @click="showConfirmPassword = !showConfirmPassword"
            class="absolute right-2 top-1/2 -translate-y-1/2 text-xs text-slate-400 hover:text-emerald-400"
            tabindex="-1"
          >
            {{ showConfirmPassword ? 'Ascunde' : 'Arată' }}
          </button>
        </div>
        <p v-if="touched.confirmPassword && errors.confirmPassword" class="text-red-400 text-xs mt-1">
          {{ errors.confirmPassword }}
        </p>
      </div>

      <p v-if="error" role="alert" class="text-red-400 text-sm">{{ error }}</p>

      <button
        type="submit"
        :disabled="loading"
        class="w-full py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex items-center justify-center gap-2"
      >
        <svg v-if="loading" class="animate-spin h-4 w-4" viewBox="0 0 24 24" fill="none">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
        </svg>
        {{ loading ? 'Se încarcă...' : 'Creează cont' }}
      </button>
    </form>
    <p class="mt-4 text-sm text-slate-400 text-center">
      Ai deja cont?
      <router-link to="/login" class="text-emerald-400 hover:underline">Login</router-link>
    </p>
  </div>
</template>