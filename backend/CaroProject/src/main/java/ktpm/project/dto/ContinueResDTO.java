package ktpm.project.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ktpm.project.dto.ErrorDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ContinueResDTO implements Serializable {
    int code;
    ErrorDTO error;
    RoomDTO room;
}
