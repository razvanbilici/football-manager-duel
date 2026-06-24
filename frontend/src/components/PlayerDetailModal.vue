<script setup>
import { computed, onMounted, onUnmounted } from 'vue'
import { formatPrice } from '../utils/player'
import PlayerAvatar from './PlayerAvatar.vue'

const props = defineProps({
  player: { type: Object, default: null },
  show: { type: Boolean, default: false },
  isOwned: { type: Boolean, default: false },
})
const emit = defineEmits(['close', 'buy'])

const positionColor = computed(() => {
  if (!props.player) return {}
  const p = (props.player.position || '').toUpperCase()
  if (p === 'GK') return { bg: '#78350f', text: '#fcd34d' }
  if (['CB','LB','RB','LWB','RWB'].includes(p)) return { bg: '#1e3a5f', text: '#60a5fa' }
  if (['CDM','CM','CAM','LM','RM'].includes(p)) return { bg: '#14401e', text: '#4ade80' }
  return { bg: '#4c1d1d', text: '#f87171' }
})

const isUnavailable = computed(() => props.player?.available === false)

function onKey(e) {
  if (e.key === 'Escape') emit('close')
}
onMounted(() => window.addEventListener('keydown', onKey))
onUnmounted(() => window.removeEventListener('keydown', onKey))
</script>

<template>
  <Teleport to="body">
    <div v-if="show && player"
      class="fixed inset-0 z-50 flex items-center justify-center p-4"
      style="background: rgba(0,0,0,0.75); backdrop-filter: blur(4px)"
      @click.self="emit('close')">

      <div class="relative w-full max-w-md rounded-2xl overflow-hidden shadow-2xl"
        style="background: var(--bg-surface); border: 1px solid var(--border)">

        <!-- Close button -->
        <button @click="emit('close')"
          class="absolute top-3 right-3 z-10 w-8 h-8 rounded-full flex items-center justify-center transition-colors"
          style="background: var(--bg-elevated); color: var(--text-muted)">
          ✕
        </button>

        <!-- Header -->
        <div class="p-6 flex gap-4 items-center" style="border-bottom: 1px solid var(--border)">
          <PlayerAvatar :position="player.position" size="lg" />
          <div>
            <div class="flex items-center gap-2 mb-1">
              <span class="text-xs px-2 py-0.5 rounded font-mono font-bold"
                :style="{ background: positionColor.bg, color: positionColor.text }">
                {{ player.position }}
              </span>
              <span v-if="player.shirtNumber" class="text-xs" style="color: var(--text-muted)">#{{ player.shirtNumber }}</span>
            </div>
            <h2 class="text-xl font-bold" style="color: var(--text-primary)">{{ player.name }}</h2>
            <p v-if="player.nickname" class="text-sm" style="color: var(--text-muted)">{{ player.nickname }}</p>
          </div>
        </div>

        <!-- Stats grid -->
        <div class="grid grid-cols-2 gap-px p-0" style="background: var(--border)">
          <div v-for="stat in [
            { label: 'Vârstă', value: player.age ? player.age + ' ani' : '—' },
            { label: 'Înălțime', value: player.heightCm ? player.heightCm + ' cm' : '—' },
            { label: 'Naționalitate', value: player.nationality || '—' },
            { label: 'Picior preferat', value: player.preferredFoot || '—' },
            { label: 'Club', value: player.playerTeamName || '—' },
            { label: 'Valoare de piață', value: formatPrice(player.price) },
          ]" :key="stat.label"
            class="px-4 py-3"
            style="background: var(--bg-surface)">
            <p class="text-xs mb-0.5" style="color: var(--text-muted)">{{ stat.label }}</p>
            <p class="text-sm font-semibold" style="color: var(--text-primary)">{{ stat.value }}</p>
          </div>
        </div>

        <!-- Availability + action -->
        <div class="p-4 flex items-center justify-between gap-4" style="border-top: 1px solid var(--border)">
          <div class="flex items-center gap-2">
            <div class="w-2 h-2 rounded-full"
              :style="{ background: isUnavailable ? 'var(--danger)' : 'var(--success)' }"></div>
            <span class="text-sm" :style="{ color: isUnavailable ? 'var(--danger)' : 'var(--success)' }">
              {{ isUnavailable ? 'Indisponibil' : 'Disponibil' }}
            </span>
          </div>

          <div v-if="isOwned"
            class="px-4 py-2 rounded-lg text-sm font-medium"
            style="background: var(--bg-elevated); color: var(--text-muted)">
            Deja în echipă
          </div>
          <button v-else-if="!isUnavailable"
            @click="emit('buy', player); emit('close')"
            class="px-5 py-2 rounded-lg text-sm font-bold transition-colors"
            style="background: var(--accent); color: #0d1117">
            Cumpără — {{ formatPrice(player.price) }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>
