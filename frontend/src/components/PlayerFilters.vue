<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
  modelValue: { type: Object, default: () => ({}) }
})
const emit = defineEmits(['update:modelValue'])

const positions = ['GK', 'CB', 'LB', 'RB', 'CDM', 'CM', 'CAM', 'LW', 'RW', 'ST']

const filters = reactive({
  q: '',
  position: '',
  minPrice: '',
  maxPrice: '',
  minAge: '',
  maxAge: '',
  available: false,
  ...props.modelValue
})

watch(filters, (val) => {
  const params = {}
  if (val.q) params.q = val.q
  if (val.position) params.position = val.position
  if (val.minPrice) params.minPrice = val.minPrice
  if (val.maxPrice) params.maxPrice = val.maxPrice
  if (val.minAge) params.minAge = val.minAge
  if (val.maxAge) params.maxAge = val.maxAge
  if (val.available) params.available = true
  emit('update:modelValue', params)
}, { deep: true })
</script>

<template>
  <div class="space-y-5 p-4 rounded-xl border" style="background: var(--bg-surface); border-color: var(--border)">
    <h3 class="font-semibold text-sm uppercase tracking-widest" style="color: var(--text-muted)">Filtre</h3>

    <!-- Search -->
    <div>
      <label class="block text-xs mb-1" style="color: var(--text-muted)">Caută jucător</label>
      <input v-model="filters.q" placeholder="Nume sau poreclă..."
        class="w-full px-3 py-2 rounded-lg text-sm outline-none"
        style="background: var(--bg-elevated); border: 1px solid var(--border); color: var(--text-primary)" />
    </div>

    <!-- Position -->
    <div>
      <label class="block text-xs mb-2" style="color: var(--text-muted)">Poziție</label>
      <div class="flex flex-wrap gap-1.5">
        <button @click="filters.position = ''"
          :class="['text-xs px-2 py-1 rounded transition-colors', !filters.position ? 'text-black font-semibold' : '']"
          :style="!filters.position ? 'background: var(--accent)' : 'background: var(--bg-elevated); color: var(--text-muted)'"
        >Toate</button>
        <button v-for="pos in positions" :key="pos"
          @click="filters.position = filters.position === pos ? '' : pos"
          :class="['text-xs px-2 py-1 rounded transition-colors', filters.position === pos ? 'font-semibold text-black' : '']"
          :style="filters.position === pos ? 'background: var(--accent)' : 'background: var(--bg-elevated); color: var(--text-muted)'"
        >{{ pos }}</button>
      </div>
    </div>

    <!-- Price range -->
    <div>
      <label class="block text-xs mb-2" style="color: var(--text-muted)">Preț (€)</label>
      <div class="flex gap-2">
        <input v-model="filters.minPrice" type="number" placeholder="Min"
          class="w-full px-2 py-1.5 rounded text-xs outline-none"
          style="background: var(--bg-elevated); border: 1px solid var(--border); color: var(--text-primary)" />
        <input v-model="filters.maxPrice" type="number" placeholder="Max"
          class="w-full px-2 py-1.5 rounded text-xs outline-none"
          style="background: var(--bg-elevated); border: 1px solid var(--border); color: var(--text-primary)" />
      </div>
    </div>

    <!-- Age range -->
    <div>
      <label class="block text-xs mb-2" style="color: var(--text-muted)">Vârstă</label>
      <div class="flex gap-2">
        <input v-model="filters.minAge" type="number" placeholder="Min" min="15" max="45"
          class="w-full px-2 py-1.5 rounded text-xs outline-none"
          style="background: var(--bg-elevated); border: 1px solid var(--border); color: var(--text-primary)" />
        <input v-model="filters.maxAge" type="number" placeholder="Max" min="15" max="45"
          class="w-full px-2 py-1.5 rounded text-xs outline-none"
          style="background: var(--bg-elevated); border: 1px solid var(--border); color: var(--text-primary)" />
      </div>
    </div>

    <!-- Available only -->
    <label class="flex items-center gap-2 cursor-pointer">
      <input type="checkbox" v-model="filters.available" class="accent-teal-400 w-4 h-4" />
      <span class="text-sm" style="color: var(--text-primary)">Doar disponibili</span>
    </label>

    <!-- Reset -->
    <button @click="Object.assign(filters, { q:'', position:'', minPrice:'', maxPrice:'', minAge:'', maxAge:'', available: false })"
      class="w-full py-2 rounded-lg text-sm transition-colors"
      style="background: var(--bg-elevated); color: var(--text-muted)">
      Resetează filtrele
    </button>
  </div>
</template>
