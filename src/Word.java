import java.util.Optional;

public class Word {

    private String word;
    private Optional<Integer> popularity;

    Word(String word, Optional<Integer> popularity) {
        this.word = word;
        this.popularity = popularity;
    }

    String getWord() {
        return word;
    }

    int getPopularityInt() {
        return popularity.get();
    }

    Optional<Integer> getPopularityOptional() {
        return popularity;
    }

    public String toString() {
        if (popularity.isPresent())
            return word + " -> " + popularity.get();
        else return word;
    }
}
