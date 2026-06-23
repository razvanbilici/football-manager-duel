<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { useNotificationStore } from '../stores/notifications'
import { transferApi } from '../api'
import { formatPrice } from '../utils/player'
import PlayerAvatar from '../components/PlayerAvatar.vue'

const auth = useAuthStore()
const notifications = useNotificationStore()
const route = useRoute()

const tab = ref('market')
const loading = ref(true)
const message = ref('')

const listings = ref([])
const incoming = ref([])
const sent = ref([])
const recent = ref([])

const offerForm = ref({ listingId: null, playerId: null, fromUserTeamId: null, price: '' })

const tabs = [
  { id: 'market', label: 'Piață' },
  { id: 'offers', label: 'Oferte primite', auth: true },
  { id: 'sent', label: 'Oferte trimise', auth: true },
  { id: 'history', label: 'Istoric' },
]

const visibleTabs = computed(() => tabs.filter((t) => !t.auth || auth.isLoggedIn))

async function load() {
  loading.value = true
  try {
    const tasks = [transferApi.getListings(), transferApi.getRecent()]
    if (auth.isLoggedIn) {
      tasks.push(transferApi.getIncomingProposals(), transferApi.getSentProposals())
    }
    const results = await Promise.all(tasks)
    listings.value = results[0].data
    recent.value = results[1].data
    if (auth.isLoggedIn) {
      incoming.value = results[2].data
      sent.value = results[3].data
    }
  } finally {
    loading.value = false
  }
}

function openOffer(listing) {
  offerForm.value = {
    listingId: listing.id,
    playerId: listing.player.id,
    fromUserTeamId: listing.userTeamId,
    price: String(Number(listing.askingPrice)),
  }
  tab.value = 'market'
}

async function sendOffer() {
  if (!auth.isLoggedIn) return
  message.value = ''
  try {
    await transferApi.createProposal(
      offerForm.value.playerId,
      offerForm.value.fromUserTeamId,
      Number(offerForm.value.price),
    )
    message.value = 'Ofertă trimisă!'
    offerForm.value = { listingId: null, playerId: null, fromUserTeamId: null, price: '' }
    await load()
  } catch (e) {
    message.value = e.response?.data?.message || 'Ofertă eșuată'
  }
}

async function acceptOffer(id) {
  try {
    await transferApi.acceptProposal(id)
    message.value = 'Ofertă acceptată — transfer finalizat!'
    await auth.fetchProfile()
    await notifications.fetchUnreadCount()
    await load()
  } catch (e) {
    message.value = e.response?.data?.message || 'Eroare la acceptare'
  }
}

async function rejectOffer(id) {
  try {
    await transferApi.rejectProposal(id)
    message.value = 'Ofertă refuzată.'
    await notifications.fetchUnreadCount()
    await load()
  } catch (e) {
    message.value = e.response?.data?.message || 'Eroare'
  }
}

function isError(msg) {
  return /eșuat|eroare|insufficient/i.test(msg)
}

watch(
  () => route.query.tab,
  (t) => {
    if (t && visibleTabs.value.some((x) => x.id === t)) tab.value = t
  },
  { immediate: true },
)

onMounted(load)
</script>

