package ktpm.project.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class RoomDTO implements Serializable {
    String id;
    String roomName;
    Integer betPoint;
    Integer guestPoint;
    Integer hostPoint;
    Boolean isHostPlayFirst;
    Boolean hasPassword;
    UserDTO host;
    UserDTO guest;
    String background;
    @JsonIgnore
    Boolean guestReady;
    @JsonIgnore
    Boolean hostReady;
}
