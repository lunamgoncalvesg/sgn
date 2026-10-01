package com.sgn.repo;

import com.sgn.ent.Bloque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BloqueRepo extends JpaRepository<Bloque, Long> {

    List<Bloque> findByProfLegajoAndAnio(String legajo, int anio);

    List<Bloque> findByCursoIdAndAnio(Long cursoId, int anio);
}