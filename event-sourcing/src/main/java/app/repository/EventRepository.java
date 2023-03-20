package app.repository;

import app.model.request.events.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends MongoRepository<Event, Integer> {
    List<Event> findByTimestampGreaterThanOrderByTimestamp(LocalDateTime timestamp);
}
