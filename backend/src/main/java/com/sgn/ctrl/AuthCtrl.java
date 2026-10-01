package com.sgn.ctrl;

import com.sgn.dto.Dtos.*;
import com.sgn.srv.AuthSrv;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthCtrl {

    private final AuthSrv srv;

    public AuthCtrl(AuthSrv srv) {
        this.srv = srv;
    }

    @PostMapping("/login")
    public LoginRes login(@Valid @RequestBody LoginReq req) {
        return srv.login(req);
    }

    @PostMapping("/cambiar-password")
    public void cambiarPassword(
            @Valid @RequestBody CambiarPasswordReq req,
            Authentication authentication) {

        srv.cambiarPassword(authentication.getName(), req);
    }
}