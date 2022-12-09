package model;

import com.opencsv.exceptions.CsvValidationException;
import dataTools.CSVParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MarketPlace {

    private Map<String,Integer> suppliedGoods;
    private Map<String,Integer> demandedGoods;
    private Map<String, Float> prices;

    public MarketPlace() throws CsvValidationException, IOException {
        this.prices = new HashMap<>();
        Map<String,Map<String,String>> basePrices = CSVParser.loadCSV("data/Goods.csv", "name");
        for (String good: basePrices.keySet()){
            this.prices.put(good, Float.valueOf(basePrices.get(good).get("baseprice")));
        }
    }

    public Map<String, Float> getPrices() {
        return prices;
    }

    @Override
    public String toString(){
        return this.prices.toString();
    }
}
