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

@Service
public class AuthSrv {

private final AuthenticationManager authMgr;
private final JwtUtil jwt;
private final UsuarioRepo repo;
private final PasswordEncoder passwordEncoder;

public AuthSrv(
        AuthenticationManager authMgr,
        JwtUtil jwt,
        UsuarioRepo repo,
        PasswordEncoder passwordEncoder) {

    this.authMgr = authMgr;
    this.jwt = jwt;
    this.repo = repo;
    this.passwordEncoder = passwordEncoder;
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
