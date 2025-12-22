package com.library.library_borrow_and_book_tracking.repository;

import com.library.library_borrow_and_book_tracking.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);
}
