import java.io.*;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Files;

public class Main {

    private static String pathNameForCorpus="C:\\---\\---\\Corpus";//PATHNAME for Corpus is Needed as the first Argument.
    private static Scanner UserInput = new Scanner(System.in);
    private static String userString;
    private static boolean monitorStatus = true;
    private static DataManager DM = new DataManager();
    private static LinkedHashSet<String> arg2Queries = new LinkedHashSet<String>();

    public static void main(String[] args) throws IOException {
        check_args(args);
        if(DM.hasData()==false) {
            commandLineParse commandLine;
            corpus Corpus = new corpus(pathNameForCorpus);
            stopList StopList = new stopList();
            StopList.createStopWordsTxtFile();
            InvertedIndex invertedIndex = new InvertedIndex(StopList, Corpus, DM);
            if(args.length==2){
                commandLine = new commandLineParse(invertedIndex,DM,arg2Queries);
            }else{
                commandLine = new commandLineParse(invertedIndex, DM);
            }

            DM.invertedIndexToFile(invertedIndex);
            while (monitorStatus) {
                monitorStatus = commandLine.monitorCommandLine();
            }
        }else{
            commandLineParse commandLine = new commandLineParse(DM.getStoredInvertedIndex(), DM);
            DM.getStoredInvertedIndex().printPostings();
            while (monitorStatus) {
                monitorStatus = commandLine.monitorCommandLine();
            }
        }

    }


    private static void check_args(String[] Args) throws FileNotFoundException {
        Scanner userInput = new Scanner(System.in);
        if(Args.length==0){
            System.out.println("Arguments given is 0. PathName for corpus is not found \nPlease enter a valid Path Name for Corpus:");
            int attempts=4;
            while(true) {
                String path_name = userInput.next();
                if (Files.exists(Path.of(path_name))) {
                    System.out.println("File Path Found: \"" + path_name + "\"");
                    pathNameForCorpus = path_name;
                    break;
                } else {
                    attempts--;
                    if (attempts == 0) {
                        System.out.println("File Path For Corpus Not Found");
                        System.exit(0);
                    }
                    System.out.println("File Path Not Found: >" + path_name + "< Attempts Remaining before Exit: "+attempts);
                }
            }
        }else if(Args.length==1 ){
            pathNameForCorpus=Args[0];
            if(Files.exists(Path.of(pathNameForCorpus))){
             System.out.println("File Path Found: \"" + pathNameForCorpus+ "\"");
            }else{
                System.out.println("File Path Not Found");
                System.exit(0);
            }
        }else if(Args.length==2 && Args[1]!=null){
            File queries= new File(Args[1]);
            pathNameForCorpus=Args[0];
            try(BufferedReader b=new BufferedReader(new FileReader(queries))){
                for(String line; (line=b.readLine())!=null;){
                    arg2Queries.add(line);
                }
            } catch (IOException e) {
                 e.printStackTrace();
            }
            System.out.println("ARGS=2 "+Args[1]);
        }
    }


}