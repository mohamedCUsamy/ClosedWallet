package com.closedwallet.Repository;

import com.et.closedwallet.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
