package actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestKit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import search.AggregatedSearchResponse;
import search.SearchEngine;
import search.SearchResponse;
import stubserver.StubServer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static org.assertj.core.api.Assertions.assertThat;

public class MasterActorTest {
    private static final int TIMEOUT_1 = 600;
    private static final int TIMEOUT_2 = 1300;
    private static final String REQUEST = "request";

    static ActorSystem system;

    @BeforeClass
    public static void setUp() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(
                system,
                scala.concurrent.duration.Duration.create(2, TimeUnit.SECONDS),
                false
        );
        system = null;
    }

    @Test
    public void allChildActorsRespondedOnTime_resultsFromAllSearchEnginesReturned() {
        new TestKit(system) {
            {
                final ActorRef masterActor =
                        system.actorOf(
                                MasterActor.props(
                                        () -> new StubServer(
                                                Map.of(
                                                        SearchEngine.GOOGLE, TIMEOUT_1,
                                                        SearchEngine.YANDEX, TIMEOUT_1,
                                                        SearchEngine.BING, TIMEOUT_1
                                                )
                                        )
                                )
                        );

                final Object result =
                        ask(masterActor, REQUEST, Duration.ofSeconds(2))
                                .toCompletableFuture()
                                .join();

                Assert.assertTrue(result instanceof AggregatedSearchResponse);
                AggregatedSearchResponse response = (AggregatedSearchResponse) result;
                List<SearchResponse> searchResponses = response.searchResponses();

                assertThat(searchResponses).hasSize(3);

                List<SearchEngine> searchEngines =
                        searchResponses.stream()
                                .map(SearchResponse::searchEngine)
                                .toList();
                assertThat(searchEngines).containsExactlyInAnyOrder(
                        SearchEngine.GOOGLE,
                        SearchEngine.YANDEX,
                        SearchEngine.BING
                );
            }
        };
    }

    @Test
    public void onlyTwoChildActorsRespondedOnTime_resultsFromTwoSearchEnginesReturned() {
        new TestKit(system) {
            {
                final ActorRef masterActor =
                        system.actorOf(
                                MasterActor.props(
                                        () -> new StubServer(
                                                Map.of(
                                                        SearchEngine.GOOGLE, TIMEOUT_1,
                                                        SearchEngine.YANDEX, TIMEOUT_1,
                                                        SearchEngine.BING, TIMEOUT_2
                                                )
                                        )
                                )
                        );

                final Object result =
                        ask(masterActor, REQUEST, Duration.ofSeconds(2))
                                .toCompletableFuture()
                                .join();

                Assert.assertTrue(result instanceof AggregatedSearchResponse);
                AggregatedSearchResponse response = (AggregatedSearchResponse) result;
                List<SearchResponse> searchResponses = response.searchResponses();

                assertThat(searchResponses).hasSize(2);

                List<SearchEngine> searchEngines =
                        searchResponses.stream()
                                .map(SearchResponse::searchEngine)
                                .toList();
                assertThat(searchEngines).containsExactlyInAnyOrder(
                        SearchEngine.GOOGLE,
                        SearchEngine.YANDEX
                );
            }
        };
    }

    @Test
    public void noneOfChildActorsRespondedOnTime_noResultsReturned() {
        new TestKit(system) {
            {
                final ActorRef masterActor =
                        system.actorOf(
                                MasterActor.props(
                                        () -> new StubServer(
                                                Map.of(
                                                        SearchEngine.GOOGLE, TIMEOUT_2,
                                                        SearchEngine.YANDEX, TIMEOUT_2,
                                                        SearchEngine.BING, TIMEOUT_2
                                                )
                                        )
                                )
                        );

                final Object result =
                        ask(masterActor, REQUEST, Duration.ofSeconds(2))
                                .toCompletableFuture()
                                .join();

                Assert.assertTrue(result instanceof AggregatedSearchResponse);
                AggregatedSearchResponse response = (AggregatedSearchResponse) result;

                assertThat(response.searchResponses()).isEmpty();
            }
        };
    }
}
