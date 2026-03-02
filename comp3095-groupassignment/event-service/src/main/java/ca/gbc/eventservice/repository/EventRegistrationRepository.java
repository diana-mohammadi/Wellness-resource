package ca.gbc.eventservice.repository;

import ca.gbc.eventservice.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    List<EventRegistration> findByEventId(Long eventId);

    Optional<EventRegistration> findByEventIdAndStudentId(Long eventId, String studentId);

    long countByEventId(Long eventId);
}
