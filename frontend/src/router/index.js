import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  { path: '/', name: 'home', component: () => import('../views/HomeView.vue') },
  { path: '/login', name: 'login', component: () => import('../views/LoginView.vue') },
  { path: '/register', name: 'register', component: () => import('../views/RegisterView.vue') },
  { path: '/players', name: 'players', component: () => import('../views/PlayersView.vue') },
  { path: '/clubs', name: 'clubs', component: () => import('../views/ClubsView.vue') },
  { path: '/clubs/:id', name: 'club-detail', component: () => import('../views/ClubDetailView.vue') },
  { path: '/my-team', name: 'my-team', component: () => import('../views/MyTeamView.vue'), meta: { requiresAuth: true } },
  { path: '/community', name: 'community', component: () => import('../views/CommunityView.vue') },
  { path: '/goat', name: 'goat', component: () => import('../views/AboutView.vue') },
  { path: '/transfers', name: 'transfers', component: () => import('../views/TransfersView.vue') },
  { path: '/profile', name: 'profile', component: () => import('../views/ProfileView.vue'), meta: { requiresAuth: true } },
  { path: '/admin', name: 'admin', component: () => import('../views/AdminView.vue'), meta: { requiresAuth: true } },
  { path: '/user/:id', name: 'user-profile', component: () => import('../views/UserProfileView.vue') },
  { path: '/:pathMatch(.*)*', name: 'not-found', component: () => import('../views/NotFoundView.vue') },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
})

export default router
