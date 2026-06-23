package com.football.repository;

import com.football.entity.UserTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, Long> {
    @Query("SELECT ut FROM UserTeam ut WHERE ut.submitted = true ORDER BY ut.votes DESC")
    List<UserTeam> findAllSubmittedOrderByVotes();
}
