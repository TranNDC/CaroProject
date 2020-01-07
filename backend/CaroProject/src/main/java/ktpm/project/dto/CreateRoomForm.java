package ktpm.project.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreateRoomForm implements Serializable {
    String roomName;
    Integer betPoint;
    String password;
    String host;
    String background;
}
