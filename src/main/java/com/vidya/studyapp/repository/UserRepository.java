package com.vidya.studyapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vidya.studyapp.entity.Role;
import com.vidya.studyapp.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    List<User> findByRoleAndEnabledFalse(String role);
    List<User> findByRole(Role role);

}

