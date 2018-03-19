import javax.swing.text.html.Option;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiFunction;
// Since we can check the DictionaryTree's hash table for any child trees, we dont need seperate classes for Empty
// and Cons, since we can query the hash table to check if a leaf or not.
public class DictionaryTree {
    // Map containing all of the sub-trees of this node.
    // Key - character to branch on / branch in tree.
    // Value - the child tree.
    private Map<Character, DictionaryTree> children = new LinkedHashMap<>();
    private Optional<String> word;
    private Optional<Integer> popularity;

    /**
     * Constructor to create new node in the tree.
     * @param word
     * @param popularity
     */
    DictionaryTree(Optional<String> word, Optional<Integer> popularity) {
        this.children = new LinkedHashMap<>();
        this.word = word;
        this.popularity = popularity;
    }

    DictionaryTree() {
        this.children = new LinkedHashMap<>();
        this.word = Optional.empty();
        this.popularity = Optional.empty();
    }

    /**
     * Inserts the given word into this dictionary.
     * If the word already exists, nothing will change.
     *
     * @param word the word to insert
     */
    void insert(String word) {
        // insert the word with an empty value for popularity, since no popularity has been given for the word.
        if (!contains(word)) { // If the word is not in the tree
            int before = allWords().size();
            int after;
            insertNewWord(word, 0, Optional.empty());
            assert(before == (after = allWords().size()) - 1); // Tree should now contain one extra word.
        }
    }

    /**
     * Inserts the given word into this dictionary with the given popularity.
     * If the word already exists, the popularity will be overriden by the given value.
     *
     * @param word       the word to insert
     * @param popularity the popularity of the inserted word (line number)
     */
    void insert(String word, int popularity) {

        // Assign a popularity value to the word being added (i.e. the line in the text file) so that the word can be
        // compared to other words of the same prefix.
        if (!contains(word)) { // If the word is in the tree but with no popularity.
            insertNewWord(word, 0, Optional.of(popularity));
        }

    }

    /**
     * Helper method for the insert() methods. The method traverses the tree by each character in the
     * word (e.g. if 'hello' is being added, h is traversed, then e, then l) until a node in the tree no longer has
     * the required child to branch to, (e.g. the child doesnt contain 'o' so hello isnt already in the tree).
     *
     *
     * @param word The word that is to be inserted.
     * @param i The index that we have reached in the word (during the recursive calls).
     * @param popularity The popularity value of the word being added.
     */
    void insertNewWord(String word, int i, Optional<Integer> popularity) {
        // The branching character is the next character in the word to be added.
        char branchChar = word.charAt(i);
        // If the branching character is already in the tree, look to follow the path.
        if (children.containsKey(branchChar)) {
            // If the end of the word has not already been reached. Make a recursive call to continue following the path.
            if (i < word.length() - 1)
                children.get(branchChar).insertNewWord(word, i + 1, popularity);
            // If the end of the word is reached (and the word already has a 'position' in the tree to be placed in.
            else {
                // Set the word and popularity.
                children.get(branchChar).word = Optional.of(word);
                children.get(branchChar).popularity = popularity;
            }
        } else { // Else create a new path for the word.
            if (i == word.length() - 1) { // The branching character is the last character in the addWord.
                children.put(branchChar, new DictionaryTree(Optional.of(word), popularity));
            }
            else {
                // If the end of the word has not been reached, add a node for the word (but dont add the word - use
                // Optional.empty() until the word is specifically entered)
                children.put(branchChar, new DictionaryTree(Optional.empty(), Optional.empty()));
                // Recursive call
                children.get(branchChar).insertNewWord(word, i + 1, popularity);
            }
        }
    }

    /**
     * Removes the specified word from this dictionary.
     * Returns true if the caller can delete this node without losing part of the dictionary,
     * i.e. if this node has no children after deleting the specified word.
     *
     * @param word the word to delete from this dictionary
     * @return whether or not the parent can delete this node from its children
     */
    boolean remove(String word) {
        int numWordsBefore = allWords().size();
        int numWordsAfter;
        char branchChar;
        DictionaryTree node = this;
        boolean wasLeaf = false;
        if (contains(word)) {
            // Find if the end of the word is a leaf or not.
            for (int i = 0 ; i < word.length() ; i++) {
                branchChar = word.charAt(i);
                node = node.children.get(branchChar);
                if (i == (word.length() - 1) && node.isLeaf()) { // If the last node in the word is a leaf.
                        wasLeaf = true;
                }

            }

            branchChar = word.charAt(0);
            List<Word> wordList = children.get(branchChar).allWordObjs(new ArrayList<>());
            this.children.remove(branchChar);
            for (Word entry : wordList) {
                if (!entry.getWord().equals(word))
                    if (entry.getPopularityOptional().isPresent())
                        this.insert(entry.getWord(), entry.getPopularityInt());
                    else this.insert(entry.getWord());
            }

            assert(allWords().size() == (numWordsBefore - 1)); // Assert that exactly one word is removed from the tree.

            return wasLeaf;
        } else return false; // If no word present, return false.
    }
    

