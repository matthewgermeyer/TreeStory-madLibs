package com.teamtreehouse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Prompter {
    private BufferedReader mReader;
    private Set<String> mCensoredWords;

    public Prompter() {
        mReader = new BufferedReader(new InputStreamReader(System.in));
        loadCensoredWords();
    }

    private void loadCensoredWords() {
        mCensoredWords = new HashSet<String>();
        Path file = Paths.get("resources", "censored_words.txt");
        List<String> words = null;
        try {
            words = Files.readAllLines(file);
        } catch (IOException e) {
            System.out.println("Couldn't load censored words");
            e.printStackTrace();
        }
        mCensoredWords.addAll(words);
    }

    public void run(Template tmpl) {
        List<String> results = null;
        try {
            results = promptForWords(tmpl);
        } catch (IOException e) {
            System.out.println("There was a problem prompting for words");
            e.printStackTrace();
            System.exit(0);
        }
        if (results == null){
          fudgeResults(tmpl);
        }
      String finished = tmpl.render(results);;
      System.out.printf("Your TreeStory:%n%n%s", finished);
    }

  private String fudgeResults(Template tmpl) {
    List<String> fakeResults = Arrays.asList(
        "friend",
        "talented",
        "java programmer",
        "high five");

    return tmpl.render(fakeResults);
  }

    /**
     * Prompts user for each of the blanks
     *
     * @param tmpl The compiled template
     * @return
     * @throws IOException
     */
    public List<String> promptForWords(Template tmpl) throws IOException {
        List<String> words = new ArrayList<>();
        for (String phrase : tmpl.getPlaceHolders()) {
            String word = promptForWord(phrase);
            words.add(word);
        }
        return words;
    }
    /**
     * Prompts the user for the answer to the fill in the blank.  Value is guaranteed to be not in the censored words list.
     *
     * @param phrase The word that the user should be prompted.  eg: adjective, proper noun, name
     * @return What the user responded
     */
    public String promptForWord(String phrase) throws IOException {
      System.out.printf("Replace: %s%n", phrase);
      String response = mReader.readLine();

      while (!isValid(response)) {
        System.out.printf("Replace: %s%n", phrase);
         response = mReader.readLine();
      }
      return response;
    }

  private boolean isValid(String response) {
      if (mCensoredWords.contains(response)){
        return false;
      }
      return true;
  }

  public String promptForStory() throws IOException {
        System.out.println("Give us a story surround replacing words with double underscore:  __word__");
        String story = mReader.readLine();

        if (story == "") {
             story = "Thanks __name__ for helping me out.  You are really a __adjective__ __noun__ and I owe you a __noun__.";
        }
        return story;
    }
}
