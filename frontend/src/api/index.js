import api from './client'

export const authApi = {
  login: (email, password) => api.post('/auth/login', { email, password }),
  register: (name, email, password) => api.post('/auth/register', { name, email, password }),
}

export const userApi = {
  me: () => api.get('/users/me'),
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
  getSubmitted: () => api.get('/user-teams'),
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

export const notificationApi = {
  getAll: () => api.get('/notifications'),
  getUnreadCount: () => api.get('/notifications/unread-count'),
  markRead: (id) => api.patch(`/notifications/${id}/read`),
}
