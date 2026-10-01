package com.sgn.repo;

import com.sgn.ent.Curso;
import com.sgn.enm.Esp;
import com.sgn.enm.Turno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CursoRepo extends JpaRepository<Curso, Long> {
    Optional<Curso> findByAnioAndDivAndTurnoAndEsp(int anio, int div, Turno turno, Esp esp);
}