    /**
     * Determines whether or not the specified word is in this dictionary.
     *
     * @param word the word whose presence will be checked
     * @return true if the specified word is stored in this tree; false otherwise
     */
    boolean contains(String word) {
        char branchChar = word.charAt(0);
        if (word.length() == 1)
            if (children.containsKey(branchChar)) {
                return children.get(branchChar).word.isPresent();
            } else return false;
        else if (children.containsKey(branchChar))
            return children.get(branchChar).contains(word.substring(1));
        else return false;

    }

    /**
     * This method uses the predict(prefix, n) method using n == 1, so that the most likely word is returned (the most
     * popular word).
     *
     * @param prefix the prefix of the word returned
     * @return a word that starts with the given prefix, or an empty optional
     * if no such word is found.
     */
    Optional<String> predict(String prefix) {
        // Finds the sub-tree which has the root node of the 'prefix', then return a random word from the sub-tree.
        Optional<DictionaryTree> foundPrefix = findPrefix(prefix);
        if (foundPrefix.equals(Optional.empty())) {
            return Optional.empty();
        } else {
            // Get the list of all Word (objects) in the sub-trees of the prefix
            List<Word> listPredictions = foundPrefix.get().allWordObjs(new ArrayList<>());
            for (int i = 0 ; i < listPredictions.size() ; i++) {
                if (listPredictions.get(i).getWord().equals(prefix))
                    listPredictions.remove(i);
            }
            if (listPredictions.isEmpty())
                return Optional.empty();
            // Choses the first item in the list of possible meanings - could be changed
            return Optional.of(listPredictions.get(0).getWord());
        }
    }

    /**
     * Predicts the (at most) n most popular full English words based on the specified prefix.
     * If no word with the specified prefix is found, an empty list is returned.
     *
     * @param prefix the prefix of the words found
     * @param n the length of the list to be returned / number of words to be returned
     * @return the (at most) n most popular words with the specified prefix
     */
    List<String> predict(String prefix, int n) {
        // Traverse the tree until we reach the node which contains the prefix.
        Optional<DictionaryTree> foundPrefix = findPrefix(prefix);
        // If the prefix does not exist in the tree, return an empty list of words.
        if (foundPrefix.equals(Optional.empty())) {
            return new ArrayList<>();
        } else {
            // Get the list of all Word (objects) in the sub-trees of the prefix
            List<Word> listPredictions = foundPrefix.get().allWordObjs(new ArrayList<>());

            // Assert that, in order for this method to be used, all words in the tree must have a respective
            // popularity value.
            assert(allHavePopularity(listPredictions));

            // Using an insertion sort, sort the list of words in ascending order by their popularity values.
            listPredictions = insertionSort(listPredictions);
            if (listPredictions.isEmpty())
                return new ArrayList<>();
            // Reduce the size of the list to satisfy the parameter n.
            while (listPredictions.size() > n) {
                listPredictions.remove(listPredictions.size() - 1);
            }
            // Populate an array list to be returned with the String values for each word.
            ArrayList<String> returnlist = new ArrayList<>();
            for (Word word : listPredictions) {
                returnlist.add(word.getWord());
            }
            return returnlist;
        }
    }

    /**
     * Method used for an assertion check in predict(prefix, n)
     * @param toCheck list to check.
     * @return true if all of the Word's in the list have a popularity value.
     */
    boolean allHavePopularity(List<Word> toCheck) {
        for (Word entry : toCheck) {
            // Return false if a word doesnt have a popularity value.
            if (!entry.getPopularityOptional().isPresent())
                return false;
        }
        return true;

    }

    /**
     * An insertion sort to sort a list of Word objects, this sorts the words by popularity value, in ascending order.
     *
     * @param input The list of words to be sorted by popularity.
     * @return the list sorted by popularity in ascending order.
     */
    List<Word> insertionSort(List<Word> input) {
        int n = input.size();
        for (int i = 1 ; i < n ; ++i) {
            Word keyWord = input.get(i);
            int keyPop = input.get(i).getPopularityInt();
            int j = i-1;

            /* Move elements of arr[0..i-1], that are
               greater than key, to one position ahead
               of their current position */
            while (j >= 0 && input.get(j).getPopularityInt() < keyPop) {
                input.set(j + 1, input.get(j));
                j = j-1;
            }
            input.set(j + 1, keyWord);
        }
        return input;
    }


    /**
     * Method to traverse the tree and return the location of the specified prefix - which can then be used for other
     * purposes.
     *
     * @param prefix The prefix which we want to find.
     * @return The DictionaryTree (node) in the tree which holds the prefix.
     */
    Optional<DictionaryTree> findPrefix(String prefix) {
        DictionaryTree current;
        char branchChar = prefix.charAt(0);

        if (children.containsKey(branchChar)) {
            current = children.get(branchChar);
        } else return Optional.empty();

        for (int i = 1 ; i < prefix.length() ; i++) {
            branchChar = prefix.charAt(i);
            if (current.children.containsKey(branchChar)) {
                current = current.children.get(branchChar);
            } else return Optional.empty();
        }
        return Optional.of(current);
    }

