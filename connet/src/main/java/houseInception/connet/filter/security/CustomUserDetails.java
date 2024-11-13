package houseInception.connet.filter.security;

import houseInception.connet.domain.User;
import houseInception.connet.domain.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private Long userId;
    private String email;
    private UserRole userRole;

    public CustomUserDetails(String email) {
        this.email = email;
    }

    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.userRole = user.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (userRole != null) {
            return List.of(() -> userRole.toString());
        }
        return List.of();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
