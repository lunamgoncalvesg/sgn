package com.sgn.srv;

import com.sgn.config.JwtUtil;
import com.sgn.dto.Dtos.LoginReq;
import com.sgn.dto.Dtos.LoginRes;
import com.sgn.ent.Usuario;
import com.sgn.repo.UsuarioRepo;
import com.sgn.util.NotaReglas;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sgn.dto.Dtos.CambiarPasswordReq;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.Instant;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthSrv {

private final AuthenticationManager authMgr;
private final JwtUtil jwt;
private final UsuarioRepo repo;
private final PasswordEncoder passwordEncoder;
private final MailSrv mailSrv;
private final ConcurrentHashMap<String, Instant> recoveryRequests = new ConcurrentHashMap<>();
private final SecureRandom secureRandom = new SecureRandom();
private static final Duration RECOVERY_COOLDOWN = Duration.ofMinutes(3);

public AuthSrv(
        AuthenticationManager authMgr,
        JwtUtil jwt,
        UsuarioRepo repo,
        PasswordEncoder passwordEncoder,
        MailSrv mailSrv) {

    this.authMgr = authMgr;
    this.jwt = jwt;
    this.repo = repo;
    this.passwordEncoder = passwordEncoder;
    this.mailSrv = mailSrv;
}

    @Transactional
    public void recuperarPassword(String ident) {
        String identificador = NotaReglas.normalizarIdent(ident);
        if (identificador.isBlank()) return;

        Instant ahora = Instant.now();
        recoveryRequests.entrySet().removeIf(entry -> entry.getValue().isBefore(ahora.minus(RECOVERY_COOLDOWN)));
        Instant anterior = recoveryRequests.putIfAbsent(identificador, ahora);
        if (anterior != null) {
            if (anterior.isAfter(ahora.minus(RECOVERY_COOLDOWN))) return;
            if (!recoveryRequests.replace(identificador, anterior, ahora)) return;
        }

        Usuario u = repo.findByCuil(identificador)
            .or(() -> repo.findByMailIgnoreCase(ident.trim()))
            .orElse(null);
        if (u == null || !u.isActivo()) return;

        String passwordTemporal = generarPasswordTemporal();
        u.setPass(passwordEncoder.encode(passwordTemporal));
        u.setPrimerIngreso(true);
        repo.save(u);
        if (!mailSrv.passwordTemporal(u.getMail(), u.nomCompleto(), passwordTemporal)) {
            recoveryRequests.remove(identificador, ahora);
            throw new IllegalStateException("No se pudo enviar la contraseña temporal");
        }
    }

    private String generarPasswordTemporal() {
        String caracteres = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
        StringBuilder password = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            password.append(caracteres.charAt(secureRandom.nextInt(caracteres.length())));
        }
        return password.toString();
    }

    @Transactional
    public LoginRes login(LoginReq req) {
        String ident = NotaReglas.normalizarIdent(req.ident());
        Usuario u = repo.findByCuil(ident)
            .or(() -> repo.findByMail(req.ident().trim()))
            .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        if (!u.isActivo()) {
            throw new BadCredentialsException("Usuario inactivo");
        }
        try {
            authMgr.authenticate(new UsernamePasswordAuthenticationToken(u.getMail(), req.pass()));
        } catch (Exception e) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        u.setUltAcc(LocalDateTime.now());
repo.save(u);

var token = jwt.gen(
    u.getMail(),
    u.getLegajo(),
    u.getTipo().name()
);

return new LoginRes(
    token,
    u.getLegajo(),
    u.nomCompleto(),
    u.getTipo(),
    u.isPrimerIngreso()
);
    }

@Transactional
public void cambiarPassword(String mail, CambiarPasswordReq req) {

    Usuario u = repo.findByMail(mail)
        .orElseThrow(() ->
            new BadCredentialsException("Usuario no encontrado"));

    if (!u.isActivo()) {
        throw new BadCredentialsException("Usuario inactivo");
    }

    if (!passwordEncoder.matches(req.actual(), u.getPass())) {
        throw new BadCredentialsException(
            "La contraseña actual es incorrecta"
        );
    }

    if (req.nueva().equals(req.actual())) {
        throw new IllegalArgumentException(
            "La nueva contraseña debe ser diferente"
        );
    }

    u.setPass(passwordEncoder.encode(req.nueva()));

    u.setPrimerIngreso(false);

    repo.save(u);
}
}
