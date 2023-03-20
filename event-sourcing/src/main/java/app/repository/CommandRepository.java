package app.repository;

import app.model.request.commands.Command;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommandRepository extends MongoRepository<Command, Integer> {
}
