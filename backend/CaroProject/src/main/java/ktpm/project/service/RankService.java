package ktpm.project.service;

import ktpm.project.dto.RankingDTO;
import ktpm.project.dto.UserDTO;
import ktpm.project.model.UserDAO;
import ktpm.project.repository.RankRepo;
import ktpm.project.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankService {
    @Autowired
    RankRepo rankRepo;

    @Autowired
    UserRepo userRepo;

    public RankingDTO getRanking(){
        RankingDTO res = new RankingDTO();
        res.setRanking(rankRepo.getRanking());
        return res;
    }

    public void AddToRank(UserDTO user){
        AddToRank(user.getUsername());
    }

    public void AddToRank(String username) {
        UserDAO userDAO = userRepo.findFirstByUsername(username).orElse(null);
        if (userDAO != null){
            rankRepo.save(userDAO);
        }
    }

    public void reloadRanking(){
        List<UserDAO> users = userRepo.findAll();
        rankRepo.reload(users);
    }
}
