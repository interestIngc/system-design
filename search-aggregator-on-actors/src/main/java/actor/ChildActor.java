package actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import search.SearchQuery;
import search.SearchResponse;
import stubserver.StubServer;

import java.io.IOException;

import static akka.actor.Status.Failure;

public class ChildActor extends AbstractActor {
    private final StubServer server;

    public ChildActor(StubServer server) {
        this.server = server;
    }

    public static Props props(StubServer stubServer) {
        return Props.create(ChildActor.class, stubServer);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchQuery.class, this::handleQuery).build();
    }

    private void handleQuery(SearchQuery searchQuery) {
        try {
            JsonObject response = server.fetchData(searchQuery.searchEngine, searchQuery.request);

            SearchResponse searchResponse = new SearchResponse();
            searchResponse.setSearchEngine(response.get("engine").getAsString());

            JsonArray jsonArray = response.getAsJsonArray("items");
            for (JsonElement jsonElement : jsonArray) {
                String text = jsonElement.getAsJsonObject().get("text").getAsString();
                searchResponse.addItem(new SearchResponse.Item(text));
            }

            sendResults(searchResponse);
        } catch (IOException | InterruptedException e) {
            sendResults(new Failure(e));
        }
    }

    private void sendResults(Object result) {
        getSender().tell(result, getSelf());

        getContext().stop(getSelf());
    }
}
