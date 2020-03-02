package land.face.foods.objects;

import java.util.HashMap;
import java.util.Map;

public enum NutrientType {

  PROTEIN("protein", "Protein"),
  DAIRY("dairy", "Dairy"),
  CARBOHYDRATE("carbohydrates", "Carbs"),
  PRODUCE("produce", "Produce");

  private final String dataName;
  private final String prettyName;

  NutrientType(String dataName, String prettyName) {
    this.dataName = dataName;
    this.prettyName = prettyName;
  }

  // Name used for save/load of values
  public String getDataName() {
    return dataName;
  }

    public String getPrettyName() {
        return prettyName;
    }

  private static final Map<String, NutrientType> copyOfValues = buildStringToAttributeMap();

  private static Map<String, NutrientType> buildStringToAttributeMap() {
    Map<String, NutrientType> values = new HashMap<>();
    for (NutrientType stat : NutrientType.values()) {
      if (stat.getDataName() == null) {
        continue;
      }
      values.put(stat.getDataName(), stat);
    }
    return values;
  }

  public static NutrientType fromName(String name) {
    return copyOfValues.getOrDefault(name, null);
  }

}
