package ktpm.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SocketDTO implements Serializable {
    String username;
    String roomId;
}
