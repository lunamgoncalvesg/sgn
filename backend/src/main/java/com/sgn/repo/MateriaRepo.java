package com.sgn.repo;

import com.sgn.ent.Materia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MateriaRepo extends JpaRepository<Materia, Long> {
    Optional<Materia> findByNomIgnoreCase(String nom);
}
