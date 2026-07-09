package com.janwypych.ProjectManagementApi.repositories.user;

import com.janwypych.ProjectManagementApi.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional <User> findByEmail(String email);
}
