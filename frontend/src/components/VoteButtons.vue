<script setup>
import { ref, onMounted } from 'vue'
import { voteApi } from '../api'
import { useAuthStore } from '../stores/auth'

const props = defineProps({
  targetType: { type: String, required: true },
  targetId: { type: Number, required: true },
})

const auth = useAuthStore()
const upVotes = ref(0)
const downVotes = ref(0)
const myVote = ref(null)
const loading = ref(false)

async function loadVotes() {
  try {
    const { data } = await voteApi.get(props.targetType, props.targetId)
    upVotes.value = data.upVotes
    downVotes.value = data.downVotes
    myVote.value = data.myVote
  } catch {
    /* public endpoint may fail without auth for myVote only */
  }
}

async function vote(type) {
  if (!auth.isLoggedIn) return
  loading.value = true
  try {
    const { data } = await voteApi.cast(props.targetType, props.targetId, type)
    upVotes.value = data.upVotes
    downVotes.value = data.downVotes
    myVote.value = data.myVote
  } finally {
    loading.value = false
  }
}

onMounted(loadVotes)
</script>

<template>
  <div class="flex items-center gap-2">
    <button
      @click="vote('UP')"
      :disabled="!auth.isLoggedIn || loading"
      :class="[
        'px-2 py-1 rounded text-sm transition-colors',
        myVote === 'UP' ? 'bg-emerald-600' : 'bg-slate-800 hover:bg-slate-700',
        !auth.isLoggedIn && 'opacity-50 cursor-not-allowed',
      ]"
    >
      ▲ {{ upVotes }}
    </button>
    <button
      @click="vote('DOWN')"
      :disabled="!auth.isLoggedIn || loading"
      :class="[
        'px-2 py-1 rounded text-sm transition-colors',
        myVote === 'DOWN' ? 'bg-red-600' : 'bg-slate-800 hover:bg-slate-700',
        !auth.isLoggedIn && 'opacity-50 cursor-not-allowed',
      ]"
    >
      ▼ {{ downVotes }}
    </button>
  </div>
</template>
