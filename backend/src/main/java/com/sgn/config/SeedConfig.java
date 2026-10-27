package com.sgn.config;

import com.sgn.enm.*;
import com.sgn.ent.*;
import com.sgn.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class SeedConfig {

    private static final int ANIO = 2026;
    private static final String PASS = "1234";

    @Bean
    CommandLineRunner seed(
            UsuarioRepo usrRepo, CursoRepo curRepo, MateriaRepo matRepo,
            BloqueRepo bloqueRepo, InscripcionRepo inscRepo, NotaRepo notaRepo,
            PasswordEncoder enc) {
        return args -> {
            if (usrRepo.count() > 0) return;


var admin = usr(usrRepo, enc, "0000", "20111111112", "Admin", "Sistema",
    "admin@et35.local", null, TipoUsr.ADMIN);

var ofic = usr(usrRepo, enc, "9999", "20222222223", "Oficina", "Alumnos",
    "oficina@et35.local", null, TipoUsr.OFICINA);

var prof1 = usr(usrRepo, enc, "1111", "20333333334", "Luna", "Goncalves",
    "lunamgoncalvesguerreiro@gmail.com", null, TipoUsr.PROFESOR);

var prof2 = usr(usrRepo, enc, "2222", "20444444445", "Victoria", "Dominguez",
    "victoriadmg19@gmail.com", null, TipoUsr.PROFESOR);

var alum1 = usr(usrRepo, enc, "3333", "20555555556", "Sofia", "Galindez",
    "semmagalindez@gmail.com", "semmagalindez@gmail.com", TipoUsr.ALUMNO);

var alum2 = usr(usrRepo, enc, "4444", "20666666667", "Angeline", "Iannopollo",
    "iannopolloangeline@gmail.com", "iannopolloangeline@gmail.com", TipoUsr.ALUMNO);

            var mat = mat(matRepo, "Matemática");
            var len = mat(matRepo, "Lengua");
            mat(matRepo, "Historia");

            var cur = curso(curRepo, 4, 1, Turno.MANANA, Esp.COMPUTACION);
            curso(curRepo, 3, 2, Turno.TARDE, Esp.AUTOMOTORES);

            var bMat = bloque(bloqueRepo, mat, cur, prof1);
            var bLen = bloque(bloqueRepo, len, cur, prof2);

            insc(inscRepo, alum1, cur);
            insc(inscRepo, alum2, cur);

            nota(notaRepo, bMat, alum1, TipoNota.BIM, 1, null, "7.50", prof1);
            nota(notaRepo, bMat, alum1, TipoNota.PRUEBA, 1, "Parcial 1", "8.00", prof1);
            nota(notaRepo, bLen, alum1, TipoNota.BIM, 1, null, "5.00", prof2);
            nota(notaRepo, bMat, alum2, TipoNota.BIM, 1, null, "6.00", prof1);

        };
    }

    private Usuario usr(UsuarioRepo repo, PasswordEncoder enc, String leg, String cuil,
                        String nom, String ape, String mail, String tutor, TipoUsr tipo) {
        var u = new Usuario();
        u.setLegajo(leg);
        u.setCuil(cuil);
        u.setNom(nom);
        u.setApe(ape);
        u.setMail(mail);
        u.setMailTutor(tutor);
        u.setPass(enc.encode(PASS));
        u.setTipo(tipo);
        u.setActivo(true);
        return repo.save(u);
    }

    private Materia mat(MateriaRepo repo, String nom) {
        var m = new Materia();
        m.setNom(nom);
        return repo.save(m);
    }

    private Curso curso(CursoRepo repo, int anio, int div, Turno t, Esp e) {
        var c = new Curso();
        c.setAnio(anio);
        c.setDiv(div);
        c.setTurno(t);
        c.setEsp(e);
        return repo.save(c);
    }

    private Bloque bloque(BloqueRepo repo, Materia m, Curso c, Usuario p) {
        var b = new Bloque();
        b.setMateria(m);
        b.setCurso(c);
        b.setProf(p);
        b.setAnio(ANIO);
        return repo.save(b);
    }

    private void insc(InscripcionRepo repo, Usuario a, Curso c) {
        var i = new Inscripcion();
        i.setAlumno(a);
        i.setCurso(c);
        i.setAnio(ANIO);
        i.setActiva(true);
        repo.save(i);
    }

    private void nota(NotaRepo repo, Bloque b, Usuario a, TipoNota tipo, int per,
                      String titulo, String val, Usuario prof) {
        var n = new Nota();
        n.setBloque(b);
        n.setAlumno(a);
        n.setTipo(tipo);
        n.setNumPer(per);
        n.setTitulo(titulo);
        n.setVal(new BigDecimal(val));
        n.setEst(new BigDecimal(val).compareTo(BigDecimal.valueOf(6)) < 0
            ? EstNota.EN_PROCESO : EstNota.NORMAL);
        n.setCargadoPor(prof);
        n.setFecCarga(LocalDateTime.now());
        n.setFecMod(LocalDateTime.now());
        repo.save(n);
    }
}
