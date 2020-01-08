package ktpm.project.dto;

import ktpm.project.model.RankingDAO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RankingDTO {
    List<RankingDAO> ranking;
}
