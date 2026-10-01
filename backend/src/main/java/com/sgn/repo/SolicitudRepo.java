package com.sgn.repo;

import com.sgn.ent.Solicitud;
import com.sgn.enm.EstSol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudRepo extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByEstInOrderByFecSolDesc(List<EstSol> estados);
    List<Solicitud> findBySolicitanteLegajoOrderByFecSolDesc(String legajo);
    List<Solicitud> findBySolicitanteLegajoAndEst(String legajo, EstSol est);
    List<Solicitud> findAllByOrderByFecSolDesc();
    boolean existsByNotaIdAndEst(Long notaId, EstSol est);
}
