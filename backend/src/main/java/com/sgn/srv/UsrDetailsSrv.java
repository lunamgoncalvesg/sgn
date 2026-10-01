package com.sgn.srv;

import com.sgn.ent.Usuario;
import com.sgn.repo.UsuarioRepo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsrDetailsSrv implements UserDetailsService {

    private final UsuarioRepo repo;

    public UsrDetailsSrv(UsuarioRepo repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Usuario u = repo.findByMail(mail)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        if (!u.isActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo");
        }
        return new User(
            u.getMail(),
            u.getPass(),
            List.of(new SimpleGrantedAuthority("ROLE_" + u.getTipo().name()))
        );
    }

    public Usuario actual(String mail) {
        return repo.findByMail(mail).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
