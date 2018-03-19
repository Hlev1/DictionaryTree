# DictionaryTree

# Word Prediction
## Methods to implement
#### size - returns the number of nodes in the tree.
###### Helper methods - none
I calculate the size of the given tree using the `fold` method. I do however have my original code for this method commented out (This code works as intended). Using the `fold` method, I pass an anonomous function which calculates the size of the tree.

#### height - returns the height of the tree. Since the tree is never empty, height should always be non-negative.
###### Helper methods - none
I also calculate the height of the tree using the `fold` method. I pass a similar anonomous function, but instead finding the max height of the current tree, and return this height + 1.

#### maximumBranching - each node has a number of children - this should return the maximum number of children in a held by any node.
###### Helper methods - none
Similarly, I use the fold method to calculate the maximum branching of the tree. The anonomous function gets the number of children for each node and then compares this with the number of chidren of each child node to see if there is any node with a greater number of children.

#### longestWord - returns the longest word stored in this tree.
###### Helper methods - `isLeaf()`
This is a recursive procedure, if the method is called on a leaf then an empty string is returned. Else, 

#### numLeaves - returns the number of leaves in this tree, i.e. the number of words in this tree which are not prefixes of any other word.
###### Helper methods - `isLeaf()`
I use the `fold()` method to calculate the number of leaves. I pass an anonomous function that checks to see if the current node is a leaf, and if it is returns 1 (else returns 0) plus the number of leaves in the nodes sub-trees. Since this is called recursively up the tree, the number of leaves is passed up through the tree until the root node is reached.

#### contains - returns true if the given word is held in this tree, and false otherwise.
###### Helper methods - none
This is a method which recursively finds the next character in the word and checks if the node has a branch which branches on that next character, in each recursive call the substring of the word is passed which is the rest of the word which has not already been found yet. Then, there is the base case that says - if the length of the word to find == 1 (there is one character left to find), if this character exists as a child, return true. However true is only returned if the variable containing the word at that node is present. (This means that it would only return true if the word to find has been implicitly inserted into the tree, and not inserted during the insertion of a longer word containing that prefix).

#### allWords - returns all words held in this tree.
###### Helper methods - `allWords(List<String> all)`
Here I call a recursive helper function to carry out a depth-first search of the tree. First inserting (into the list to return) the word at the current node if present (so no non-valid prefixes are entered). Then making a recursive call on each of the children, passing the list of all words.

#### insert (without popularity) - inserts the given word into this tree.
###### Helper methods - `insertNewWord(String word, int popularity)`
I initially check to see if the tree already contains the word, if not then I call the helper method. This helper method is recursive. Since the word being inserted doesnt have a popularity value, I just pass Optional.empty(). This method then traverses down the tree each call looking for the next character in the word. Once the condition is met where the node does not have a child of the 'branching character'. Then the word must be inserted into the tree at this position. If the rest of the word to enter into the tree is only one character long, then this new child is created with the word value of the word to be inserted (and a popularity value of Optional.empty()). Else the next character in the word is added as a child, but the word and popularity value is set to Optional.empty() since it is not the end of the word. And then the recursive call is made on the rest of the word.

#### insert (with popularity) - inserts the given word into this tree.
###### Helper methods - `insertNewWord(String word, int popularity)`
This method is exactly the same as the original insert method. Both use the same helper function. However, the difference is that, before we were inserting each word with a popularity of Optional.empty. Instead when a popularity value is given we insert the word with popularity Optional.of(popularity).

#### remove - removes the given word from this tree.
###### Helper methods - `allWordObjs(List all)`
First check that the tree contains the word to be removed - else return false. Then, initially we traverse down to the end of the word which is being removed, and check to see if it is a leaf node. If so, then we set the variable `wasLeaf` to be true. This is the variable which we shall return at the end of the method. If this node is not a leaf, then the variable to be returned will equal false. We then call the `allWordObjs` helper method on the node which is the first character of the node to be deleted. This returns an array list of all of the words held in that sub-tree. We then delete the link from the root node of the tree to the first character in the word to be deleted. And then iterate through the array list of words we have, inserting each word back into the tree - carefull to not insert the word that we just deleted, back into the tree.

#### predict - given a prefix, this method should return a word in this tree that starts with this prefix.
###### Helper methods - `allWordObjs()`, `findPrefix()`
This method calls the `findPrefix()` helper function which returns the DictionaryTree of the prefix entered. If this DictionaryTree is Optional.empty() (the prefix is nowhere in the tree) then Optional.empty() is returned. Else, `allWordObjs()` is called on the DictionaryTree of the prefix. Then, this list of words is iterated over and the given prefix is removed (so that we dont predict the same word that was given). Then I return the first word in the given list (which will result in returning the word most similar to the prefix). If this list of possible words is empty, i.e. the prefix is a leaf, then Optional.empty is returned.

### Frequency-based prediction
Similarly to the basic predict method, I get the list of words in the sub-tree of the found prefix. However, then I carry out an insertion sort on all of the Word objects in this list, sorting by popularity. However before I carry out this insertion sort I have an assertion that checks every Word object in this list does indeed have a popularity value which it can be sorted on

What are the advantages/disadvantages of using a tree for predicting multiple words with ranked popularities?
- Advantages : The tree data structure is fast to traverse/query compared to an array for example. By using popularity values, we are able to give a more accurate prediction to the user.
- Disadvantages : We first need the popularity of all words in order to use the tree with words ranked by popularity. In addition sorting the elements by this popularity can be slow.

### Fold
I have implemented fold for all 4 methods. The method recursively calls itself on all child nodes. And then computes the value using the BiFunction, which in turn is added to the Collection.