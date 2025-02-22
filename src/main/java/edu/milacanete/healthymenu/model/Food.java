package edu.milacanete.healthymenu.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Food {
    private String name;
    private String category;
    private int weightGram;

    public Food(String name, String category, int weightGram) {
        this.name = name;
        this.category = category;
        this.weightGram = weightGram;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getWeightGram() {
        return weightGram;
    }

    public void setWeightGram(int weightGram) {
        this.weightGram = weightGram;
    }

    public float getWeightInOz() {
        return new BigDecimal(weightGram * 0.035274f).setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    public String getDescription() {
        return name + "(" + weightGram + "g) - categoria: " + category;
    }

    @Override
    public String toString() {
        return name + ";" + category + ";" + weightGram;
    }
}
