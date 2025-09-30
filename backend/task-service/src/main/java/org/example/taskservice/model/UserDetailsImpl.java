package org.example.userservice.model;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails{
    private UUID id;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;

    
    public UUID getUUID() { return id; };
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {return authorities;}
    @Override public String getPassword() {return null;}
    @Override public String getUsername() {return username;}
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }


}
