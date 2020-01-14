package ktpm.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class JoinResDTO implements Serializable {
    int code; // 200 | 400
    ErrorDTO error;
    RoomDTO room;
}
