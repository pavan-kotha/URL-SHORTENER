package com.pavankotha.shortener.domain.repositories;

import com.pavankotha.shortener.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    boolean  existsByEmail(String email);
}
