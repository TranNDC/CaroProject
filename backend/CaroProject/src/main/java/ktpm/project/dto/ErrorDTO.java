package ktpm.project.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
class Error implements Serializable{
    String detail;
    String title;
}

@Getter
@Setter
@NoArgsConstructor
public class ErrorDTO implements Serializable {
    Error error;
    String getTitle(){
        return error.getTitle();
    }

    String getDetail(){
        return error.getDetail();
    }

    void setTitle(String title){
        error.setTitle(title);
    }

    void setDetail(String detail){
        error.setDetail(detail);
    }

    public  ErrorDTO(String title, String detail){
        error = new Error();
        error.setDetail(detail);
        error.setTitle(title);
    }
}
