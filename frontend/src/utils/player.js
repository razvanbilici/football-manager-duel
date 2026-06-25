const DEFENDER = new Set(['CB', 'LB', 'RB', 'RWB', 'LWB'])
const MIDFIELDER = new Set(['CM', 'CDM', 'CAM', 'RM', 'LM'])
const ATTACKER = new Set(['ST', 'LW', 'RW'])

export function getPlayerCategory(position) {
  const p = (position || '').toUpperCase()
  if (p === 'GK') return 'gk'
  if (DEFENDER.has(p)) return 'defender'
  if (MIDFIELDER.has(p)) return 'midfielder'
  if (ATTACKER.has(p)) return 'attacker'
  return 'midfielder'
}

export function getPlayerAvatar(position) {
  return `/assets/players/${getPlayerCategory(position)}.svg`
}

export function formatBudget(budget) {
  const val = Number(budget)
  if (val >= 1_000_000_000) return `€${(val / 1_000_000_000).toFixed(2)}B`
  if (val >= 1_000_000) return `€${(val / 1_000_000).toFixed(0)}M`
  return `€${(val / 1_000).toFixed(0)}K`
}

export function formatPrice(price) {
  const val = Number(price)
  if (val >= 1_000_000) return `€${(val / 1_000_000).toFixed(1)}M`
  return `€${(val / 1_000).toFixed(0)}K`
}

export function formatCompact(value) {
  if (value == null || isNaN(value)) return '—'
  const num = Number(value)
  if (num >= 1_000_000_000_000) return (num / 1_000_000_000_000).toFixed(2).replace(/\.00$/, '') + 'T'
  if (num >= 1_000_000_000) return (num / 1_000_000_000).toFixed(2).replace(/\.00$/, '') + 'B'
  if (num >= 1_000_000) return (num / 1_000_000).toFixed(1).replace(/\.0$/, '') + 'M'
  if (num >= 1_000) return (num / 1_000).toFixed(0) + 'K'
  return num.toLocaleString('ro-RO')
}
