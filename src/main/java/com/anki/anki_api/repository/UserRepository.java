package com.anki.anki_api.repository;

import com.anki.anki_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    java.util.List<User> findByRole(com.anki.anki_api.entity.Role role);
    org.springframework.data.domain.Page<User> findByRole(com.anki.anki_api.entity.Role role, org.springframework.data.domain.Pageable pageable);

    // Bypass soft-delete filter (@Where) to avoid re-seeding into unique constraints
    @Query(value = "select count(*) from users", nativeQuery = true)
    long countAllRows();
}
