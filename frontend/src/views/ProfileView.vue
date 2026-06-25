<template>
  <div class="profile-view">
    <h1 class="page-title">Profilul meu</h1>
    <p class="page-subtitle">Gestioneaza informatiile contului tau</p>

    <!-- Section 1: Edit profile -->
    <div class="card profile-section">
      <h2 class="section-title">Informatii cont</h2>
      <div class="form-group">
        <label class="form-label">Nume</label>
        <input v-model="name" class="input-field" type="text" placeholder="Numele tau" />
      </div>
      <div class="form-group">
        <label class="form-label">Email</label>
        <input v-model="email" class="input-field" type="email" placeholder="Email-ul tau" />
      </div>
      <div v-if="profileMsg" :class="['msg', profileMsgType]">{{ profileMsg }}</div>
      <button class="btn-primary" :disabled="profileLoading" @click="saveProfile">
        {{ profileLoading ? 'Se salveaza...' : 'Salveaza modificarile' }}
      </button>
    </div>

    <!-- Section 2: Change password -->
    <div class="card profile-section">
      <h2 class="section-title">Schimba parola</h2>
      <div class="form-group">
        <label class="form-label">Parola curenta</label>
        <input v-model="currentPassword" class="input-field" type="password" placeholder="Parola curenta" />
      </div>
      <div class="form-group">
        <label class="form-label">Parola noua</label>
        <input v-model="newPassword" class="input-field" type="password" placeholder="Parola noua" />
      </div>
      <div class="form-group">
        <label class="form-label">Confirma parola noua</label>
        <input v-model="confirmPassword" class="input-field" type="password" placeholder="Confirma parola noua" />
      </div>
      <div v-if="confirmPassword && newPassword !== confirmPassword" class="msg error">
        Parolele nu coincid
      </div>
      <div v-if="passwordMsg" :class="['msg', passwordMsgType]">{{ passwordMsg }}</div>
      <button
        class="btn-primary"
        :disabled="passwordLoading || (!!confirmPassword && newPassword !== confirmPassword)"
        @click="changePassword"
      >
        {{ passwordLoading ? 'Se schimba...' : 'Schimba parola' }}
      </button>
    </div>

    <!-- Section 3: Statistics -->
    <div class="card profile-section">
      <h2 class="section-title">Statisticile mele</h2>
      <div v-if="statsLoading" class="stats-grid">
        <div v-for="i in 7" :key="i" class="skeleton stat-skeleton" />
      </div>
      <div v-else-if="statsError" class="msg error">{{ statsError }}</div>
      <div v-else class="stats-grid">
        <div class="stat-card">
          <span class="stat-label">Transferuri efectuate</span>
          <span class="stat-value">{{ stats.totalTransfers }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-label">Total cheltuit</span>
          <span class="stat-value">{{ formatCompact(stats.totalSpent) }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-label">Total incasat</span>
          <span class="stat-value">{{ formatCompact(stats.totalEarned) }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-label">Jucatori in lot</span>
          <span class="stat-value">{{ stats.squadSize }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-label">Jucatori pe teren</span>
          <span class="stat-value">{{ stats.playersOnPitch }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-label">Valoare totala lot</span>
          <span class="stat-value">{{ formatCompact(stats.squadTotalValue) }}</span>
        </div>
        <div class="stat-card">
          <span class="stat-label">Voturi primite</span>
          <span class="stat-value">{{ stats.teamVotes }}</span>
        </div>
      </div>

      <!-- Pie chart: votes -->
      <div class="card mt-4">
        <h3 class="font-semibold mb-4" style="color: var(--text-primary)">Voturi echipa</h3>
        <div v-if="totalVotes === 0" class="text-center py-8" style="color: var(--text-muted)">
          Niciun vot primit inca
        </div>
        <div v-else class="flex flex-col sm:flex-row items-center gap-6">
          <svg width="160" height="160" viewBox="0 0 32 32" class="shrink-0">
            <circle r="16" cx="16" cy="16" fill="var(--danger)" opacity="0.7" />
            <circle
              r="16" cx="16" cy="16"
              fill="transparent"
              :stroke="'var(--success)'"
              stroke-width="32"
              :stroke-dasharray="`${upPct} ${100 - upPct}`"
              stroke-dashoffset="25"
              transform="rotate(-90 16 16)"
            />
            <circle r="10" cx="16" cy="16" fill="var(--bg-surface)" />
            <text x="16" y="15" text-anchor="middle"
              font-size="4" font-weight="bold" fill="var(--text-primary)">
              {{ upPct.toFixed(0) }}%
            </text>
            <text x="16" y="20" text-anchor="middle"
              font-size="2.5" fill="var(--text-muted)">pozitive</text>
          </svg>
          <div class="space-y-3 flex-1">
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-2">
                <div class="w-3 h-3 rounded-full" style="background: var(--success)"></div>
                <span class="text-sm" style="color: var(--text-primary)">Voturi pozitive (▲)</span>
              </div>
              <span class="font-bold" style="color: var(--success)">{{ upVotes }}</span>
            </div>
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-2">
                <div class="w-3 h-3 rounded-full" style="background: var(--danger)"></div>
                <span class="text-sm" style="color: var(--text-primary)">Voturi negative (▼)</span>
              </div>
              <span class="font-bold" style="color: var(--danger)">{{ downVotes }}</span>
            </div>
            <div class="flex items-center justify-between pt-2"
              style="border-top: 1px solid var(--border)">
              <span class="text-sm" style="color: var(--text-muted)">Total voturi</span>
              <span class="font-semibold" style="color: var(--text-primary)">{{ totalVotes }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { profileApi } from '../api'
import { formatPrice, formatCompact } from '../utils/player'

const auth = useAuthStore()

const name = ref(auth.user?.name ?? '')
const email = ref(auth.user?.email ?? '')
const profileLoading = ref(false)
const profileMsg = ref('')
const profileMsgType = ref('success')

const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const passwordLoading = ref(false)
const passwordMsg = ref('')
const passwordMsgType = ref('success')

const stats = ref(null)
const statsLoading = ref(true)
const statsError = ref('')

const upVotes = computed(() => stats.value?.teamVotes || 0)
const downVotes = computed(() => stats.value?.teamDownVotes || 0)
const totalVotes = computed(() => upVotes.value + downVotes.value)
const upPct = computed(() =>
  totalVotes.value === 0 ? 50 : (upVotes.value / totalVotes.value) * 100)

async function saveProfile() {
  profileMsg.value = ''
  profileLoading.value = true
  try {
    await profileApi.update({ name: name.value, email: email.value })
    await auth.fetchProfile()
    profileMsg.value = 'Profilul a fost actualizat cu succes.'
    profileMsgType.value = 'success'
  } catch (e) {
    profileMsg.value = e?.response?.data?.message ?? 'A aparut o eroare. Incearca din nou.'
    profileMsgType.value = 'error'
  } finally {
    profileLoading.value = false
  }
}

async function changePassword() {
  if (newPassword.value !== confirmPassword.value) return
  passwordMsg.value = ''
  passwordLoading.value = true
  try {
    await profileApi.changePassword({ currentPassword: currentPassword.value, newPassword: newPassword.value })
    passwordMsg.value = 'Parola a fost schimbata cu succes.'
    passwordMsgType.value = 'success'
    currentPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''
  } catch (e) {
    passwordMsg.value = e?.response?.data?.message ?? 'A aparut o eroare. Verifica parola curenta.'
    passwordMsgType.value = 'error'
  } finally {
    passwordLoading.value = false
  }
}

onMounted(async () => {
  try {
    const { data } = await profileApi.getStats()
    stats.value = data
  } catch {
    statsError.value = 'Nu s-au putut incarca statisticile.'
  } finally {
    statsLoading.value = false
  }
})
</script>

<style scoped>
.profile-view {
  max-width: 680px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.profile-section {
  margin-bottom: 1.5rem;
}

.section-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 1.25rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-label {
  display: block;
  font-size: 0.85rem;
  color: var(--text-muted);
  margin-bottom: 0.35rem;
}

.msg {
  font-size: 0.85rem;
  padding: 0.5rem 0.75rem;
  border-radius: 6px;
  margin-bottom: 1rem;
}

.msg.success {
  color: var(--success);
  background: rgba(63, 185, 80, 0.1);
  border: 1px solid rgba(63, 185, 80, 0.25);
}

.msg.error {
  color: var(--danger);
  background: rgba(248, 81, 73, 0.1);
  border: 1px solid rgba(248, 81, 73, 0.25);
}

.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}

.stat-card {
  background: var(--bg-elevated);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.stat-label {
  font-size: 0.8rem;
  color: var(--text-muted);
}

.stat-value {
  font-size: 1.3rem;
  font-weight: 700;
  color: var(--accent);
}

.stat-skeleton {
  height: 72px;
}
</style>
