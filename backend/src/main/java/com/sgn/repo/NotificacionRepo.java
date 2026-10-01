package com.sgn.repo;

import com.sgn.ent.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepo extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findAllByOrderByFecDesc();
}
