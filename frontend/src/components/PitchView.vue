<script setup>
import { computed } from 'vue'
import { computePitchLayout } from '../utils/pitchLayout'
import PlayerAvatar from './PlayerAvatar.vue'

const props = defineProps({
  formation: { type: Object, default: null },
  players: { type: Array, default: () => [] },
  interactive: { type: Boolean, default: false },
  dragOverSlot: { type: Number, default: null },
})

const emit = defineEmits(['slot-click', 'remove', 'drop-on-slot', 'dragover-slot', 'dragleave-slot'])

const layout = computed(() =>
  computePitchLayout(props.formation?.positions || [], props.players),
)
</script>

<template>
  <div
    class="relative w-full rounded-xl overflow-hidden border-2 border-white/20 select-none"
    style="aspect-ratio: 2/3; background: linear-gradient(180deg, #166534 0%, #15803d 40%, #16a34a 100%)"
  >
    <!-- Pitch markings -->
    <div class="absolute inset-0 pointer-events-none">
      <div class="absolute inset-3 border-2 border-white/30 rounded-sm" />
      <div class="absolute left-1/2 top-3 bottom-3 w-0.5 bg-white/30 -translate-x-1/2" />
      <div class="absolute left-1/2 top-1/2 w-24 h-24 border-2 border-white/30 rounded-full -translate-x-1/2 -translate-y-1/2" />
      <div class="absolute left-1/2 bottom-3 w-2/5 h-1/5 border-2 border-white/30 border-b-0 -translate-x-1/2" />
      <div class="absolute left-1/2 top-3 w-2/5 h-1/5 border-2 border-white/30 border-t-0 -translate-x-1/2" />
      <div class="absolute left-1/2 bottom-0 w-1/4 h-1 border-2 border-white/40 border-b-0 -translate-x-1/2" />
      <div class="absolute left-1/2 top-0 w-1/4 h-1 border-2 border-white/40 border-t-0 -translate-x-1/2" />
    </div>

    <div v-if="!formation" class="absolute inset-0 flex items-center justify-center text-white/60 text-sm">
      Selectează o formație
    </div>

    <div
      v-for="item in layout"
      :key="item.slotNumber"
      class="absolute -translate-x-1/2 -translate-y-1/2 flex flex-col items-center gap-0.5"
      :style="{ top: `${item.top}%`, left: `${item.left}%` }"
      @dragover.prevent="emit('dragover-slot', item.slotNumber)"
      @dragleave="emit('dragleave-slot', item.slotNumber)"
      @drop.prevent="emit('drop-on-slot', $event, item.slotNumber)"
    >
      <button
        v-if="interactive"
        type="button"
        class="flex flex-col items-center group"
        @click="emit('slot-click', item)"
      >
        <PlayerAvatar v-if="item.player" :position="item.position" size="md" />
        <div
          v-else
          class="w-14 h-14 rounded-full border-2 border-dashed border-white/40 bg-black/20 flex items-center justify-center text-xs text-white/50 transition-all duration-200"
          :style="dragOverSlot === item.slotNumber
            ? 'border-color: #00d4aa; background: rgba(0,212,170,0.2); box-shadow: 0 0 14px rgba(0,212,170,0.5); border-style: solid'
            : ''"
        >
          {{ item.position }}
        </div>
        <span
          v-if="item.player"
          class="mt-1 max-w-20 truncate text-[10px] font-medium text-white bg-black/50 px-1.5 py-0.5 rounded"
        >
          {{ item.player.nickname || item.player.name }}
        </span>
        <button
          v-if="item.player && interactive"
          type="button"
          class="text-[9px] text-red-300 opacity-0 group-hover:opacity-100 transition-opacity"
          @click.stop="emit('remove', item.player.id)"
        >
          scoate
        </button>
      </button>

      <template v-else>
        <PlayerAvatar v-if="item.player" :position="item.position" size="md" />
        <div
          v-else
          class="w-14 h-14 rounded-full border-2 border-dashed border-white/30 bg-black/10 flex items-center justify-center text-xs text-white/40"
        >
          {{ item.position }}
        </div>
        <span
          v-if="item.player"
          class="max-w-20 truncate text-[10px] font-medium text-white bg-black/50 px-1.5 py-0.5 rounded"
        >
          {{ item.player.nickname || item.player.name }}
        </span>
      </template>
    </div>
  </div>
</template>
