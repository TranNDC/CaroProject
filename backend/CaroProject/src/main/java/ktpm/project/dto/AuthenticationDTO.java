package ktpm.project.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuthenticationDTO implements Serializable {
    String token;
    UserDTO user;
}
