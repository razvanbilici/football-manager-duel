<template>
  <div class="admin-view" style="max-width: 1200px; margin: 0 auto; padding: 2rem 1rem;">
    <div v-if="!auth.isAdmin" class="text-center py-20">
      <p style="color: var(--danger)">Acces interzis</p>
      <router-link to="/" class="btn-secondary mt-4 inline-block">Inapoi acasa</router-link>
    </div>
    <div v-else>
      <h1 class="page-title">Panou Admin</h1>
      <p class="page-subtitle">Gestioneaza jucatori, cluburi si utilizatori</p>

      <!-- Tabs -->
      <div style="display: flex; gap: 0; border-bottom: 1px solid var(--border); margin-bottom: 2rem;">
        <button
          v-for="tab in tabs"
          :key="tab.key"
          @click="activeTab = tab.key"
          style="padding: 0.75rem 1.5rem; background: none; border: none; cursor: pointer; font-size: 0.95rem; font-weight: 500; color: var(--text-muted); transition: color 0.15s;"
          :style="activeTab === tab.key ? 'color: var(--text-primary); border-bottom: 2px solid var(--accent);' : ''"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- TAB 1: Jucatori -->
      <div v-if="activeTab === 'players'">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;">
          <h2 style="font-size: 1.2rem; font-weight: 600;">Jucatori ({{ players.length }})</h2>
          <button class="btn-primary" @click="togglePlayerForm">
            {{ showPlayerForm ? 'Anuleaza' : 'Adauga jucator nou' }}
          </button>
        </div>

        <!-- Player form -->
        <div v-if="showPlayerForm" ref="playerFormRef" class="card" style="margin-bottom: 1.5rem;">
          <h3 style="font-size: 1rem; font-weight: 600; margin-bottom: 1.25rem; color: var(--accent);">
            {{ editingPlayer ? 'Editeaza jucator' : 'Jucator nou' }}
          </h3>
          <form @submit.prevent="submitPlayerForm" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 1rem;">
            <div>
              <label style="display: block; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.3rem;">Nume *</label>
              <input v-model="playerForm.name" class="input-field" placeholder="Ex: Lionel Messi" required />
            </div>
            <div>
              <label style="display: block; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.3rem;">Poreclă</label>
              <input v-model="playerForm.nickname" class="input-field" placeholder="Ex: La Pulga" />
            </div>
            <div>
              <label style="display: block; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.3rem;">Pozitie *</label>
              <select v-model="playerForm.position" class="input-field" required>
                <option value="">Selecteaza...</option>
                <option v-for="pos in positions" :key="pos" :value="pos">{{ pos }}</option>
              </select>
            </div>
            <div>
              <label style="display: block; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.3rem;">Pret (€)</label>
              <input v-model.number="playerForm.price" type="number" min="0" class="input-field" placeholder="Ex: 50000000" />
            </div>
            <div>
              <label style="display: block; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.3rem;">Varsta</label>
              <input v-model.number="playerForm.age" type="number" min="14" max="50" class="input-field" placeholder="Ex: 25" />
            </div>
            <div>
              <label style="display: block; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.3rem;">Inaltime (cm)</label>
              <input v-model.number="playerForm.heightCm" type="number" min="140" max="220" class="input-field" placeholder="Ex: 180" />
            </div>
            <div>
              <label style="display: block; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.3rem;">Nationalitate</label>
              <input v-model="playerForm.nationality" class="input-field" placeholder="Ex: Romanian" />
            </div>
            <div>
              <label style="display: block; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.3rem;">Picior preferat</label>
              <select v-model="playerForm.preferredFoot" class="input-field">
                <option value="">Selecteaza...</option>
                <option value="Left">Left</option>
                <option value="Right">Right</option>
                <option value="Both">Both</option>
              </select>
            </div>
            <div>
              <label style="display: block; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.3rem;">Numar tricou</label>
              <input v-model.number="playerForm.shirtNumber" type="number" min="1" max="99" class="input-field" placeholder="Ex: 10" />
            </div>
            <div>
              <label style="display: block; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.3rem;">Club</label>
              <select v-model="playerForm.playerTeamId" class="input-field">
                <option :value="null">Fara club</option>
                <option v-for="club in clubs" :key="club.id" :value="club.id">{{ club.name }}</option>
              </select>
            </div>
            <div style="display: flex; align-items: center; gap: 0.5rem; padding-top: 1.4rem;">
              <input id="available-check" v-model="playerForm.available" type="checkbox" style="width: 16px; height: 16px; accent-color: var(--accent);" />
              <label for="available-check" style="font-size: 0.9rem;">Disponibil</label>
            </div>
            <div style="grid-column: 1 / -1; display: flex; gap: 0.75rem; margin-top: 0.5rem;">
              <button type="submit" class="btn-primary" :disabled="playerFormLoading">
                {{ playerFormLoading ? 'Se salveaza...' : (editingPlayer ? 'Salveaza modificarile' : 'Adauga jucator') }}
              </button>
              <button type="button" @click="cancelPlayerForm" style="padding: 0.625rem 1.25rem; background: var(--bg-elevated); border: 1px solid var(--border); border-radius: 8px; color: var(--text-muted); cursor: pointer;">
                Anuleaza
              </button>
            </div>
          </form>
          <p v-if="playerFormSuccess" style="margin-top: 0.75rem; color: var(--success); font-size: 0.9rem;">{{ playerFormSuccess }}</p>
          <p v-if="playerFormError" style="margin-top: 0.75rem; color: var(--danger); font-size: 0.9rem;">{{ playerFormError }}</p>
        </div>

        <!-- Skeleton -->
        <div v-if="loadingPlayers" style="display: flex; flex-direction: column; gap: 0.5rem;">
          <div v-for="i in 8" :key="i" class="skeleton" style="height: 48px; border-radius: 8px;"></div>
        </div>

        <!-- Players table -->
        <div v-else class="card" style="padding: 0; overflow: hidden;">
          <div style="overflow-x: auto;">
            <table style="width: 100%; border-collapse: collapse; font-size: 0.875rem;">
              <thead>
                <tr style="border-bottom: 1px solid var(--border);">
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500; white-space: nowrap;">#</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Nume</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Pozitie</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Club</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Varsta</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Pret</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Disponibil</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Actiuni</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="players.length === 0">
                  <td colspan="8" style="padding: 2rem; text-align: center; color: var(--text-muted);">Niciun jucator gasit</td>
                </tr>
                <tr
                  v-for="player in players"
                  :key="player.id"
                  style="border-bottom: 1px solid var(--border); transition: background 0.1s;"
                  @mouseenter="e => e.currentTarget.style.background = 'var(--bg-elevated)'"
                  @mouseleave="e => e.currentTarget.style.background = ''"
                >
                  <td style="padding: 0.75rem 1rem; color: var(--text-muted);">{{ player.shirtNumber ?? '—' }}</td>
                  <td style="padding: 0.75rem 1rem; font-weight: 500;">{{ player.name }}</td>
                  <td style="padding: 0.75rem 1rem;">
                    <span :style="positionBadgeStyle(player.position)" style="padding: 0.2rem 0.5rem; border-radius: 6px; font-size: 0.75rem; font-weight: 600;">
                      {{ player.position }}
                    </span>
                  </td>
                  <td style="padding: 0.75rem 1rem; color: var(--text-muted);">{{ player.playerTeamName ?? '—' }}</td>
                  <td style="padding: 0.75rem 1rem;">{{ player.age ?? '—' }}</td>
                  <td style="padding: 0.75rem 1rem;">{{ player.price ? formatPrice(player.price) : '—' }}</td>
                  <td style="padding: 0.75rem 1rem;">
                    <span :style="player.available ? 'color: var(--success)' : 'color: var(--danger)'">
                      {{ player.available ? 'Da' : 'Nu' }}
                    </span>
                  </td>
                  <td style="padding: 0.75rem 1rem;">
                    <div style="display: flex; gap: 0.5rem;">
                      <button
                        @click="startEditPlayer(player)"
                        style="padding: 0.3rem 0.7rem; background: var(--bg-elevated); border: 1px solid var(--border); border-radius: 6px; color: var(--text-primary); cursor: pointer; font-size: 0.8rem;"
                      >Editeaza</button>
                      <button
                        @click="deletePlayer(player)"
                        style="padding: 0.3rem 0.7rem; background: transparent; border: 1px solid var(--danger); border-radius: 6px; color: var(--danger); cursor: pointer; font-size: 0.8rem;"
                      >Sterge</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- TAB 2: Cluburi -->
      <div v-if="activeTab === 'clubs'">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;">
          <h2 style="font-size: 1.2rem; font-weight: 600;">Cluburi ({{ clubs.length }})</h2>
          <button class="btn-primary" @click="toggleClubForm">
            {{ showClubForm ? 'Anuleaza' : 'Adauga club nou' }}
          </button>
        </div>

        <!-- Club form -->
        <div v-if="showClubForm" class="card" style="margin-bottom: 1.5rem;">
          <h3 style="font-size: 1rem; font-weight: 600; margin-bottom: 1.25rem; color: var(--accent);">Club nou</h3>
          <form @submit.prevent="submitClubForm" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 1rem;">
            <div>
              <label style="display: block; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.3rem;">Nume *</label>
              <input v-model="clubForm.name" class="input-field" placeholder="Ex: FC Barcelona" required />
            </div>
            <div>
              <label style="display: block; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.3rem;">UCL (trofee)</label>
              <input v-model.number="clubForm.ucl" type="number" min="0" class="input-field" placeholder="Ex: 5" />
            </div>
            <div>
              <label style="display: block; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.3rem;">Liga (trofee)</label>
              <input v-model.number="clubForm.league" type="number" min="0" class="input-field" placeholder="Ex: 26" />
            </div>
            <div>
              <label style="display: block; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 0.3rem;">Cupa (trofee)</label>
              <input v-model.number="clubForm.cup" type="number" min="0" class="input-field" placeholder="Ex: 31" />
            </div>
            <div style="grid-column: 1 / -1; display: flex; gap: 0.75rem; margin-top: 0.5rem;">
              <button type="submit" class="btn-primary" :disabled="clubFormLoading">
                {{ clubFormLoading ? 'Se salveaza...' : 'Adauga club' }}
              </button>
              <button type="button" @click="cancelClubForm" style="padding: 0.625rem 1.25rem; background: var(--bg-elevated); border: 1px solid var(--border); border-radius: 8px; color: var(--text-muted); cursor: pointer;">
                Anuleaza
              </button>
            </div>
          </form>
          <p v-if="clubFormSuccess" style="margin-top: 0.75rem; color: var(--success); font-size: 0.9rem;">{{ clubFormSuccess }}</p>
          <p v-if="clubFormError" style="margin-top: 0.75rem; color: var(--danger); font-size: 0.9rem;">{{ clubFormError }}</p>
        </div>

        <!-- Skeleton -->
        <div v-if="loadingClubs" style="display: flex; flex-direction: column; gap: 0.5rem;">
          <div v-for="i in 6" :key="i" class="skeleton" style="height: 48px; border-radius: 8px;"></div>
        </div>

        <!-- Clubs table -->
        <div v-else class="card" style="padding: 0; overflow: hidden;">
          <div style="overflow-x: auto;">
            <table style="width: 100%; border-collapse: collapse; font-size: 0.875rem;">
              <thead>
                <tr style="border-bottom: 1px solid var(--border);">
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Nume</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Jucatori</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">UCL</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Liga</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Cupa</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="clubs.length === 0">
                  <td colspan="5" style="padding: 2rem; text-align: center; color: var(--text-muted);">Niciun club gasit</td>
                </tr>
                <tr
                  v-for="club in clubs"
                  :key="club.id"
                  style="border-bottom: 1px solid var(--border); transition: background 0.1s;"
                  @mouseenter="e => e.currentTarget.style.background = 'var(--bg-elevated)'"
                  @mouseleave="e => e.currentTarget.style.background = ''"
                >
                  <td style="padding: 0.75rem 1rem; font-weight: 500;">{{ club.name }}</td>
                  <td style="padding: 0.75rem 1rem; color: var(--text-muted);">{{ club.playerCount ?? '—' }}</td>
                  <td style="padding: 0.75rem 1rem;">{{ club.ucl ?? '—' }}</td>
                  <td style="padding: 0.75rem 1rem;">{{ club.league ?? '—' }}</td>
                  <td style="padding: 0.75rem 1rem;">{{ club.cup ?? '—' }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- TAB 3: Utilizatori -->
      <div v-if="activeTab === 'users'">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.25rem;">
          <h2 style="font-size: 1.2rem; font-weight: 600;">Utilizatori ({{ users.length }})</h2>
        </div>

        <p v-if="usersActionSuccess" style="margin-bottom: 1rem; color: var(--success); font-size: 0.9rem;">{{ usersActionSuccess }}</p>
        <p v-if="usersActionError" style="margin-bottom: 1rem; color: var(--danger); font-size: 0.9rem;">{{ usersActionError }}</p>

        <!-- Skeleton -->
        <div v-if="loadingUsers" style="display: flex; flex-direction: column; gap: 0.5rem;">
          <div v-for="i in 6" :key="i" class="skeleton" style="height: 48px; border-radius: 8px;"></div>
        </div>

        <!-- Users table -->
        <div v-else class="card" style="padding: 0; overflow: hidden;">
          <div style="overflow-x: auto;">
            <table style="width: 100%; border-collapse: collapse; font-size: 0.875rem;">
              <thead>
                <tr style="border-bottom: 1px solid var(--border);">
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">ID</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Nume</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Email</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Rol</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Buget</th>
                  <th style="padding: 0.875rem 1rem; text-align: left; color: var(--text-muted); font-weight: 500;">Actiuni</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="users.length === 0">
                  <td colspan="6" style="padding: 2rem; text-align: center; color: var(--text-muted);">Niciun utilizator gasit</td>
                </tr>
                <tr
                  v-for="user in users"
                  :key="user.id"
                  style="border-bottom: 1px solid var(--border); transition: background 0.1s;"
                  @mouseenter="e => e.currentTarget.style.background = 'var(--bg-elevated)'"
                  @mouseleave="e => e.currentTarget.style.background = ''"
                >
                  <td style="padding: 0.75rem 1rem; color: var(--text-muted); font-size: 0.8rem;">{{ user.id }}</td>
                  <td style="padding: 0.75rem 1rem; font-weight: 500;">{{ user.name }}</td>
                  <td style="padding: 0.75rem 1rem; color: var(--text-muted);">{{ user.email }}</td>
                  <td style="padding: 0.75rem 1rem;">
                    <span
                      :style="user.role === 'ADMIN'
                        ? 'background: var(--accent); color: #0d1117;'
                        : 'background: var(--bg-elevated); color: var(--text-muted);'"
                      style="padding: 0.2rem 0.6rem; border-radius: 6px; font-size: 0.75rem; font-weight: 600;"
                    >{{ user.role }}</span>
                  </td>
                  <td style="padding: 0.75rem 1rem;">{{ user.budget != null ? formatPrice(user.budget) : '—' }}</td>
                  <td style="padding: 0.75rem 1rem;">
                    <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                      <button
                        v-if="user.role === 'USER'"
                        @click="setUserRole(user.id, 'ADMIN')"
                        style="padding: 0.3rem 0.7rem; background: var(--accent); color: #0d1117; border: none; border-radius: 6px; cursor: pointer; font-size: 0.8rem; font-weight: 600;"
                      >Fa Admin</button>
                      <button
                        v-if="user.role === 'ADMIN'"
                        @click="setUserRole(user.id, 'USER')"
                        style="padding: 0.3rem 0.7rem; background: var(--bg-elevated); border: 1px solid var(--border); border-radius: 6px; color: var(--text-primary); cursor: pointer; font-size: 0.8rem;"
                      >Fa User</button>
                      <button
                        @click="deleteUser(user)"
                        :disabled="user.id === auth.user?.id"
                        :style="user.id === auth.user?.id
                          ? 'opacity: 0.4; cursor: not-allowed; padding: 0.3rem 0.7rem; background: transparent; border: 1px solid var(--border); border-radius: 6px; color: var(--text-muted); font-size: 0.8rem;'
                          : 'padding: 0.3rem 0.7rem; background: transparent; border: 1px solid var(--danger); border-radius: 6px; color: var(--danger); cursor: pointer; font-size: 0.8rem;'"
                      >Sterge</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <!-- TAB 4: Prezentare generala -->
      <div v-if="activeTab === 'overview'">
        <h2 style="font-size: 1.2rem; font-weight: 600; margin-bottom: 1.5rem;">Prezentare generala</h2>

        <div v-if="users.length === 0 && players.length === 0 && clubs.length === 0"
          style="color: var(--text-muted); padding: 3rem; text-align: center; background: var(--bg-surface); border: 1px solid var(--border); border-radius: 12px;">
          Incarca datele din celelalte tab-uri mai intai
        </div>

        <div v-else style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 1.25rem;">
          <div class="card" style="display: flex; flex-direction: column; gap: 0.5rem;">
            <div style="font-size: 2rem;">👤</div>
            <div style="font-size: 2.25rem; font-weight: 700; color: var(--accent);">{{ users.length }}</div>
            <div style="font-size: 0.9rem; color: var(--text-muted);">Total utilizatori</div>
          </div>
          <div class="card" style="display: flex; flex-direction: column; gap: 0.5rem;">
            <div style="font-size: 2rem;">⚽</div>
            <div style="font-size: 2.25rem; font-weight: 700; color: var(--accent);">{{ players.length }}</div>
            <div style="font-size: 0.9rem; color: var(--text-muted);">Total jucatori</div>
          </div>
          <div class="card" style="display: flex; flex-direction: column; gap: 0.5rem;">
            <div style="font-size: 2rem;">🏆</div>
            <div style="font-size: 2.25rem; font-weight: 700; color: var(--accent);">{{ clubs.length }}</div>
            <div style="font-size: 0.9rem; color: var(--text-muted);">Total cluburi</div>
          </div>
          <div class="card" style="display: flex; flex-direction: column; gap: 0.5rem;">
            <div style="font-size: 2rem;">⚙</div>
            <div style="font-size: 1.5rem; font-weight: 700; color: var(--accent); word-break: break-word;">{{ auth.user?.name ?? '—' }}</div>
            <div style="font-size: 0.9rem; color: var(--text-muted);">Administrator activ</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { useAuthStore } from '../stores/auth'
import { playerApi, clubApi, adminApi } from '../api'
import { formatPrice } from '../utils/player'
import { getPlayerCategory } from '../utils/player'

const auth = useAuthStore()

const tabs = [
  { key: 'players', label: 'Jucatori' },
  { key: 'clubs', label: 'Cluburi' },
  { key: 'users', label: 'Utilizatori' },
  { key: 'overview', label: 'Prezentare generala' },
]
const activeTab = ref('players')

// ── Players ──
const players = ref([])
const clubs = ref([])
const loadingPlayers = ref(false)
const loadingClubs = ref(false)

const showPlayerForm = ref(false)
const editingPlayer = ref(null)
const playerFormLoading = ref(false)
const playerFormSuccess = ref('')
const playerFormError = ref('')
const playerFormRef = ref(null)

const positions = ['GK', 'CB', 'LB', 'RB', 'CDM', 'CM', 'CAM', 'LW', 'RW', 'ST', 'CF']

const emptyPlayerForm = () => ({
  name: '',
  nickname: '',
  position: '',
  price: null,
  age: null,
  heightCm: null,
  nationality: '',
  preferredFoot: '',
  shirtNumber: null,
  available: true,
  playerTeamId: null,
})

const playerForm = reactive(emptyPlayerForm())

async function loadPlayers() {
  loadingPlayers.value = true
  try {
    const { data } = await playerApi.getAll({ size: 500 })
    players.value = data.content ?? data
  } catch {
    // silent
  } finally {
    loadingPlayers.value = false
  }
}

async function loadClubs() {
  loadingClubs.value = true
  try {
    const { data } = await clubApi.getAll()
    clubs.value = Array.isArray(data) ? data : (data.content ?? [])
  } catch {
    // silent
  } finally {
    loadingClubs.value = false
  }
}

function togglePlayerForm() {
  if (showPlayerForm.value) {
    cancelPlayerForm()
  } else {
    editingPlayer.value = null
    Object.assign(playerForm, emptyPlayerForm())
    showPlayerForm.value = true
  }
}

function cancelPlayerForm() {
  showPlayerForm.value = false
  editingPlayer.value = null
  playerFormSuccess.value = ''
  playerFormError.value = ''
  Object.assign(playerForm, emptyPlayerForm())
}

function startEditPlayer(player) {
  editingPlayer.value = player
  Object.assign(playerForm, {
    name: player.name ?? '',
    nickname: player.nickname ?? '',
    position: player.position ?? '',
    price: player.price ?? null,
    age: player.age ?? null,
    heightCm: player.heightCm ?? null,
    nationality: player.nationality ?? '',
    preferredFoot: player.preferredFoot ?? '',
    shirtNumber: player.shirtNumber ?? null,
    available: player.available ?? true,
    playerTeamId: player.playerTeamId ?? null,
  })
  playerFormSuccess.value = ''
  playerFormError.value = ''
  showPlayerForm.value = true
  nextTick(() => {
    playerFormRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  })
}

async function submitPlayerForm() {
  playerFormLoading.value = true
  playerFormSuccess.value = ''
  playerFormError.value = ''
  try {
    const payload = { ...playerForm }
    if (editingPlayer.value) {
      await adminApi.updatePlayer(editingPlayer.value.id, payload)
      playerFormSuccess.value = 'Jucatorul a fost actualizat cu succes!'
    } else {
      await adminApi.createPlayer(payload)
      playerFormSuccess.value = 'Jucatorul a fost adaugat cu succes!'
    }
    await loadPlayers()
    setTimeout(() => {
      cancelPlayerForm()
    }, 1500)
  } catch (err) {
    playerFormError.value = err?.response?.data?.message ?? 'A aparut o eroare. Incearca din nou.'
  } finally {
    playerFormLoading.value = false
  }
}

async function deletePlayer(player) {
  if (!window.confirm(`Stergi jucatorul "${player.name}"? Aceasta actiune nu poate fi anulata.`)) return
  try {
    await adminApi.deletePlayer(player.id)
    await loadPlayers()
  } catch (err) {
    alert(err?.response?.data?.message ?? 'Eroare la stergere.')
  }
}

// ── Clubs ──
const showClubForm = ref(false)
const clubFormLoading = ref(false)
const clubFormSuccess = ref('')
const clubFormError = ref('')

const emptyClubForm = () => ({ name: '', ucl: null, league: null, cup: null })
const clubForm = reactive(emptyClubForm())

function toggleClubForm() {
  if (showClubForm.value) {
    cancelClubForm()
  } else {
    Object.assign(clubForm, emptyClubForm())
    showClubForm.value = true
  }
}

function cancelClubForm() {
  showClubForm.value = false
  clubFormSuccess.value = ''
  clubFormError.value = ''
  Object.assign(clubForm, emptyClubForm())
}

async function submitClubForm() {
  clubFormLoading.value = true
  clubFormSuccess.value = ''
  clubFormError.value = ''
  try {
    await adminApi.createClub({ ...clubForm })
    clubFormSuccess.value = 'Clubul a fost adaugat cu succes!'
    await loadClubs()
    setTimeout(() => {
      cancelClubForm()
    }, 1500)
  } catch (err) {
    clubFormError.value = err?.response?.data?.message ?? 'A aparut o eroare. Incearca din nou.'
  } finally {
    clubFormLoading.value = false
  }
}

// ── Users ──
const users = ref([])
const loadingUsers = ref(false)
const usersLoaded = ref(false)
const usersActionSuccess = ref('')
const usersActionError = ref('')

async function loadUsers() {
  loadingUsers.value = true
  usersActionSuccess.value = ''
  usersActionError.value = ''
  try {
    const { data } = await adminApi.getUsers()
    users.value = Array.isArray(data) ? data : (data.content ?? [])
    usersLoaded.value = true
  } catch {
    // silent
  } finally {
    loadingUsers.value = false
  }
}

async function setUserRole(id, role) {
  usersActionSuccess.value = ''
  usersActionError.value = ''
  try {
    await adminApi.setRole(id, role)
    usersActionSuccess.value = `Rolul a fost schimbat in ${role} cu succes!`
    await loadUsers()
  } catch (err) {
    usersActionError.value = err?.response?.data?.message ?? 'Eroare la schimbarea rolului.'
  }
}

async function deleteUser(user) {
  if (!window.confirm(`Stergi utilizatorul "${user.name}"? Aceasta actiune nu poate fi anulata.`)) return
  usersActionSuccess.value = ''
  usersActionError.value = ''
  try {
    await adminApi.deleteUser(user.id)
    usersActionSuccess.value = 'Utilizatorul a fost sters cu succes!'
    await loadUsers()
  } catch (err) {
    usersActionError.value = err?.response?.data?.message ?? 'Eroare la stergere.'
  }
}

watch(activeTab, (tab) => {
  if (tab === 'users' && !usersLoaded.value) {
    loadUsers()
  }
})

// ── Position badge ──
const POSITION_COLORS = {
  GK: { bg: '#e3b34133', color: 'var(--warning)' },
  CB: { bg: '#3fb95033', color: 'var(--success)' },
  LB: { bg: '#3fb95033', color: 'var(--success)' },
  RB: { bg: '#3fb95033', color: 'var(--success)' },
  CDM: { bg: '#00d4aa22', color: 'var(--accent)' },
  CM: { bg: '#00d4aa22', color: 'var(--accent)' },
  CAM: { bg: '#00d4aa22', color: 'var(--accent)' },
  LW: { bg: '#f8514933', color: 'var(--danger)' },
  RW: { bg: '#f8514933', color: 'var(--danger)' },
  ST: { bg: '#f8514933', color: 'var(--danger)' },
  CF: { bg: '#f8514933', color: 'var(--danger)' },
}

function positionBadgeStyle(pos) {
  const c = POSITION_COLORS[pos] ?? { bg: 'var(--bg-elevated)', color: 'var(--text-muted)' }
  return `background: ${c.bg}; color: ${c.color};`
}

onMounted(async () => {
  if (!auth.isAdmin) return
  await Promise.all([loadPlayers(), loadClubs()])
})
</script>
