package app.service;

import app.model.request.commands.Command;
import app.model.request.commands.CreateSubscription;
import app.model.request.commands.ExtendSubscription;
import app.model.response.admin.SubscriptionInfo;
import app.repository.CommandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {
    private final Logger logger = LoggerFactory.getLogger(AdminService.class);
    private final CommandRepository commandRepository;

    @Autowired
    public AdminService(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    public void createSubscription(int subscriptionId, LocalDateTime expiryTime)
            throws AdminServiceException {
        if (exists(subscriptionId)) {
            throw new AdminServiceException(
                    String.format("subscription with id %d already exists", subscriptionId));
        }

        logger.info(
                String.format(
                        "creating subscription with subscriptionId %d and expiryTime %s",
                        subscriptionId,
                        expiryTime.toString()
                )
        );
        commandRepository.insert(new CreateSubscription(subscriptionId, expiryTime));
    }

    public void extendSubscription(int subscriptionId, Duration duration)
            throws AdminServiceException {
        if (!exists(subscriptionId)) {
            throw new AdminServiceException(
                    String.format("subscription with id %d does not exist", subscriptionId));
        }

        logger.info(
                String.format(
                        "extending subscription with subscriptionId %d for duration %s",
                        subscriptionId,
                        duration.toString()
                )
        );
        commandRepository.insert(new ExtendSubscription(subscriptionId, duration));
    }

    public SubscriptionInfo getSubscriptionInfo(int subscriptionId)
            throws AdminServiceException {
        if (!exists(subscriptionId)) {
            throw new AdminServiceException(
                    String.format("subscription with id %d does not exist", subscriptionId));
        }

        List<Command> commands =
                commandRepository.findAll(
                        Example.of(new Command(subscriptionId)));

        Optional<CreateSubscription> createCommand =
                commands.stream()
                        .filter(command -> command instanceof CreateSubscription)
                        .map(command -> (CreateSubscription) command)
                        .findAny();
        List<ExtendSubscription> extendCommands =
                commands.stream()
                    .filter(command -> command instanceof ExtendSubscription)
                    .map(command -> (ExtendSubscription) command)
                    .collect(Collectors.toList());

        LocalDateTime expiryTime = createCommand.orElseThrow().expiryTime();
        for (ExtendSubscription extendCommand : extendCommands) {
            expiryTime = expiryTime.plus(extendCommand.duration());
        }

        return new SubscriptionInfo(subscriptionId, expiryTime);
    }

    public boolean exists(int subscriptionId) {
        return commandRepository.exists(Example.of(new Command(subscriptionId)));
    }

    public static class AdminServiceException extends Exception {
        public AdminServiceException(String message) {
            super(message);
        }
    }
}
