package ca.gbc.wellnessresourceservice.repository;

import ca.gbc.wellnessresourceservice.model.WellnessResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WellnessResourceRepository extends JpaRepository<WellnessResource, Long> {

    List<WellnessResource> findByCategory(String category);

    @Query("SELECT w FROM WellnessResource w WHERE " +
           "LOWER(w.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(w.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<WellnessResource> searchByKeyword(@Param("keyword") String keyword);
}
