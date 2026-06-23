package com.football.config;

import com.football.entity.*;
import com.football.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final TeamFormationRepository formationRepo;
    private final TacticRepository tacticRepo;
    private final PlayerTeamRepository playerTeamRepo;
    private final PlayerRepository playerRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedFormations();
        seedTactics();
        seedClubs();
        seedAdmin();
        normalizeBudgets();
        log.info("✅  Data seeding complete");
    }

    // ─── Formations ───────────────────────────────────────────────────────────

    private void seedFormations() {
        if (formationRepo.count() > 0) return;

        log.info("Seeding formations...");

        // 4-3-3
        TeamFormation f433 = new TeamFormation();
        f433.setName("4-3-3");
        f433.setDescription("Classic attacking formation with wide forwards");
        f433 = formationRepo.save(f433);
        addPositions(f433, new String[][]{
                {"1", "GK"}, {"2", "RB"}, {"3", "CB"}, {"4", "CB"}, {"5", "LB"},
                {"6", "CM"}, {"7", "CM"}, {"8", "CM"},
                {"9", "RW"}, {"10", "ST"}, {"11", "LW"}
        });

        // 4-4-2
        TeamFormation f442 = new TeamFormation();
        f442.setName("4-4-2");
        f442.setDescription("Traditional balanced formation");
        f442 = formationRepo.save(f442);
        addPositions(f442, new String[][]{
                {"1", "GK"}, {"2", "RB"}, {"3", "CB"}, {"4", "CB"}, {"5", "LB"},
                {"6", "RM"}, {"7", "CM"}, {"8", "CM"}, {"9", "LM"},
                {"10", "ST"}, {"11", "ST"}
        });

        // 4-2-3-1
        TeamFormation f4231 = new TeamFormation();
        f4231.setName("4-2-3-1");
        f4231.setDescription("Defensive midfield shield with an attacking trio");
        f4231 = formationRepo.save(f4231);
        addPositions(f4231, new String[][]{
                {"1", "GK"}, {"2", "RB"}, {"3", "CB"}, {"4", "CB"}, {"5", "LB"},
                {"6", "CDM"}, {"7", "CDM"},
                {"8", "CAM"}, {"9", "CAM"}, {"10", "CAM"},
                {"11", "ST"}
        });

        // 3-5-2
        TeamFormation f352 = new TeamFormation();
        f352.setName("3-5-2");
        f352.setDescription("Three at the back with wing-backs");
        f352 = formationRepo.save(f352);
        addPositions(f352, new String[][]{
                {"1", "GK"}, {"2", "CB"}, {"3", "CB"}, {"4", "CB"},
                {"5", "RWB"}, {"6", "CM"}, {"7", "CM"}, {"8", "CM"}, {"9", "LWB"},
                {"10", "ST"}, {"11", "ST"}
        });

        // 5-3-2
        TeamFormation f532 = new TeamFormation();
        f532.setName("5-3-2");
        f532.setDescription("Solid defensive formation");
        f532 = formationRepo.save(f532);
        addPositions(f532, new String[][]{
                {"1", "GK"}, {"2", "RB"}, {"3", "CB"}, {"4", "CB"}, {"5", "CB"}, {"6", "LB"},
                {"7", "CM"}, {"8", "CM"}, {"9", "CM"},
                {"10", "ST"}, {"11", "ST"}
        });
    }

    private void addPositions(TeamFormation formation, String[][] slots) {
        for (String[] slot : slots) {
            FormationPosition fp = new FormationPosition();
            fp.setFormation(formation);
            fp.setSlotNumber(Integer.parseInt(slot[0]));
            fp.setPosition(slot[1]);
            formation.getPositions().add(fp);
        }
        formationRepo.save(formation);
    }

    // ─── Tactics ──────────────────────────────────────────────────────────────

    private void seedTactics() {
        if (tacticRepo.count() > 0) return;

        log.info("Seeding tactics...");

        tactic("High Press", Tactic.Style.OFFENSIVE,
                "Aggressive pressing high up the pitch, quick transitions");
        tactic("Tiki-Taka", Tactic.Style.OFFENSIVE,
                "Short passing and movement, maintain possession");
        tactic("Counter-Attack", Tactic.Style.BALANCED,
                "Sit deep and exploit spaces on the break");
        tactic("Gegenpress", Tactic.Style.OFFENSIVE,
                "Immediate press to win the ball back after losing it");
        tactic("Park the Bus", Tactic.Style.DEFENSIVE,
                "Deep defensive block, prioritise not conceding");
        tactic("Low Block", Tactic.Style.DEFENSIVE,
                "Compact defensive shape, limited attacking intent");
        tactic("Possession Play", Tactic.Style.BALANCED,
                "Control tempo through ball retention");
    }

    private void tactic(String details, Tactic.Style style, String desc) {
        Tactic t = new Tactic();
        t.setDetails(details + " – " + desc);
        t.setStyle(style);
        tacticRepo.save(t);
    }

    // ─── Default clubs & players ──────────────────────────────────────────────

    private void seedClubs() {
        if (playerTeamRepo.count() > 0) return;

        log.info("Seeding default clubs and players...");

        // Barcelona
        PlayerTeam barca = club("Barcelona", 5, 27, 31);
        players(barca, new Object[][]{
                {"Marc-André ter Stegen", "Ter Stegen", "GK", 8_000_000},
                {"Jules Koundé",         "Koundé",     "CB", 60_000_000},
                {"Ronald Araújo",        "Araújo",     "CB", 55_000_000},
                {"Alejandro Balde",      "Balde",      "LB", 40_000_000},
                {"Pedri",                "Pedri",      "CM", 100_000_000},
                {"Frenkie de Jong",      "De Jong",    "CM", 65_000_000},
                {"Gavi",                 "Gavi",       "CAM",90_000_000},
                {"Raphinha",             "Raphinha",   "RW", 70_000_000},
                {"Robert Lewandowski",   "Lewy",       "ST", 50_000_000},
                {"Ferran Torres",        "Ferran",     "LW", 40_000_000},
        });

        // Arsenal
        PlayerTeam arsenal = club("Arsenal", 0, 13, 14);
        players(arsenal, new Object[][]{
                {"David Raya",           "Raya",       "GK",  20_000_000},
                {"Ben White",            "White",      "RB",  45_000_000},
                {"William Saliba",       "Saliba",     "CB",  80_000_000},
                {"Gabriel Magalhães",    "Gabriel",    "CB",  55_000_000},
                {"Oleksandr Zinchenko",  "Zinchenko",  "LB",  35_000_000},
                {"Thomas Partey",        "Partey",     "CDM", 40_000_000},
                {"Martin Ødegaard",      "Ødegaard",   "CAM",100_000_000},
                {"Bukayo Saka",          "Saka",       "RW", 120_000_000},
                {"Leandro Trossard",     "Trossard",   "LW",  45_000_000},
                {"Gabriel Martinelli",   "Martinelli", "ST",  70_000_000},
        });

        // Real Madrid
        PlayerTeam real = club("Real Madrid", 14, 35, 20);
        players(real, new Object[][]{
                {"Thibaut Courtois",     "Courtois",   "GK",  30_000_000},
                {"Dani Carvajal",        "Carvajal",   "RB",  20_000_000},
                {"Éder Militão",         "Militão",    "CB",  70_000_000},
                {"Antonio Rüdiger",      "Rüdiger",    "CB",  25_000_000},
                {"Ferland Mendy",        "Mendy",      "LB",  30_000_000},
                {"Aurélien Tchouaméni",  "Tchouaméni", "CDM", 80_000_000},
                {"Luka Modrić",          "Modrić",     "CM",  10_000_000},
                {"Toni Kroos",           "Kroos",      "CM",  5_000_000},
                {"Vinícius Jr.",         "Vini Jr.",   "LW",  180_000_000},
                {"Jude Bellingham",      "Bellingham", "CAM", 200_000_000},
                {"Kylian Mbappé",        "Mbappé",     "ST",  180_000_000},
        });

        // Manchester City
        PlayerTeam mancity = club("Manchester City", 8, 9, 6);
        players(mancity, new Object[][]{
                {"Ederson",              "Ederson",    "GK",  40_000_000},
                {"Kyle Walker",          "Walker",     "RB",  15_000_000},
                {"Rúben Dias",           "R. Dias",    "CB",  80_000_000},
                {"Manuel Akanji",        "Akanji",     "CB",  50_000_000},
                {"Joško Gvardiol",       "Gvardiol",   "LB",  90_000_000},
                {"Rodri",                "Rodri",      "CDM", 150_000_000},
                {"Kevin De Bruyne",      "KDB",        "CAM", 50_000_000},
                {"Bernardo Silva",       "Bernardo",   "CM",  80_000_000},
                {"Phil Foden",           "Foden",      "RW",  150_000_000},
                {"Jeremy Doku",          "Doku",       "LW",  60_000_000},
                {"Erling Haaland",       "Haaland",    "ST",  200_000_000},
        });

        // Liverpool
        PlayerTeam liverpool = club("Liverpool", 6, 19, 8);
        players(liverpool, new Object[][]{
                {"Alisson Becker",       "Alisson",    "GK",  30_000_000},
                {"Trent Alexander-Arnold","TAA",       "RB",  80_000_000},
                {"Virgil van Dijk",      "VVD",        "CB",  40_000_000},
                {"Ibrahima Konaté",      "Konaté",     "CB",  60_000_000},
                {"Andy Robertson",       "Robertson",  "LB",  30_000_000},
                {"Alexis Mac Allister",  "Mac Allister","CM", 70_000_000},
                {"Dominik Szoboszlai",   "Szoboszlai", "CAM", 70_000_000},
                {"Cody Gakpo",           "Gakpo",      "LW",  50_000_000},
                {"Mohamed Salah",        "Salah",      "RW",  50_000_000},
                {"Darwin Núñez",         "Núñez",      "ST",  60_000_000},
        });

        // Bayern Munich
        PlayerTeam bayern = club("Bayern Munich", 6, 32, 20);
        players(bayern, new Object[][]{
                {"Manuel Neuer",         "Neuer",      "GK",  5_000_000},
                {"Josip Stanisić",       "Stanisić",   "RB",  20_000_000},
                {"Dayot Upamecano",      "Upamecano",  "CB",  55_000_000},
                {"Min-jae Kim",          "Kim",        "CB",  60_000_000},
                {"Alphonso Davies",      "Davies",     "LB",  70_000_000},
                {"Joshua Kimmich",       "Kimmich",    "CDM", 60_000_000},
                {"Leon Goretzka",        "Goretzka",   "CM",  40_000_000},
                {"Jamal Musiala",        "Musiala",    "CAM", 150_000_000},
                {"Leroy Sané",           "Sané",       "RW",  50_000_000},
                {"Thomas Müller",        "Müller",     "RW",  5_000_000},
                {"Harry Kane",           "Kane",       "ST",  100_000_000},
        });
    }

    private PlayerTeam club(String name, int ucl, int league, int cup) {
        PlayerTeam pt = new PlayerTeam();
        pt.setName(name);
        pt.setUcl(ucl);
        pt.setLeague(league);
        pt.setCup(cup);
        return playerTeamRepo.save(pt);
    }

    private void players(PlayerTeam team, Object[][] data) {
        for (Object[] d : data) {
            Player p = new Player();
            p.setName((String) d[0]);
            p.setNickname((String) d[1]);
            p.setPosition((String) d[2]);
            p.setPrice(BigDecimal.valueOf(((Number) d[3]).longValue()));
            p.setPlayerTeam(team);
            playerRepo.save(p);
        }
    }

    // ─── Admin user ───────────────────────────────────────────────────────────

    private void seedAdmin() {
        if (userRepo.existsByEmail("admin@football.com")) return;

        log.info("Seeding admin user...");
        User admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@football.com");
        admin.setPassword(passwordEncoder.encode("admin1234"));
        admin.setRole(User.Role.ADMIN);
        admin.setBudget(2_000_000_000.0);

        UserTeam adminTeam = new UserTeam();
        adminTeam.setName("Admin Team");
        admin.setUserTeam(adminTeam);

        userRepo.save(admin);
    }

    /** Ensure all users have the 2B starting budget (dev convenience). */
    private void normalizeBudgets() {
        userRepo.findAll().forEach(user -> {
            if (user.getBudget() == null || user.getBudget() < 2_000_000_000.0) {
                user.setBudget(2_000_000_000.0);
                userRepo.save(user);
            }
        });
    }
}
