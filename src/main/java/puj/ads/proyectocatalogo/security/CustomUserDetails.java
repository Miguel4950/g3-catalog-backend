package puj.ads.proyectocatalogo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import puj.ads.proyectocatalogo.model.Usuario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;
    private final List<GrantedAuthority> authorities = new ArrayList<>();

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
        switch (usuario.getTipoUsuarioId()) {
            case 3 -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                authorities.add(new SimpleGrantedAuthority("ROLE_BIBLIOTECARIO"));
                authorities.add(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"));
            }
            case 2 -> {
                authorities.add(new SimpleGrantedAuthority("ROLE_BIBLIOTECARIO"));
                authorities.add(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"));
            }
            default -> authorities.add(new SimpleGrantedAuthority("ROLE_ESTUDIANTE"));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return usuario.getIntentosFallidos() < 5;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.getEstadoUsuarioId() != null && usuario.getEstadoUsuarioId() == 1;
    }

    public Integer getUserId() {
        return usuario.getId();
    }
}
