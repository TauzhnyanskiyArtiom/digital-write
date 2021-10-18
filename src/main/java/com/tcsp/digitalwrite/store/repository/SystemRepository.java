package com.tcsp.digitalwrite.store.repository;

import com.tcsp.digitalwrite.store.entity.SystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemRepository extends JpaRepository<SystemEntity, String> {

    Optional<SystemEntity> findById(String id);
}