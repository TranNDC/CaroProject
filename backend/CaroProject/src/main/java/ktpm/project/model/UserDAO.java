package ktpm.project.model;

import ktpm.project.utils.utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "Users")
public class UserDAO {
    @Id String id;
    String username;
    String password;
    Integer winCount;
    Integer loseCount;
    Integer drawCount;
    Integer points;
    String avatar;



    public UserDAO(String _username, String _password) {
        username = _username;
        password = _password;
        winCount =0;
        loseCount =0;
        drawCount =0;
        avatar = "";
    }

    public void setRamdomAvatar(Integer nAva) {
        avatar = utils.randomImage(nAva);
    }
}
