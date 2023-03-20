package stubserver;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import search.SearchEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class StubServer {
    private final Map<SearchEngine, Integer> searchEngineDelays;

    public StubServer() {
        searchEngineDelays =
                Map.of(
                        SearchEngine.GOOGLE, 0,
                        SearchEngine.YANDEX, 0,
                        SearchEngine.BING, 0
                );
    }

    public StubServer(Map<SearchEngine, Integer> searchEngineDelays) {
        this.searchEngineDelays = searchEngineDelays;
    }

    public JsonObject fetchData(SearchEngine searchEngine, String request)
            throws IOException, InterruptedException {
        Thread.sleep(searchEngineDelays.get(searchEngine));

        Gson gson = new Gson();

        InputStream inputStream =
                getClass().getClassLoader()
                        .getResourceAsStream(searchEngine.name() + "/response.json");

        if (inputStream == null) {
            throw new IOException("Could not open input stream to read json file from resources");
        }
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            return gson.fromJson(bufferedReader, JsonObject.class);
        }
    }
}
