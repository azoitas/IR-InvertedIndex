
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;

public class stopList {
    private final String[] stopWordsArray = {"a", "about", "actually", "after", "again", "against", "all", "almost", "also",
            "although", "always", "am", "an", "and", "any", "are", "as", "at", "be", "became", "become", "because", "been",
            "but", "by", "can", "could", "did", "do", "does", "doing", "down", "each", "either", "else", "few", "for", "from",
            "had", "has", "have", "having", "he", "he'd", "he'll", "hence", "he's", "her", "here", "here's", "hers", "herself", "him",
            "himself", "his", "how", "how's", "I", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "it", "it's", "its", "itself",
            "just", "let's", "may", "maybe", "me", "mine", "more", "most", "must", "my", "myself", "neither", "nor", "not", "of", "oh",
            "on", "ok", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "she", "she'd", "she'll", "she's", "so",
            "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they",
            "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "we",
            "we'd", "we'll", "we're", "we've", "were", "what", "what's", "when", "whenever", "when's", "where", "whereas", "wherever",
            "where's", "whether", "which", "while", "who", "whoever", "who's", "whose", "whom", "why", "why's", "will", "with", "within",
            "would", "yes", "yet", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves"};
    //StopWordsArray is only used to provide a desired list of stop words to the Linked Hash set.

    private LinkedHashSet<String> stopWordsLHS = new LinkedHashSet<String>(170);

    public stopList() {
        initializeLinkedHashSet();
    }

    private void initializeLinkedHashSet() {
        for (String word : stopWordsArray) {
            stopWordsLHS.add(word.replaceAll("'",""));
        }
    }

    public void createStopWordsTxtFile() throws IOException {
        try {
            File stopWordFile = new File("StopWords.txt");
            if (stopWordFile.createNewFile() == true) {
                FileWriter toFile = new FileWriter(stopWordFile.getName());
                for (String stopWord : stopWordsLHS) {
                    toFile.write(stopWord);
                    toFile.write("\r\n");
                }
                System.out.println("File-->'" + stopWordFile.getName() + "' Created Successfully.");
                toFile.close();
            } else {
                System.out.println("StopWords File Loaded.");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public LinkedHashSet<String> getStopWordsLHS() {
    return stopWordsLHS;
    }

}