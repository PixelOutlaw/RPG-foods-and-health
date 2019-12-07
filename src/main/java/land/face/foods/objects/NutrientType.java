package land.face.foods.objects;

public enum NutrientType {

    PROTEIN("protein"),
    DAIRY("dairy"),
    CARBOHYDRATE("carbohydrates"),
    VEGETABLE("vegetables");

    private final String nutrientName;

    NutrientType(String nutrientName){
        this.nutrientName = nutrientName;
    }

    public String getNutrientName(){
        return nutrientName;
    }

}
