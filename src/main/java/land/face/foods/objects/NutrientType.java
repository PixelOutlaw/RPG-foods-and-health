package land.face.foods.objects;

import java.util.HashMap;
import java.util.Map;

public enum NutrientType {

    PROTEIN("protein"),
    DAIRY("dairy"),
    CARBOHYDRATE("carbohydrates"),
    PRODUCE("produce");

    private final String nutrientName;

    NutrientType(String nutrientName){
        this.nutrientName = nutrientName;
    }

    public String getNutrientName(){
        return nutrientName;
    }

    private static final Map<String, NutrientType> copyOfValues = buildStringToAttributeMap();

    private static Map<String, NutrientType> buildStringToAttributeMap() {
        Map<String, NutrientType> values = new HashMap<>();
        for (NutrientType stat : NutrientType.values()) {
            if (stat.getNutrientName() == null) {
                continue;
            }
            values.put(stat.getNutrientName(), stat);
        }
        return values;
    }

    public static NutrientType fromName(String name) {
        return copyOfValues.getOrDefault(name, null);
    }

}
