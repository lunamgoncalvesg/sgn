package com.sgn.ctrl;

import com.sgn.dto.Dtos.*;
import com.sgn.ent.Usuario;
import com.sgn.srv.BoletinSrv;
import com.sgn.srv.NotaSrv;
import com.sgn.srv.UsrDetailsSrv;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notas")
public class NotaCtrl {

    private final NotaSrv srv;
    private final BoletinSrv bolSrv;
    private final UsrDetailsSrv det;

    public NotaCtrl(NotaSrv srv, BoletinSrv bolSrv, UsrDetailsSrv det) {
        this.srv = srv;
        this.bolSrv = bolSrv;
        this.det = det;
    }

    private Usuario me() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return det.actual(auth.getName());
    }

    @GetMapping("/bloques/prof")
@PreAuthorize("hasRole('PROFESOR')")
public List<BloqueRes> misBloques() {
    Usuario usuario = me();
    List<BloqueRes> resultado = srv.bloquesProf(usuario.getLegajo());
    return resultado;
}

    @GetMapping("/bloques/ver")
    @PreAuthorize("hasAnyRole('PRECEPTOR','DIRECTIVO','OFICINA','ADMIN')")
    public List<BloqueRes> verBloques() {
        return srv.bloquesVer();
    }

    @GetMapping("/bloques/{id}/alumnos")
    @PreAuthorize("hasAnyRole('PROFESOR','PRECEPTOR','DIRECTIVO','OFICINA','ADMIN')")
    public List<AlumnoRes> alumnos(@PathVariable long id) {
        return srv.alumnosBloque(id, me());
    }

    @GetMapping("/bloques/{bloqueId}/alumnos/{legajo}")
    @PreAuthorize("hasAnyRole('PROFESOR','ALUMNO','PRECEPTOR','DIRECTIVO','OFICINA','ADMIN')")
    public List<NotaRes> notas(@PathVariable long bloqueId, @PathVariable String legajo) {
        return srv.notasAlumnoBloque(bloqueId, legajo, me());
    }

    @PostMapping
    @PreAuthorize("hasRole('PROFESOR')")
    public NotaRes cargar(@Valid @RequestBody NotaReq req) {
        return srv.cargar(req, me());
    }

    @PostMapping("/solicitud")
    @PreAuthorize("hasRole('PROFESOR')")
    public SolRes solicitar(@Valid @RequestBody SolReq req) {
        return srv.solCambio(req, me());
    }

    @PostMapping("/solicitud/{id}/aplicar")
    @PreAuthorize("hasRole('PROFESOR')")
    public NotaRes aplicar(@PathVariable long id) {
        return srv.aplicarSol(id, me());
    }

    @GetMapping("/solicitudes")
    @PreAuthorize("hasAnyRole('OFICINA','ADMIN')")
    public List<SolRes> solicitudes(@RequestParam(defaultValue = "false") boolean todas) {
        return srv.pendOficina(todas);
    }

    @GetMapping("/solicitudes/prof")
    @PreAuthorize("hasRole('PROFESOR')")
    public List<SolRes> solicitudesProf() {
        return srv.solsProf(me().getLegajo());
    }

    @PostMapping("/solicitud/{id}/resolver")
    @PreAuthorize("hasAnyRole('OFICINA','ADMIN')")
    public SolRes resolver(@PathVariable long id, @RequestParam boolean ok) {
        return srv.resolverSol(id, ok, me());
    }

    @PostMapping("/reporte")
    @PreAuthorize("hasRole('ALUMNO')")
    public MsgRes reporte(@Valid @RequestBody RepReq req) {
        srv.repError(req, me());
        return new MsgRes("Reporte enviado al docente");
    }

    @GetMapping("/reportes/prof")
    @PreAuthorize("hasRole('PROFESOR')")
    public List<RepRes> reportesProf() {
        return srv.repProf(me().getLegajo());
    }

    @PostMapping("/reportes/{id}/solicitar")
    @PreAuthorize("hasRole('PROFESOR')")
    public SolRes repSolicitar(@PathVariable long id, @Valid @RequestBody SolReq req) {
        return srv.repDocSolicita(id, req, me());
    }

    @GetMapping("/alumno/materias")
    @PreAuthorize("hasRole('ALUMNO')")
    public List<BloqueRes> materiasAlumno() {
        return srv.materiasAlumno(me().getLegajo());
    }

    @GetMapping("/alumno/rendimiento")
    @PreAuthorize("hasRole('ALUMNO')")
    public RendRes rendimiento() {
        return srv.rendimiento(me().getLegajo());
    }

    @GetMapping("/alumno/historial")
    @PreAuthorize("hasRole('ALUMNO')")
    public List<NotaRes> historial() {
        return srv.historialAlumno(me().getLegajo());
    }

    @GetMapping("/alumno/boletines")
    @PreAuthorize("hasRole('ALUMNO')")
    public List<BolRes> boletines() {
        return srv.boletines(me().getLegajo());
    }

    @GetMapping("/alumno/boletin.pdf")
    @PreAuthorize("hasRole('ALUMNO')")
    public ResponseEntity<byte[]> boletinPdf() {
        byte[] pdf = bolSrv.pdfAlumno(me().getLegajo(), me());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=boletin.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    @GetMapping("/alumno/{legajo}/boletin.pdf")
    @PreAuthorize("hasAnyRole('OFICINA','ADMIN')")
    public ResponseEntity<byte[]> boletinPdfAlumno(@PathVariable String legajo) {
        byte[] pdf = bolSrv.pdfAlumno(legajo, me());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=boletin-" + legajo + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    @GetMapping("/anio/{anio}")
    @PreAuthorize("hasAnyRole('OFICINA','ADMIN','PRECEPTOR','DIRECTIVO')")
    public List<NotaRes> porAnio(@PathVariable int anio) {
        return srv.todasNotasAnio(anio);
    }

    @GetMapping("/auditoria")
    @PreAuthorize("hasAnyRole('ADMIN','OFICINA')")
    public List<AudRes> auditoria() {
        return srv.auditoria();
    }
}
