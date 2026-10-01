package com.sgn.dto;

import com.sgn.enm.TipoUsr;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.List;

public final class Dtos {

    private Dtos() {}

    public record LoginReq(@NotBlank String ident, @NotBlank String pass) {}
    public record CambiarPasswordReq(
    @NotBlank String actual,
    @NotBlank String nueva
) {}
   public record LoginRes(
    String token,
    String legajo,
    String nom,
    TipoUsr tipo,
    boolean primerIngreso
) {}
    public record PerfilRes(
        String legajo, String cuil, String nom, String mail, TipoUsr tipo,
        String curso, int anioLectivo
    ) {}
    public record UsrRes(
        String legajo, String cuil, String nom, String nom2, String ape, String ape2,
        String mail, String mailTutor, TipoUsr tipo, boolean activo
    ) {}
    public record UsrReq(
        String legajo, String cuil, String nom, String nom2, String ape, String ape2,
        String mail, String mailTutor, String pass, TipoUsr tipo
    ) {}
    public record CursoRes(long id, int anio, int div, String turno, String esp, String etiqueta) {}
    public record CursoReq(int anio, int div, String turno, String esp) {}
    public record MateriaRes(long id, String nom) {}
    public record MateriaReq(@NotBlank String nom) {}
    public record BloqueRes(long id, String titulo, long materiaId, String materia, long cursoId, String curso, int anio) {}
    public record AlumnoRes(String legajo, String nomCompleto) {}
    public record NotaRes(
        long id, String tipo, int numPer, String titulo, String val, String est,
        String fecCarga, String fecMod, String materia, String curso, int anio
    ) {}
    public record NotaReq(
        long bloqueId, String legajo, String tipo, int numPer,
        String titulo, BigDecimal val
    ) {}
    public record SolReq(long notaId, @NotBlank String motivo, BigDecimal valNuevo) {}
    public record SolRes(
        long id, long notaId, String alumno, String materia, String motivo,
        String valActual, String valNuevo, String est, String fecSol,
        String docente, String autorizo, String fecRes
    ) {}
    public record RepReq(long notaId, @NotBlank String motivo) {}
    public record RepRes(
        long id, long notaId, String alumno, String materia, String motivo, String est, String fecRep
    ) {}
    public record BolRes(long id, int anio, String tipoPer, int numPer, String contenido, String fecGen) {}
    public record RendRes(double pctGlobal, double promGeneral, List<MateriaRend> materias, List<PeriodoRend> periodos) {}
    public record MateriaRend(String materia, double promNoOf, double pct, String est) {}
    public record PeriodoRend(String tipo, int numPer, double promNoOf) {}
    public record InscReq(String legajo, long cursoId, int anio) {}
    public record BloqueReq(long materiaId, long cursoId, String profLegajo, int anio) {}
    public record AudRes(
        long id, String actor, String autorizo, String alumno, String materia,
        String valAnt, String valNuevo, String motivo, String accion, String est, String fec
    ) {}
    public record MailRes(long id, String dest, String asunto, boolean enviado, String error, String fec) {}
    public record MsgRes(String msg) {}
}
