package ktpm.project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RoomsDTO implements Serializable {
    List<RoomDTO> rooms;
}
