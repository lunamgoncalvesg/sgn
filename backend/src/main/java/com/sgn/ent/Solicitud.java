package com.sgn.ent;

import com.sgn.enm.EstSol;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitud")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "nota_id")
    private Nota nota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "solicitante_legajo")
    private Usuario solicitante;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Column(precision = 4, scale = 2)
    private BigDecimal valActual;

    @Column(precision = 4, scale = 2)
    private BigDecimal valNuevo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstSol est;

    @Column(nullable = false)
    private LocalDateTime fecSol;

    private LocalDateTime fecRes;

    @ManyToOne
    @JoinColumn(name = "autorizo_legajo")
    private Usuario autorizo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Nota getNota() { return nota; }
    public void setNota(Nota nota) { this.nota = nota; }
    public Usuario getSolicitante() { return solicitante; }
    public void setSolicitante(Usuario solicitante) { this.solicitante = solicitante; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public BigDecimal getValActual() { return valActual; }
    public void setValActual(BigDecimal valActual) { this.valActual = valActual; }
    public BigDecimal getValNuevo() { return valNuevo; }
    public void setValNuevo(BigDecimal valNuevo) { this.valNuevo = valNuevo; }
    public EstSol getEst() { return est; }
    public void setEst(EstSol est) { this.est = est; }
    public LocalDateTime getFecSol() { return fecSol; }
    public void setFecSol(LocalDateTime fecSol) { this.fecSol = fecSol; }
    public LocalDateTime getFecRes() { return fecRes; }
    public void setFecRes(LocalDateTime fecRes) { this.fecRes = fecRes; }
    public Usuario getAutorizo() { return autorizo; }
    public void setAutorizo(Usuario autorizo) { this.autorizo = autorizo; }
}
