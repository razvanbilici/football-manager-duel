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
const dragOverSlot = ref(null)

const team = computed(() => auth.user?.userTeam)
const currentFormation = computed(() =>
  formations.value.find((f) => f.id === form.value.formationId) || team.value?.formation || null,
)

const form = ref({ name: '', formationId: null, tacticId: null })
const assignForm = ref({ playerId: '', slotNumber: 1 })
const sellForm = ref({ playerId: '', askingPrice: '' })

const listedPlayerIds = computed(() => new Set(myListings.value.map((l) => l.player.id)))
const benchPlayers = computed(() => (team.value?.players || []).filter(isOnBench))
const lineupCount = computed(() => (team.value?.players || []).filter(isInLineup).length)
const formationNeedsSave = computed(() =>
  form.value.formationId != null && form.value.formationId !== team.value?.formation?.id,
)
const slots = computed(() => currentFormation.value?.positions || [])
const squadPlayers = computed(() => team.value?.players || [])

const totalValue = computed(() =>
  squadPlayers.value.reduce((sum, tp) => sum + Number(tp.player?.price || 0), 0),
)

function positionBg(position) {
  const p = (position || '').toUpperCase()
  if (p === 'GK') return 'background:#78350f;color:#fcd34d'
  if (['CB','LB','RB','LWB','RWB'].includes(p)) return 'background:#1e3a5f;color:#60a5fa'
  if (['CDM','CM','CAM','LM','RM'].includes(p)) return 'background:#14401e;color:#4ade80'
  return 'background:#4c1d1d;color:#f87171'
}

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
    } catch { myListings.value = [] }
  } finally {
    loading.value = false
  }
}

async function ensureSettingsSaved() {
  if (!team.value) return
  const needsSave =
    form.value.name !== team.value.name ||
    form.value.formationId !== team.value.formation?.id ||
    form.value.tacticId !== team.value.tactic?.id
  if (!needsSave) return
  await teamApi.update(team.value.id, {
    name: form.value.name,
    formationId: form.value.formationId,
    tacticId: form.value.tacticId,
  })
  await auth.fetchProfile()
}

