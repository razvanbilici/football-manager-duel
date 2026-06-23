<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { formationApi, tacticApi, teamApi, transferApi } from '../api'
import { formatBudget, formatPrice } from '../utils/player'
import { isOnBench, isInLineup } from '../utils/pitchLayout'
import PitchView from '../components/PitchView.vue'
import PlayerAvatar from '../components/PlayerAvatar.vue'

const auth = useAuthStore()
const formations = ref([])
const tactics = ref([])
const myListings = ref([])
const loading = ref(true)
const saving = ref(false)
const assigning = ref(false)
const message = ref('')

const team = computed(() => auth.user?.userTeam)
const currentFormation = computed(() =>
  formations.value.find((f) => f.id === form.value.formationId) || team.value?.formation || null,
)

const form = ref({ name: '', formationId: null, tacticId: null })
const assignForm = ref({ playerId: '', slotNumber: 1 })
const sellForm = ref({ playerId: '', askingPrice: '' })

const listedPlayerIds = computed(() => new Set(myListings.value.map((l) => l.player.id)))

const benchPlayers = computed(() =>
  (team.value?.players || []).filter(isOnBench),
)

const lineupCount = computed(() =>
  (team.value?.players || []).filter(isInLineup).length,
)

const formationNeedsSave = computed(() =>
  form.value.formationId != null && form.value.formationId !== team.value?.formation?.id,
)

const slots = computed(() => currentFormation.value?.positions || [])

async function load() {
  loading.value = true
  try {
    await auth.fetchProfile()
    const [fRes, tRes] = await Promise.all([formationApi.getAll(), tacticApi.getAll()])
    formations.value = fRes.data
    tactics.value = tRes.data

    if (team.value) {
      form.value.name = team.value.name
      form.value.formationId = team.value.formation?.id ?? null
      form.value.tacticId = team.value.tactic?.id ?? null
    }

    try {
      const { data } = await transferApi.getMyListings()
      myListings.value = data
    } catch {
      myListings.value = []
    }
  } finally {
    loading.value = false
  }
}

async function ensureSettingsSaved() {
  if (!team.value) return
  const needsSave =
    form.value.name !== team.value.name
    || form.value.formationId !== team.value.formation?.id
    || form.value.tacticId !== team.value.tactic?.id
  if (!needsSave) return
  await teamApi.update(team.value.id, {
    name: form.value.name,
    formationId: form.value.formationId,
    tacticId: form.value.tacticId,
  })
  await auth.fetchProfile()
}

async function saveSettings() {
  if (!team.value) return
  saving.value = true
  message.value = ''
  try {
    await ensureSettingsSaved()
    message.value = 'Setări salvate!'
  } catch (e) {
    message.value = e.response?.data?.message || 'Eroare la salvare'
  } finally {
    saving.value = false
  }
}

function selectBenchPlayer(playerId) {
  assignForm.value.playerId = String(playerId)
}

function prepareSell(playerId) {
  sellForm.value.playerId = String(playerId)
  sellForm.value.askingPrice = ''
}

async function assignPlayer() {
  if (!team.value || !assignForm.value.playerId) {
    message.value = 'Selectează un jucător de pe bancă'
    return
  }
  if (!form.value.formationId) {
    message.value = 'Selectează și salvează o formație mai întâi'
    return
  }
  assigning.value = true
  message.value = ''
  try {
    await ensureSettingsSaved()
    await teamApi.addPlayer(
      team.value.id,
      Number(assignForm.value.playerId),
      assignForm.value.slotNumber,
    )
    await auth.fetchProfile()
    message.value = 'Jucător plasat pe teren!'
    assignForm.value.playerId = ''
  } catch (e) {
    message.value = e.response?.data?.message || 'Nu s-a putut plasa jucătorul'
  } finally {
    assigning.value = false
  }
}

async function removeFromLineup(playerId) {
  if (!team.value) return
  try {
    await teamApi.removePlayer(team.value.id, playerId)
    await auth.fetchProfile()
    message.value = 'Jucător scos de pe teren (pe bancă)'
  } catch (e) {
    message.value = e.response?.data?.message || 'Eroare'
  }
}

async function listForSale() {
  if (!sellForm.value.playerId || !sellForm.value.askingPrice) {
    message.value = 'Selectează jucătorul și prețul'
    return
  }
  message.value = ''
  try {
    await transferApi.createListing(
      Number(sellForm.value.playerId),
      Number(sellForm.value.askingPrice),
    )
    message.value = 'Jucător listat pe piață!'
    sellForm.value = { playerId: '', askingPrice: '' }
    await load()
  } catch (e) {
    message.value = e.response?.data?.message || 'Listare eșuată'
  }
}

async function cancelListing(id) {
  try {
    await transferApi.cancelListing(id)
    message.value = 'Listare anulată'
    await load()
  } catch (e) {
    message.value = e.response?.data?.message || 'Eroare'
  }
}

function onSlotClick(item) {
  if (item.player) return
  assignForm.value.slotNumber = item.slotNumber
  document.getElementById('assign-section')?.scrollIntoView({ behavior: 'smooth' })
}

