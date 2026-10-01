package com.sgn.ent;

import com.sgn.enm.TipoBol;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "boletin")
public class Boletin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alumno_legajo")
    private Usuario alumno;

    @Column(nullable = false)
    private int anio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoBol tipoPer;

    @Column(nullable = false)
    private int numPer;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(nullable = false)
    private LocalDateTime fecGen;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getAlumno() { return alumno; }
    public void setAlumno(Usuario alumno) { this.alumno = alumno; }
    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }
    public TipoBol getTipoPer() { return tipoPer; }
    public void setTipoPer(TipoBol tipoPer) { this.tipoPer = tipoPer; }
    public int getNumPer() { return numPer; }
    public void setNumPer(int numPer) { this.numPer = numPer; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public LocalDateTime getFecGen() { return fecGen; }
    public void setFecGen(LocalDateTime fecGen) { this.fecGen = fecGen; }
}
