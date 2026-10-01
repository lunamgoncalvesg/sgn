package com.sgn.ent;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String dest;

    @Column(nullable = false, length = 200)
    private String asunto;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String cuerpo;

    @Column(nullable = false)
    private boolean enviado;

    @Column(length = 500)
    private String error;

    @Column(nullable = false)
    private LocalDateTime fec;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDest() { return dest; }
    public void setDest(String dest) { this.dest = dest; }
    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }
    public String getCuerpo() { return cuerpo; }
    public void setCuerpo(String cuerpo) { this.cuerpo = cuerpo; }
    public boolean isEnviado() { return enviado; }
    public void setEnviado(boolean enviado) { this.enviado = enviado; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public LocalDateTime getFec() { return fec; }
    public void setFec(LocalDateTime fec) { this.fec = fec; }
}
