package actor;


import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.Props;
import search.AggregatedSearchResponse;
import search.SearchEngine;
import search.SearchQuery;
import search.SearchResponse;
import stubserver.StubServer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static akka.actor.Status.Failure;

public class MasterActor extends AbstractActorWithTimers {
    private static final int RESPONSE_TIMEOUT_MS = 1000;
    private static final List<SearchEngine> SEARCH_ENGINES =
            List.of(
                    SearchEngine.GOOGLE,
                    SearchEngine.YANDEX,
                    SearchEngine.BING
            );

    private final List<SearchResponse> responses = new ArrayList<>();
    private final Supplier<StubServer> serverSupplier;

    private ActorRef parent;

    public MasterActor(Supplier<StubServer> serverSupplier) {
        this.serverSupplier = serverSupplier;
    }

    public static Props props(Supplier<StubServer> serverSupplier) {
        return Props.create(MasterActor.class, serverSupplier);
    }

    public static Props defaultProps() {
        return props(StubServer::new);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::handleIncomingRequest)
                .match(SearchResponse.class, this::handleResponseFromChild)
                .match(ResponseTimeout.class, e -> sendResultsAndStop())
                .match(Failure.class, this::sendResultsAndStop)
                .build();
    }

    private void handleIncomingRequest(String request) {
        parent = getSender();

        for (SearchEngine searchEngine : SEARCH_ENGINES) {
            ActorRef actor =
                    getContext().actorOf(
                            ChildActor.props(serverSupplier.get()),
                            searchEngine.name()
                    );
            actor.tell(new SearchQuery(searchEngine, request), getSelf());
        }

        getTimers().startSingleTimer(
                "timeout",
                new ResponseTimeout(),
                Duration.ofMillis(RESPONSE_TIMEOUT_MS)
        );
    }

    private void handleResponseFromChild(SearchResponse searchResponse) {
        responses.add(searchResponse);

        if (responses.size() == SEARCH_ENGINES.size()) {
            sendResultsAndStop();
        }
    }

    private void sendResultsAndStop() {
        sendResultsAndStop(new AggregatedSearchResponse(responses));
    }

    private void sendResultsAndStop(Object result) {
        parent.tell(result, getSelf());

        getContext().stop(getSelf());
    }

    private static final class ResponseTimeout {}
}
