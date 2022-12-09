package model;

import com.opencsv.exceptions.CsvValidationException;
import dataTools.CSVParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProductionMethod {

    private final String name;
    private final Map<String, Integer> inOutPut;
    private final Map<String, Integer> workForce;
    static final String[] GOODS = {"wood", "iron", "steel", "tools"};
    static final String[] WORKERS = {"machinists", "laborers", "engineers"};


    public String getName() {
        return name;
    }

    public ProductionMethod(String name) throws CsvValidationException, IOException {
        this.name = name;
        Map<String, String> data = CSVParser.retrieveLineByName("data/ProductionMethods.csv", name);
        this.inOutPut = new HashMap<>();
        for (String possibleGood: data.keySet()){
            if (Arrays.asList(GOODS).contains(possibleGood)){
                this.inOutPut.put(possibleGood, Integer.valueOf(data.get(possibleGood)));
            }
        }
        this.workForce = new HashMap<>();
        for (String possibleWorker: data.keySet()){
            if (Arrays.asList(WORKERS).contains(possibleWorker)){
                this.workForce.put(possibleWorker, Integer.valueOf(data.get(possibleWorker)));
            }
        }
    }

    public Map<String, Integer> produce(Map<String, Integer> previousState, int level){
        for (String good : this.inOutPut.keySet()){
            if (previousState.containsKey(good)){
                previousState.put(good, previousState.get(good) + this.inOutPut.get(good) * level);
            }
            else{
                previousState.put(good, this.inOutPut.get(good) * level);
            }
        }
        return previousState;
    }

    public Map<String, Integer> checkWorkers(Map<String, Integer> previousState, int level) {
        for (String worker : this.workForce.keySet()){
            int needs = this.workForce.get(worker) * level;
            if (previousState.containsKey(worker)){
                previousState.put(worker, previousState.get(worker) + needs);
            }
            else{
                previousState.put(worker, needs);
            }
        }
        return previousState;
    }



    @Override
    public String toString(){
        StringBuilder representation = new StringBuilder("Method - %s%n In/Output per level%n".formatted(this.name));
        for (String good : this.inOutPut.keySet()){
            representation.append(" %s : %d%n".formatted(good, this.inOutPut.get(good)));
        }
        representation.append("\nRequired workers per level\n");
        for (String worker : this.workForce.keySet()){
            representation.append(" %s : %d%n".formatted(worker, this.workForce.get(worker)));
        }
        return representation.toString();
    }


}
