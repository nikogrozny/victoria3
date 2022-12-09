package dataTools;

import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CSVParser {

    public static Map<String,Map<String, String>> loadCSV(String fileName, String idName)
            throws IOException, CsvValidationException {
        CSVReaderHeaderAware csvIn = new CSVReaderHeaderAware(new FileReader(fileName));
        Map<String, String> field;
        Map<String,Map<String, String>> dataframe = new HashMap<>();
        while ((field = csvIn.readMap()) != null){
            dataframe.put(field.get(idName), field);
        }
        return dataframe;
    }

    public static Map<String, String> retrieveLineByName(String fileName, String selectedItem)
            throws IOException, CsvValidationException {
        Map<String,Map<String, String>> dataframe = loadCSV(fileName, "name");
        return dataframe.get(selectedItem);
    }

    public static List<Map<String,String>> retriveLinesByConditions(String fileName, Map<String,String> conditions)
            throws CsvValidationException, IOException {
        Map<String,Map<String, String>> dataframe = loadCSV(fileName, "id");
        List<Map<String,String>> selectedOutput = new ArrayList<>();
        for (String id: dataframe.keySet()){
            Map<String, String> line = dataframe.get(id);
            boolean meetAllConditions = true;
            for (String key: conditions.keySet()){
                if (!Objects.equals(line.get(key), conditions.get(key))){
                    meetAllConditions = false;
                }
            }
            if (meetAllConditions){
                selectedOutput.add(line);
            }
        }
        return selectedOutput;
    }
}
