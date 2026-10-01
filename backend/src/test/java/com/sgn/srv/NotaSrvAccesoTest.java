package com.sgn.srv;

import com.sgn.ent.Bloque;
import com.sgn.ent.Curso;
import com.sgn.ent.Usuario;
import com.sgn.enm.TipoUsr;
import com.sgn.repo.BloqueRepo;
import com.sgn.repo.InscripcionRepo;
import com.sgn.repo.NotaRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotaSrvAccesoTest {

    @Mock NotaRepo notaRepo;
    @Mock BloqueRepo bloqueRepo;
    @Mock InscripcionRepo inscRepo;
    @Mock com.sgn.repo.SolicitudRepo solRepo;
    @Mock com.sgn.repo.ReporteRepo repRepo;
    @Mock com.sgn.repo.BoletinRepo bolRepo;
    @Mock com.sgn.repo.UsuarioRepo usrRepo;
    @Mock com.sgn.repo.AuditoriaRepo audRepo;
    @Mock MailSrv mail;
    @Mock com.fasterxml.jackson.databind.ObjectMapper mapper;

    @InjectMocks
    NotaSrv srv;

    private Bloque bloque;
    private Usuario alumA;
    private Usuario alumB;

    @BeforeEach
    void setup() {
        var cur = new Curso();
        cur.setId(1L);
        cur.setAnio(4);
        cur.setDiv(1);

        bloque = new Bloque();
        bloque.setId(10L);
        bloque.setCurso(cur);
        bloque.setAnio(2026);
        var mat = new com.sgn.ent.Materia();
        mat.setNom("Matemática");
        bloque.setMateria(mat);

        alumA = new Usuario();
        alumA.setLegajo("1001");
        alumA.setTipo(TipoUsr.ALUMNO);

        alumB = new Usuario();
        alumB.setLegajo("1002");
        alumB.setTipo(TipoUsr.ALUMNO);
    }

    @Test
    void alumnoNoPuedeVerNotasDeOtro() {
        when(bloqueRepo.findById(10L)).thenReturn(Optional.of(bloque));
        assertThrows(AccessDeniedException.class,
            () -> srv.notasAlumnoBloque(10L, alumB.getLegajo(), alumA));
    }

    @Test
    void solicitudSinMotivoFalla() {
        var prof = new Usuario();
        prof.setLegajo("D001");
        prof.setTipo(TipoUsr.PROFESOR);
        var req = new com.sgn.dto.Dtos.SolReq(1L, "  ", java.math.BigDecimal.TEN);
        assertThrows(IllegalArgumentException.class, () -> srv.solCambio(req, prof));
    }
}
