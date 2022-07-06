
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class Document implements Serializable {

    private String documentName;
    private Integer ID;
    private LinkedHashSet<String> docWordList= new LinkedHashSet<String>();
    private HashMap <String, Integer> wordAndOccurrences =new HashMap<String, Integer>(); // Maps every significant word and frequency within the text

    public Document(String name, Integer id) {
    documentName = name;
    ID=id;
    }

    public void addSignificantWord(String sw){
        docWordList.add(sw);
        if(wordAndOccurrences.containsKey(sw)){
            wordAndOccurrences.put(sw, wordAndOccurrences.get(sw)+1); //word already in, so we increase its frequency by 1.
        }else{
            wordAndOccurrences.put(sw,1);
        }
    }

    public Integer getWordFrequency(String s){ //How many times the same word is seen within the document.
        Integer frequency = 0;
        if(wordAndOccurrences.containsKey(s)){
            frequency=wordAndOccurrences.get(s);
        }
        return frequency;
    }

    boolean contains(String word){
        if(wordAndOccurrences.containsKey(word)){
            return true;
        }
        else return false;
    }

    public Integer getID(){
        return ID;
    }

    public String getDocumentName(){
        return documentName;
    }

    public void printDocument(){
        System.out.println("DOCUMENT "+ID+": "+documentName+" | Contains Word(s):");
        for(String word: docWordList){
            System.out.println(word+" - Frequency: "+getWordFrequency(word));
        }
    }

    public String toString() {
      String s = "";
        s+="DOCUMENT "+ID+": "+documentName+" | Contains Word(s): ";
        for(String word: docWordList){
            s+=word+" - Frequency: "+getWordFrequency(word)+" | ";
        }
        return s;
    }

}