<template>
  <div>
    <h1 class="text-2xl font-bold mb-2">Transferuri</h1>
    <p class="text-slate-400 mb-6">Cumpără, vinde și negociază jucători între utilizatori.</p>

    <div class="flex flex-wrap gap-2 mb-6">
      <button
        v-for="t in visibleTabs"
        :key="t.id"
        @click="tab = t.id"
        :class="[
          'px-4 py-2 rounded-lg text-sm transition-colors',
          tab === t.id ? 'bg-emerald-600 text-white' : 'bg-slate-800 hover:bg-slate-700',
        ]"
      >
        {{ t.label }}
        <span
          v-if="t.id === 'offers' && incoming.length"
          class="ml-1 px-1.5 py-0.5 rounded-full bg-red-500 text-[10px]"
        >
          {{ incoming.length }}
        </span>
      </button>
    </div>

    <p v-if="message" class="mb-4 text-sm" :class="isError(message) ? 'text-red-400' : 'text-emerald-400'">
      {{ message }}
    </p>

    <div v-if="loading" class="text-slate-400">Se încarcă...</div>

    <!-- Market -->
    <div v-else-if="tab === 'market'">
      <div
        v-if="offerForm.playerId"
        class="mb-6 p-4 rounded-xl bg-slate-900 border border-emerald-800"
      >
        <h3 class="font-semibold mb-3">Trimite ofertă de cumpărare</h3>
        <div class="flex flex-col sm:flex-row gap-3">
          <input
            v-model="offerForm.price"
            type="number"
            min="1"
            placeholder="Sumă oferită (€)"
            class="flex-1 px-3 py-2 rounded-lg bg-slate-950 border border-slate-700 outline-none focus:border-emerald-500"
          />
          <button
            @click="sendOffer"
            class="px-4 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500"
          >
            Trimite oferta
          </button>
          <button
            @click="offerForm = { listingId: null, playerId: null, fromUserTeamId: null, price: '' }"
            class="px-4 py-2 rounded-lg bg-slate-800 hover:bg-slate-700"
          >
            Anulează
          </button>
        </div>
      </div>

      <div v-if="listings.length === 0" class="text-slate-400">
        Niciun jucător listat. Vinde jucători din secțiunea Echipa mea.
      </div>
      <div v-else class="grid sm:grid-cols-2 gap-4">
        <div
          v-for="listing in listings"
          :key="listing.id"
          class="p-4 rounded-xl bg-slate-900 border border-slate-800"
        >
          <div class="flex gap-3">
            <PlayerAvatar :position="listing.player.position" size="md" />
            <div class="flex-1">
              <h3 class="font-semibold">{{ listing.player.name }}</h3>
              <p class="text-sm text-slate-400">{{ listing.player.position }}</p>
              <p class="text-sm text-slate-500 mt-1">
                Vânzător: {{ listing.sellerName }} ({{ listing.userTeamName }})
              </p>
              <p class="text-emerald-400 font-medium mt-2">
                Preț cerut: {{ formatPrice(listing.askingPrice) }}
              </p>
            </div>
          </div>
          <button
            v-if="auth.isLoggedIn"
            @click="openOffer(listing)"
            class="mt-3 w-full py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-sm"
          >
            Fă o ofertă
          </button>
        </div>
      </div>
    </div>

    <!-- Incoming offers -->
    <div v-else-if="tab === 'offers'">
      <div v-if="incoming.length === 0" class="text-slate-400">
        Niciună ofertă primită.
      </div>
      <div v-else class="space-y-4">
        <div
          v-for="offer in incoming"
          :key="offer.id"
          class="p-4 rounded-xl bg-slate-900 border border-slate-800"
        >
          <div class="flex gap-3 items-start">
            <PlayerAvatar :position="offer.player.position" size="md" />
            <div class="flex-1">
              <p class="font-semibold">{{ offer.player.name }} ({{ offer.player.position }})</p>
              <p class="text-sm text-slate-400 mt-1">
                {{ offer.proposerName }} oferă {{ formatPrice(offer.offeredPrice) }}
              </p>
              <p class="text-xs text-slate-500">{{ offer.createdAt }}</p>
            </div>
          </div>
          <div class="mt-3 flex gap-2">
            <button
              @click="acceptOffer(offer.id)"
              class="px-4 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-sm"
            >
              Acceptă
            </button>
            <button
              @click="rejectOffer(offer.id)"
              class="px-4 py-2 rounded-lg bg-red-700 hover:bg-red-600 text-sm"
            >
              Refuză
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Sent offers -->
    <div v-else-if="tab === 'sent'">
      <div v-if="sent.length === 0" class="text-slate-400">Nicio ofertă trimisă.</div>
      <div v-else class="space-y-3">
        <div
          v-for="offer in sent"
          :key="offer.id"
          class="p-4 rounded-xl bg-slate-900 border border-slate-800 flex justify-between items-center gap-4"
        >
          <div>
            <span class="font-semibold">{{ offer.player.name }}</span>
            <span class="text-sm text-slate-400 ml-2">{{ formatPrice(offer.offeredPrice) }}</span>
          </div>
          <span
            :class="[
              'text-xs px-2 py-1 rounded',
              offer.status === 'PENDING' && 'bg-amber-900/50 text-amber-300',
              offer.status === 'ACCEPTED' && 'bg-emerald-900/50 text-emerald-300',
              offer.status === 'REJECTED' && 'bg-red-900/50 text-red-300',
            ]"
          >
            {{ offer.status }}
          </span>
        </div>
      </div>
    </div>

    <!-- History -->
    <div v-else-if="tab === 'history'">
      <div v-if="recent.length === 0" class="text-slate-400">Niciun transfer încă.</div>
      <div v-else class="space-y-3">
        <div
          v-for="t in recent"
          :key="t.id"
          class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 p-4 rounded-xl bg-slate-900 border border-slate-800"
        >
          <div>
            <span class="font-semibold">{{ t.player.name }}</span>
            <span class="text-slate-400 text-sm ml-2">({{ t.player.position }})</span>
          </div>
          <div class="text-sm text-slate-400">{{ t.fromTeamName }} → {{ t.toTeamName }}</div>
          <div class="flex items-center gap-4 text-sm">
            <span class="text-emerald-400 font-medium">{{ formatPrice(t.price) }}</span>
            <span class="px-2 py-0.5 rounded bg-slate-800 text-xs">{{ t.type }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
