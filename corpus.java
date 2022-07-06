import java.io.File;
import java.util.LinkedHashSet;
import java.util.HashMap;

public class corpus {

    private LinkedHashSet<String> corpusFileNames = new LinkedHashSet<String>(170);
    private HashMap<Integer,String> amendedFilesNamesHM2 = new HashMap<Integer,String>(170); //Duplicate Doc Name Safe
    private File[] docFolderRawNames;


    public corpus(String CORPUS_PATHNAME) {
            File Directory = new File(CORPUS_PATHNAME);
            docFolderRawNames = Directory.listFiles();
            String fileNameFixed;
            Integer id = 0;
            for (File directoryFiles : docFolderRawNames) {
                id++;
                fileNameFixed = cleanString(removeExtension(directoryFiles.getName()));
                corpusFileNames.add(cleanString(removeExtension(directoryFiles.getName())));
                amendedFilesNamesHM2.put(id, fileNameFixed);
            }
    }

    private String removeExtension(String fileName){
        String amendedName = fileName;
        if(fileName.contains(".")){
            amendedName = fileName.substring(0,fileName.lastIndexOf('.'));
        }
        return amendedName;
    }

    private String cleanString(String fileName){
        String limitSymbols = fileName;
            limitSymbols = limitSymbols.replaceAll("[-_]"," ");
            limitSymbols = limitSymbols.replaceAll("\\s{2,}", " ");
            limitSymbols = limitSymbols.replaceAll("[^A-Za-z0-9 $]","");
        return limitSymbols;
    }


    public LinkedHashSet<String> getCorpusLHS(){
        return corpusFileNames;
    }
    public HashMap<Integer,String> getCorpusHM2(){
        return amendedFilesNamesHM2;
    }
    public File[] getDocFolderRawNames(){
        return docFolderRawNames;
    }

}