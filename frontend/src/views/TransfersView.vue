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

const isMyListing = (listing) => listing.userTeamId === auth.user?.userTeam?.id

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
    <p class="mb-6 text-sm" style="color: var(--text-muted)">Cumpără, vinde și negociază jucători între utilizatori.</p>

    <!-- Tab bar -->
    <div class="flex flex-wrap gap-2 mb-6">
      <button
        v-for="t in visibleTabs"
        :key="t.id"
        @click="tab = t.id"
        class="px-4 py-2 rounded-lg text-sm transition-colors"
        :style="tab === t.id
          ? 'background: transparent; color: var(--accent); font-weight: 600; border-bottom: 2px solid var(--accent); border-radius: 0'
          : 'background: var(--bg-elevated); color: var(--text-muted); border-bottom: 2px solid transparent; border-radius: 8px'"
      >
        {{ t.label }}
        <span
          v-if="t.id === 'offers' && incoming.length"
          class="ml-1 px-1.5 py-0.5 rounded-full bg-red-500 text-[10px] text-white"
        >
          {{ incoming.length }}
        </span>
      </button>
    </div>

    <p v-if="message" class="mb-4 text-sm"
      :style="{ color: isError(message) ? 'var(--danger)' : 'var(--success)' }">
      {{ message }}
    </p>

    <!-- Skeleton -->
    <div v-if="loading" class="grid sm:grid-cols-2 gap-4">
      <div v-for="i in 4" :key="i" class="skeleton h-32 rounded-xl" />
    </div>

    <!-- Market -->
    <div v-else-if="tab === 'market'">
      <!-- Offer form -->
      <div v-if="offerForm.playerId" class="mb-6 p-4 rounded-xl"
        style="background: var(--bg-surface); border: 1px solid var(--accent)">
        <h3 class="font-semibold mb-3">Trimite ofertă de cumpărare</h3>
        <div class="flex flex-col sm:flex-row gap-3">
          <input
            v-model="offerForm.price"
            type="number"
            min="1"
            placeholder="Sumă oferită (€)"
            class="flex-1 px-3 py-2 rounded-lg text-sm outline-none"
            style="background: var(--bg-elevated); border: 1px solid var(--border); color: var(--text-primary)"
          />
          <button @click="sendOffer"
            class="px-4 py-2 rounded-lg text-sm font-semibold"
            style="background: var(--accent); color: #0d1117">
            Trimite oferta
          </button>
          <button
            @click="offerForm = { listingId: null, playerId: null, fromUserTeamId: null, price: '' }"
            class="px-4 py-2 rounded-lg text-sm"
            style="background: var(--bg-elevated); color: var(--text-muted)">
            Anulează
          </button>
        </div>
      </div>

      <div v-if="listings.length === 0" class="flex flex-col items-center py-16 text-center">
        <div class="text-4xl mb-3">🏪</div>
        <p class="font-medium" style="color: var(--text-primary)">Piata este goala</p>
        <p class="text-sm mt-1" style="color: var(--text-muted)">Niciun jucator listat momentan</p>
      </div>
      <div v-else class="grid sm:grid-cols-2 gap-4">
        <div v-for="listing in listings" :key="listing.id"
          class="p-4 rounded-xl"
          style="background: var(--bg-surface); border: 1px solid var(--border); border-left: 3px solid var(--accent)">
          <div class="flex gap-3">
            <PlayerAvatar :position="listing.player.position" size="md" />
            <div class="flex-1">
              <h3 class="font-semibold" style="color: var(--text-primary)">{{ listing.player.name }}</h3>
              <p class="text-sm" style="color: var(--text-muted)">{{ listing.player.position }}</p>
              <p class="text-xs mt-1" style="color: var(--text-muted)">
                Vânzător: {{ listing.sellerName }} ({{ listing.userTeamName }})
              </p>
              <p class="text-sm font-semibold mt-2" style="color: var(--accent)">
                Preț cerut: {{ formatPrice(listing.askingPrice) }}
              </p>
            </div>
          </div>
          <template v-if="auth.isLoggedIn">
            <button v-if="!isMyListing(listing)" @click="openOffer(listing)"
              class="mt-3 w-full py-2 rounded-lg text-sm font-semibold"
              style="background: var(--accent); color: #0d1117">
              Fă o ofertă
            </button>
            <div v-else class="mt-3 w-full py-2 rounded-lg text-sm text-center"
              style="background: var(--bg-elevated); color: var(--text-muted); border: 1px solid var(--border)">
              Listarea ta
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- Incoming offers -->
    <div v-else-if="tab === 'offers'">
      <div v-if="incoming.length === 0" class="flex flex-col items-center py-16 text-center">
        <div class="text-4xl mb-3">📬</div>
        <p class="font-medium" style="color: var(--text-primary)">Nicio oferta primita</p>
        <p class="text-sm mt-1" style="color: var(--text-muted)">Cand cineva face o oferta pe jucatorii tai, apare aici</p>
      </div>
      <div v-else class="space-y-4">
        <div v-for="offer in incoming" :key="offer.id"
          class="p-4 rounded-xl"
          style="background: var(--bg-surface); border: 1px solid var(--border); border-left: 3px solid var(--warning)">
          <div class="flex gap-3 items-start">
            <PlayerAvatar :position="offer.player.position" size="md" />
            <div class="flex-1">
              <p class="font-semibold" style="color: var(--text-primary)">{{ offer.player.name }} ({{ offer.player.position }})</p>
              <p class="text-sm mt-1" style="color: var(--text-muted)">
                {{ offer.proposerName }} oferă <span style="color: var(--accent)">{{ formatPrice(offer.offeredPrice) }}</span>
              </p>
              <p class="text-xs mt-0.5" style="color: var(--text-muted)">{{ offer.createdAt }}</p>
            </div>
          </div>
          <div class="mt-3 flex gap-2">
            <button @click="acceptOffer(offer.id)"
              class="px-4 py-2 rounded-lg text-sm font-semibold"
              style="background: var(--success); color: #0d1117">
              Acceptă
            </button>
            <button @click="rejectOffer(offer.id)"
              class="px-4 py-2 rounded-lg text-sm font-semibold"
              style="background: var(--danger); color: white">
              Refuză
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Sent offers -->
    <div v-else-if="tab === 'sent'">
      <div v-if="sent.length === 0" class="flex flex-col items-center py-16 text-center">
        <div class="text-4xl mb-3">📤</div>
        <p class="font-medium" style="color: var(--text-primary)">Nicio oferta trimisa</p>
        <p class="text-sm mt-1" style="color: var(--text-muted)">Fa o oferta din sectiunea Piata</p>
      </div>
      <div v-else class="space-y-3">
        <div v-for="offer in sent" :key="offer.id"
          class="p-4 rounded-xl flex justify-between items-center gap-4"
          style="background: var(--bg-surface); border: 1px solid var(--border)">
          <div>
            <span class="font-semibold" style="color: var(--text-primary)">{{ offer.player.name }}</span>
            <span class="text-sm ml-2" style="color: var(--accent)">{{ formatPrice(offer.offeredPrice) }}</span>
          </div>
          <span class="badge"
            :style="
              offer.status === 'PENDING'  ? 'background: rgba(227,179,65,0.15); color: var(--warning)'  :
              offer.status === 'ACCEPTED' ? 'background: rgba(63,185,80,0.15); color: var(--success)'  :
                                            'background: rgba(248,81,73,0.15); color: var(--danger)'">
            {{ offer.status }}
          </span>
        </div>
      </div>
    </div>

    <!-- History -->
    <div v-else-if="tab === 'history'">
      <div v-if="recent.length === 0" class="flex flex-col items-center py-16 text-center">
        <div class="text-4xl mb-3">📋</div>
        <p class="font-medium" style="color: var(--text-primary)">Niciun transfer inca</p>
        <p class="text-sm mt-1" style="color: var(--text-muted)">Istoricul transferurilor va aparea aici</p>
      </div>
      <div v-else class="space-y-3">
        <div v-for="t in recent" :key="t.id"
          class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 p-4 rounded-xl"
          style="background: var(--bg-surface); border: 1px solid var(--border)">
          <div>
            <span class="font-semibold" style="color: var(--text-primary)">{{ t.player.name }}</span>
            <span class="text-sm ml-2" style="color: var(--text-muted)">({{ t.player.position }})</span>
          </div>
          <div class="text-sm" style="color: var(--text-muted)">{{ t.fromTeamName }} → {{ t.toTeamName }}</div>
          <div class="flex items-center gap-4 text-sm">
            <span class="font-semibold" style="color: var(--accent)">{{ formatPrice(t.price) }}</span>
            <span class="px-2 py-0.5 rounded text-xs"
              style="background: var(--bg-elevated); color: var(--text-muted)">{{ t.type }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
