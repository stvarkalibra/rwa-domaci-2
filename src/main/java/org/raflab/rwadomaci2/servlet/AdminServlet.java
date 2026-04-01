package org.raflab.rwadomaci2.servlet;

import org.raflab.rwadomaci2.listener.AppInitializer;
import org.raflab.rwadomaci2.model.Day;
import org.raflab.rwadomaci2.service.AppState;
import org.raflab.rwadomaci2.util.HtmlUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/odabrana-jela")
public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AppState appState = getAppState();
        String password = req.getParameter("lozinka");

        if (!isAuthorized(appState, password)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("text/html;charset=UTF-8");

            PrintWriter out = resp.getWriter();
            out.println(HtmlUtil.pageStart("Zabranjen pristup"));
            out.println("<div class='card'>");
            out.println("<div class='error'>Pogrešna ili nedostajuća lozinka.</div>");
            out.println("</div>");
            out.println(HtmlUtil.pageEnd());
            return;
        }

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println(HtmlUtil.pageStart("Odabrana jela"));
        out.println("<div class='card'>");
        out.println("<h1>Pregled svih porudžbina</h1>");
        out.println("<p class='small'>Količina poručenih jela grupisana po danima.</p>");
        out.println("</div>");

        Map<Day, Map<String, Integer>> groupedCounts = appState.getGroupedCounts();

        for (Day day : Day.values()) {
            out.println("<div class='card'>");
            out.println("<h2>" + HtmlUtil.escape(day.getDisplayName()) + "</h2>");

            Map<String, Integer> countsForDay = groupedCounts.get(day);
            if (countsForDay == null || countsForDay.isEmpty()) {
                out.println("<p>Nema porudžbina za ovaj dan.</p>");
            } else {
                out.println("<table>");
                out.println("<tr><th>Jelo</th><th>Količina</th></tr>");
                for (Map.Entry<String, Integer> entry : countsForDay.entrySet()) {
                    out.println("<tr><td>" + HtmlUtil.escape(entry.getKey()) + "</td><td>" + entry.getValue() + "</td></tr>");
                }
                out.println("</table>");
            }

            out.println("</div>");
        }

        out.println("<div class='card'>");
        out.println("<h2>Restart sistema</h2>");
        out.println("<form method='post' action='" + req.getContextPath() + "/odabrana-jela'>");
        out.println("<input type='hidden' name='lozinka' value='" + HtmlUtil.escape(password) + "'/>");
        out.println("<button type='submit'>Obriši sve porudžbine</button>");
        out.println("</form>");
        out.println("</div>");

        out.println(HtmlUtil.pageEnd());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AppState appState = getAppState();
        String password = req.getParameter("lozinka");

        if (!isAuthorized(appState, password)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.setContentType("text/html;charset=UTF-8");

            PrintWriter out = resp.getWriter();
            out.println(HtmlUtil.pageStart("Zabranjen pristup"));
            out.println("<div class='card'><div class='error'>Pogrešna lozinka.</div></div>");
            out.println(HtmlUtil.pageEnd());
            return;
        }

        appState.resetAllOrders();
        resp.sendRedirect(req.getContextPath() + "/odabrana-jela?lozinka=" + password);
    }

    private boolean isAuthorized(AppState appState, String password) {
        return password != null && password.equals(appState.getAdminPassword());
    }

    private AppState getAppState() {
        return (AppState) getServletContext().getAttribute(AppInitializer.APP_STATE_KEY);
    }
}