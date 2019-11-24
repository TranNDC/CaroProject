package ktpm.project.dto;

import ktpm.project.model.UserDAO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
public class UserDTO implements UserDetails {
    String username;
    Integer winCount;
    Integer loseCount;
    Integer drawCount;
    Integer points;
    String avatar;

    public UserDTO(UserDAO userDAO) {
        username = userDAO.getUsername();
        winCount = userDAO.getWinCount();
        loseCount = userDAO.getLoseCount();
        drawCount = userDAO.getDrawCount();
        points = userDAO.getPoints();
        avatar = userDAO.getAvatar();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
