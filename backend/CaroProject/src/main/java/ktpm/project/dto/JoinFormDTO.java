package ktpm.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class JoinFormDTO implements Serializable {
    String roomId;
    String username;
    String password;
}
