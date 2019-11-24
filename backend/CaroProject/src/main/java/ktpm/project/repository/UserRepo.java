package ktpm.project.repository;

import ktpm.project.dto.UserDTO;
import ktpm.project.model.UserDAO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@RepositoryRestResource(collectionResourceRel = "Users")
public interface UserRepo extends MongoRepository<UserDAO,String> {
//    List<UserDAO> findByUsername(String username);
    Optional<UserDAO> findFirstByUsername(String username);
}
