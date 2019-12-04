package land.face.foods.managers;

import org.bukkit.entity.Player;

import java.util.UUID;

public class HealthStatus {

    private int protein;
    private int carbohydrates;
    private int dairy;
    private int vegetables;
    private int healthScore;
    private int healthyBoi;

    private UUID uuid;

    public void setUUID(UUID playerUUID){
        this.uuid = playerUUID;
    }

    public int getProtein(){
        return protein;
    }

    public void setProtein(int amount){
        protein += amount;
    }

    public int getCarbohydrates(){
        return carbohydrates;
    }

    public void setCarbohydrates(int amount){
        carbohydrates += amount;
    }

    public int getDairy(){
        return dairy;
    }

    public void setDairy(int amount){
        dairy += amount;
    }

    public int getVegetables(){
        return vegetables;
    }

    public void setVegetables(int amount){
        vegetables += amount;
    }

    public int getHealthScore(){
        return healthScore;
    }

    public void setHealthScore(int amount){
        healthScore += amount;
    }

    public int getHealthyBoi(){
        return healthyBoi;
    }

    public void setHealthyBoi(int amount){
        healthyBoi += amount;
    }


}