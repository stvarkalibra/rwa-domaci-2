package org.raflab.rwadomaci2.listener;

import org.raflab.rwadomaci2.model.Day;
import org.raflab.rwadomaci2.service.AppState;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebListener
public class AppInitializer implements ServletContextListener {

    public static final String APP_STATE_KEY = "appState";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        AppState appState = new AppState();

        try {
            appState.setMealsForDay(Day.PONEDELJAK, loadLines("ponedeljak.txt"));
            appState.setMealsForDay(Day.UTORAK, loadLines("utorak.txt"));
            appState.setMealsForDay(Day.SREDA, loadLines("sreda.txt"));
            appState.setMealsForDay(Day.CETVRTAK, loadLines("cetvrtak.txt"));
            appState.setMealsForDay(Day.PETAK, loadLines("petak.txt"));
            appState.setAdminPassword(loadPassword("password.txt"));
        } catch (IOException e) {
            throw new RuntimeException("Greška pri učitavanju početnih fajlova.", e);
        }

        context.setAttribute(APP_STATE_KEY, appState);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    private List<String> loadLines(String fileName) throws IOException {
        InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(fileName);

        if (is == null) {
            throw new IOException("Fajl nije pronađen: " + fileName);
        }

        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) {
                    lines.add(trimmed);
                }
            }
        }

        return lines;
    }

    private String loadPassword(String fileName) throws IOException {
        List<String> lines = loadLines(fileName);
        if (lines.isEmpty()) {
            throw new IOException("password.txt je prazan.");
        }
        return lines.get(0);
    }
}