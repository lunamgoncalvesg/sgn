package com.sgn.ctrl;

import com.sgn.dto.Dtos.*;
import com.sgn.enm.TipoUsr;
import com.sgn.srv.NotaSrv;
import com.sgn.srv.UsuarioSrv;
import com.sgn.srv.UsrDetailsSrv;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioCtrl {

    private final UsuarioSrv srv;
    private final UsrDetailsSrv det;
    private final NotaSrv notaSrv;

    public UsuarioCtrl(UsuarioSrv srv, UsrDetailsSrv det, NotaSrv notaSrv) {
        this.srv = srv;
        this.det = det;
        this.notaSrv = notaSrv;
    }

    @GetMapping("/me")
    public PerfilRes me() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return notaSrv.perfil(det.actual(auth.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OFICINA','ADMIN')")
    public List<UsrRes> listar(@RequestParam(required = false) TipoUsr tipo) {
        return srv.listar(tipo);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OFICINA','ADMIN')")
    public UsrRes crear(@Valid @RequestBody UsrReq req) {
        return srv.crear(req);
    }

    @PutMapping("/{legajo}")
    @PreAuthorize("hasAnyRole('OFICINA','ADMIN')")
    public UsrRes editar(@PathVariable String legajo, @RequestBody UsrReq req) {
        return srv.editar(legajo, req);
    }

    @PostMapping("/{legajo}/reset-password")
@PreAuthorize("hasRole('ADMIN')")
public MsgRes resetPassword(@PathVariable String legajo) {
    return new MsgRes(srv.resetPassword(legajo));
}

    @PatchMapping("/{legajo}/activo")
    @PreAuthorize("hasRole('ADMIN')")
    public UsrRes toggle(@PathVariable String legajo, @RequestParam boolean activo) {
        return srv.toggle(legajo, activo);
    }

    @GetMapping("/cursos")
    @PreAuthorize("hasAnyRole('OFICINA','ADMIN')")
    public List<CursoRes> cursos() {
        return srv.cursos();
    }

    @PostMapping("/cursos")
    @PreAuthorize("hasRole('ADMIN')")
    public CursoRes crearCurso(@RequestBody CursoReq req) {
        return srv.crearCurso(req);
    }

    @GetMapping("/materias")
    @PreAuthorize("hasAnyRole('OFICINA','ADMIN','PROFESOR')")
    public List<MateriaRes> materias() {
        return srv.materias();
    }

    @PostMapping("/materias")
    @PreAuthorize("hasRole('ADMIN')")
    public MateriaRes crearMateria(@Valid @RequestBody MateriaReq req) {
        return srv.crearMateria(req);
    }

    @PostMapping("/inscripcion")
@PreAuthorize("hasAnyRole('OFICINA','ADMIN')")
public MsgRes inscribir(@RequestBody InscReq req) {
    System.out.println(">>> INSCRIPCION RECIBIDA: " + req);

    try {
        srv.inscribir(req);
        System.out.println(">>> INSCRIPCION OK");
        return new MsgRes("Alumno inscripto");
    } catch (Exception e) {
        System.out.println(">>> ERROR INSCRIPCION:");
        e.printStackTrace();
        throw e;
    }
}
    @PostMapping("/bloque")
    @PreAuthorize("hasAnyRole('OFICINA','ADMIN')")
    public MsgRes bloque(@RequestBody BloqueReq req) {
        srv.asignarBloque(req);
        return new MsgRes("Bloque asignado");
    }

    @GetMapping("/notificaciones")
    @PreAuthorize("hasRole('ADMIN')")
    public List<MailRes> notificaciones() {
        return srv.notificaciones();
    }
}
