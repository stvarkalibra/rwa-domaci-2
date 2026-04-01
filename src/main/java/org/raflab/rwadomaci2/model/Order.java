package org.raflab.rwadomaci2.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class Order {

    private final Map<Day, String> selectedMeals = new EnumMap<>(Day.class);

    public void setMeal(Day day, String meal) {
        selectedMeals.put(day, meal);
    }

    public String getMeal(Day day) {
        return selectedMeals.get(day);
    }

    public Map<Day, String> getSelectedMeals() {
        return Collections.unmodifiableMap(selectedMeals);
    }
}