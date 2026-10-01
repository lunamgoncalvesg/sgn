package com.sgn.repo;

import com.sgn.ent.Usuario;
import com.sgn.enm.TipoUsr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepo extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByMail(String mail);
    Optional<Usuario> findByCuil(String cuil);
    List<Usuario> findByTipo(TipoUsr tipo);
    List<Usuario> findByTipoAndActivoTrue(TipoUsr tipo);
    boolean existsByMail(String mail);
    boolean existsByCuil(String cuil);
}
