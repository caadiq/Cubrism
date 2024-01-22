package com.credential.cubrism.server.authentication.repository;

import com.credential.cubrism.server.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}