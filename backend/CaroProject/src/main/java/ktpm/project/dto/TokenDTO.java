package ktpm.project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class TokenDTO implements Serializable {
    String token;
    TokenDTO(String _token){
        token = _token;
    }
}
