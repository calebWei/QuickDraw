package nz.ac.auckland.se206;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * This class represents a user of the application. It handles the user's creation, verification,
 * and data within the user.
 */
public class User {

  public static String currentUser;

  /**
   * This method creates a new user as a JSON file.
   *
   * @param user The user to be created.
   * @throws IOException If the file cannot be created.
   */
  public static void createNewUser(User user) throws IOException {
    // Create a new Gson object with pretty printing
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Writer writer = new FileWriter("src/main/resources/users/" + user.icon + ".json");
    gson.toJson(user, writer);
    // Flush the data and then close it
    writer.flush();
    writer.close();
  }

  /** This method dereferences a user. */
  public static void dereferenceUser() {
    currentUser = null;
  }

  /**
   * This method returns the user with the given username.
   *
   * @param icon The username of the user.
   * @return The user with the given username.
   * @throws IOException If the file cannot be read.
   */
  public static User getUser(String icon) throws IOException {
    // Create a new Gson object
    Gson gson = new Gson();
    File file = new File("src/main/resources/users/" + icon + ".json");
    Reader reader = Files.newBufferedReader(file.toPath());
    // Convert the JSON file to a User object
    User user = gson.fromJson(reader, User.class);
    reader.close();
    currentUser = user.icon;
    return user;
  }

  /**
   * This method saves the user's data.
   *
   * @param user The user to be saved.
   * @throws IOException If the file cannot be saved.
   */
  public static void saveUser(User user) throws IOException {
    // Create a new Gson object with pretty printing
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Writer writer = new FileWriter("src/main/resources/users/" + user.icon + ".json");
    gson.toJson(user, writer);
    // Flush the data and then close it
    writer.flush();
    writer.close();
  }

  /**
   * This method deletes a user permanently.
   *
   * @param icon The username of the user.
   */
  public static void deleteUser(String icon) {
    File file = new File("src/main/resources/users/" + icon + ".json");
    file.delete();
  }

  /**
   * This method clears the user's word history.
   *
   * @param icon The username of the user.
   * @throws IOException If the file cannot be read.
   */
  public static void clearUserHistory(String icon) throws IOException {
    User user = getUser(icon);
    user.wordHistory.clear();
    saveUser(user);
  }

  // Getters and setters
  public static ArrayList<String> getWordHistory() throws IOException {
    return User.getUser(currentUser).wordHistory;
  }

  public void setWordHistory(String currentWord) {
    wordHistory.add(currentWord);
  }

  public static int getGamesWon() throws IOException {
    return User.getUser(currentUser).gamesWon;
  }

  public void setGamesWon(int gamesWon) {
    this.gamesWon = gamesWon;
  }

  public static int getGamesLost() throws IOException {
    return User.getUser(currentUser).gamesLost;
  }

  public void setGamesLost(int gamesLost) {
    this.gamesLost = gamesLost;
  }

  public static double getBestTime() throws IOException {
    return User.getUser(currentUser).bestTime;
  }

  public void setBestTime(double bestTime) {
    this.bestTime = Double.parseDouble(String.format("%.2f", bestTime));
  }

  public static int getAccuracyLevel() throws IOException {
    return User.getUser(currentUser).accuracyLevel;
  }

  public void setAccuracyLevel(int accuracyLevel) {
    this.accuracyLevel = accuracyLevel;
  }

  public static int getWordsLevel() throws IOException {
    return User.getUser(currentUser).wordsLevel;
  }

  public void setWordsLevel(int wordsLevel) {
    this.wordsLevel = wordsLevel;
  }

  public static int getConfidenceLevel() throws IOException {
    return User.getUser(currentUser).confidenceLevel;
  }

  public void setConfidenceLevel(int confidenceLevel) {
    this.confidenceLevel = confidenceLevel;
  }

  public static String getUsername() throws IOException {
    return User.getUser(currentUser).username;
  }

  public static int getLongestStreak() throws IOException {
    return User.getUser(currentUser).longestStreak;
  }

  public void setLongestStreak(int longestStreak) {
    this.longestStreak = longestStreak;
  }

  public static int getFiveGameStreaks() throws IOException {
    return User.getUser(currentUser).fiveGameStreaks;
  }

  public static int getTenGameStreaks() throws IOException {
    return User.getUser(currentUser).tenGameStreaks;
  }

  public static int getFifteenGameStreaks() throws IOException {
    return User.getUser(currentUser).fifteenGameStreaks;
  }

  public static int getGamesWonUnderThirty() throws IOException {
    return User.getUser(currentUser).gamesWonUnder30;
  }

  public static int getGamesWonUnderFifteen() throws IOException {
    return User.getUser(currentUser).gamesWonUnder15;
  }

  public static int getGamesWonUnderFive() throws IOException {
    return User.getUser(currentUser).gamesWonUnder5;
  }

  private final String username;
  private final String icon;
  private ArrayList<String> wordHistory = new ArrayList<>();
  private int gamesLost = 0;
  private int gamesWon = 0;
  private double bestTime = 60;
  private int accuracyLevel = 1;
  private int wordsLevel = 1;
  private int timeLevel = 1;
  private int confidenceLevel = 1;
  private int longestStreak = 0;
  private int gamesWonUnder30 = 0;
  private int gamesWonUnder15 = 0;
  private int gamesWonUnder5 = 0;
  private int fiveGameStreaks = 0;
  private int tenGameStreaks = 0;
  private int fifteenGameStreaks = 0;

  /**
   * This constructor creates a user with a username and icon.
   *
   * @param username The username of the user.
   * @param icon The icon of the user.
   */
  public User(String username, String icon) {
    this.username = username;
    this.icon = icon;
    currentUser = icon;
  }

  public int getTimeLevel() {
    return timeLevel;
  }

  public void setTimeLevel(int timeLevel) {
    this.timeLevel = timeLevel;
  }

  public void incrementFiveGameStreaks() {
    this.fiveGameStreaks++;
  }

  public void incrementTenGameStreaks() {
    this.tenGameStreaks++;
  }

  public void incrementFifteenGameStreaks() {
    this.fifteenGameStreaks++;
  }

  public void incrementGamesWonUnderThirty() {
    this.gamesWonUnder30++;
  }

  public void incrementGamesWonUnderFifteen() {
    this.gamesWonUnder15++;
  }

  public void incrementGamesWonUnderFive() {
    this.gamesWonUnder5++;
  }
}
