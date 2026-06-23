package com.football.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "formation_position")
@Data
@NoArgsConstructor
public class FormationPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id", nullable = false)
    private TeamFormation formation;

    @Column(name = "slot_number", nullable = false)
    private Integer slotNumber; // 1..11

    @Column(nullable = false)
    private String position; // GK, CB, LB, RB, CM, CAM, LW, RW, ST, etc.
}
