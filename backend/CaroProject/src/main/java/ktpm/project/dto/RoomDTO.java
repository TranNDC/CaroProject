package ktpm.project.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoomDTO implements Serializable {
    String id;
    String roomName;
    Integer betPoint;
    Boolean hasPassword;
    String host;
    String guest;
    String background;
}
