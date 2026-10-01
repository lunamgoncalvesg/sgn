package com.sgn.repo;

import com.sgn.ent.Boletin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoletinRepo extends JpaRepository<Boletin, Long> {
    List<Boletin> findByAlumnoLegajoOrderByAnioDescFecGenDesc(String legajo);
    List<Boletin> findByAlumnoLegajoAndAnio(String legajo, int anio);
}
