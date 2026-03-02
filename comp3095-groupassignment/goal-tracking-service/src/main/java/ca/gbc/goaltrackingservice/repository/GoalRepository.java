package ca.gbc.goaltrackingservice.repository;

import ca.gbc.goaltrackingservice.model.Goal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends MongoRepository<Goal, String> {

    List<Goal> findByStudentId(String studentId);

    List<Goal> findByCategory(String category);

    List<Goal> findByStatus(Goal.GoalStatus status);
}
