package ktpm.project.model;

import ktpm.project.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Getter
@Setter
@RedisHash("Rooms")
public class RoomDAO implements Serializable {
    @Id
    Integer id;
    String name;
    String password;
    Integer betPoint;
    Integer isWaiting;
    @Indexed
    String host;
    @Indexed
    String guest;
}
