package com.sgn.repo;

import com.sgn.ent.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepo extends JpaRepository<Auditoria, Long> {
    List<Auditoria> findAllByOrderByFecDesc();
}
