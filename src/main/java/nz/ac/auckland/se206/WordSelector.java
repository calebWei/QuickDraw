package nz.ac.auckland.se206;

import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import nz.ac.auckland.se206.words.CategorySelector;
import nz.ac.auckland.se206.words.CategorySelector.Difficulty;

/**
 * This class creates a list of words depending on the chosen level of the game. It stores list of
 * words according to difficulty as a static field, as well as the full word list for the chosen
 * level, the current word list that erase previously used words, and the word history of those
 * previously used. The word history and current word list can be gotten and set for the purpose of
 * record keeping.
 */
public class WordSelector {

  // This static block loads up the word lists and separates them depending on difficulty
  static {
    try {
      CategorySelector categorySelector = new CategorySelector();
      // Loads each individual difficulty as separate lists
      fullWordListE = categorySelector.getCategoryLevel(Difficulty.E);
      fullWordListM = categorySelector.getCategoryLevel(Difficulty.M);
      fullWordListH = categorySelector.getCategoryLevel(Difficulty.H);
    } catch (CsvException | URISyntaxException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static final List<String> fullWordListE;
  private static final List<String> fullWordListM;
  private static final List<String> fullWordListH;
  private static List<String> fullWordList;
  private static List<String> currentWordList;
  private static ArrayList<String> wordHistory;

  /**
   * This method gets the wordHistory array from the class object
   *
   * @return wordHistory
   */
  public static ArrayList<String> getWordHistory() {
    return wordHistory;
  }

  /**
   * This method gets the currentWordList list from the class object
   *
   * @return currentWordList
   */
  public List<String> getCurrentWordList() {
    return currentWordList;
  }

  /**
   * This constructor creates a full word list depending on the level chosen by the user. It then
   * copies this to the current word list and initialize the word history. The current word and word
   * history should be modified after this constructor if appropriate.
   *
   * @param level chosen by the player
   * @throws URISyntaxException if the file path is invalid
   * @throws IOException if the file cannot be read
   * @throws CsvException if the file is not in the correct format
   */
  public WordSelector(int level) throws URISyntaxException, IOException, CsvException {
    fullWordList = new ArrayList<>();
    // switch case for different level settings
    switch (level) {
      case 1 -> fullWordList = fullWordListE;
        // case 2 is for medium level
      case 2 -> {
        fullWordList.addAll(fullWordListE);
        fullWordList.addAll(fullWordListM);
      }
        // case 3
      case 3 -> {
        fullWordList.addAll(fullWordListE);
        fullWordList.addAll(fullWordListM);
        fullWordList.addAll(fullWordListH);
      }
        // default case
      case 4 -> fullWordList = fullWordListH;
      default -> fullWordList = null;
    }

    // set current words and initialize wordHistory
    currentWordList = fullWordList;
    wordHistory = new ArrayList<>();
  }

  /**
   * This method gets a random word from the currentWordList and remove that word and add it to
   * history.
   *
   * @return random word from currentWordList
   */
  public String getRandomWord() {
    String word;
    // reset currentWordList if it's empty
    if (currentWordList.size() == 0) {
      currentWordList = fullWordList;
    }
    // choose random word from currentWordList and add to history
    word = currentWordList.remove(new Random().nextInt(currentWordList.size()));
    wordHistory.add(word);
    return word;
  }
}
