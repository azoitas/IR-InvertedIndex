import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;


public class InvertedIndex implements Serializable {
    private LinkedHashSet<String> significantWords;
    private LinkedHashSet<String> sigWordsStemmed;
    private LinkedHashSet<String> stopWordsLHS;
    private HashMap<Integer,String> corpusFilesHM2;
    private GUI GUI;
    private int output_Flag=3;
    private int numDocsReturned=0;
    private String resultString="";
    private String resultStringStemmed="";
    private double[] precisionRecallLastQuery;
    /**
     * The Dictionary when given a KeyWord (Non-StopWord)
     * retrieves a postings List for that word. The PostingsList retrieves
     * (prints) all the documents associated with it.
     */
    private HashMap<Integer,Document> docLookup = new HashMap<Integer,Document>();
    private HashMap<Integer,Document> docLookup2 = new HashMap<Integer,Document>();
    private HashMap<String, PostingsList> Dictionary = new HashMap<String,PostingsList>();
    private HashMap<String, PostingsList> stemmedDictionary = new HashMap<String,PostingsList>();
    private DataManager dataMGR;
    private Stemmer PorterStemmer = new Stemmer();
    private LinkedHashSet<String> porterResults = new LinkedHashSet<String>();
    private LinkedHashSet<String> argsIs2Results= new LinkedHashSet<String>();

    public InvertedIndex(stopList sl, corpus cor, DataManager dm) throws IOException {
        dataMGR = dm;
        stopWordsLHS = sl.getStopWordsLHS();
        corpusFilesHM2 = cor.getCorpusHM2();
        significantWords = new LinkedHashSet<String>();
        significantWords = initializeSignificantWords2();

    }

    private LinkedHashSet<String> initializeSignificantWords2() throws IOException {
        String docName;
        for(Integer DocumentID: corpusFilesHM2.keySet()){
            docName=corpusFilesHM2.get(DocumentID);
            LinkSigWordsToDocuments(docName, DocumentID); //Method that Takes docName and ID to add to significantWords
        }
        return significantWords;
    }

    /**
     * Takes apart a String to look at each word on its own. Any word that is not considered a StopWord has
     * a PostingsList created for it and the document added to it. If a PostingsList is already created for
     * a particular word, then a check is done to see if the current document has already been added, if not it
     * is then added to the PostingsList.
     * @param documentName The String of the Document Title retrieved as the value from the HashMap in the Corpus.
     * @param docID The Unique Integer value that can obtain the Document Title from the Hashmap in the Corpus.
     */
    private void LinkSigWordsToDocuments(String documentName, Integer docID) throws IOException {
        Document doc = new Document(documentName, docID);
        Document doc2 = new Document(documentName, docID);
        String[] split = documentName.trim().split(" ");
        String[] wordsForStemming = wordsForStemming(split); // We take our array of split words and remove stop words from it.
        wordsForPorter(wordsForStemming); //Passing in an array of words that is written to txt file to be read by the Porter Stemmer
        LinkedHashSet<String> tmp = PorterStemmer.StemmerMethod(dataMGR.getPorterDataFile()); // We have an instance of the stemmer we call only passing in the file name/location of the words we wrote to the txt file.
        for(String pr : tmp){ //
            porterResults.add(pr);
            doc2.addSignificantWord(pr);
        if(!stemmedDictionary.containsKey(pr)){ //if we do not have the stemmed word
            stemmedDictionary.put(pr, new PostingsList(pr));
            stemmedDictionary.get(pr).addDocument(doc2);
        }else{
                 if (!stemmedDictionary.get(pr.toLowerCase()).contains(doc2)) {
                stemmedDictionary.get(pr.toLowerCase()).addDocument(doc2);
                }
            }
        }

        for (String word : split) {
            if (!stopWordsLHS.contains(word.toLowerCase()) && word.length() > 0) {
                significantWords.add(word.toLowerCase());
                doc.addSignificantWord(word.toLowerCase());
                if (!Dictionary.containsKey(word.toLowerCase())) {
                    Dictionary.put(word.toLowerCase(), new PostingsList(word.toLowerCase()));
                    Dictionary.get(word.toLowerCase()).addDocument(doc);
                } else {
                    if (!Dictionary.get(word.toLowerCase()).contains(doc)) {
                        Dictionary.get(word.toLowerCase()).addDocument(doc);
                    }
                }
            }
        }
        docLookup2.put(docID,doc);
    }



