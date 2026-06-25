package com.football.config;

import com.football.entity.*;
import com.football.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        migratePlayerData();
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
                {"Marc-André ter Stegen", "Ter Stegen", "GK",  8_000_000, 32, 187, "German",    "Right", 1,  false},
                {"Jules Koundé",          "Koundé",     "CB", 60_000_000, 25, 181, "French",    "Right", 23, true},
                {"Ronald Araújo",         "Araújo",     "CB", 55_000_000, 25, 188, "Uruguayan", "Right", 4,  true},
                {"Alejandro Balde",       "Balde",      "LB", 40_000_000, 20, 178, "Spanish",   "Left",  3,  true},
                {"Pedri",                 "Pedri",      "CM",100_000_000, 22, 174, "Spanish",   "Right", 8,  true},
                {"Frenkie de Jong",       "De Jong",    "CM", 65_000_000, 27, 181, "Dutch",     "Right", 21, false},
                {"Gavi",                  "Gavi",       "CAM",90_000_000, 20, 173, "Spanish",   "Right", 6,  false},
                {"Raphinha",              "Raphinha",   "RW", 70_000_000, 27, 176, "Brazilian", "Left",  11, true},
                {"Robert Lewandowski",    "Lewy",       "ST", 50_000_000, 36, 185, "Polish",    "Right", 9,  true},
                {"Ferran Torres",         "Ferran",     "LW", 40_000_000, 24, 182, "Spanish",   "Right", 7,  true},
        });

        // Arsenal
        PlayerTeam arsenal = club("Arsenal", 0, 13, 14);
        players(arsenal, new Object[][]{
                {"David Raya",           "Raya",        "GK",  20_000_000, 29, 183, "Spanish",   "Right", 22, true},
                {"Ben White",            "White",       "RB",  45_000_000, 26, 183, "English",   "Right", 4,  true},
                {"William Saliba",       "Saliba",      "CB",  80_000_000, 23, 192, "French",    "Right", 12, true},
                {"Gabriel Magalhães",    "Gabriel",     "CB",  55_000_000, 26, 190, "Brazilian", "Left",  6,  true},
                {"Oleksandr Zinchenko",  "Zinchenko",   "LB",  35_000_000, 27, 175, "Ukrainian", "Left",  35, false},
                {"Thomas Partey",        "Partey",      "CDM", 40_000_000, 31, 185, "Ghanaian",  "Right", 5,  false},
                {"Martin Ødegaard",      "Ødegaard",    "CAM",100_000_000, 25, 178, "Norwegian", "Right", 8,  true},
                {"Bukayo Saka",          "Saka",        "RW", 120_000_000, 22, 178, "English",   "Left",  7,  true},
                {"Leandro Trossard",     "Trossard",    "LW",  45_000_000, 29, 173, "Belgian",   "Right", 19, true},
                {"Gabriel Martinelli",   "Martinelli",  "ST",  70_000_000, 22, 181, "Brazilian", "Left",  11, true},
        });

        // Real Madrid
        PlayerTeam real = club("Real Madrid", 14, 35, 20);
        players(real, new Object[][]{
                {"Thibaut Courtois",    "Courtois",    "GK",  30_000_000, 32, 199, "Belgian",    "Right", 1,  false},
                {"Dani Carvajal",       "Carvajal",    "RB",  20_000_000, 32, 173, "Spanish",    "Right", 2,  false},
                {"Éder Militão",        "Militão",     "CB",  70_000_000, 26, 186, "Brazilian",  "Right", 3,  true},
                {"Antonio Rüdiger",     "Rüdiger",     "CB",  25_000_000, 31, 190, "German",     "Right", 22, true},
                {"Ferland Mendy",       "Mendy",       "LB",  30_000_000, 29, 180, "French",     "Left",  23, true},
                {"Aurélien Tchouaméni", "Tchouaméni",  "CDM", 80_000_000, 24, 188, "French",     "Right", 18, true},
                {"Luka Modrić",         "Modrić",      "CM",  10_000_000, 39, 172, "Croatian",   "Right", 10, true},
                {"Toni Kroos",          "Kroos",       "CM",   5_000_000, 34, 183, "German",     "Right", 8,  true},
                {"Vinícius Jr.",        "Vini Jr.",    "LW", 180_000_000, 24, 176, "Brazilian",  "Right", 7,  true},
                {"Jude Bellingham",     "Bellingham",  "CAM",200_000_000, 21, 186, "English",    "Right", 5,  true},
                {"Kylian Mbappé",       "Mbappé",      "ST", 180_000_000, 25, 178, "French",     "Right", 9,  true},
        });

        // Manchester City
        PlayerTeam mancity = club("Manchester City", 8, 9, 6);
        players(mancity, new Object[][]{
                {"Ederson",             "Ederson",     "GK",  40_000_000, 30, 188, "Brazilian",  "Right", 31, true},
                {"Kyle Walker",         "Walker",      "RB",  15_000_000, 34, 183, "English",    "Right", 2,  true},
                {"Rúben Dias",          "R. Dias",     "CB",  80_000_000, 27, 187, "Portuguese", "Right", 3,  true},
                {"Manuel Akanji",       "Akanji",      "CB",  50_000_000, 29, 187, "Swiss",      "Right", 25, true},
                {"Joško Gvardiol",      "Gvardiol",    "LB",  90_000_000, 22, 185, "Croatian",   "Left",  24, true},
                {"Rodri",               "Rodri",       "CDM",150_000_000, 28, 191, "Spanish",    "Right", 16, false},
                {"Kevin De Bruyne",     "KDB",         "CAM", 50_000_000, 33, 181, "Belgian",    "Right", 17, false},
                {"Bernardo Silva",      "Bernardo",    "CM",  80_000_000, 29, 173, "Portuguese", "Right", 20, true},
                {"Phil Foden",          "Foden",       "RW", 150_000_000, 24, 171, "English",    "Right", 47, true},
                {"Jeremy Doku",         "Doku",        "LW",  60_000_000, 22, 172, "Belgian",    "Right", 11, true},
                {"Erling Haaland",      "Haaland",     "ST", 200_000_000, 24, 194, "Norwegian",  "Left",  9,  true},
        });

        // Liverpool
        PlayerTeam liverpool = club("Liverpool", 6, 19, 8);
        players(liverpool, new Object[][]{
                {"Alisson Becker",          "Alisson",      "GK", 30_000_000, 31, 191, "Brazilian",  "Right", 1,  true},
                {"Trent Alexander-Arnold",  "TAA",          "RB", 80_000_000, 26, 175, "English",    "Right", 66, true},
                {"Virgil van Dijk",         "VVD",          "CB", 40_000_000, 33, 193, "Dutch",      "Right", 4,  true},
                {"Ibrahima Konaté",         "Konaté",       "CB", 60_000_000, 25, 194, "French",     "Right", 5,  true},
                {"Andy Robertson",          "Robertson",    "LB", 30_000_000, 30, 178, "Scottish",   "Left",  26, true},
                {"Alexis Mac Allister",     "Mac Allister", "CM", 70_000_000, 25, 174, "Argentine",  "Right", 10, true},
                {"Dominik Szoboszlai",      "Szoboszlai",   "CAM",70_000_000, 23, 186, "Hungarian",  "Right", 8,  true},
                {"Cody Gakpo",              "Gakpo",        "LW", 50_000_000, 25, 189, "Dutch",      "Right", 18, true},
                {"Mohamed Salah",           "Salah",        "RW", 50_000_000, 32, 175, "Egyptian",   "Left",  11, false},
                {"Darwin Núñez",            "Núñez",        "ST", 60_000_000, 25, 187, "Uruguayan",  "Left",  9,  true},
        });

        // Bayern Munich
        PlayerTeam bayern = club("Bayern Munich", 6, 32, 20);
        players(bayern, new Object[][]{
                {"Manuel Neuer",      "Neuer",      "GK",  5_000_000, 38, 193, "German",      "Right", 1,  false},
                {"Josip Stanisić",    "Stanisić",   "RB", 20_000_000, 24, 183, "Croatian",    "Right", 5,  true},
                {"Dayot Upamecano",   "Upamecano",  "CB", 55_000_000, 25, 186, "French",      "Right", 2,  true},
                {"Min-jae Kim",       "Kim",        "CB", 60_000_000, 28, 190, "South Korean","Right", 3,  true},
                {"Alphonso Davies",   "Davies",     "LB", 70_000_000, 23, 180, "Canadian",    "Left",  19, true},
                {"Joshua Kimmich",    "Kimmich",    "CDM",60_000_000, 29, 177, "German",      "Right", 6,  true},
                {"Leon Goretzka",     "Goretzka",   "CM", 40_000_000, 29, 189, "German",      "Right", 8,  false},
                {"Jamal Musiala",     "Musiala",    "CAM",150_000_000,21, 180, "German",      "Right", 42, true},
                {"Leroy Sané",        "Sané",       "RW", 50_000_000, 28, 183, "German",      "Right", 10, true},
                {"Thomas Müller",     "Müller",     "RW",  5_000_000, 34, 186, "German",      "Right", 25, true},
                {"Harry Kane",        "Kane",       "ST",100_000_000, 30, 188, "English",     "Right", 9,  true},
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

    // data columns: name, nickname, position, price, age, heightCm, nationality, preferredFoot, shirtNumber, available
    private void players(PlayerTeam team, Object[][] data) {
        for (Object[] d : data) {
            Player p = new Player();
            p.setName((String) d[0]);
            p.setNickname((String) d[1]);
            p.setPosition((String) d[2]);
            p.setPrice(BigDecimal.valueOf(((Number) d[3]).longValue()));
            p.setAge((Integer) d[4]);
            p.setHeightCm((Integer) d[5]);
            p.setNationality((String) d[6]);
            p.setPreferredFoot((String) d[7]);
            p.setShirtNumber((Integer) d[8]);
            p.setAvailable((Boolean) d[9]);
            p.setPlayerTeam(team);
            playerRepo.save(p);
        }
    }

    // ─── Migration: backfill personal details for existing players ────────────

    private void migratePlayerData() {
        boolean needsMigration = playerRepo.findAll().stream().anyMatch(p -> p.getAge() == null);
        if (!needsMigration) return;

        log.info("Migrating player personal data...");

        Map<String, Object[]> details = new HashMap<>();
        // Barcelona
        details.put("Marc-André ter Stegen", new Object[]{32, 187, "German",      "Right", 1,  false});
        details.put("Jules Koundé",           new Object[]{25, 181, "French",      "Right", 23, true});
        details.put("Ronald Araújo",          new Object[]{25, 188, "Uruguayan",   "Right", 4,  true});
        details.put("Alejandro Balde",        new Object[]{20, 178, "Spanish",     "Left",  3,  true});
        details.put("Pedri",                  new Object[]{22, 174, "Spanish",     "Right", 8,  true});
        details.put("Frenkie de Jong",        new Object[]{27, 181, "Dutch",       "Right", 21, false});
        details.put("Gavi",                   new Object[]{20, 173, "Spanish",     "Right", 6,  false});
        details.put("Raphinha",               new Object[]{27, 176, "Brazilian",   "Left",  11, true});
        details.put("Robert Lewandowski",     new Object[]{36, 185, "Polish",      "Right", 9,  true});
        details.put("Ferran Torres",          new Object[]{24, 182, "Spanish",     "Right", 7,  true});
        // Arsenal
        details.put("David Raya",             new Object[]{29, 183, "Spanish",     "Right", 22, true});
        details.put("Ben White",              new Object[]{26, 183, "English",     "Right", 4,  true});
        details.put("William Saliba",         new Object[]{23, 192, "French",      "Right", 12, true});
        details.put("Gabriel Magalhães",      new Object[]{26, 190, "Brazilian",   "Left",  6,  true});
        details.put("Oleksandr Zinchenko",    new Object[]{27, 175, "Ukrainian",   "Left",  35, false});
        details.put("Thomas Partey",          new Object[]{31, 185, "Ghanaian",    "Right", 5,  false});
        details.put("Martin Ødegaard",        new Object[]{25, 178, "Norwegian",   "Right", 8,  true});
        details.put("Bukayo Saka",            new Object[]{22, 178, "English",     "Left",  7,  true});
        details.put("Leandro Trossard",       new Object[]{29, 173, "Belgian",     "Right", 19, true});
        details.put("Gabriel Martinelli",     new Object[]{22, 181, "Brazilian",   "Left",  11, true});
        // Real Madrid
        details.put("Thibaut Courtois",       new Object[]{32, 199, "Belgian",     "Right", 1,  false});
        details.put("Dani Carvajal",          new Object[]{32, 173, "Spanish",     "Right", 2,  false});
        details.put("Éder Militão",           new Object[]{26, 186, "Brazilian",   "Right", 3,  true});
        details.put("Antonio Rüdiger",        new Object[]{31, 190, "German",      "Right", 22, true});
        details.put("Ferland Mendy",          new Object[]{29, 180, "French",      "Left",  23, true});
        details.put("Aurélien Tchouaméni",    new Object[]{24, 188, "French",      "Right", 18, true});
        details.put("Luka Modrić",            new Object[]{39, 172, "Croatian",    "Right", 10, true});
        details.put("Toni Kroos",             new Object[]{34, 183, "German",      "Right", 8,  true});
        details.put("Vinícius Jr.",           new Object[]{24, 176, "Brazilian",   "Right", 7,  true});
        details.put("Jude Bellingham",        new Object[]{21, 186, "English",     "Right", 5,  true});
        details.put("Kylian Mbappé",          new Object[]{25, 178, "French",      "Right", 9,  true});
        // Manchester City
        details.put("Ederson",                new Object[]{30, 188, "Brazilian",   "Right", 31, true});
        details.put("Kyle Walker",            new Object[]{34, 183, "English",     "Right", 2,  true});
        details.put("Rúben Dias",             new Object[]{27, 187, "Portuguese",  "Right", 3,  true});
        details.put("Manuel Akanji",          new Object[]{29, 187, "Swiss",       "Right", 25, true});
        details.put("Joško Gvardiol",         new Object[]{22, 185, "Croatian",    "Left",  24, true});
        details.put("Rodri",                  new Object[]{28, 191, "Spanish",     "Right", 16, false});
        details.put("Kevin De Bruyne",        new Object[]{33, 181, "Belgian",     "Right", 17, false});
        details.put("Bernardo Silva",         new Object[]{29, 173, "Portuguese",  "Right", 20, true});
        details.put("Phil Foden",             new Object[]{24, 171, "English",     "Right", 47, true});
        details.put("Jeremy Doku",            new Object[]{22, 172, "Belgian",     "Right", 11, true});
        details.put("Erling Haaland",         new Object[]{24, 194, "Norwegian",   "Left",  9,  true});
        // Liverpool
        details.put("Alisson Becker",         new Object[]{31, 191, "Brazilian",   "Right", 1,  true});
        details.put("Trent Alexander-Arnold", new Object[]{26, 175, "English",     "Right", 66, true});
        details.put("Virgil van Dijk",        new Object[]{33, 193, "Dutch",       "Right", 4,  true});
        details.put("Ibrahima Konaté",        new Object[]{25, 194, "French",      "Right", 5,  true});
        details.put("Andy Robertson",         new Object[]{30, 178, "Scottish",    "Left",  26, true});
        details.put("Alexis Mac Allister",    new Object[]{25, 174, "Argentine",   "Right", 10, true});
        details.put("Dominik Szoboszlai",     new Object[]{23, 186, "Hungarian",   "Right", 8,  true});
        details.put("Cody Gakpo",             new Object[]{25, 189, "Dutch",       "Right", 18, true});
        details.put("Mohamed Salah",          new Object[]{32, 175, "Egyptian",    "Left",  11, false});
        details.put("Darwin Núñez",           new Object[]{25, 187, "Uruguayan",   "Left",  9,  true});
        // Bayern Munich
        details.put("Manuel Neuer",           new Object[]{38, 193, "German",      "Right", 1,  false});
        details.put("Josip Stanisić",         new Object[]{24, 183, "Croatian",    "Right", 5,  true});
        details.put("Dayot Upamecano",        new Object[]{25, 186, "French",      "Right", 2,  true});
        details.put("Min-jae Kim",            new Object[]{28, 190, "South Korean","Right", 3,  true});
        details.put("Alphonso Davies",        new Object[]{23, 180, "Canadian",    "Left",  19, true});
        details.put("Joshua Kimmich",         new Object[]{29, 177, "German",      "Right", 6,  true});
        details.put("Leon Goretzka",          new Object[]{29, 189, "German",      "Right", 8,  false});
        details.put("Jamal Musiala",          new Object[]{21, 180, "German",      "Right", 42, true});
        details.put("Leroy Sané",             new Object[]{28, 183, "German",      "Right", 10, true});
        details.put("Thomas Müller",          new Object[]{34, 186, "German",      "Right", 25, true});
        details.put("Harry Kane",             new Object[]{30, 188, "English",     "Right", 9,  true});

        List<Player> players = playerRepo.findAll();
        for (Player p : players) {
            if (p.getAge() == null) {
                Object[] d = details.get(p.getName());
                if (d != null) {
                    p.setAge((Integer) d[0]);
                    p.setHeightCm((Integer) d[1]);
                    p.setNationality((String) d[2]);
                    p.setPreferredFoot((String) d[3]);
                    p.setShirtNumber((Integer) d[4]);
                    p.setAvailable((Boolean) d[5]);
                    playerRepo.save(p);
                }
            }
        }
        log.info("Player data migration complete");
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

    /** Set the 2B starting budget only for users who have never had one. */
    private void normalizeBudgets() {
        userRepo.findAll().forEach(user -> {
            if (user.getBudget() == null) {
                user.setBudget(2_000_000_000.0);
                userRepo.save(user);
            }
        });
    }
}
