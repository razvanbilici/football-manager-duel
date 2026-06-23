<script setup>
import { computed } from 'vue'
import { getPlayerAvatar } from '../utils/player'

const props = defineProps({
  position: { type: String, required: true },
  size: { type: String, default: 'md' },
})

const src = computed(() => getPlayerAvatar(props.position))

const sizeClass = computed(() => {
  const sizes = { sm: 'w-10 h-10', md: 'w-14 h-14', lg: 'w-16 h-16' }
  return sizes[props.size] || sizes.md
})
</script>

<template>
  <img
    :src="src"
    :alt="position"
    :class="['rounded-full bg-slate-800 border-2 border-white/20 object-cover', sizeClass]"
  />
</template>
