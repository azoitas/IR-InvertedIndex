import java.io.*;

public class DataManager implements Serializable{
    private final String porterDataFile ="PORTER_DATA.txt";
    private final String indexDataFile ="INVERTED_INDEX_DATA.txt";
    private File indexDocFile = new File(indexDataFile);
    private File porterFile = new File(porterDataFile);

    public DataManager() {
    checkFileExistence();
    }

    public String getPorterDataFile() {
        return porterDataFile;
    }

    private void checkFileExistence(){
        try{
            if(indexDocFile.createNewFile()==true){
                FileWriter toFile = new FileWriter(indexDocFile.getName());
                toFile.close();
            }
            if(porterFile.createNewFile()==true){
                FileWriter toFile = new FileWriter(porterFile.getName());
                toFile.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    boolean hasData(){
        if(indexDocFile.listFiles() == null ||porterFile.listFiles()==null){
            return false;
        }
        return true;
    }

    void invertedIndexToFile(InvertedIndex inverted_Index) throws IOException {
        ObjectOutputStream os1 = new ObjectOutputStream(new FileOutputStream(indexDocFile));
        os1.writeObject(inverted_Index);
        os1.close();
    }

    InvertedIndex getStoredInvertedIndex(){
        InvertedIndex ivr=null;
        try{
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(indexDocFile));
            ivr = (InvertedIndex) input.readObject();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ivr;
    }


}
