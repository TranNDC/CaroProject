package ktpm.project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class SingInForm implements Serializable {
    String username;
    String password;
}
