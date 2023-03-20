import actor.MasterActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import search.AggregatedSearchResponse;

import java.time.Duration;

import static akka.actor.Status.Failure;
import static akka.pattern.Patterns.ask;

public class Main {
    public static void main(String[] args) throws Throwable {
        ActorSystem system = ActorSystem.create("searchAggregator");
        ActorRef actor = system.actorOf(
                MasterActor.defaultProps(),
                "masterActor"
        );

        Object result =
                ask(actor, "cats", Duration.ofSeconds(2))
                        .toCompletableFuture()
                        .join();

        if (result instanceof AggregatedSearchResponse response) {
            System.out.println(response);
        } else if (result instanceof Failure failure) {
            throw failure.cause();
        }
    }
}
