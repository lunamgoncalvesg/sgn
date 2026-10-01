package com.sgn.config;

import com.sgn.repo.UsuarioRepo;
import com.sgn.srv.UsrDetailsSrv;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsrDetailsSrv usrDetailsSrv;
    private final UsuarioRepo usuarioRepo;

    public JwtFilter(
            JwtUtil jwtUtil,
            UsrDetailsSrv usrDetailsSrv,
            UsuarioRepo usuarioRepo
    ) {
        this.jwtUtil = jwtUtil;
        this.usrDetailsSrv = usrDetailsSrv;
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain
    ) throws ServletException, IOException {

        var hdr = req.getHeader("Authorization");

        if (hdr != null && hdr.startsWith("Bearer ")) {

            var token = hdr.substring(7);

            try {
                if (jwtUtil.valid(token)) {

                    var mail = jwtUtil.mail(token);

                    var det = usrDetailsSrv.loadUserByUsername(mail);

                    var auth = new UsernamePasswordAuthenticationToken(
                            det,
                            null,
                            det.getAuthorities()
                    );

                    auth.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(req)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(auth);

                    var usuario = usuarioRepo.findByMail(mail).orElse(null);

                    if (usuario != null
                            && usuario.isPrimerIngreso()
                            && !esCambioPassword(req)) {

                        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        res.setContentType("application/json");
                        res.getWriter().write(
                                "{\"error\":\"Debe cambiar su contraseña antes de continuar\"}"
                        );
                        return;
                    }
                }

            } catch (Exception ignored) {
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(req, res);
    }

    private boolean esCambioPassword(HttpServletRequest req) {
        return "/api/auth/cambiar-password".equals(req.getRequestURI())
                && "POST".equalsIgnoreCase(req.getMethod());
    }
}