package com.sgn.ent;

import com.sgn.enm.Esp;
import com.sgn.enm.Turno;
import jakarta.persistence.*;

@Entity
@Table(name = "curso")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int anio;

    @Column(nullable = false)
    private int div;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Turno turno;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Esp esp;

    public String etiqueta() {
        return anio + "°" + div + " " + turno.name().toLowerCase();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }
    public int getDiv() { return div; }
    public void setDiv(int div) { this.div = div; }
    public Turno getTurno() { return turno; }
    public void setTurno(Turno turno) { this.turno = turno; }
    public Esp getEsp() { return esp; }
    public void setEsp(Esp esp) { this.esp = esp; }
}
