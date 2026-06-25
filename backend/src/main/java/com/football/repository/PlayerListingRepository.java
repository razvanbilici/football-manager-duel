package com.football.repository;

import com.football.entity.PlayerListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerListingRepository extends JpaRepository<PlayerListing, Long>, JpaSpecificationExecutor<PlayerListing> {
    List<PlayerListing> findByActiveTrueOrderByCreatedAtDesc();
    List<PlayerListing> findBySellerIdAndActiveTrueOrderByCreatedAtDesc(Long sellerId);
    Optional<PlayerListing> findByPlayerIdAndActiveTrue(Long playerId);
    Optional<PlayerListing> findByIdAndSellerId(Long id, Long sellerId);
}
