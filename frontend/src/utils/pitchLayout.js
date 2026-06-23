const ROW_TOP = {
  0: 86,
  1: 72,
  2: 58,
  3: 46,
  4: 34,
  5: 18,
}

const H_ORDER = {
  LWB: 0, LB: 5, LM: 10, LW: 15,
  CDM: 20, CM: 25, CAM: 30,
  CB: 35, GK: 35, ST: 40,
  RW: 45, RM: 50, RB: 55, RWB: 60,
}

function getRow(position) {
  const p = (position || '').toUpperCase()
  if (p === 'GK') return 0
  if (['LB', 'LWB', 'RB', 'RWB', 'CB'].includes(p)) return 1
  if (p === 'CDM') return 2
  if (['LM', 'RM', 'CM'].includes(p)) return 3
  if (p === 'CAM') return 4
  if (['LW', 'RW', 'ST'].includes(p)) return 5
  return 3
}

/**
 * Compute absolute pitch coordinates for each formation slot.
 * Returns array of { slotNumber, position, top, left, player }
 */
export function computePitchLayout(formationPositions, teamPlayers = []) {
  if (!formationPositions?.length) return []

  const bySlot = {}
  for (const tp of teamPlayers) {
    if (tp.slotNumber != null && tp.slotNumber >= 1 && tp.slotNumber <= 11) {
      bySlot[tp.slotNumber] = tp
    }
  }

  const slots = [...formationPositions].sort((a, b) => a.slotNumber - b.slotNumber)
  const byRow = {}

  for (const slot of slots) {
    const row = getRow(slot.position)
    if (!byRow[row]) byRow[row] = []
    byRow[row].push(slot)
  }

  const result = []
  for (const [rowKey, rowSlots] of Object.entries(byRow)) {
    const top = ROW_TOP[rowKey]
    const sorted = [...rowSlots].sort(
      (a, b) => (H_ORDER[a.position] ?? 30) - (H_ORDER[b.position] ?? 30),
    )
    const count = sorted.length

    sorted.forEach((slot, index) => {
      const left = count === 1 ? 50 : 12 + (76 / (count - 1)) * index
      result.push({
        slotNumber: slot.slotNumber,
        position: slot.position,
        top,
        left,
        player: bySlot[slot.slotNumber]?.player || null,
        teamPlayer: bySlot[slot.slotNumber] || null,
      })
    })
  }

  return result
}

export const BENCH_SLOT_START = 12

export function isOnBench(teamPlayer) {
  const slot = teamPlayer?.slotNumber
  return slot == null || slot >= BENCH_SLOT_START
}

export function isInLineup(teamPlayer) {
  const slot = teamPlayer?.slotNumber
  return slot != null && slot >= 1 && slot <= 11
}
