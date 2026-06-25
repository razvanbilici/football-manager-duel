package com.football.repository;

import com.football.entity.UserTeam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTeamRepository extends JpaRepository<UserTeam, Long>, JpaSpecificationExecutor<UserTeam> {
    @Query("SELECT ut FROM UserTeam ut WHERE ut.submitted = true ORDER BY ut.votes DESC")
    List<UserTeam> findAllSubmittedOrderByVotes();

    Page<UserTeam> findBySubmittedTrue(Pageable pageable);
}
