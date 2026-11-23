package com.careerdb.role;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface RoleCanonicalRepository extends JpaRepository<RoleCanonical, UUID> {
}