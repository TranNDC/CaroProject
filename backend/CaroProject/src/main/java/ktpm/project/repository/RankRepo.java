package ktpm.project.repository;

import ktpm.project.dto.UserDTO;
import ktpm.project.model.RankingDAO;
import ktpm.project.model.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class RankRepo {
    @Autowired
    RedisTemplate redisTemplate;

    @Value("${number.users.ranking}")
    Integer noRanking;

    private String rankKey() {
        return "ranking";
    }

    private void addUserToRank(UserDAO user){
        redisTemplate.opsForZSet().add(rankKey(), user.getUsername(), user.getPoints());
    }

    public void reload(List<UserDAO> users) {
        redisTemplate.delete(rankKey());
        for (UserDAO user : users) {
            addUserToRank(user);
        }
    }

    public void save(UserDAO user) {
        addUserToRank(user);
    }

    public void save(String username, Integer points) {
        redisTemplate.opsForZSet().add(rankKey(), username, points);
    }


    public long getRankByUsername(String username){
        return redisTemplate.opsForZSet().rank(rankKey(),username);

    }

    public List<RankingDAO> getRanking() {

        List<RankingDAO> rankingResult = new ArrayList<>();
        Set<String> usernameRank;

        usernameRank = redisTemplate.opsForZSet().reverseRange(rankKey(), 0, 100);

        for (String username : usernameRank) {
            RankingDAO user = new RankingDAO();
            user.setUsername(username);
            double score = redisTemplate.opsForZSet().score(rankKey(), username);
            user.setPoints((int) score);
            rankingResult.add(user);
        }
        return rankingResult;
    }

}
