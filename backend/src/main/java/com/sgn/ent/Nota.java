package com.sgn.ent;

import com.sgn.enm.EstNota;
import com.sgn.enm.TipoNota;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "nota")
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bloque_id")
    private Bloque bloque;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alumno_legajo")
    private Usuario alumno;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoNota tipo;

    @Column(nullable = false)
    private int numPer;

    @Column(length = 200)
    private String titulo;

    @Column(precision = 4, scale = 2)
    private BigDecimal val;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstNota est = EstNota.NORMAL;

    @ManyToOne
    @JoinColumn(name = "cargado_por")
    private Usuario cargadoPor;

    @Column(nullable = false)
    private LocalDateTime fecCarga;

    private LocalDateTime fecMod;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Bloque getBloque() { return bloque; }
    public void setBloque(Bloque bloque) { this.bloque = bloque; }
    public Usuario getAlumno() { return alumno; }
    public void setAlumno(Usuario alumno) { this.alumno = alumno; }
    public TipoNota getTipo() { return tipo; }
    public void setTipo(TipoNota tipo) { this.tipo = tipo; }
    public int getNumPer() { return numPer; }
    public void setNumPer(int numPer) { this.numPer = numPer; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public BigDecimal getVal() { return val; }
    public void setVal(BigDecimal val) { this.val = val; }
    public EstNota getEst() { return est; }
    public void setEst(EstNota est) { this.est = est; }
    public Usuario getCargadoPor() { return cargadoPor; }
    public void setCargadoPor(Usuario cargadoPor) { this.cargadoPor = cargadoPor; }
    public LocalDateTime getFecCarga() { return fecCarga; }
    public void setFecCarga(LocalDateTime fecCarga) { this.fecCarga = fecCarga; }
    public LocalDateTime getFecMod() { return fecMod; }
    public void setFecMod(LocalDateTime fecMod) { this.fecMod = fecMod; }
}
