import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class commandLineParse {//t5

    private final String[] Flag ={"SEARCH","WORDTXT","DOCTXT","DOCUMENT","DOC","PRINT","SETPREF","HELP","EXIT"};
    private HashMap<String,Integer> taskFlags = new HashMap<String,Integer>();
    private HashSet<String> queriesRecorded= new HashSet<String>();
    private boolean Exit=false;
    private String input="";
    private String resultFlag="";
    private static Scanner UserInputCL = new Scanner(System.in);
    private InvertedIndex invertedIndex;
    private boolean StemFlag = false;
    private DataManager dataMGR;
    private Stemmer PorterStemmer= new Stemmer();
    int outputFlag=3;
    private boolean argsIs2=false;

    public commandLineParse(InvertedIndex ie, DataManager DM) {
        invertedIndex = ie;
        dataMGR= DM;
         for(int i = 0; i< Flag.length; i++){
             taskFlags.put(Flag[i],i);
         }
    }

    public commandLineParse(InvertedIndex ie, DataManager DM, LinkedHashSet<String> cmd_args2) {
        invertedIndex = ie;
        dataMGR= DM;
        for(int i = 0; i< Flag.length; i++){
            taskFlags.put(Flag[i],i);
        }
        processArg2(cmd_args2);
    }

    String checkInputOROutput(String userInput){
        String userRequestedTask = userInput.toString().toUpperCase();
        if(taskFlags.containsKey(userRequestedTask)) {
            Integer TaskID = taskFlags.get(userRequestedTask);
            switch (TaskID){
                case 0:
                    return Flag[0];
                case 1:
                    return Flag[1];
                case 2:
                    return Flag[2];
                case 3:
                    return Flag[3];
                case 4:
                    return Flag[4];
                case 5:
                    return Flag[5];
                case 6:
                    return Flag[6];
                case 7:
                    return Flag[7];
                case 8:
                    return Flag[8];
                default:
                        return "NAF";
            }
        }
        return "NAF";
    }

    public void displayFlags(){
        System.out.print("[");
        for(int i=0;i< Flag.length-1;i++){
            System.out.print(Flag[i]+", ");
        }System.out.println(Flag[Flag.length-1]+"]");
    }

    public boolean monitorCommandLine() throws IOException {
        if(argsIs2==false) {
            askStemmingPreferences();
            askOutputResultsPreferences();
        }
        while (Exit == false) {
            System.out.println("\n>>Command Line Enter a Recognized Command<<");
            displayFlags();
            input = UserInputCL.next();
            resultFlag = checkInputOROutput(input);
            UserInputCL.nextLine();
            switch (resultFlag) {
                case "HELP":
                    help();
                    break;
                case "SEARCH":
                    System.out.println("SEARCH QUERY:: Enter Query:\n-SEARCH=");
                    input = UserInputCL.nextLine().toLowerCase();
                    String userQuery = input;
                    queriesRecorded.add(input);
                    input = input.replaceAll("[.?]","");
                    input = input.replaceAll("\\s{2,}", " ");
                    String[] array = input.split(" ");
                    if(StemFlag==true) {
                        porterQuerySearch(wordsForPorter(array), userQuery); //words in array get modified by stemmer and stemmed words get sent on to be searched.
                    }else {
                            querySearch(array, userQuery);
                    }
                    break;
                case "DOCUMENT":
                case "DOC":
                    docSearch(processDocument());
                    break;
                case "WORDTXT":
                    createWordTextFile();
                    break;
                case "DOCTXT":
                    createDOCTextFile();
                    break;
                case "PRINT":
                    System.out.println("PRINT:: \n ---TYPE '1' For Index Print\n ---TYPE '2' To Print Postings List\n ---TYPE '3' To Print Documents\n" +
                            " ---TYPE '4' to print Precision & Recall of last Query");
                    input = UserInputCL.nextLine();
                    if(input.equals("2")){
                        printPostings();
                    }else if(input.equals("3")){
                        invertedIndex.printAllDocuments();
                    }else if(input.equals("4")) {
                        invertedIndex.printPrecisionAndRecallOfLastQuery();
                    }else {
                        printIndex();
                    }
                    break;
                case "NAF":
                    System.out.println("'" + input + "' is not recognized as an internal command.");
                    break;
                case "SETPREF":
                    askStemmingPreferences();
                    askOutputResultsPreferences();
                    break;
                case "EXIT":
                    Exit = true;
                    break;
            }
        }
        System.out.println("Exited Command Line Input");
        UserInputCL.close();
        return false;
    }


    private void printIndex() {
        System.out.println("-PRINT_INDEX= \nWORD or DOC ?");
        String in = "";
        while (in != "WORD" || in != "DOC" || in != "EXIT") {
            in = UserInputCL.next().toUpperCase();

            if (in.equals("WORD")) {
                System.out.println("wPrintIndex: " + in);
                logPrintIndex(createWordTextFile());
                break;
            } else if (in.equals("DOC")) {
                System.out.println("dPrintIndex: " + in);
                logPrintIndex(createDOCTextFile());
                break;
            }else if(in.equals("EXIT") || in.equals("BACK")||in.equals("RETURN")){
                System.out.println("Exited Print Index");
                break;
            }
            else if(in.equals("HELP")){
                help();
                return;
            }
        }
    }

    private void printPostings(){
        invertedIndex.printPostings();
    }

    private String createDOCTextFile(){
        Integer docID = processDocument();
        Document doc = invertedIndex.returnDocument(docID);
        if( doc != null){
            try{
                File docFile = new File("document_"+docID+"_SearchResults.txt");
                if(docFile.createNewFile()==true){
                    FileWriter toFile = new FileWriter(docFile.getName());
                    System.out.println();
                    toFile.write(doc.toString());
                    System.out.println("File: '" + docFile.getName() + "' Created.\nPlease Check Project Folder.");
                    toFile.close();
                }else{
                    System.out.println("Document Search File Already Made");
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            System.out.println("-Invalid DOC ID-");
            return null;
        }
        return doc.toString();
    }

    private String createWordTextFile(){
        System.out.println("Type word from the Dictionary to make a file.");
        String wordPosting="";
        input = UserInputCL.next();
        wordPosting=invertedIndex.getWordPostingString(input.toString().toLowerCase());
        if(wordPosting!=null){
            try{
                File docFile = new File(input+"_SearchResults.txt");
                if(docFile.createNewFile()==true){
                    FileWriter toFile = new FileWriter(docFile.getName());
                    System.out.println();
                    toFile.write(wordPosting);
                    System.out.println("File: '" + docFile.getName() + "' Created.\nPlease Check Project Folder.");
                    toFile.close();
                    return input+": "+wordPosting;
                }else{
                    System.out.println("Word Search File Already Made");
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
         System.out .println("Word Not Found in Dictionary.");
        }
        return null;
    }

    private Integer processDocument(){
        System.out.println("DOCUMENT SEARCH: Enter Integer DOC ID # to\n-SEARCH=DOC ***");
        Integer idInput=-1;
        while(true) {
            try {
                input = UserInputCL.next();
                String escape = input.toString().toLowerCase();
                if (escape=="exit"||escape=="quit"||escape=="back"||escape=="help"){
                    idInput=-1;
                }else {
                    idInput = Integer.parseInt(input);
                }
                break;
            } catch (NumberFormatException nfe) {
                System.out.println("Input: "+input+" is not a valid DOC ID");
            }
        }
        return idInput;
    }

    private void logPrintIndex(String docORword){
        try{
            File PrintLog = new File("Print.txt");
            if(PrintLog.createNewFile()==true){
                FileWriter toFile = new FileWriter(PrintLog.getName());
                System.out.println();
                toFile.write("PRINT LOG: "+docORword+"\n");
                System.out.println("PrintIndex Logged Successfully");
                toFile.close();
            }else{
             FileWriter fwriter = new FileWriter("Print.txt",true);
             BufferedWriter bufferedWriter= new BufferedWriter(fwriter);

                bufferedWriter.write("\nPRINT LOG: "+docORword);
                bufferedWriter.newLine();
                bufferedWriter.close();
                System.out.println("PrintIndex Logged Successfully");
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void querySearch(String[] words,String userQuery){
        invertedIndex.Query(words, userQuery);
    }

    public void porterQuerySearch(LinkedHashSet<String> plhs, String userQuery){
        invertedIndex.portersQuery(plhs, userQuery);
    }

    public void docSearch(Integer DocID){
        if(DocID !=-1){
            System.out.println("Doc Search of "+DocID);
          invertedIndex.printDocument(DocID);
        }
    }

    /** wordsForPorter Function
     *  is a method that runs when the porter flag is set to true
     *  and a SEARCH is done the words entered are taken and put into
     *  a string array. This method writes it to a file for the porter stemmer
     *  algorithm to takes that file of words and process it.
     * **/
    private LinkedHashSet<String> wordsForPorter(String[] wordsToBeStemmed) throws IOException {
        try {
            File prtFile = new File(dataMGR.getPorterDataFile());
            FileWriter writeToPtr = new FileWriter(prtFile, false);
            for(String words: wordsToBeStemmed){
             writeToPtr.write(words+"\n");
            }
            writeToPtr.close();
            PorterStemmer.StemmerMethod(prtFile.getName());
        }catch (IOException e){
            e.printStackTrace();
        }
        return PorterStemmer.StemmerMethod(dataMGR.getPorterDataFile());
    }

    private void askStemmingPreferences(){
        System.out.println("Would you like to Include Stemming y/n?");
        input=UserInputCL.next().toLowerCase();
        if(input.equals("y")||input.equals("yes")){
            StemFlag = true;
            // invertedIndex.printPortersPostings();    //this line prints stemmed keywords
            System.out.println("Stemming Enabled");
        }else{
            System.out.println("Stemming Disabled");
            StemFlag = false;
        }
    }

    private void askOutputResultsPreferences(){
        System.out.println("Return options:\nType '1' for results to be displayed on a GUI\nType '2' for results to sent to both GUI AND text file\nType '3' for txt file\n");
        try {
            outputFlag = UserInputCL.nextInt();
            if (outputFlag == 1) {
                invertedIndex.setOutput_Flag(1);
            } else if (outputFlag == 2) {
                invertedIndex.setOutput_Flag(2);
            } else {// 3 Default txt file
                invertedIndex.setOutput_Flag(3);
            }
        }catch (NoSuchElementException e){
            invertedIndex.setOutput_Flag(3);
        }

    }

    private void processArg2(LinkedHashSet<String>args2){
        argsIs2=true;
        invertedIndex.setOutput_Flag(3);
        for(String s: args2){
            String s2;
            System.out.println("IN ARGS2: "+s);
            queriesRecorded.add(s);
            s2 = s.replaceAll("[.?]","");
            s2 = s.replaceAll("\\s{2,}", " ");
            String[] array = s2.split(" ");
            invertedIndex.argsIs2Query(array, s);
        }
        LinkedHashSet<String> args2res = invertedIndex.getArgsIs2Results();

        Scanner scan = new Scanner(System.in);
        System.out.println("Enter Name for Results in Text File:");
        String UserInputTxtFile= scan.next();
        String txtFilePath=UserInputTxtFile+".txt";
        if(UserInputTxtFile.length()<1 || UserInputTxtFile==null){
            txtFilePath="2Args_results.txt";
        }
        try{
            File docFile = new File(txtFilePath);
            if(docFile.createNewFile()==true){
                FileWriter toFile = new FileWriter(docFile.getName());
                System.out.println();
                for(String res: args2res) {
                    toFile.append(res); //Writing to the file the results of the word search.
                }

                System.out.println("File: '" + txtFilePath + "' Created.\nPlease Check Project Folder.");
                toFile.close();
            }else{
                System.out.println("Word Search File Already Made");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void help(){
        String Commands ="1.  'SEARCH' --- Prompts User to input a Query. Searches and Retrieves Documents relating to Query.\n" +
                "2.  'WORDTXT' --- Prompts User for a single word and auto creates a txt file containing results of a query search of that word.\n"+
                "3.  'DOCTXT' --- Requests a Numerical input from user that corresponds to a stored documents ID Number and prints that document to a TXT File.\n"+
                "4.  'DOCUMENT'--- Searches and retrieves Document Based on entered ID Number.\n" +
                "5.  'DOC' --- This is a Shorthand Command of the DOCUMENT command, it does the same thing and retrieves the document based on the ID number given.\n" +
                "6.  'PRINT' --- Prompts user to choose to print Index (creates text file based on either word or ID #), print the postings list, or print all documents.\n" +
                "7.  'SETPREF' --- Allows user to change Search Results Output and Stemming Options.\n" +
                "8.  'EXIT' --- Terminates Program.";
        System.out.println(Commands);
    }

}