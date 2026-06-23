<script setup>
import { formatPrice } from '../utils/player'
import PlayerAvatar from './PlayerAvatar.vue'

defineProps({
  player: { type: Object, required: true },
  showBuy: { type: Boolean, default: false },
})

const emit = defineEmits(['buy'])
</script>

<template>
  <div class="bg-slate-900 border border-slate-800 rounded-lg p-4 hover:border-emerald-800 transition-colors">
    <div class="flex gap-3">
      <PlayerAvatar :position="player.position" size="md" />
      <div class="flex-1 min-w-0">
        <div class="flex justify-between items-start gap-2">
          <div>
            <h3 class="font-semibold truncate">{{ player.name }}</h3>
            <p v-if="player.nickname" class="text-sm text-slate-400">{{ player.nickname }}</p>
          </div>
          <span class="text-xs px-2 py-0.5 rounded bg-emerald-900/50 text-emerald-300 font-mono shrink-0">
            {{ player.position }}
          </span>
        </div>
        <div class="mt-3 flex justify-between items-center text-sm">
          <span class="text-slate-400 truncate">{{ player.playerTeamName }}</span>
          <span class="text-emerald-400 font-medium shrink-0">{{ formatPrice(player.price) }}</span>
        </div>
        <button
          v-if="showBuy"
          @click="emit('buy', player)"
          class="mt-3 w-full py-1.5 rounded bg-emerald-600 hover:bg-emerald-500 text-sm transition-colors"
        >
          Cumpără
        </button>
      </div>
    </div>
  </div>
</template>