    /**
     *
     * @return the number of leaves in this tree, i.e. the number of words which are
     * not prefixes of any other word.
     */
    int numLeaves() {
        /*
        int num = 0;
        if (isLeaf()) {
            return 1;
        } else {
            for (char entry : children.keySet()) {
                num += children.get(entry).numLeaves();
            }
        }
        return num;
        */

        return fold(

                (tree, list) -> {
                    int num = tree.isLeaf() ? 1 : 0;
                    for (int i : list)
                        num += i;
                    return num;
                }

        );
    }

    /**
     * Finds the maximum number of children that any node has.
     *
     * @return the maximum number of children held by any node in this tree
     */
    int maximumBranching() {
        /*
        if (isLeaf())
            return 0;
        int max = children.size();
        for (char entry : children.keySet()) {
            // Finds the branching factor for every node in the tree - recursively.
            int numBranches = children.get(entry).maximumBranching();
            // If a nodes branching factor is greater than the current max, set it to the current max.
            if (max < numBranches) {
                // Set the size of this new maximum branching factor.
                max = numBranches;
            }
        }
        return max;
        */

        return fold(

                (tree, list) -> {
                    int branching = tree.children.size();
                    for (int i : list) {
                        if (i > branching)
                            branching = i;
                    }
                    return branching;
                }

        );
    }

    /**
     * Finds the height of the tree.
     *
     * @return the height of this tree, i.e. the length of the longest branch
     */
    int height() {
        /*
        int h = 0;
        if (children.isEmpty())
            return -1;

        for (Map.Entry<Character, DictionaryTree> entry : children.entrySet()) {
            h = Math.max(h, (entry.getValue()).height());
        }
        return h + 1;
        */

        return fold(

                (tree, list) -> {
                    int height = -1; // The height of a single node is 0.
                    for (int i : list)
                        height = Math.max(i, height); // Find the sub-tree which has the greatest height.
                    return height + 1; // Return that height plus 1 (for the root node itself).
                }

        );

    }

    /**
     * Method to find the longest word in the tree.
     *
     * @return the longest word in the tree
     */
    String longestWord() {
        if (isLeaf())
            return "";
        int max = -1;
        String largest = "";
        for (char entry : children.keySet()) {
            // Creates every possible word in the tree.
            String word = children.get(entry).longestWord();
            // Checks the length of the created word with the length of the longest word.
            if (max < word.length()) {
                // Set the new longest word.
                largest = entry + word;
                // Set the length of this new longest word.
                max = word.length();
            }
        }
        return largest;
    }

    /**
     * @return the number of nodes in this tree
     */

    int size() {
        /*
        int s = 1;
        for (Map.Entry<Character, DictionaryTree> entry : children.entrySet()) {
            s += entry.getValue().size();
        }
        return s;
        */
        return fold(

                (tree, list) -> {
                    int size = 1;
                    for (int i : list)
                        size += i;
                    return size;
                }

        );
    }

    /**
     * @return all words stored in this tree as a list
     */
    List<String> allWords() {
        return allWords(new ArrayList<>());
    }

    /**
     * Helper method for the allWords() method - to recursively add valid words to the array list parameterized
     *
     * @param all
     * @return The List containing all words.
     */
    List<String> allWords(List<String> all) {
        if (word.isPresent())
            all.add(word.get());

        for (Map.Entry<Character, DictionaryTree> entry : children.entrySet()) {
            entry.getValue().allWords(all);
        }
        return all;
    }

    /**
     * Method to return all of the words in a tree as a Word object - this contains the word, and the popularity
     * of that word.
     *
     * @param all
     * @return The List containing all of the Word objects.
     */
    List<Word> allWordObjs(List<Word> all) {
        if (word.isPresent() && popularity.isPresent())
            all.add(new Word(word.get(), popularity));
        else if (word.isPresent() && !popularity.isPresent())
            all.add(new Word(word.get(), Optional.empty()));

        for (Map.Entry<Character, DictionaryTree> entry : children.entrySet()) {
            entry.getValue().allWordObjs(all);
        }
        return all;
    }

    /**
     * Folds the tree using the given function. Each of this node's children is folded with the same function,
     * and these results are stored in a collection, cResults, say, then the final result is calculated
     * using f.apply(this, cResults).
     *
     * @param f   the summarising function, which is passed the result of invoking the given function
     * @param <A> the type of the folded value
     * @return the result of folding the tree using f
     */
    <A> A fold(BiFunction<DictionaryTree, Collection<A>, A> f) {
        ArrayList<A> c = new ArrayList<>();
        for (Map.Entry<Character, DictionaryTree> entry : children.entrySet()) {
            c.add(entry.getValue().fold(f));
        }
        return f.apply(this, c);
    }

    /**
     * Checks if the DictionaryTree is a leaf
     *
     * @return true/false
     */
    boolean isLeaf(){
        return children.isEmpty();
    }


}
