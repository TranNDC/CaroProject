package ktpm.project.controller.http;

import ktpm.project.dto.RankingDTO;
import ktpm.project.dto.RoomsDTO;
import ktpm.project.service.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class RankController {
    @Autowired
    RankService rankService;

    @GetMapping(value = "api/rank")
    public ResponseEntity<?> getRank(){
        RankingDTO res = rankService.getRanking();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
