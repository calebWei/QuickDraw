package nz.ac.auckland.se206.dictionary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * This class is used to find definitions of a given word using a particular api. It creates a link
 * which requires internet access. No internet access will cause this to fail.
 */
public class DictionarySearch {

  private static final String API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

  /**
   * This method access the api dictionaryapi.dev to find a list of definitions for a particular
   * word
   *
   * @param word The word that needs a definition
   * @return A list of all available definitions from the api
   * @throws IOException
   */
  public static List<String> searchDefinition(String word) throws IOException {
    List<String> definitions = new ArrayList<>();

    // Create a link to the api
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(API_URL + word).build();
    Response response = client.newCall(request).execute();
    ResponseBody responseBody = response.body();

    String jsonString = responseBody.string();

    // If there is no definitions available, then we return a null value for the definition list
    try {
      JSONObject jsonObj = (JSONObject) new JSONTokener(jsonString).nextValue();
      String title = jsonObj.getString("title");
      String subMessage = jsonObj.getString("message");
      return null;
    } catch (ClassCastException e) {
    }

    JSONArray jArray = (JSONArray) new JSONTokener(jsonString).nextValue();

    // Get meanings
    for (int e = 0; e < jArray.length(); e++) {
      JSONObject jsonEntryObj = jArray.getJSONObject(e);
      JSONArray jsonMeanings = jsonEntryObj.getJSONArray("meanings");

      String partOfSpeech = "[not specified]";

      // Get part of speech ie nouns, verbs
      for (int m = 0; m < jsonMeanings.length(); m++) {
        JSONObject jsonMeaningObj = jsonMeanings.getJSONObject(m);
        String pos = jsonMeaningObj.getString("partOfSpeech");

        if (!pos.isEmpty()) {
          partOfSpeech = pos;
        }

        JSONArray jsonDefinitions = jsonMeaningObj.getJSONArray("definitions");
        // Get definitions
        for (int d = 0; d < jsonDefinitions.length(); d++) {
          JSONObject jsonDefinitionObj = jsonDefinitions.getJSONObject(d);

          String definition = jsonDefinitionObj.getString("definition");
          if (!definition.isEmpty()) {
            definitions.add(definition);
          }
        }
      }
    }
    // return all available definitions
    return definitions;
  }
}
