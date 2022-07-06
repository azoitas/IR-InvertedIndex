import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class PostingsList implements Serializable {

    private String specialWordKey;
    private final LinkedHashSet<Document> DocList = new LinkedHashSet<Document>();
    private HashMap<Integer, Document> docIDLookUpPL = new HashMap<Integer, Document>();
    private Integer freqInDocs =0;
    private HashSet<Integer> setOfDocuments = new LinkedHashSet<Integer>();

    /**
     * @param swKey Significant Word Key. This string is the word that "points/refers" to the documents associated with it.
     */
    public PostingsList(String swKey){
    specialWordKey = swKey;
    }

    public void addDocument(Document doc){
        setOfDocuments.add(doc.getID());

        freqInDocs++;
        DocList.add(doc);
        docIDLookUpPL.put(doc.getID(),doc);
    }

    public boolean contains(Document d){
        if(docIDLookUpPL.containsKey(d.getID())){
            return true;
        }
        else{
            return false;
        }
    }


    public void print(){
        System.out.print(specialWordKey+spaceIndentForPrint()+"---> Document Frequency: "+ freqInDocs + fIndent(freqInDocs)+", Found in:");
        for (Document document: DocList) {
            System.out.print(" [DOC ID: "+document.getID()+" | Frequency in Document: "+document.getWordFrequency(specialWordKey)+"],");
        }
        System.out.println();
    }

    public String getPrintInStringForm(){
        String s;
        s=specialWordKey+spaceIndentForPrint()+"---> Document Frequency: "+ freqInDocs + fIndent(freqInDocs)+"\n Found in:\n";
        for (Document document: DocList) {
          s+=" [DOC ID: "+document.getID()+" | Frequency in Document: "+document.getWordFrequency(specialWordKey)+"]\n";
        }
        return s;
    }

    public HashSet<Integer> getSetOfDocumentIDsThatPointToThisWord() {
        return setOfDocuments;
    }

    public String toString(){
        String s="";
        for (Document document: DocList) {
            s+="[DOC ID: "+document.getID()+" | Frequency in Document: "+document.getWordFrequency(specialWordKey)+"], ";
        }
        return s;
    }

    private String spaceIndentForPrint(){
        Integer i = specialWordKey.length();
        String pad=" ";
       if(i<15){
          pad += new String(new char[15-i]).replace('\0','-');
       }
        return pad;
    }

    private String fIndent(Integer f) {
        if(f<10){
            return "  ";
        }else
            return " ";
    }
}
