package ktpm.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MoveDTO implements Serializable {
    String roomId;
    String username;
    int x;
    int y;
    String result;
}
