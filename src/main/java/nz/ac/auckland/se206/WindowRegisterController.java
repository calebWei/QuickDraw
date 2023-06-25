package nz.ac.auckland.se206;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class WindowRegisterController {

  @FXML private TextField usernameInput;
  @FXML private Label errorLabel;
  @FXML private ImageView icon1;
  @FXML private ImageView icon2;
  @FXML private ImageView icon3;
  @FXML private ImageView icon4;
  @FXML private ImageView icon5;
  @FXML private ImageView icon6;
  @FXML private Circle highlight1;
  @FXML private Circle highlight2;
  @FXML private Circle highlight3;
  @FXML private Circle highlight4;
  @FXML private Circle highlight5;
  @FXML private Circle highlight6;
  private String icon;
  private SoundPlayer sound;
  /** Initialises the controller. */
  public void initialize() {
    sound = new SoundPlayer();
    // Set the default icon
    String[] icons = new String[] {"ghost", "cactus", "mug", "bulb", "chick", "pineapple"};
    ImageView[] images = new ImageView[] {icon1, icon2, icon3, icon4, icon5, icon6};
    icon = "";

    // Finding all the files in the directory
    File file = new File("src/main/resources/users/");
    File[] files = file.listFiles();
    assert files != null;

    // Looping through all the files in the directory
    for (File f : files) {
      for (int i = 0; i < 6; i++) {
        // If the file name is the same as the icon name, set the icon to be disabled
        if (f.getName().equals(icons[i] + ".json")) {
          images[i].setDisable(true);
          images[i].setOpacity(0.3);
        }
      }
    }
  }

  /**
   * This method switch the scene to the Profile Select page upon completion of signup
   *
   * @param start Activates when the Register button is pressed and registration is successful
   */
  @FXML
  private void onSwitchProfileSelect(MouseEvent start) {
    // Check if icon is selected
    if (Objects.equals(icon, "")) {
      errorLabel.setText("Please select an icon");
    } else {
      // Get scene that the button event is in
      Rectangle button = (Rectangle) start.getSource();
      Scene scene = button.getScene();
      // Change scene to canvas
      try {
        // Check if username already exists
        if (checkFields()) {
          // Create new user
          registerUser();
          scene.setRoot(App.loadFxml("profileSelect"));
        } else {
          errorLabel.setText("Please fill username");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    System.out.println("Switch to profile select");
  }

  /**
   * This method switch the scene to the Main Menu
   *
   * @param start Activates when the mainMenu button is pressed
   */
  @FXML
  private void onSwitchMainMenu(MouseEvent start) {
    System.out.println("Switch to main menu");
    // Get scene that the mouse event is in
    Rectangle rectangle = (Rectangle) start.getSource();
    Scene scene = rectangle.getScene();
    // Change scene to main menu
    try {
      scene.setRoot(App.loadFxml("mainMenu"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method checks if the correct fields are entered, including confirm password
   *
   * @return true if the correct fields are entered
   */
  private boolean checkFields() {
    // Check if all fields are filled
    return (!usernameInput.getText().isEmpty()) && (!Objects.equals(icon, ""));
  }

  /** This method sets the opacity of the selected icon to indicate which have been selected */
  @FXML
  private void onSelectIconOne() {
    // Selected icon highlighted
    highlight1.setVisible(true);
    highlight2.setVisible(false);
    highlight3.setVisible(false);
    highlight4.setVisible(false);
    highlight5.setVisible(false);
    highlight6.setVisible(false);

    icon = "ghost";
  }

  /** This method sets the opacity of the selected icon to indicate which have been selected */
  @FXML
  private void onSelectIconTwo() {
    // Selected icon highlighted
    highlight1.setVisible(false);
    highlight2.setVisible(true);
    highlight3.setVisible(false);
    highlight4.setVisible(false);
    highlight5.setVisible(false);
    highlight6.setVisible(false);

    icon = "cactus";
  }

  /** This method sets the opacity of the selected icon to indicate which have been selected */
  @FXML
  private void onSelectIconThree() {
    // Selected icon highlighted
    highlight1.setVisible(false);
    highlight2.setVisible(false);
    highlight3.setVisible(true);
    highlight4.setVisible(false);
    highlight5.setVisible(false);
    highlight6.setVisible(false);

    icon = "mug";
  }

  /** This method sets the opacity of the selected icon to indicate which have been selected */
  @FXML
  private void onSelectIconFour() {
    // Selected icon highlighted
    highlight1.setVisible(false);
    highlight2.setVisible(false);
    highlight3.setVisible(false);
    highlight4.setVisible(true);
    highlight5.setVisible(false);
    highlight6.setVisible(false);

    icon = "bulb";
  }

  /** This method sets the opacity of the selected icon to indicate which have been selected */
  @FXML
  private void onSelectIconFive() {
    // Selected icon highlighted
    highlight1.setVisible(false);
    highlight2.setVisible(false);
    highlight3.setVisible(false);
    highlight4.setVisible(false);
    highlight5.setVisible(true);
    highlight6.setVisible(false);

    icon = "chick";
  }

  /** This method sets the opacity of the selected icon to indicate which have been selected */
  @FXML
  private void onSelectIconSix() {
    // Selected icon highlighted
    highlight1.setVisible(false);
    highlight2.setVisible(false);
    highlight3.setVisible(false);
    highlight4.setVisible(false);
    highlight5.setVisible(false);
    highlight6.setVisible(true);

    icon = "pineapple";
  }

  /** This method creates a new user and saves it to the database */
  private void registerUser() throws IOException {
    User currentUser = new User(usernameInput.getText(), this.icon);
    User.createNewUser(currentUser);
  }

  /** This method plays the sound effect for buttons in the application */
  @FXML
  private void playPop() {
    sound.playPop();
  }
}