async function submitTeam() {
  if (!team.value) return
  try {
    await ensureSettingsSaved()
    await teamApi.submit(team.value.id)
    await auth.fetchProfile()
    message.value = 'Echipa a fost publicată în comunitate!'
  } catch (e) {
    message.value = e.response?.data?.message || 'Publicare eșuată'
  }
}

function isError(msg) {
  return /eroare|eșuat|nu s-a putut|selectează jucătorul/i.test(msg)
}

const squadPlayers = computed(() => team.value?.players || [])

onMounted(load)
</script>

<template>
  <div>
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
      <h1 class="text-2xl font-bold">Echipa mea</h1>
      <div v-if="auth.user" class="text-emerald-400 font-medium">
        Buget: {{ formatBudget(auth.user.budget) }}
      </div>
    </div>

    <div v-if="loading" class="text-slate-400">Se încarcă...</div>
    <template v-else-if="team">
      <p
        v-if="message"
        class="mb-4 text-sm"
        :class="isError(message) ? 'text-red-400' : 'text-emerald-400'"
      >
        {{ message }}
      </p>

      <!-- Settings -->
      <section class="mb-8 p-5 rounded-xl bg-slate-900 border border-slate-800">
        <h2 class="font-semibold mb-4">Setări echipă</h2>
        <div class="grid sm:grid-cols-3 gap-4">
          <div>
            <label class="block text-sm text-slate-400 mb-1">Nume echipă</label>
            <input
              v-model="form.name"
              class="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-700 focus:border-emerald-500 outline-none"
            />
          </div>
          <div>
            <label class="block text-sm text-slate-400 mb-1">Formație</label>
            <select
              v-model="form.formationId"
              class="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-700 focus:border-emerald-500 outline-none"
            >
              <option :value="null">— Selectează —</option>
              <option v-for="f in formations" :key="f.id" :value="f.id">{{ f.name }}</option>
            </select>
          </div>
          <div>
            <label class="block text-sm text-slate-400 mb-1">Tactică</label>
            <select
              v-model="form.tacticId"
              class="w-full px-3 py-2 rounded-lg bg-slate-950 border border-slate-700 focus:border-emerald-500 outline-none"
            >
              <option :value="null">— Selectează —</option>
              <option v-for="t in tactics" :key="t.id" :value="t.id">{{ t.details }}</option>
            </select>
          </div>
        </div>
        <p v-if="formationNeedsSave" class="mt-3 text-sm text-amber-400">
          Salvează formația înainte de a plasa jucători pe teren.
        </p>
        <button
          @click="saveSettings"
          :disabled="saving"
          class="mt-4 px-4 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 disabled:opacity-50 transition-colors"
        >
          {{ saving ? 'Se salvează...' : 'Salvează' }}
        </button>
      </section>

      <!-- Pitch -->
      <section class="mb-8">
        <div class="flex items-center justify-between mb-4">
          <h2 class="font-semibold">Formație pe teren</h2>
          <span class="text-sm text-slate-400">{{ lineupCount }}/11 pe teren</span>
        </div>
        <p class="text-sm text-slate-500 mb-3">
          Poți plasa orice jucător pe orice poziție. Click pe slot gol sau folosește formularul de mai jos.
        </p>
        <div class="max-w-md mx-auto">
          <PitchView
            :formation="currentFormation"
            :players="team.players"
            interactive
            @slot-click="onSlotClick"
            @remove="removeFromLineup"
          />
        </div>
      </section>

      <!-- Squad -->
      <section class="mb-8 p-5 rounded-xl bg-slate-900 border border-slate-800">
        <h2 class="font-semibold mb-4">Lot jucători ({{ squadPlayers.length }})</h2>
        <div v-if="squadPlayers.length === 0" class="text-sm text-slate-500">
          Niciun jucător. Cumpără jucători de la cluburi.
        </div>
        <div v-else class="flex flex-wrap gap-3">
          <div
            v-for="tp in squadPlayers"
            :key="tp.id"
            class="flex flex-col gap-2 px-3 py-2 rounded-lg bg-slate-950 border border-slate-700 min-w-[180px]"
          >
            <div class="flex items-center gap-2">
              <PlayerAvatar :position="tp.player.position" size="sm" />
              <div>
                <div class="text-sm font-medium">{{ tp.player.name }}</div>
                <div class="text-xs text-slate-400">
                  {{ tp.player.position }}
                  <span v-if="isInLineup(tp)"> · pe teren #{{ tp.slotNumber }}</span>
                  <span v-else-if="listedPlayerIds.has(tp.player.id)" class="text-amber-400"> · la vânzare</span>
                  <span v-else> · bancă</span>
                </div>
              </div>
            </div>
            <div class="flex gap-1">
              <button
                v-if="isInLineup(tp)"
                @click="removeFromLineup(tp.player.id)"
                class="text-xs px-2 py-1 rounded bg-slate-800 hover:bg-slate-700"
              >
                Scoate de pe teren
              </button>
              <button
                v-if="!listedPlayerIds.has(tp.player.id)"
                @click="prepareSell(tp.player.id)"
                class="text-xs px-2 py-1 rounded bg-amber-800 hover:bg-amber-700"
              >
                Vinde
              </button>
              <button
                v-if="!isInLineup(tp) && !listedPlayerIds.has(tp.player.id)"
                @click="selectBenchPlayer(tp.player.id)"
                class="text-xs px-2 py-1 rounded bg-emerald-800 hover:bg-emerald-700"
              >
                Plasează
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- Sell -->
      <section
        v-if="sellForm.playerId"
        class="mb-8 p-5 rounded-xl bg-slate-900 border border-amber-900/50"
      >
        <h2 class="font-semibold mb-4">Pune jucător la vânzare</h2>
        <p class="text-sm text-slate-400 mb-3">
          Jucătorul va apărea pe piața de transferuri. Alți utilizatori pot trimite oferte pe care le poți accepta sau refuza.
        </p>
        <div class="flex flex-col sm:flex-row gap-3">
          <input
            v-model="sellForm.askingPrice"
            type="number"
            min="1"
            placeholder="Preț cerut (€)"
            class="flex-1 px-3 py-2 rounded-lg bg-slate-950 border border-slate-700 focus:border-amber-500 outline-none"
          />
          <button
            @click="listForSale"
            class="px-4 py-2 rounded-lg bg-amber-600 hover:bg-amber-500"
          >
            Listează
          </button>
          <button
            @click="sellForm = { playerId: '', askingPrice: '' }"
            class="px-4 py-2 rounded-lg bg-slate-800 hover:bg-slate-700"
          >
            Anulează
          </button>
        </div>
      </section>

      <!-- My listings -->
      <section v-if="myListings.length" class="mb-8 p-5 rounded-xl bg-slate-900 border border-slate-800">
        <h2 class="font-semibold mb-4">Listări active</h2>
        <div class="space-y-2">
          <div
            v-for="listing in myListings"
            :key="listing.id"
            class="flex justify-between items-center p-3 rounded-lg bg-slate-950"
          >
            <span>{{ listing.player.name }} — {{ formatPrice(listing.askingPrice) }}</span>
            <button
              @click="cancelListing(listing.id)"
              class="text-xs px-2 py-1 rounded bg-red-800 hover:bg-red-700"
            >
              Anulează
            </button>
          </div>
        </div>
      </section>

      <!-- Assign -->
      <section
        id="assign-section"
        class="mb-8 p-5 rounded-xl bg-slate-900 border border-slate-800"
      >
        <h2 class="font-semibold mb-4">Plasează jucător pe teren</h2>
        <p class="text-sm text-slate-400 mb-4">
          Orice jucător poate fi plasat pe orice slot din formație.
        </p>
        <div v-if="!form.formationId" class="text-sm text-amber-400 mb-4">
          Selectează o formație din setările de mai sus.
        </div>
        <div v-else-if="benchPlayers.length === 0" class="text-sm text-slate-500 mb-4">
          Niciun jucător disponibil pe bancă (toți sunt pe teren sau la vânzare).
        </div>
        <div v-else class="flex flex-col sm:flex-row gap-3">
          <select
            v-model="assignForm.playerId"
            class="flex-1 px-3 py-2 rounded-lg bg-slate-950 border border-slate-700 focus:border-emerald-500 outline-none"
          >
            <option value="">— Jucător —</option>
            <option
              v-for="tp in benchPlayers.filter((p) => !listedPlayerIds.has(p.player.id))"
              :key="tp.player.id"
              :value="String(tp.player.id)"
            >
              {{ tp.player.name }} ({{ tp.player.position }})
            </option>
          </select>
          <select
            v-model.number="assignForm.slotNumber"
            :disabled="!slots.length"
            class="w-48 px-3 py-2 rounded-lg bg-slate-950 border border-slate-700 focus:border-emerald-500 outline-none disabled:opacity-50"
          >
            <option v-for="slot in slots" :key="slot.slotNumber" :value="slot.slotNumber">
              Slot {{ slot.slotNumber }} ({{ slot.position }})
            </option>
          </select>
          <button
            @click="assignPlayer"
            :disabled="assigning || !assignForm.playerId || !slots.length"
            class="px-4 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 disabled:opacity-50 transition-colors"
          >
            {{ assigning ? 'Se plasează...' : 'Plasează' }}
          </button>
        </div>
      </section>

      <!-- Submit -->
      <section class="p-5 rounded-xl bg-slate-900 border border-slate-800">
        <h2 class="font-semibold mb-2">Publică echipa</h2>
        <p class="text-sm text-slate-400 mb-4">
          Ai nevoie de 11 jucători plasați pe teren și o formație selectată.
        </p>
        <div class="flex items-center gap-4">
          <button
            @click="submitTeam"
            :disabled="team.submitted || lineupCount < 11"
            class="px-4 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 disabled:opacity-50 transition-colors"
          >
            {{ team.submitted ? 'Deja publicată' : 'Publică echipa' }}
          </button>
          <span v-if="team.submitted" class="text-emerald-400 text-sm">
            ▲ {{ team.votes }} voturi
          </span>
        </div>
      </section>
    </template>
  </div>
</template>
