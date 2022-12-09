package model;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static dataTools.CSVParser.retriveLinesByConditions;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class Building {

    final private String buildingType;
    private int level;
    private Map<String,Integer> employed;
    private final Map<String,String> possibleProductionMethods;
    private Map<String,ProductionMethod> productionMethods;
    private float cash;
    private Map<String,Float> wages;


    public Building(String type) {
        this.buildingType = type;
        this.level = 0;
        this.cash = 0;
        this.employed = new HashMap<>();
        this.possibleProductionMethods = new HashMap<>();
        this.productionMethods = new HashMap<>();
        try {
            Map<String, String> conditions = new HashMap<>();
            conditions.put("building",  type);
            List<Map<String, String>> retrieved = retriveLinesByConditions("data/Buildings.csv", conditions);
            for (Map<String,String> line: retrieved){
                this.possibleProductionMethods.put(line.get("method"), line.get("type"));
                if (Objects.equals(line.get("default"), "true")){
                    ProductionMethod newMethod = new ProductionMethod(line.get("method"));
                    this.productionMethods.put(line.get("type"), newMethod);
                }
            }
        } catch (CsvValidationException | IOException e) {
            throw new RuntimeException(e);
        }
        this.wages = new HashMap<>();
        this.wages.put("laborers", (float) 0.05);
        this.wages.put("machinists", (float) 0.2);
        this.wages.put("engineers", (float) 0.5);
    }

    public int getLevel() {
        return level;
    }

    public void upgrade(){
        this.level++;
    }

    public void downgrade() throws Exception {
        if (this.level > 0){
            this.level--;
        }
        else{
            throw new Exception("Building already at level 0");
        }
    }

    public Map<String, Integer> getEmployed() {
        return employed;
    }

    public int computeEffectiveLevel(){
        Map<String, Integer> requiredWorkersPerLevel = new HashMap<>();
        for (ProductionMethod productionMethod: this.productionMethods.values()){
            requiredWorkersPerLevel = productionMethod.checkWorkers(requiredWorkersPerLevel, 1);
        }
        int effectiveLevel = this.level;
        for (String workerCategory : requiredWorkersPerLevel.keySet()){
            if (requiredWorkersPerLevel.get(workerCategory) > 0) {
                int hiredWorkers = 0;
                if (this.employed.containsKey(workerCategory)) {
                    hiredWorkers = this.employed.get(workerCategory);
                }
                effectiveLevel = min(effectiveLevel, hiredWorkers / requiredWorkersPerLevel.get(workerCategory));
            }
        }
        return effectiveLevel;
    }

    public Map<String, Integer> computeMaxWorkers(){
        Map<String, Integer> neededWorkers = new HashMap<>();
        for (ProductionMethod productionMethod: this.productionMethods.values()){
            neededWorkers = productionMethod.checkWorkers(neededWorkers, this.level);
        }
        return neededWorkers;
    }

    public int hire(String workerType, int availableWorkers){
        Map<String, Integer> maxWorkers = computeMaxWorkers();
        int hiredWorkers = 0;
        if (maxWorkers.containsKey(workerType)){
            if (this.employed.containsKey(workerType)){
                hiredWorkers = max(min(availableWorkers, maxWorkers.get(workerType) - this.employed.get(workerType)), 0);
                this.employed.put(workerType, hiredWorkers + this.employed.get(workerType));
            }
            else{
                hiredWorkers = min(availableWorkers, maxWorkers.get(workerType));
                this.employed.put(workerType, hiredWorkers);
            }
        }
        return hiredWorkers;

    }

    public void switchProductionMethods(ProductionMethod newMethod) {
        if (this.possibleProductionMethods.containsKey(newMethod.getName())){
            this.productionMethods.put(this.possibleProductionMethods.get(newMethod.getName()), newMethod);
            this.reduceStaff();
        }
    }

    private void reduceStaff() {
        Map<String, Integer> maxWorkers = computeMaxWorkers();
        for (String workerType: this.employed.keySet()){
            if (this.computeMaxWorkers().containsKey(workerType)){
                this.employed.put(workerType, min(this.employed.get(workerType), maxWorkers.get(workerType)));
            }
            else{
                this.employed.remove(workerType);
            }
        }
    }

    @Override
    public String toString(){
        StringBuilder representation = new StringBuilder("Building - %s%n workers%n".formatted(this.buildingType));
        for (String good : this.employed.keySet()){
            representation.append(" %s : %d%n".formatted(good, this.employed.get(good)));
        }
        for (String methodName: this.possibleProductionMethods.keySet()){
            representation.append(" - %s (%s)".formatted(methodName, this.possibleProductionMethods.get(methodName)));
        }
        representation.append("%n maxLevel : %s%n".formatted(this.level));
        representation.append("%n actualLevel : %s%n".formatted(this.computeEffectiveLevel()));
        representation.append("%n cash : $%s%n".formatted(this.cash));
        for (ProductionMethod productionMethod: this.productionMethods.values()){
            representation.append("\n").append(productionMethod);
        }
        return representation.toString();
    }


    public Map<String, Integer> produce(Map<String, Float> prices) {
        Map<String, Integer> productionResult = new HashMap<>();
        for (ProductionMethod productionMethod: this.productionMethods.values()){
            productionResult = productionMethod.produce(productionResult, this.computeEffectiveLevel());
        }
        float productionGain = 0;
        for (String good : productionResult.keySet()){
            productionGain += productionResult.get(good) * prices.get(good);
        }
        for (String workerType: this.employed.keySet()){
            productionGain -= this.employed.get(workerType) * this.wages.get(workerType);
        }
        this.cash += productionGain;
        return productionResult;
    }
}
