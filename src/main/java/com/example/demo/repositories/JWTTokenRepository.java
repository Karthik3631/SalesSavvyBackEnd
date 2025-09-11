package com.example.demo.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.JWTToken;

@Repository
public interface JWTTokenRepository extends JpaRepository<JWTToken, Integer> {
	
	@Query("SELECT t FROM JWTToken t where t.user.userId= :userId")
	JWTToken findByUserId(int userId);

	Optional<JWTToken> findByToken(String token);
	
}
