package com.demo.repository;

import com.demo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {

	List<Role> findAllByUserId(int userId);
}
