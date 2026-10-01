package com.sgn.ent;

import jakarta.persistence.*;

@Entity
@Table(name = "bloque", uniqueConstraints = @UniqueConstraint(columnNames = {"materia_id", "curso_id", "anio"}))
public class Bloque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "materia_id")
    private Materia materia;

    @ManyToOne(optional = false)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @ManyToOne(optional = false)
    @JoinColumn(name = "prof_legajo")
    private Usuario prof;

    @Column(nullable = false)
    private int anio;

    public String titulo() {
        return materia.getNom() + " " + curso.etiqueta();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Materia getMateria() { return materia; }
    public void setMateria(Materia materia) { this.materia = materia; }
    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }
    public Usuario getProf() { return prof; }
    public void setProf(Usuario prof) { this.prof = prof; }
    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }
}
