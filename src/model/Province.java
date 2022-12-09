package model;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Province {

    private int arableLands;
    private String name;
    private Map<String,Integer> resourceSpots;
    private Map<String,Building> industrialBuildings;
    private Map<String,Integer> availableWorkers;
    private MarketPlace localMarket;

    public Province(String name) throws CsvValidationException, IOException {
        this.name = name;
        this.industrialBuildings = new HashMap<>();
        this.availableWorkers = new HashMap<>();
        this.localMarket = new MarketPlace();
    }

    public Map<String, Building> getIndustrialBuildings() {
        return industrialBuildings;
    }

    public MarketPlace getLocalMarket() {
        return localMarket;
    }

    public void buildIndustrialBuilding(String type){
        if (!this.industrialBuildings.containsKey(type)){
            this.industrialBuildings.put(type, new Building(type));
        }
        this.industrialBuildings.get(type).upgrade();
    }

    public void switchProductionMethods(String buildingType, String methodName) throws CsvValidationException,
            IOException {
        if (this.industrialBuildings.containsKey(buildingType)){
            this.industrialBuildings.get(buildingType).switchProductionMethods(new ProductionMethod(methodName));
        }
    }

    public void migrateIn(String workersType, int nbWorkers) {
        if (this.availableWorkers.containsKey(workersType)){
            this.availableWorkers.put(workersType, nbWorkers + this.availableWorkers.get(workersType));
        }
        else{
            this.availableWorkers.put(workersType, nbWorkers);
        }
    }

    public void updateEmployment() {
        for (String specificWorkers : this.availableWorkers.keySet()){
            for (String rankedBuilding: this.industrialBuildings.keySet()){
                int hired = this.industrialBuildings.get(rankedBuilding).hire(specificWorkers,
                        this.availableWorkers.get(specificWorkers));
                this.availableWorkers.put(specificWorkers, this.availableWorkers.get(specificWorkers) - hired);
            }
        }
    }


    public void productionStep() {
        for (String building: this.industrialBuildings.keySet()){
            this.industrialBuildings.get(building).produce(this.getLocalMarket().getPrices());
        }
    }

    @Override
    public String toString(){
        StringBuilder representation = new StringBuilder("***Province - %s***%n%n".formatted(this.name));
        representation.append(this.getLocalMarket()).append("\n\n");
        for (String buildingType: this.industrialBuildings.keySet()){
            representation.append(this.industrialBuildings.get(buildingType).toString()).append("\n___\n");
        }
        return representation.toString();
    }
}
