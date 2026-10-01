package com.sgn.ent;

import com.sgn.enm.TipoUsr;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @Column(length = 20)
    private String legajo;

    @Column(length = 13, unique = true)
    private String cuil;

    @Column(nullable = false, length = 80)
    private String nom;

    @Column(length = 80)
    private String nom2;

    @Column(nullable = false, length = 80)
    private String ape;

    @Column(length = 80)
    private String ape2;

    @Column(nullable = false, unique = true)
    private String mail;

    private String mailTutor;

    @Column(nullable = false)
    private String pass;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoUsr tipo;

    private LocalDateTime ultAcc;

    @Column(nullable = false)
    private boolean activo = true;

    public String nomCompleto() {
        String n = nom + (nom2 != null && !nom2.isBlank() ? " " + nom2 : "");
        String a = ape + (ape2 != null && !ape2.isBlank() ? " " + ape2 : "");
        return n + " " + a;
    }

    @Column(nullable = false)
private boolean primerIngreso = true;

public boolean isPrimerIngreso() {
    return primerIngreso;
}

public void setPrimerIngreso(boolean primerIngreso) {
    this.primerIngreso = primerIngreso;
}

    public String getLegajo() { return legajo; }
    public void setLegajo(String legajo) { this.legajo = legajo; }
    public String getCuil() { return cuil; }
    public void setCuil(String cuil) { this.cuil = cuil; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getNom2() { return nom2; }
    public void setNom2(String nom2) { this.nom2 = nom2; }
    public String getApe() { return ape; }
    public void setApe(String ape) { this.ape = ape; }
    public String getApe2() { return ape2; }
    public void setApe2(String ape2) { this.ape2 = ape2; }
    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }
    public String getMailTutor() { return mailTutor; }
    public void setMailTutor(String mailTutor) { this.mailTutor = mailTutor; }
    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }
    public TipoUsr getTipo() { return tipo; }
    public void setTipo(TipoUsr tipo) { this.tipo = tipo; }
    public LocalDateTime getUltAcc() { return ultAcc; }
    public void setUltAcc(LocalDateTime ultAcc) { this.ultAcc = ultAcc; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
