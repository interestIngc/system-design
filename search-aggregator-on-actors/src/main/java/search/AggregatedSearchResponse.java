package search;

import java.util.List;

public class AggregatedSearchResponse {
    private final List<SearchResponse> searchResponses;

    public AggregatedSearchResponse(List<SearchResponse> searchResponses) {
        this.searchResponses = searchResponses;
    }

    public List<SearchResponse> searchResponses() {
        return searchResponses;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AggregatedSearchResponse{\n");
        builder.append("\tsearchResponses=[\n");
        for (SearchResponse searchResponse : searchResponses) {
            builder.append(searchResponse).append(",\n");
        }
        builder.append("\t]\n");
        builder.append("}");
        return builder.toString();
    }
}
