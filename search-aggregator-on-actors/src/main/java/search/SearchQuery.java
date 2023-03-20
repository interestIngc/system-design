package search;

public class SearchQuery {
    public final SearchEngine searchEngine;
    public final String request;

    public SearchQuery(SearchEngine searchEngine, String request) {
        this.searchEngine = searchEngine;
        this.request = request;
    }
}
