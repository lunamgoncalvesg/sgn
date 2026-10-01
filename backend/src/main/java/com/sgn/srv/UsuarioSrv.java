package com.sgn.srv;

import com.sgn.dto.Dtos.*;
import com.sgn.ent.*;
import com.sgn.enm.Esp;
import com.sgn.enm.TipoUsr;
import com.sgn.enm.Turno;
import com.sgn.repo.*;
import com.sgn.util.NotaReglas;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UsuarioSrv {

    private final UsuarioRepo repo;
    private final CursoRepo cursoRepo;
    private final MateriaRepo matRepo;
    private final BloqueRepo bloqueRepo;
    private final InscripcionRepo inscRepo;
    private final PasswordEncoder enc;
    private final NotificacionRepo notifRepo;
    private final MailSrv mailSrv;

    public UsuarioSrv(UsuarioRepo repo, CursoRepo cursoRepo, MateriaRepo matRepo,
                  BloqueRepo bloqueRepo, InscripcionRepo inscRepo,
                  PasswordEncoder enc, NotificacionRepo notifRepo,
                  MailSrv mailSrv) {
        this.repo = repo;
        this.cursoRepo = cursoRepo;
        this.matRepo = matRepo;
        this.bloqueRepo = bloqueRepo;
        this.inscRepo = inscRepo;
        this.enc = enc;
        this.notifRepo = notifRepo;
        this.mailSrv = mailSrv;
    }

    public UsrRes toRes(Usuario u) {
        return new UsrRes(u.getLegajo(), u.getCuil(), u.getNom(), u.getNom2(), u.getApe(), u.getApe2(),
            u.getMail(), u.getMailTutor(), u.getTipo(), u.isActivo());
    }

    public List<UsrRes> listar(TipoUsr tipo) {
        var list = tipo != null ? repo.findByTipo(tipo) : repo.findAll();
        return list.stream().map(this::toRes).toList();
    }

@Transactional
public UsrRes crear(UsrReq req) {

    if (req.legajo() == null || req.legajo().isBlank()) {
        throw new IllegalArgumentException("Legajo obligatorio");
    }

    if (req.nom() == null || req.ape() == null ||
        req.mail() == null || req.tipo() == null) {
        throw new IllegalArgumentException(
            "Nombre, apellido, mail y tipo son obligatorios"
        );
    }

    NotaReglas.validarCuil(req.cuil());

    String cuil = req.cuil().replaceAll("[^0-9]", "");

    if (repo.existsById(req.legajo())) {
        throw new IllegalArgumentException("Legajo existente");
    }

    if (repo.existsByMail(req.mail())) {
        throw new IllegalArgumentException("Mail existente");
    }

    if (repo.existsByCuil(cuil)) {
        throw new IllegalArgumentException("CUIL existente");
    }

    var u = new Usuario();

    u.setLegajo(req.legajo().trim());
    u.setCuil(cuil);
    u.setNom(req.nom());
    u.setNom2(req.nom2());
    u.setApe(req.ape());
    u.setApe2(req.ape2());
    u.setMail(req.mail().trim());
    u.setMailTutor(req.mailTutor());

    String passwordTemporal = generarPasswordTemporal();

    u.setPass(enc.encode(passwordTemporal));
    u.setPrimerIngreso(true);

    u.setTipo(req.tipo());
    u.setActivo(true);

    repo.save(u);

    mailSrv.passwordTemporal(
        u.getMail(),
        u.nomCompleto(),
        passwordTemporal
    );

    return toRes(u);
}

    @Transactional
    public UsrRes editar(String legajo, UsrReq req) {
        Usuario u = repo.findById(legajo).orElseThrow(() -> new NoSuchElementException("No encontrado"));
        if (req.cuil() != null && !req.cuil().isBlank()) {
            NotaReglas.validarCuil(req.cuil());
            String cuil = req.cuil().replaceAll("[^0-9]", "");
            repo.findByCuil(cuil).ifPresent(otro -> {
                if (!otro.getLegajo().equals(legajo)) {
                    throw new IllegalArgumentException("CUIL existente");
                }
            });
            u.setCuil(cuil);
        }
        if (req.nom() != null) u.setNom(req.nom());
        u.setNom2(req.nom2());
        if (req.ape() != null) u.setApe(req.ape());
        u.setApe2(req.ape2());
        if (req.mail() != null && !req.mail().isBlank()) {
            if (!req.mail().equalsIgnoreCase(u.getMail()) && repo.existsByMail(req.mail())) {
                throw new IllegalArgumentException("Mail existente");
            }
            u.setMail(req.mail());
        }
        u.setMailTutor(req.mailTutor());
        if (req.pass() != null && !req.pass().isBlank()) u.setPass(enc.encode(req.pass()));
        if (req.tipo() != null) u.setTipo(req.tipo());
        return toRes(repo.save(u));
    }

    @Transactional
    public UsrRes toggle(String legajo, boolean activo) {
        Usuario u = repo.findById(legajo).orElseThrow(() -> new NoSuchElementException("No encontrado"));
        u.setActivo(activo);
        return toRes(repo.save(u));
    }

    public List<CursoRes> cursos() {
        return cursoRepo.findAll().stream().map(this::toCurso).toList();
    }

    public List<MateriaRes> materias() {
        return matRepo.findAll().stream().map(m -> new MateriaRes(m.getId(), m.getNom())).toList();
    }

    @Transactional
    public CursoRes crearCurso(CursoReq req) {
        Turno turno;
        Esp esp;
        try {
            turno = Turno.valueOf(req.turno());
            esp = Esp.valueOf(req.esp());
        } catch (Exception e) {
            throw new IllegalArgumentException("Turno o especialidad inválidos");
        }
        if (cursoRepo.findByAnioAndDivAndTurnoAndEsp(req.anio(), req.div(), turno, esp).isPresent()) {
            throw new IllegalArgumentException("El curso ya existe");
        }
        var c = new Curso();
        c.setAnio(req.anio());
        c.setDiv(req.div());
        c.setTurno(turno);
        c.setEsp(esp);
        return toCurso(cursoRepo.save(c));
    }

    @Transactional
    public MateriaRes crearMateria(MateriaReq req) {
        if (matRepo.findByNomIgnoreCase(req.nom().trim()).isPresent()) {
            throw new IllegalArgumentException("La materia ya existe");
        }
        var m = new Materia();
        m.setNom(req.nom().trim());
        m = matRepo.save(m);
        return new MateriaRes(m.getId(), m.getNom());
    }


@Transactional
public Inscripcion inscribir(InscReq req) {

    Usuario al = repo.findById(req.legajo())
        .orElseThrow(() -> new NoSuchElementException(
            "Alumno no encontrado: " + req.legajo()
        ));

    if (al.getTipo() != TipoUsr.ALUMNO) {
        throw new IllegalArgumentException("Debe ser alumno");
    }

    Curso nuevoCurso = cursoRepo.findById(req.cursoId())
        .orElseThrow(() -> new NoSuchElementException(
            "Curso no encontrado: " + req.cursoId()
        ));

    var activas = inscRepo.findByAlumnoLegajoAndAnioAndActivaTrue(
        req.legajo(),
        req.anio()
    );

    for (var actual : activas) {
        actual.setActiva(false);
        inscRepo.save(actual);
    }

    var existente = inscRepo.findByAlumnoLegajoAndCursoIdAndAnio(
        req.legajo(),
        req.cursoId(),
        req.anio()
    );

    if (existente.isPresent()) {
        var inscripcion = existente.get();
        inscripcion.setActiva(true);
        return inscRepo.save(inscripcion);
    }

    var nueva = new Inscripcion();
    nueva.setAlumno(al);
    nueva.setCurso(nuevoCurso);
    nueva.setAnio(req.anio());
    nueva.setActiva(true);

    return inscRepo.save(nueva);
}

    @Transactional
    public Bloque asignarBloque(BloqueReq req) {
        Usuario prof = repo.findById(req.profLegajo())
            .orElseThrow(() -> new NoSuchElementException("Docente no encontrado"));
        if (prof.getTipo() != TipoUsr.PROFESOR) throw new IllegalArgumentException("Debe ser profesor");
        Materia m = matRepo.findById(req.materiaId()).orElseThrow(() -> new NoSuchElementException("Materia no encontrada"));
        Curso c = cursoRepo.findById(req.cursoId()).orElseThrow(() -> new NoSuchElementException("Curso no encontrado"));
        boolean dup = bloqueRepo.findByCursoIdAndAnio(c.getId(), req.anio()).stream()
            .anyMatch(b -> b.getMateria().getId().equals(m.getId()));
        if (dup) throw new IllegalArgumentException("Ya existe un bloque para esa materia, curso y año");
        var b = new Bloque();
        b.setMateria(m);
        b.setCurso(c);
        b.setProf(prof);
        b.setAnio(req.anio());
        return bloqueRepo.save(b);
    }

    public List<MailRes> notificaciones() {
        return notifRepo.findAllByOrderByFecDesc().stream()
            .map(n -> new MailRes(n.getId(), n.getDest(), n.getAsunto(), n.isEnviado(), n.getError(),
                n.getFec() != null ? n.getFec().toString() : null))
            .toList();
    }

    private CursoRes toCurso(Curso c) {
        return new CursoRes(c.getId(), c.getAnio(), c.getDiv(), c.getTurno().name(), c.getEsp().name(), c.etiqueta());
    }
    private String generarPasswordTemporal() {

    String caracteres =
        "ABCDEFGHJKLMNPQRSTUVWXYZ" +
        "abcdefghijkmnopqrstuvwxyz" +
        "23456789";

    java.security.SecureRandom random =
        new java.security.SecureRandom();

    StringBuilder password = new StringBuilder();

    for (int i = 0; i < 10; i++) {
        password.append(
            caracteres.charAt(
                random.nextInt(caracteres.length())
            )
        );
    }

    return password.toString();
}

@Transactional
public String resetPassword(String legajo) {

    Usuario u = repo.findById(legajo)
        .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

    if (!u.isActivo()) {
        throw new IllegalArgumentException("El usuario está inactivo");
    }

    String passwordTemporal = generarPasswordTemporal();

    u.setPass(enc.encode(passwordTemporal));

    u.setPrimerIngreso(true);

    repo.save(u);

    mailSrv.passwordTemporal(
        u.getMail(),
        u.nomCompleto(),
        passwordTemporal
    );

    return "Contraseña restablecida y enviada por correo";
}
}
