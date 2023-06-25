package nz.ac.auckland.se206;

import java.net.URISyntaxException;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * This class creates the sound effects for the application. It takes in mp3 files in the resources
 * and each method plays a different sound for various uses in the app
 */
public class SoundPlayer {
  private static Media popSound;
  private static MediaPlayer popPlayer;
  private static Media startSound;
  private static MediaPlayer startPlayer;
  private static Media hintSound;
  private static MediaPlayer hintPlayer;
  private static Media characterSound;
  private static MediaPlayer characterPlayer;

  /** this static blick initialize all the sound and its relative players */
  static {
    try {
      // Assign sounds with the appropriate path
      popSound = new Media(App.class.getResource("/sounds/game.mp3").toURI().toString());
      startSound = new Media(App.class.getResource("/sounds/ding.mp3").toURI().toString());
      hintSound = new Media(App.class.getResource("/sounds/hint.mp3").toURI().toString());
      characterSound = new Media(App.class.getResource("/sounds/character.mp3").toURI().toString());

    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

    // Convert the mp3 files to players to be used
    popPlayer = new MediaPlayer(popSound);
    startPlayer = new MediaPlayer(startSound);
    hintPlayer = new MediaPlayer(hintSound);
    characterPlayer = new MediaPlayer(characterSound);
  }

  /** This method plays the stated sound in the application when required. */
  public void playPop() {
    // Stops other sounds then play
    stopPlayers();
    popPlayer.play();
  }

  /** This method plays the stated sound in the application when required. */
  public void playStart() {
    // Stops other sounds then play
    stopPlayers();
    startPlayer.play();
  }

  /** This method plays the stated sound in the application when required. */
  public void playHint() {
    // Stops other sounds then play
    stopPlayers();
    hintPlayer.play();
  }

  /** This method plays the stated sound in the application when required. */
  public void playCharacter() {
    // Stops other sounds then play
    stopPlayers();
    characterPlayer.play();
  }

  /** This method stops all other sound so that more sounds can be played */
  private void stopPlayers() {
    // Stops other sounds
    popPlayer.stop();
    startPlayer.stop();
    hintPlayer.stop();
    characterPlayer.stop();
  }
}
