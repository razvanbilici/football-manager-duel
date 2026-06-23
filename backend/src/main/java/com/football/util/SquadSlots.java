package com.football.util;

import com.football.entity.UserTeamPlayer;

import java.util.List;

/** Lineup slots 1–11; bench slots start at 12 (SQLite NOT NULL safe). */
public final class SquadSlots {

    public static final int LINEUP_MIN = 1;
    public static final int LINEUP_MAX = 11;
    public static final int BENCH_SLOT_START = 12;

    private SquadSlots() {}

    public static boolean isOnBench(Integer slotNumber) {
        return slotNumber == null || slotNumber >= BENCH_SLOT_START;
    }

    public static boolean isInLineup(Integer slotNumber) {
        return slotNumber != null
                && slotNumber >= LINEUP_MIN
                && slotNumber <= LINEUP_MAX;
    }

    public static int nextBenchSlot(List<UserTeamPlayer> squad) {
        return squad.stream()
                .map(UserTeamPlayer::getSlotNumber)
                .filter(s -> s != null && s >= BENCH_SLOT_START)
                .max(Integer::compareTo)
                .map(max -> max + 1)
                .orElse(BENCH_SLOT_START);
    }
}
