package com.yahir.ColorManager.Users;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Integer>{
    
    Users findByEmail(String email);

    boolean existsByEmail(String email);
}
