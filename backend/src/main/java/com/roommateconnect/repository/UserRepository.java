package com.roommateconnect.repository;

import com.roommateconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    // "join fetch" loads the roles in the same query. Needed because roles are LAZY
    // and open-in-view is off, so they cannot be loaded later outside a transaction.
    @Query("select distinct u from User u left join fetch u.roles where u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);
}