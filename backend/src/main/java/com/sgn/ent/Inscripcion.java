package com.sgn.ent;

import jakarta.persistence.*;

@Entity
@Table(name = "inscripcion", uniqueConstraints = @UniqueConstraint(columnNames = {"alumno_legajo", "curso_id", "anio"}))
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alumno_legajo")
    private Usuario alumno;

    @ManyToOne(optional = false)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @Column(nullable = false)
    private int anio;

    @Column(nullable = false)
    private boolean activa;

    public boolean isActiva() {
    return activa;
}

public void setActiva(boolean activa) {
    this.activa = activa;
}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getAlumno() { return alumno; }
    public void setAlumno(Usuario alumno) { this.alumno = alumno; }
    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }
    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }
}
