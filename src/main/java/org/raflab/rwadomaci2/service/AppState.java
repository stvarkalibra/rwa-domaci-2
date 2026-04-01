package org.raflab.rwadomaci2.service;

import org.raflab.rwadomaci2.model.Day;
import org.raflab.rwadomaci2.model.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppState {

    private final Map<Day, List<String>> availableMeals = new EnumMap<>(Day.class);
    private final List<Order> orders = new ArrayList<>();
    private String adminPassword;
    private int resetVersion = 0;

    public synchronized void setMealsForDay(Day day, List<String> meals) {
        availableMeals.put(day, new ArrayList<>(meals));
    }

    public synchronized List<String> getMealsForDay(Day day) {
        List<String> meals = availableMeals.get(day);
        return meals == null ? Collections.<String>emptyList() : Collections.unmodifiableList(meals);
    }

    public synchronized Map<Day, List<String>> getAllMeals() {
        Map<Day, List<String>> copy = new EnumMap<>(Day.class);
        for (Map.Entry<Day, List<String>> entry : availableMeals.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }

    public synchronized void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public synchronized String getAdminPassword() {
        return adminPassword;
    }

    public synchronized void addOrder(Order order) {
        orders.add(order);
    }

    public synchronized List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    public synchronized int getResetVersion() {
        return resetVersion;
    }

    public synchronized void resetAllOrders() {
        orders.clear();
        resetVersion++;
    }

    public synchronized Map<Day, Map<String, Integer>> getGroupedCounts() {
        Map<Day, Map<String, Integer>> result = new EnumMap<>(Day.class);

        for (Day day : Day.values()) {
            result.put(day, new LinkedHashMap<String, Integer>());
        }

        for (Order order : orders) {
            for (Day day : Day.values()) {
                String meal = order.getMeal(day);
                if (meal != null) {
                    Map<String, Integer> mealCounts = result.get(day);
                    Integer current = mealCounts.get(meal);
                    mealCounts.put(meal, current == null ? 1 : current + 1);
                }
            }
        }

        return result;
    }
}