    private String[] wordsForStemming(String[] originalSet){
        ArrayList<String > temp = new ArrayList<String>();
        for(String word: originalSet) {
            if (!stopWordsLHS.contains(word.toLowerCase()) && word.length() > 0) {
                temp.add(word.toLowerCase());
            }
        }
        String[] wordsToStem = temp.toArray(new String[temp.size()]);
        return wordsToStem;
    }

    public void printAllDocuments(){
    for(int id=1; id<=corpusFilesHM2.size();id++){
        System.out.println("Document id "+id+": "+corpusFilesHM2.get(id));
     }
    }

    public void printPostings(){
       for(String SpecialWords: significantWords){
           Dictionary.get(SpecialWords).print();
       }
       System.out.println("Number of Listings: "+significantWords.size());
    }

    public void printPortersPostings(){
        for(String s: porterResults){
            stemmedDictionary.get(s).print();
        }
        System.out.println("Number of Listings: "+porterResults.size());
    }


    public void printSignificantWords(){
        int i=0;
        for(String sigWord: significantWords){
            i++;
            System.out.println("#"+i+": >"+sigWord+"<");
        }
        System.out.println("*****");
    }

    public void argsIs2Query(String[] wordToSearch, String originalQuery){
        numDocsReturned=0;
        resultString=originalQuery+"\n";
        for(String word : wordToSearch) {
            if (Dictionary.containsKey(word)) {
                resultString+=Dictionary.get(word).getPrintInStringForm()+" ";
            }
        }
        resultString+="\n";
        argsIs2Results.add(resultString);
    }

    public LinkedHashSet<String>getArgsIs2Results(){
        return argsIs2Results;
    }

    public void Query(String[] wordToSearch, String originalQuery){
        numDocsReturned=0;
        resultString=originalQuery+"\n";
        for(String word : wordToSearch) {
            if (Dictionary.containsKey(word)) {
                Dictionary.get(word).print();
                resultString+=Dictionary.get(word).getPrintInStringForm()+" ";
            }
        }
        precisionRecallLastQuery=precisionRecall(wordToSearch);
        processOutputFlag(resultString); //Passing in what would've been printed to console as a string to GUI or TXT file.
    }

    public void portersQuery(LinkedHashSet<String> StemmedWords, String originalQuery){
        numDocsReturned=0;
        resultStringStemmed=originalQuery+"\n";
        for(String sw: StemmedWords){
            if(stemmedDictionary.containsKey(sw)) {
                stemmedDictionary.get(sw).print();
                resultStringStemmed+=stemmedDictionary.get(sw).getPrintInStringForm()+" ";
                numDocsReturned++;
            }
         }
        String query = originalQuery.replaceAll("[.?]","");
        query = query.replaceAll("\\s{2,}", " ");
        String[] query_array = query.split(" ");
        precisionRecallLastQuery=precisionRecall(query_array);
        processOutputFlag(resultStringStemmed);
    }

