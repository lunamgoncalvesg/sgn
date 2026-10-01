package com.sgn.ent;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "actor_legajo")
    private Usuario actor;

    @ManyToOne
    @JoinColumn(name = "autorizo_legajo")
    private Usuario autorizo;

    @ManyToOne
    @JoinColumn(name = "alumno_legajo")
    private Usuario alumno;

    @Column(length = 120)
    private String materia;

    @Column(precision = 4, scale = 2)
    private BigDecimal valAnt;

    @Column(precision = 4, scale = 2)
    private BigDecimal valNuevo;

    @Column(length = 500)
    private String motivo;

    @Column(nullable = false, length = 30)
    private String accion;

    @Column(length = 20)
    private String est;

    @Column(nullable = false)
    private LocalDateTime fec;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getActor() { return actor; }
    public void setActor(Usuario actor) { this.actor = actor; }
    public Usuario getAutorizo() { return autorizo; }
    public void setAutorizo(Usuario autorizo) { this.autorizo = autorizo; }
    public Usuario getAlumno() { return alumno; }
    public void setAlumno(Usuario alumno) { this.alumno = alumno; }
    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }
    public BigDecimal getValAnt() { return valAnt; }
    public void setValAnt(BigDecimal valAnt) { this.valAnt = valAnt; }
    public BigDecimal getValNuevo() { return valNuevo; }
    public void setValNuevo(BigDecimal valNuevo) { this.valNuevo = valNuevo; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getEst() { return est; }
    public void setEst(String est) { this.est = est; }
    public LocalDateTime getFec() { return fec; }
    public void setFec(LocalDateTime fec) { this.fec = fec; }
}
