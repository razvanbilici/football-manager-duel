import api from './client'

export const authApi = {
  login: (email, password, rememberMe = false) => api.post('/auth/login', { email, password, rememberMe }),
  register: (name, email, password) => api.post('/auth/register', { name, email, password }),
}

export const userApi = {
  me: () => api.get('/users/me'),
  getById: (id) => api.get(`/users/${id}`),
  setFavourite: (clubId) => api.put(`/users/me/favourite-team/${clubId}`),
}

export const playerApi = {
  getAll: (params = {}) => api.get('/players', { params }),
  getById: (id) => api.get(`/players/${id}`),
}

export const clubApi = {
  getAll: () => api.get('/player-teams'),
  getById: (id) => api.get(`/player-teams/${id}`),
}

export const formationApi = {
  getAll: () => api.get('/formations'),
}

export const tacticApi = {
  getAll: () => api.get('/tactics'),
}

export const teamApi = {
  getSubmitted: (params = {}) => api.get('/user-teams', { params }),
  getById: (id) => api.get(`/user-teams/${id}`),
  update: (id, data) => api.patch(`/user-teams/${id}`, data),
  addPlayer: (id, playerId, slotNumber) =>
    api.post(`/user-teams/${id}/players`, { playerId, slotNumber }),
  removePlayer: (id, playerId) => api.delete(`/user-teams/${id}/players/${playerId}`),
  submit: (id) => api.post(`/user-teams/${id}/submit`),
}

export const voteApi = {
  get: (targetType, targetId) => api.get(`/votes/${targetType}/${targetId}`),
  cast: (targetType, targetId, voteType) =>
    api.post(`/votes/${targetType}/${targetId}`, { voteType }),
}

export const transferApi = {
  getRecent: () => api.get('/transfers'),
  buy: (playerId) => api.post(`/transfers/buy/${playerId}`),
  getListings: () => api.get('/transfers/listings'),
  getMyListings: () => api.get('/transfers/listings/mine'),
  createListing: (playerId, askingPrice) =>
    api.post('/transfers/listings', { playerId, askingPrice }),
  cancelListing: (id) => api.delete(`/transfers/listings/${id}`),
  createProposal: (playerId, fromUserTeamId, offeredPrice) =>
    api.post('/transfers/proposals', { playerId, fromUserTeamId, offeredPrice }),
  getIncomingProposals: () => api.get('/transfers/proposals/incoming'),
  getSentProposals: () => api.get('/transfers/proposals/sent'),
  acceptProposal: (id) => api.patch(`/transfers/proposals/${id}/accept`),
  rejectProposal: (id) => api.patch(`/transfers/proposals/${id}/reject`),
}

export const adminApi = {
  getUsers: () => api.get('/admin/users'),
  deleteUser: (id) => api.delete(`/admin/users/${id}`),
  setRole: (id, role) => api.patch(`/admin/users/${id}/role`, { role }),
  createPlayer: (data) => api.post('/admin/players', data),
  updatePlayer: (id, data) => api.patch(`/admin/players/${id}`, data),
  deletePlayer: (id) => api.delete(`/admin/players/${id}`),
  createClub: (data) => api.post('/admin/clubs', data),
}

export const profileApi = {
  update: (data) => api.patch('/users/me', data),
  changePassword: (data) => api.patch('/users/me/password', data),
  getStats: () => api.get('/users/me/stats'),
}

export const notificationApi = {
  getAll: () => api.get('/notifications'),
  getUnreadCount: () => api.get('/notifications/unread-count'),
  markRead: (id) => api.patch(`/notifications/${id}/read`),
}