    /**
    *precisionRecall seeks to return both precision and recall values by
     * Looking at each keyword in the original query and counting how many times a specific document has been pointed too.
     * If multiple keywords from the query point to the same document it is assumed to be a true positive.
     * If only a few of the same document has been returned it is assumed that it may have been a false positive.
    * */
    private double[] precisionRecall(String[] wordsOfQuery){
        double TP=0;
        double FP=0;
        double FN=0;
        double Precision=0;
        double Recall=0;
        double[] PR = new double[2];
        int skippedWord=0;
        int numWordsInQuery = wordsOfQuery.length;
        if(numWordsInQuery<=0){
            return new double[]{1, 1};
        }
        HashMap<Integer,Integer> numTimesDocSeen = new HashMap<Integer,Integer>();//Want to count the times a specific doc id was returned from query.
        HashSet <Integer> queryDocIDs =new HashSet<Integer>();

        for(String word : wordsOfQuery){
           if(Dictionary.containsKey(word)) {
               for (Integer id : Dictionary.get(word).getSetOfDocumentIDsThatPointToThisWord()) {
                   queryDocIDs.add(id);
                   if (numTimesDocSeen.containsKey(id)) {
                       numTimesDocSeen.put(id, numTimesDocSeen.get(id) + 1);
                   } else {
                       numTimesDocSeen.put(id, 1);
                   }
               }
           }else{
               skippedWord++;
           }
        }
        for (Integer id : queryDocIDs) {
              if(numTimesDocSeen.get(id)>=(numWordsInQuery*.20)){
                  TP++; //True positive
              }else if(numTimesDocSeen.get(id)<((numWordsInQuery-skippedWord)*.20) && numTimesDocSeen.get(id)>=((numWordsInQuery-skippedWord)*.15)){
                FP++; // False Positive
              }else{
                  FN++; //False Negative
              }
        }
        if(TP==0 && FN==0){
            FN=FP*.25;
            TP=FN*.333;
        }
        Precision=TP/(TP+FP);
        Recall=TP/(TP+FN);
        PR[0]=Precision;
        PR[1]=Recall;
        System.out.println("Precision: "+Precision+"\nRecall: "+Recall);
        return PR;
    }

    private void processOutputFlag(String computedString){
        switch (output_Flag){
            case 1: //Just GUI
                createGUI(computedString);
                break;
            case 2: //GUI and Text File
                createGUI(computedString);
            case 3:
            default://Text File
                Scanner scan = new Scanner(System.in);
                System.out.println("Enter Name for Text File:");
                String UserInputTxtFile= scan.next();
                String txtFilePath=UserInputTxtFile+".txt";
                if(UserInputTxtFile.length()<1 || UserInputTxtFile==null){
                    txtFilePath=computedString+"_results.txt";
                }
                try{
                    File docFile = new File(txtFilePath);
                    if(docFile.createNewFile()==true){
                        FileWriter toFile = new FileWriter(docFile.getName());
                        System.out.println();
                        toFile.write(computedString); //Writing to the file the results of the word search.
                        System.out.println("File: '" + txtFilePath + "' Created.\nPlease Check Project Folder.");
                        toFile.close();
                    }else{
                        System.out.println("Word Search File Already Made");
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
                break;
        }
    }   //helper method to decide how to display results

    public void printPrecisionAndRecallOfLastQuery(){
        System.out.println("Precision: "+precisionRecallLastQuery[0]+"\nRecall: "+precisionRecallLastQuery[1]);
    };

    public void printDocument(Integer docID){
        System.out.println("Size of doclookup: "+docLookup2.size());

        if(docLookup2.containsKey(docID)){
            docLookup2.get(docID).printDocument();
        }else{
            System.out.println("DOC ID: '"+docID+"' is not found.");
        }
    }

    public Document returnDocument(Integer docID){
        if(docLookup2.containsKey(docID)){
           return docLookup2.get(docID);
        }else{
            System.out.println("DOC ID: '"+docID+"' is not found.");
            return null;
        }
    }
    public String getWordPostingString(String word){
        String posting ="";
        if(Dictionary.containsKey(word)) {
            posting=Dictionary.get(word).toString();
        }else  {
            return null;
        }
        return  posting;
    }

    private void wordsForPorter(String[] wordsToBeStemmed) throws IOException {
        try {
            File prtFile = new File(dataMGR.getPorterDataFile());
            FileWriter writeToPtr = new FileWriter(prtFile, false);
            for(String words: wordsToBeStemmed){
                writeToPtr.write(words+"\n");
            }
            writeToPtr.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void createGUI(String query){
        GUI = new GUI(query);
    }

    public void setOutput_Flag(int n){
        if(n==1){
            output_Flag=1;
        }else if(n==2){
            output_Flag=2;
        }else{
            output_Flag=3;
        }
    }



}