package search;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchResponse {
    private SearchEngine searchEngine;
    private final List<Item> items = new ArrayList<>();

    public void setSearchEngine(String engineName) {
        searchEngine = SearchEngine.valueOf(engineName.toUpperCase(Locale.ROOT));
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public SearchEngine searchEngine() {
        return searchEngine;
    }

    public List<Item> items() {
        return items;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\t\tSearchResponse{\n");
        builder.append("\t\t\tsearchEngine=").append(searchEngine).append(",\n");
        builder.append("\t\t\titems=[\n");
        for (Item item : items) {
            builder.append("\t\t\t\t").append(item).append(",\n");
        }
        builder.append("\t\t\t]\n");
        builder.append("\t\t}");

        return builder.toString();
    }

    public static class Item {
        private final String text;

        public Item(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "text='" + text + '\'' +
                    '}';
        }
    }
}