async function saveSettings() {
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

function onDragStart(event, playerId) {
  event.dataTransfer.setData('playerId', String(playerId))
  event.dataTransfer.effectAllowed = 'move'
}

async function onDropOnSlot(event, slotNumber) {
  dragOverSlot.value = null
  const playerId = event.dataTransfer.getData('playerId')
  if (!playerId || !team.value) return
  if (!form.value.formationId) {
    message.value = 'Selectează și salvează o formație mai întâi'
    return
  }
  assigning.value = true
  message.value = ''
  try {
    await ensureSettingsSaved()
    await teamApi.addPlayer(team.value.id, Number(playerId), slotNumber)
    await auth.fetchProfile()
    message.value = 'Jucător plasat pe teren!'
  } catch (e) {
    message.value = e.response?.data?.message || 'Nu s-a putut plasa jucătorul'
  } finally {
    assigning.value = false
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
    message.value = 'Selectează un jucător'
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
    await teamApi.addPlayer(team.value.id, Number(assignForm.value.playerId), assignForm.value.slotNumber)
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
    message.value = 'Jucător scos de pe teren'
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
    await transferApi.createListing(Number(sellForm.value.playerId), Number(sellForm.value.askingPrice))
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

function onSlotClick(item) {
  if (item.player) return
  assignForm.value.slotNumber = item.slotNumber
  document.getElementById('assign-section')?.scrollIntoView({ behavior: 'smooth' })
}

function isError(msg) {
  return /eroare|eșuat|nu s-a putut|selectează/i.test(msg)
}

onMounted(load)
</script>

<template>
  <div>
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
      <h1 class="text-2xl font-bold">Echipa mea</h1>
      <div v-if="auth.user" class="text-sm font-medium" style="color: var(--accent)">
        Buget: {{ formatBudget(auth.user.budget) }}
      </div>
    </div>

    <!-- Loading skeleton -->
    <div v-if="loading" class="space-y-4">
      <div class="skeleton h-32 rounded-xl" />
      <div class="grid lg:grid-cols-[1fr_300px] gap-6">
        <div class="skeleton rounded-xl" style="aspect-ratio: 2/3; max-height: 480px" />
        <div class="space-y-2">
          <div v-for="i in 6" :key="i" class="skeleton h-16 rounded-lg" />
        </div>
      </div>
    </div>

    <template v-else-if="team">
      <p v-if="message" class="mb-4 text-sm"
        :style="{ color: isError(message) ? 'var(--danger)' : 'var(--success)' }">
        {{ message }}
      </p>

      <!-- Settings -->
      <section class="mb-6 p-5 rounded-xl" style="background: var(--bg-surface); border: 1px solid var(--border)">
        <h2 class="font-semibold mb-4">Setări echipă</h2>
        <div class="grid sm:grid-cols-3 gap-4">
          <div>
            <label class="block text-xs mb-1" style="color: var(--text-muted)">Nume echipă</label>
            <input v-model="form.name" class="w-full px-3 py-2 rounded-lg text-sm outline-none"
              style="background: var(--bg-elevated); border: 1px solid var(--border); color: var(--text-primary)" />
          </div>
          <div>
            <label class="block text-xs mb-1" style="color: var(--text-muted)">Formație</label>
            <select v-model="form.formationId" class="w-full px-3 py-2 rounded-lg text-sm outline-none"
              style="background: var(--bg-elevated); border: 1px solid var(--border); color: var(--text-primary)">
              <option :value="null">— Selectează —</option>
              <option v-for="f in formations" :key="f.id" :value="f.id">{{ f.name }}</option>
            </select>
          </div>
          <div>
            <label class="block text-xs mb-1" style="color: var(--text-muted)">Tactică</label>
            <select v-model="form.tacticId" class="w-full px-3 py-2 rounded-lg text-sm outline-none"
              style="background: var(--bg-elevated); border: 1px solid var(--border); color: var(--text-primary)">
              <option :value="null">— Selectează —</option>
              <option v-for="t in tactics" :key="t.id" :value="t.id">{{ t.details }}</option>
            </select>
          </div>
        </div>
        <p v-if="formationNeedsSave" class="mt-3 text-xs" style="color: var(--warning)">
          Salvează formația înainte de a plasa jucători pe teren.
        </p>
        <button @click="saveSettings" :disabled="saving"
          class="mt-4 px-4 py-2 rounded-lg text-sm font-semibold disabled:opacity-50 transition-colors"
          style="background: var(--accent); color: #0d1117">
          {{ saving ? 'Se salvează...' : 'Salvează' }}
        </button>
      </section>

      <!-- Pitch + Squad side by side -->
      <section class="mb-6">
        <div class="flex items-center justify-between mb-4">
          <h2 class="font-semibold">Formație pe teren</h2>
          <div class="flex items-center gap-4 text-sm" style="color: var(--text-muted)">
            <span>{{ lineupCount }}/11 pe teren</span>
          </div>
        </div>

        <div class="grid lg:grid-cols-[1fr_300px] gap-6 items-start">
          <!-- Pitch -->
          <div class="max-w-sm mx-auto lg:mx-0 w-full">
            <PitchView
              :formation="currentFormation"
              :players="team.players"
              :drag-over-slot="dragOverSlot"
              interactive
              @slot-click="onSlotClick"
              @remove="removeFromLineup"
              @drop-on-slot="onDropOnSlot"
              @dragover-slot="dragOverSlot = $event"
              @dragleave-slot="dragOverSlot = null"
            />
            <p class="text-xs mt-2 text-center" style="color: var(--text-muted)">
              Trage jucătorii din panoul din dreapta pe pozițiile dorite
            </p>
          </div>

          <!-- Squad panel -->
          <div class="rounded-xl p-4" style="background: var(--bg-surface); border: 1px solid var(--border)">
            <div class="flex items-center justify-between mb-3">
              <h3 class="font-semibold text-sm">Lotul meu ({{ squadPlayers.length }})</h3>
            </div>
            <div class="text-2xl font-bold mb-1" style="color: var(--accent)">
              {{ formatPrice(totalValue) }}
            </div>
            <p class="text-xs mb-3" style="color: var(--text-muted)">Valoare totala lot</p>

            <div v-if="squadPlayers.length === 0" class="py-8 text-center text-sm" style="color: var(--text-muted)">
              Niciun jucător. Cumpără de la Cluburi.
            </div>

            <div v-else class="space-y-2 max-h-[480px] overflow-y-auto pr-1" style="scrollbar-width: thin; scrollbar-color: var(--bg-elevated) transparent;">
              <div
                v-for="tp in squadPlayers"
                :key="tp.id"
                draggable="true"
                @dragstart="onDragStart($event, tp.player.id)"
                class="flex items-center gap-2 p-2 rounded-lg cursor-grab active:cursor-grabbing transition-all"
                :style="{
                  background: 'var(--bg-elevated)',
                  border: '1px solid var(--border)',
                  opacity: isInLineup(tp) ? 0.6 : 1,
                }"
              >
                <PlayerAvatar :position="tp.player.position" size="sm" />
                <div class="flex-1 min-w-0">
                  <div class="flex items-center gap-1.5">
                    <span class="text-xs font-mono px-1 py-0.5 rounded" :style="positionBg(tp.player.position)">
                      {{ tp.player.position }}
                    </span>
                    <span class="text-sm font-medium truncate" style="color: var(--text-primary)">
                      {{ tp.player.nickname || tp.player.name }}
                    </span>
                  </div>
                  <div class="text-xs mt-0.5" :style="{
                    color: isInLineup(tp) ? 'var(--success)' : listedPlayerIds.has(tp.player.id) ? 'var(--warning)' : 'var(--text-muted)'
                  }">
                    <span v-if="isInLineup(tp)">Pe teren #{{ tp.slotNumber }}</span>
                    <span v-else-if="listedPlayerIds.has(tp.player.id)">La vânzare</span>
                    <span v-else>Bancă — trage pe teren</span>
                  </div>
                </div>
                <div class="flex flex-col gap-1 shrink-0">
                  <button v-if="isInLineup(tp)" @click="removeFromLineup(tp.player.id)"
                    class="text-[10px] px-1.5 py-0.5 rounded transition-colors"
                    style="background: var(--bg-surface); color: var(--danger)">
                    Scoate
                  </button>
                  <button v-if="!listedPlayerIds.has(tp.player.id) && !isInLineup(tp)"
                    @click="prepareSell(tp.player.id)"
                    class="text-[10px] px-1.5 py-0.5 rounded transition-colors"
                    style="background: var(--bg-surface); color: var(--warning)">
                    Vinde
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Sell form -->
      <section v-if="sellForm.playerId" class="mb-6 p-5 rounded-xl"
        style="background: var(--bg-surface); border: 1px solid var(--warning)">
        <h2 class="font-semibold mb-3">Pune jucător la vânzare</h2>
        <p class="text-sm mb-3" style="color: var(--text-muted)">
          Alți utilizatori vor putea trimite oferte. Tu alegi dacă accepți sau refuzi.
        </p>
        <div class="flex flex-col sm:flex-row gap-3">
          <input v-model="sellForm.askingPrice" type="number" min="1" placeholder="Preț cerut (€)"
            class="flex-1 px-3 py-2 rounded-lg text-sm outline-none"
            style="background: var(--bg-elevated); border: 1px solid var(--border); color: var(--text-primary)" />
          <button @click="listForSale" class="px-4 py-2 rounded-lg text-sm font-semibold"
            style="background: var(--warning); color: #0d1117">Listează</button>
          <button @click="sellForm = { playerId: '', askingPrice: '' }"
            class="px-4 py-2 rounded-lg text-sm"
            style="background: var(--bg-elevated); color: var(--text-muted)">Anulează</button>
        </div>
      </section>

      <!-- My listings -->
      <section v-if="myListings.length" class="mb-6 p-5 rounded-xl"
        style="background: var(--bg-surface); border: 1px solid var(--border)">
        <h2 class="font-semibold mb-4">Listări active</h2>
        <div class="space-y-2">
          <div v-for="listing in myListings" :key="listing.id"
            class="flex justify-between items-center p-3 rounded-lg"
            style="background: var(--bg-elevated)">
            <span class="text-sm">{{ listing.player.name }} — <span style="color: var(--accent)">{{ formatPrice(listing.askingPrice) }}</span></span>
            <button @click="cancelListing(listing.id)" class="text-xs px-2 py-1 rounded"
              style="background: var(--bg-surface); color: var(--danger)">Anulează</button>
          </div>
        </div>
      </section>

      <!-- Assign (fallback manual) -->
      <section id="assign-section" class="mb-6 p-5 rounded-xl"
        style="background: var(--bg-surface); border: 1px solid var(--border)">
        <h2 class="font-semibold mb-3">Plasează manual pe teren</h2>
        <div v-if="!form.formationId" class="text-sm" style="color: var(--warning)">
          Selectează o formație din setările de mai sus.
        </div>
        <div v-else-if="benchPlayers.length === 0" class="text-sm" style="color: var(--text-muted)">
          Niciun jucător disponibil pe bancă.
        </div>
        <div v-else class="flex flex-col sm:flex-row gap-3">
          <select v-model="assignForm.playerId" class="flex-1 px-3 py-2 rounded-lg text-sm outline-none"
            style="background: var(--bg-elevated); border: 1px solid var(--border); color: var(--text-primary)">
            <option value="">— Jucător —</option>
            <option v-for="tp in benchPlayers.filter(p => !listedPlayerIds.has(p.player.id))"
              :key="tp.player.id" :value="String(tp.player.id)">
              {{ tp.player.name }} ({{ tp.player.position }})
            </option>
          </select>
          <select v-model.number="assignForm.slotNumber" :disabled="!slots.length"
            class="w-44 px-3 py-2 rounded-lg text-sm outline-none disabled:opacity-50"
            style="background: var(--bg-elevated); border: 1px solid var(--border); color: var(--text-primary)">
            <option v-for="slot in slots" :key="slot.slotNumber" :value="slot.slotNumber">
              Slot {{ slot.slotNumber }} ({{ slot.position }})
            </option>
          </select>
          <button @click="assignPlayer" :disabled="assigning || !assignForm.playerId || !slots.length"
            class="px-4 py-2 rounded-lg text-sm font-semibold disabled:opacity-50"
            style="background: var(--accent); color: #0d1117">
            {{ assigning ? 'Se plasează...' : 'Plasează' }}
          </button>
        </div>
      </section>

      <!-- Submit -->
      <section class="p-5 rounded-xl" style="background: var(--bg-surface); border: 1px solid var(--border)">
        <h2 class="font-semibold mb-2">Publică echipa</h2>
        <p class="text-sm mb-4" style="color: var(--text-muted)">
          Ai nevoie de 11 jucători plasați pe teren și o formație selectată.
        </p>
        <div class="flex items-center gap-4">
          <button @click="submitTeam" :disabled="team.submitted || lineupCount < 11"
            class="px-4 py-2 rounded-lg text-sm font-semibold disabled:opacity-50"
            style="background: var(--accent); color: #0d1117">
            {{ team.submitted ? 'Deja publicată' : 'Publică echipa' }}
          </button>
          <span v-if="team.submitted" class="text-sm" style="color: var(--success)">
            ▲ {{ team.votes }} voturi
          </span>
        </div>
      </section>
    </template>

    <div v-else class="py-20 text-center">
      <div class="text-4xl mb-4">⚽</div>
      <p class="text-lg font-semibold mb-2" style="color: var(--text-primary)">Nu ai o echipă încă</p>
      <p class="text-sm" style="color: var(--text-muted)">Înregistrează-te pentru a-ți crea echipa.</p>
    </div>
  </div>
</template>
