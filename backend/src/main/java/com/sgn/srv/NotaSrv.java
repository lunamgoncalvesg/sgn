package com.sgn.srv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sgn.dto.Dtos.*;
import com.sgn.ent.*;
import com.sgn.enm.*;
import com.sgn.repo.*;
import com.sgn.util.NotaReglas;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotaSrv {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final NotaRepo notaRepo;
    private final BloqueRepo bloqueRepo;
    private final InscripcionRepo inscRepo;
    private final SolicitudRepo solRepo;
    private final ReporteRepo repRepo;
    private final BoletinRepo bolRepo;
    private final UsuarioRepo usrRepo;
    private final AuditoriaRepo audRepo;
    private final MailSrv mail;
    private final ObjectMapper mapper;

    @Value("${sgn.anio-activo:2026}")
    private int anioAct;

    public NotaSrv(NotaRepo notaRepo, BloqueRepo bloqueRepo, InscripcionRepo inscRepo,
                   SolicitudRepo solRepo, ReporteRepo repRepo, BoletinRepo bolRepo,
                   UsuarioRepo usrRepo, AuditoriaRepo audRepo, MailSrv mail, ObjectMapper mapper) {
        this.notaRepo = notaRepo;
        this.bloqueRepo = bloqueRepo;
        this.inscRepo = inscRepo;
        this.solRepo = solRepo;
        this.repRepo = repRepo;
        this.bolRepo = bolRepo;
        this.usrRepo = usrRepo;
        this.audRepo = audRepo;
        this.mail = mail;
        this.mapper = mapper;
    }

    public int anioActivo() {
        return anioAct;
    }

public List<BloqueRes> bloquesProf(String legajo) {
    return bloqueRepo.findByProfLegajoAndAnio(legajo, anioAct).stream()
        .map(this::toBloque).toList();
}

    public List<BloqueRes> bloquesVer() {
        return bloqueRepo.findAll().stream()
            .filter(b -> b.getAnio() == anioAct)
            .map(this::toBloque).toList();
    }

    public List<AlumnoRes> alumnosBloque(long bloqueId, Usuario actor) {
        Bloque b = bloque(bloqueId);
        chkProf(b, actor);
        return inscRepo.findByCursoIdAndAnio(b.getCurso().getId(), b.getAnio()).stream()
            .filter(Inscripcion::isActiva)
            .map(i -> new AlumnoRes(i.getAlumno().getLegajo(), i.getAlumno().nomCompleto()))
            .sorted(Comparator.comparing(AlumnoRes::nomCompleto))
            .toList();
    }

    public List<NotaRes> notasAlumnoBloque(long bloqueId, String alumLeg, Usuario actor) {
        Bloque b = bloque(bloqueId);
        chkAccesoNotas(b, actor, alumLeg);
        return notaRepo.findByBloqueIdAndAlumnoLegajo(bloqueId, alumLeg).stream()
            .map(this::toNota).toList();
    }

    @Transactional
    public NotaRes cargar(NotaReq req, Usuario prof) {
        Bloque b = bloque(req.bloqueId());
        chkProf(b, prof);
        if (prof.getTipo() == TipoUsr.PROFESOR && !b.getProf().getLegajo().equals(prof.getLegajo())) {
            throw new AccessDeniedException("Sin acceso al bloque");
        }
        NotaReglas.validarVal(req.val());
        TipoNota tipo;
        try {
            tipo = TipoNota.valueOf(req.tipo());
        } catch (Exception e) {
            throw new IllegalArgumentException("Tipo de evaluación inválido");
        }
        Usuario alum = alumno(req.legajo());
        if (alum.getTipo() != TipoUsr.ALUMNO) {
            throw new IllegalArgumentException("El legajo no corresponde a un alumno");
        }
        inscRepo.findByAlumnoLegajoAndCursoIdAndAnioAndActivaTrue(
    alum.getLegajo(),
    b.getCurso().getId(),
    b.getAnio()
)
.orElseThrow(() -> new IllegalArgumentException(
    "El alumno no está inscripto actualmente en este curso/año"
));
        if (tipo == TipoNota.BIM || tipo == TipoNota.CUAT || tipo == TipoNota.FINAL) {
            var exist = notaRepo.findByBloqueIdAndAlumnoLegajoAndTipoAndNumPer(
                req.bloqueId(), req.legajo(), tipo, req.numPer());
            if (exist.isPresent()) {
                throw new IllegalStateException("La nota ya está cargada. Solicitá una modificación.");
            }
        }

        Nota n = new Nota();
        n.setBloque(b);
        n.setAlumno(alum);
        n.setTipo(tipo);
        n.setNumPer(req.numPer());
        n.setTitulo(req.titulo());
        n.setVal(req.val());
        n.setEst(calcEst(req.val()));
        n.setCargadoPor(prof);
        n.setFecCarga(LocalDateTime.now());
        n.setFecMod(LocalDateTime.now());
        n = notaRepo.save(n);

        auditar(prof, null, alum, b.getMateria().getNom(), null, n.getVal(),
            "Carga inicial", "CARGA", n.getEst().name());

        var det = tipo + " " + req.numPer() + ": " + fmtVal(n);
        mail.notaAlumno(n.getAlumno().getMail(), n.getAlumno().getMailTutor(), b.getMateria().getNom(), det);

        if (tipo == TipoNota.BIM || tipo == TipoNota.CUAT || tipo == TipoNota.FINAL || tipo == TipoNota.DIC || tipo == TipoNota.FEB) {
            genBol(n);
        }
        return toNota(n);
    }

    @Transactional
    public SolRes solCambio(SolReq req, Usuario prof) {
        if (req.motivo() == null || req.motivo().isBlank()) {
            throw new IllegalArgumentException("El motivo es obligatorio");
        }
        Nota n = notaRepo.findById(req.notaId()).orElseThrow(() -> new NoSuchElementException("Nota no encontrada"));
        chkProf(n.getBloque(), prof);
        if (prof.getTipo() == TipoUsr.PROFESOR && !n.getBloque().getProf().getLegajo().equals(prof.getLegajo())) {
            throw new AccessDeniedException("No puede modificar notas de otro docente");
        }
        NotaReglas.validarVal(req.valNuevo());
        if (solRepo.existsByNotaIdAndEst(n.getId(), EstSol.PEND_OFICINA)) {
            throw new IllegalStateException("Ya hay una solicitud pendiente para esta nota");
        }
        var s = new Solicitud();
        s.setNota(n);
        s.setSolicitante(prof);
        s.setMotivo(req.motivo().trim());
        s.setValActual(n.getVal());
        s.setValNuevo(req.valNuevo());
        s.setEst(EstSol.PEND_OFICINA);
        s.setFecSol(LocalDateTime.now());
        s = solRepo.save(s);
        auditar(prof, null, n.getAlumno(), n.getBloque().getMateria().getNom(),
            n.getVal(), req.valNuevo(), req.motivo(), "SOLICITUD", EstSol.PEND_OFICINA.name());
        usrRepo.findByTipo(TipoUsr.OFICINA).forEach(o ->
            mail.solOficina(o.getMail(), prof.nomCompleto(), n.getBloque().getMateria().getNom(),
                req.motivo(), fmt(n.getVal()), fmt(req.valNuevo()))
        );
        return toSol(s);
    }

    @Transactional
    public NotaRes aplicarSol(long solId, Usuario prof) {
        Solicitud s = solRepo.findById(solId).orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada"));
        if (s.getEst() != EstSol.AUTORIZADA) {
            throw new IllegalStateException("Solicitud no autorizada");
        }
        chkProf(s.getNota().getBloque(), prof);
        aplicarCambio(s, prof);
        return toNota(s.getNota());
    }

    public List<SolRes> pendOficina(boolean todas) {
        var list = todas
            ? solRepo.findAllByOrderByFecSolDesc()
            : solRepo.findByEstInOrderByFecSolDesc(List.of(EstSol.PEND_OFICINA, EstSol.PEND_REVISION, EstSol.AUTORIZADA));
        return list.stream().map(this::toSol).toList();
    }

    public List<SolRes> solsProf(String legajo) {
        return solRepo.findBySolicitanteLegajoOrderByFecSolDesc(legajo).stream().map(this::toSol).toList();
    }

    @Transactional
    public SolRes resolverSol(long id, boolean autoriza, Usuario oficina) {
        Solicitud s = solRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada"));
        if (s.getEst() != EstSol.PEND_OFICINA && s.getEst() != EstSol.PEND_REVISION && s.getEst() != EstSol.AUTORIZADA) {
            throw new IllegalStateException("La solicitud ya fue procesada");
        }
        s.setAutorizo(oficina);
        s.setFecRes(LocalDateTime.now());
        if (autoriza) {
            aplicarCambio(s, oficina);
        } else {
            s.setEst(EstSol.RECHAZADA);
            solRepo.save(s);
            auditar(s.getSolicitante(), oficina, s.getNota().getAlumno(),
                s.getNota().getBloque().getMateria().getNom(), s.getValActual(), s.getValNuevo(),
                s.getMotivo(), "RECHAZO", EstSol.RECHAZADA.name());
        }
        return toSol(s);
    }

    private void aplicarCambio(Solicitud s, Usuario autorizo) {
        Nota n = s.getNota();
        BigDecimal ant = n.getVal();
        n.setVal(s.getValNuevo());
        n.setEst(calcEst(s.getValNuevo()));
        n.setFecMod(LocalDateTime.now());
        notaRepo.save(n);
        s.setEst(EstSol.APLICADA);
        s.setAutorizo(autorizo);
        s.setFecRes(LocalDateTime.now());
        solRepo.save(s);
        auditar(s.getSolicitante(), autorizo, n.getAlumno(), n.getBloque().getMateria().getNom(),
            ant, s.getValNuevo(), s.getMotivo(), "APROBACION", EstSol.APLICADA.name());
        mail.notaAlumno(n.getAlumno().getMail(), n.getAlumno().getMailTutor(),
            n.getBloque().getMateria().getNom(), "Modificada: " + fmtVal(n));
        if (n.getTipo() == TipoNota.BIM || n.getTipo() == TipoNota.CUAT || n.getTipo() == TipoNota.FINAL) {
            genBol(n);
        }
    }

    @Transactional
    public Reporte repError(RepReq req, Usuario alum) {
        Nota n = notaRepo.findById(req.notaId()).orElseThrow(() -> new NoSuchElementException("Nota no encontrada"));
        if (!n.getAlumno().getLegajo().equals(alum.getLegajo())) {
            throw new AccessDeniedException("No puede reportar esta nota");
        }
        var r = new Reporte();
        r.setNota(n);
        r.setAlumno(alum);
        r.setMotivo(req.motivo());
        r.setEst(EstRep.PENDIENTE);
        r.setFecRep(LocalDateTime.now());
        r = repRepo.save(r);
        mail.enviar(n.getBloque().getProf().getMail(), "SGN - Reporte de error en nota",
            "Alumno " + alum.nomCompleto() + " reportó error en " + n.getBloque().getMateria().getNom()
                + "\nMotivo: " + req.motivo() + "\nNota actual: " + fmtVal(n));
        return r;
    }

    public List<RepRes> repProf(String legajo) {
        return repRepo.findByNotaBloqueProfLegajoAndEst(legajo, EstRep.PENDIENTE)
            .stream().map(this::toRep).toList();
    }

    @Transactional
    public SolRes repDocSolicita(long repId, SolReq req, Usuario prof) {
        Reporte r = repRepo.findById(repId).orElseThrow(() -> new NoSuchElementException("Reporte no encontrado"));
        chkProf(r.getNota().getBloque(), prof);
        var sol = solCambio(new SolReq(r.getNota().getId(), req.motivo(), req.valNuevo()), prof);
        r.setEst(EstRep.DOC_SOLICITA);
        repRepo.save(r);
        return sol;
    }

    public List<BloqueRes> materiasAlumno(String legajo) {

    var cursoIds = inscRepo.findByAlumnoLegajoAndAnioAndActivaTrue(
        legajo,
        anioAct
    ).stream()
        .map(i -> i.getCurso().getId())
        .collect(Collectors.toSet());

    return bloqueRepo.findAll().stream()
        .filter(b -> b.getAnio() == anioAct && cursoIds.contains(b.getCurso().getId()))
        .map(this::toBloque)
        .toList();
}

    public RendRes rendimiento(String legajo) {
        var mats = new ArrayList<MateriaRend>();
        var perMap = new LinkedHashMap<String, List<Double>>();
        double sum = 0;
        int cnt = 0;
        for (BloqueRes b : materiasAlumno(legajo)) {
            var notas = notaRepo.findByBloqueIdAndAlumnoLegajo(b.id(), legajo);
            double prom = promNoOf(notas);
            double pct = prom > 0 ? (prom / 10.0) * 100 : 0;
            String est = estadoMateria(notas);
            mats.add(new MateriaRend(b.materia(), round2(prom), round2(pct), est));
            if (prom > 0) { sum += prom; cnt++; }
            for (Nota n : notas) {
                if (n.getVal() == null) continue;
                if (n.getTipo() == TipoNota.BIM || n.getTipo() == TipoNota.CUAT || n.getTipo() == TipoNota.FINAL) {
                    String k = n.getTipo().name() + "-" + n.getNumPer();
                    perMap.computeIfAbsent(k, x -> new ArrayList<>()).add(n.getVal().doubleValue());
                }
            }
        }
        var periodos = perMap.entrySet().stream()
            .map(e -> {
                String[] p = e.getKey().split("-");
                double avg = e.getValue().stream().mapToDouble(v -> v).average().orElse(0);
                return new PeriodoRend(p[0], Integer.parseInt(p[1]), round2(avg));
            }).toList();
        double general = cnt > 0 ? sum / cnt : 0;
        return new RendRes(round2(general > 0 ? (general / 10.0) * 100 : 0), round2(general), mats, periodos);
    }

    public List<NotaRes> notasMateriaAlumno(long bloqueId, String legajo) {
        return notaRepo.findByBloqueIdAndAlumnoLegajo(bloqueId, legajo).stream()
            .map(this::toNota).toList();
    }

    public List<BolRes> boletines(String legajo) {
        return bolRepo.findByAlumnoLegajoOrderByAnioDescFecGenDesc(legajo).stream()
            .map(this::toBol).toList();
    }

    public List<NotaRes> todasNotasAnio(int anio) {
        return notaRepo.findAll().stream()
            .filter(n -> n.getBloque().getAnio() == anio)
            .map(this::toNota).toList();
    }

    public List<NotaRes> historialAlumno(String legajo) {
        return notaRepo.findByAlumnoLegajoAndBloqueAnio(legajo, anioAct).stream()
            .map(this::toNota).toList();
    }

    public List<AudRes> auditoria() {
        return audRepo.findAllByOrderByFecDesc().stream().map(this::toAud).toList();
    }

    public PerfilRes perfil(Usuario u) {
        String curso = "";
        int anio = anioAct;
        if (u.getTipo() == TipoUsr.ALUMNO) {
            var insc = inscRepo.findByAlumnoLegajoAndAnioAndActivaTrue(
    u.getLegajo(),
    anioAct
);

if (!insc.isEmpty()) {
    curso = insc.get(0).getCurso().etiqueta();
    anio = insc.get(0).getAnio();
}
        }
        return new PerfilRes(u.getLegajo(), u.getCuil(), u.nomCompleto(), u.getMail(), u.getTipo(), curso, anio);
    }

    private void genBol(Nota n) {
        try {
            var alum = n.getAlumno();
            var notas = notaRepo.findByAlumnoLegajoAndBloqueAnio(alum.getLegajo(), n.getBloque().getAnio());
            TipoBol tb = switch (n.getTipo()) {
                case BIM -> TipoBol.BIM;
                case CUAT -> TipoBol.CUAT;
                case FINAL -> TipoBol.FINAL;
                default -> null;
            };
            if (tb == null) return;
            var filtradas = notas.stream()
                .filter(x -> esBol(x, tb, n.getNumPer()))
                .map(x -> Map.of(
                    "materia", x.getBloque().getMateria().getNom(),
                    "tipo", x.getTipo().name(),
                    "numPer", x.getNumPer(),
                    "val", fmtVal(x),
                    "est", x.getEst().name()
                )).toList();
            var json = mapper.writeValueAsString(filtradas);
            var bol = new Boletin();
            bol.setAlumno(alum);
            bol.setAnio(n.getBloque().getAnio());
            bol.setTipoPer(tb);
            bol.setNumPer(n.getNumPer());
            bol.setContenido(json);
            bol.setFecGen(LocalDateTime.now());
            bolRepo.save(bol);
        } catch (Exception e) {
            throw new RuntimeException("Error generando boletín", e);
        }
    }

    private boolean esBol(Nota x, TipoBol tb, int numPer) {
        return switch (tb) {
            case BIM -> x.getTipo() == TipoNota.BIM && x.getNumPer() == numPer;
            case CUAT -> x.getTipo() == TipoNota.CUAT && x.getNumPer() == numPer;
            case FINAL -> x.getTipo() == TipoNota.FINAL;
        };
    }

    private double promNoOf(List<Nota> notas) {
        var vals = notas.stream()
            .filter(n -> n.getVal() != null)
            .map(n -> n.getVal().doubleValue())
            .toList();
        if (vals.isEmpty()) return 0;
        return vals.stream().mapToDouble(v -> v).average().orElse(0);
    }

    private String estadoMateria(List<Nota> notas) {
        if (notas.isEmpty()) return "SIN_NOTAS";
        boolean algunaEnProc = notas.stream().anyMatch(n -> n.getEst() == EstNota.EN_PROCESO);
        var finales = notas.stream().filter(n -> n.getTipo() == TipoNota.FINAL).toList();
        if (!finales.isEmpty()) {
            return finales.get(0).getEst() == EstNota.EN_PROCESO ? "EN_PROCESO" : "APROBADA";
        }
        return algunaEnProc ? "EN_PROCESO" : "CURSANDO";
    }

    private EstNota calcEst(BigDecimal val) {
        return NotaReglas.enProceso(val) ? EstNota.EN_PROCESO : EstNota.NORMAL;
    }

    private Bloque bloque(long id) {
        return bloqueRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Bloque no encontrado"));
    }

    private Usuario alumno(String leg) {
        return usrRepo.findById(leg).orElseThrow(() -> new NoSuchElementException("Alumno no encontrado"));
    }

    private void chkProf(Bloque b, Usuario u) {
        if (u.getTipo() == TipoUsr.ADMIN || u.getTipo() == TipoUsr.OFICINA
            || u.getTipo() == TipoUsr.PRECEPTOR || u.getTipo() == TipoUsr.DIRECTIVO) return;
        if (u.getTipo() == TipoUsr.PROFESOR && b.getProf().getLegajo().equals(u.getLegajo())) return;
        throw new AccessDeniedException("Sin acceso al bloque");
    }

private void chkAccesoNotas(Bloque b, Usuario u, String alumLeg) {

    if (u.getTipo() == TipoUsr.ALUMNO) {

        if (!u.getLegajo().equals(alumLeg)) {
            throw new AccessDeniedException(
                "No puede consultar datos de otro alumno"
            );
        }

        inscRepo.findByAlumnoLegajoAndCursoIdAndAnioAndActivaTrue(
            alumLeg,
            b.getCurso().getId(),
            b.getAnio()
        ).orElseThrow(() -> new AccessDeniedException(
            "Sin acceso: el alumno no está inscripto actualmente en este curso/año"
        ));

        return;
    }

    chkProf(b, u);
}

    private void auditar(Usuario actor, Usuario autorizo, Usuario alumno, String materia,
                         BigDecimal ant, BigDecimal neu, String motivo, String accion, String est) {
        var a = new Auditoria();
        a.setActor(actor);
        a.setAutorizo(autorizo);
        a.setAlumno(alumno);
        a.setMateria(materia);
        a.setValAnt(ant);
        a.setValNuevo(neu);
        a.setMotivo(motivo);
        a.setAccion(accion);
        a.setEst(est);
        a.setFec(LocalDateTime.now());
        audRepo.save(a);
    }

    private BloqueRes toBloque(Bloque b) {
        return new BloqueRes(b.getId(), b.titulo(), b.getMateria().getId(), b.getMateria().getNom(),
            b.getCurso().getId(), b.getCurso().etiqueta(), b.getAnio());
    }

    private NotaRes toNota(Nota n) {
        String val = n.getEst() == EstNota.EN_PROCESO ? "En proceso" : fmtVal(n);
        return new NotaRes(n.getId(), n.getTipo().name(), n.getNumPer(),
            n.getTitulo(), val, n.getEst().name(),
            n.getFecCarga() != null ? n.getFecCarga().format(FMT) : null,
            n.getFecMod() != null ? n.getFecMod().format(FMT) : null,
            n.getBloque().getMateria().getNom(),
            n.getBloque().getCurso().etiqueta(),
            n.getBloque().getAnio());
    }

    private SolRes toSol(Solicitud s) {
        var n = s.getNota();
        return new SolRes(s.getId(), n.getId(), n.getAlumno().nomCompleto(),
            n.getBloque().getMateria().getNom(), s.getMotivo(),
            fmt(s.getValActual()), fmt(s.getValNuevo()), s.getEst().name(),
            s.getFecSol().format(FMT),
            s.getSolicitante() != null ? s.getSolicitante().nomCompleto() : "",
            s.getAutorizo() != null ? s.getAutorizo().nomCompleto() : "",
            s.getFecRes() != null ? s.getFecRes().format(FMT) : null);
    }

    private RepRes toRep(Reporte r) {
        var n = r.getNota();
        return new RepRes(r.getId(), n.getId(), r.getAlumno().nomCompleto(),
            n.getBloque().getMateria().getNom(), r.getMotivo(), r.getEst().name(),
            r.getFecRep().format(FMT));
    }

    private BolRes toBol(Boletin b) {
        return new BolRes(b.getId(), b.getAnio(), b.getTipoPer().name(), b.getNumPer(),
            b.getContenido(), b.getFecGen().format(FMT));
    }

    private AudRes toAud(Auditoria a) {
        return new AudRes(
            a.getId(),
            a.getActor() != null ? a.getActor().nomCompleto() : "",
            a.getAutorizo() != null ? a.getAutorizo().nomCompleto() : "",
            a.getAlumno() != null ? a.getAlumno().nomCompleto() : "",
            a.getMateria(),
            fmt(a.getValAnt()),
            fmt(a.getValNuevo()),
            a.getMotivo(),
            a.getAccion(),
            a.getEst(),
            a.getFec().format(FMT)
        );
    }

    private String fmtVal(Nota n) {
        return n.getVal() != null ? n.getVal().setScale(2, RoundingMode.HALF_UP).toPlainString() : "-";
    }

    private String fmt(BigDecimal v) {
        return v != null ? v.setScale(2, RoundingMode.HALF_UP).toPlainString() : "-";
    }

    private double round2(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
