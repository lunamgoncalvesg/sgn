package com.sgn.ent;

import com.sgn.enm.EstRep;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reporte")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "nota_id")
    private Nota nota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alumno_legajo")
    private Usuario alumno;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstRep est;

    @Column(nullable = false)
    private LocalDateTime fecRep;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Nota getNota() { return nota; }
    public void setNota(Nota nota) { this.nota = nota; }
    public Usuario getAlumno() { return alumno; }
    public void setAlumno(Usuario alumno) { this.alumno = alumno; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public EstRep getEst() { return est; }
    public void setEst(EstRep est) { this.est = est; }
    public LocalDateTime getFecRep() { return fecRep; }
    public void setFecRep(LocalDateTime fecRep) { this.fecRep = fecRep; }
}
