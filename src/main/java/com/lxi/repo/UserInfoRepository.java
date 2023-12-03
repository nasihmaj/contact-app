package com.lxi.repo;

import java.util.Optional;

import javax.persistence.Id;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lxi.model.UserInfo;

public interface UserInfoRepository  extends JpaRepository<UserInfo, Integer>{

	Optional<UserInfo> findByName(String username);

	
}
