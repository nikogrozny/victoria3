import com.opencsv.exceptions.CsvValidationException;
import model.Building;
import model.Province;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws CsvValidationException, IOException {
        Province picardy = new Province("Picardy");
        picardy.buildIndustrialBuilding("Tooling Workshop");
        picardy.buildIndustrialBuilding("Tooling Workshop");
        picardy.migrateIn("laborers", 6000);
        picardy.migrateIn("machinists", 2000);
        picardy.updateEmployment();
        picardy.productionStep();
        System.out.println(picardy);
        picardy.switchProductionMethods("Tooling Workshop", "Pig Iron Tools");
        picardy.switchProductionMethods("Tooling Workshop", "Water-tube Boiler");
        picardy.migrateIn("laborers", 6000);
        picardy.migrateIn("machinists", 2000);
        picardy.updateEmployment();
        picardy.productionStep();
        System.out.println(picardy);
    }
}