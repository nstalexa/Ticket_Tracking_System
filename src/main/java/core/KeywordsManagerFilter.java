package core;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class KeywordsManagerFilter implements SearchInterface<Ticket>{
    @Getter
    @Setter
    private List<String> keywords;

    public KeywordsManagerFilter(List<String> keywords) {
        this.keywords = keywords;
    }


    @Override
    public boolean found(Ticket t, SearchContext ctx) {

        String allText = t.getTitle() + " " + (t.getDescription() == null ? "" : t.getDescription());
        String[] words = allText.split("[^a-zA-Z0-9]+");

        List<String> foundWords = new ArrayList<>();

        for (String word : words) {
            if (word.isEmpty()) continue;

            String lowerWord = word.toLowerCase();

            for (String k : keywords) {
                if (lowerWord.contains(k.toLowerCase())) {
                    foundWords.add(lowerWord);
                    break;
                }
            }
        }

        if (foundWords.isEmpty()) {
            return false;
        }

        List<String> result = foundWords.stream()
                .distinct()
                .sorted()
                .toList();

        ctx.getMatchingWords().put(t.getId(), result);
        return true;
    }
}
