<script setup>
import { computed } from 'vue'
import { formatPrice } from '../utils/player'
import PlayerAvatar from './PlayerAvatar.vue'

const props = defineProps({
  player: { type: Object, required: true },
  showBuy: { type: Boolean, default: false },
  isOwned: { type: Boolean, default: false },
})
const emit = defineEmits(['buy', 'view-detail'])

const positionColor = computed(() => {
  const p = (props.player.position || '').toUpperCase()
  if (p === 'GK') return { bg: '#78350f', text: '#fcd34d' }
  if (['CB','LB','RB','LWB','RWB'].includes(p)) return { bg: '#1e3a5f', text: '#60a5fa' }
  if (['CDM','CM','CAM','LM','RM'].includes(p)) return { bg: '#14401e', text: '#4ade80' }
  return { bg: '#4c1d1d', text: '#f87171' }
})

const isUnavailable = computed(() => props.player.available === false)
</script>

<template>
  <div
    @click="emit('view-detail', player)"
    class="relative rounded-xl overflow-hidden cursor-pointer transition-all duration-200 hover:scale-[1.02] hover:shadow-xl"
    style="background: var(--bg-surface); border: 1px solid var(--border);"
    :style="{ opacity: isUnavailable ? 0.75 : 1 }"
  >
    <!-- Unavailable banner -->
    <div v-if="isUnavailable"
      class="absolute top-0 left-0 right-0 z-10 text-center text-xs font-bold py-0.5 tracking-widest"
      style="background: var(--danger); color: white">
      INDISPONIBIL
    </div>

    <!-- Owned badge -->
    <div v-if="isOwned && !isUnavailable"
      class="absolute top-2 right-2 z-10 text-xs font-bold px-2 py-0.5 rounded-full"
      style="background: var(--accent); color: #0d1117"
      title="Deja deții acest jucător în echipă">
      ÎN ECHIPĂ ✓
    </div>

    <div class="p-4" :class="{ 'pt-6': isUnavailable }">
      <div class="flex gap-3">
        <div class="relative shrink-0">
          <PlayerAvatar :position="player.position" size="md" />
          <!-- Shirt number badge -->
          <span v-if="player.shirtNumber"
            class="absolute -bottom-1 -right-1 w-5 h-5 rounded-full text-[10px] font-bold flex items-center justify-center"
            style="background: var(--bg-elevated); border: 1px solid var(--border); color: var(--text-primary)">
            {{ player.shirtNumber }}
          </span>
        </div>

        <div class="flex-1 min-w-0">
          <div class="flex items-start justify-between gap-2">
            <div class="min-w-0">
              <h3 class="font-bold truncate" style="color: var(--text-primary)">{{ player.name }}</h3>
              <p v-if="player.nickname" class="text-xs truncate" style="color: var(--text-muted)">{{ player.nickname }}</p>
            </div>
            <span class="text-xs px-2 py-0.5 rounded font-mono font-bold shrink-0"
              :style="{ background: positionColor.bg, color: positionColor.text }">
              {{ player.position }}
            </span>
          </div>

          <!-- Stats row -->
          <div class="mt-2 flex flex-wrap gap-x-3 gap-y-1 text-xs" style="color: var(--text-muted)">
            <span v-if="player.age">{{ player.age }} ani</span>
            <span v-if="player.nationality">{{ player.nationality }}</span>
            <span v-if="player.heightCm">{{ player.heightCm }} cm</span>
          </div>

          <div class="mt-2 flex items-center justify-between">
            <span class="text-xs truncate" style="color: var(--text-muted)">{{ player.playerTeamName }}</span>
            <span class="text-sm font-bold" style="color: var(--accent)">{{ formatPrice(player.price) }}</span>
          </div>
        </div>
      </div>

      <!-- Buy / owned button -->
      <div class="mt-3" @click.stop>
        <div v-if="isOwned"
          class="w-full py-1.5 rounded-lg text-sm text-center font-medium"
          style="background: var(--bg-elevated); color: var(--text-muted)">
          Deja în echipă
        </div>
        <button v-else-if="showBuy && !isUnavailable"
          @click="emit('buy', player)"
          class="w-full py-1.5 rounded-lg text-sm font-semibold transition-colors"
          style="background: var(--accent); color: #0d1117">
          Cumpără
        </button>
        <div v-else-if="isUnavailable && showBuy"
          class="w-full py-1.5 rounded-lg text-sm text-center"
          style="background: var(--bg-elevated); color: var(--danger)">
          Indisponibil
        </div>
      </div>
    </div>
  </div>
</template>
