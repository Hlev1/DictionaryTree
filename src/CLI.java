import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * @author Harry Levick
 */
public class CLI {

    /**
     * Loads words (lines) from the given file and inserts them into
     * a dictionary.
     *
     * @param f the file from which the words will be loaded
     * @return the dictionary with the words loaded from the given file
     * @throws IOException if there was a problem opening/reading from the file
     */
    static DictionaryTree loadWords(File f) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String word;
            DictionaryTree d = new DictionaryTree(Optional.empty(), Optional.empty());

            int popularity = -1;
            System.out.println("");
            while ((word = reader.readLine()) != null) {
                d.insert(word, popularity);
                popularity --;
            }

            System.out.println("Tree size = " + d.size());
            System.out.println("Tree height = " + d.height());
            System.out.println("Longest word = " + d.longestWord());
            System.out.println("Maximum branching = " + d.maximumBranching());
            System.out.println("Number of leaves = " + d.numLeaves());
            System.out.println("There are " + d.allWords().size() + " words returned by the allWords() method");
            System.out.println("There are " + d.size() + " nodes in the tree");
            System.out.println("Tree contains 'hello'? " + d.contains("hello"));
            System.out.println("remove 'hello' -> " + d.remove("hello"));
            System.out.println("There are " + d.allWords().size() + " words returned by the allWords() method");
            System.out.println("There are " + d.size() + " nodes in the tree");
            System.out.println("Tree contains 'hello'? " + d.contains("hello"));
            System.out.println(d.predict("h"));




            return d;
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.print("Loading dictionary ... ");
        DictionaryTree d = loadWords(new File(args[0]));
        /*
        DictionaryTree d = new DictionaryTree();
        d.insert("word");
        d.insert("hello");
        d.insert("hell");
        d.insert("testing");
        d.insert("compsci");
        System.out.println("done");

        System.out.println("Tree size = " + d.size());
        System.out.println("Tree height = " + d.height());
        System.out.println("Longest word = " + d.longestWord());
        System.out.println("Maximum branching = " + d.maximumBranching());
        System.out.println("Number of leaves = " + d.numLeaves());
        System.out.println("Tree contains 'hello'? " + d.contains("hello"));
        System.out.println("There are " + d.allWords().size() + " words returned by the allWords() method");
        System.out.println("There are " + d.size() + " nodes in the tree");
        System.out.println("remove 'hello' -> " + d.remove("hell"));
        System.out.println("Tree contains 'hello'? " + d.contains("hello"));
        System.out.println("There are " + d.allWords().size() + " words returned by the allWords() method");
        System.out.println("There are " + d.size() + " nodes in the tree");
        */

        System.out.println("Enter prefixes for prediction below.");

        try (BufferedReader fromUser = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.println("---> " + d.predict(fromUser.readLine()));
            }
        }
    }

}
