package com.bloqtech.AuthModule.repositories;

import com.bloqtech.AuthModule.entity.ERole;
import com.bloqtech.AuthModule.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {

    Optional<Role> findByName(ERole name);
}
