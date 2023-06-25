package nz.ac.auckland.se206.words;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategorySelector {

  private Map<Difficulty, List<String>> difficultyListMap;

  public CategorySelector() throws CsvException, URISyntaxException, IOException {
    difficultyListMap = new HashMap<>();

    for (Difficulty difficulty : Difficulty.values()) {
      difficultyListMap.put(difficulty, new ArrayList<>());
    }

    for (String[] line : getLines()) {
      difficultyListMap.get(Difficulty.valueOf(line[1])).add(line[0]);
    }
  }

  protected List<String[]> getLines() throws CsvException, URISyntaxException, IOException {

    File file = new File(CategorySelector.class.getResource("/category_difficulty.csv").toURI());

    try (FileReader fr = new FileReader(file, StandardCharsets.UTF_8);
        CSVReader reader = new CSVReader(fr)) {
      return reader.readAll();
    }
  }

  public List<String> getCategoryLevel(Difficulty difficulty) {
    return difficultyListMap.get(difficulty);
  }

  public enum Difficulty {
    E,
    M,
    H
  }
}
