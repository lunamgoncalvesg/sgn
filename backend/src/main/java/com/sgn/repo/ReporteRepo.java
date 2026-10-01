package com.sgn.repo;

import com.sgn.ent.Reporte;
import com.sgn.enm.EstRep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReporteRepo extends JpaRepository<Reporte, Long> {
    List<Reporte> findByEstOrderByFecRepDesc(EstRep est);
    List<Reporte> findByNotaBloqueProfLegajoAndEst(String profLegajo, EstRep est);
}
