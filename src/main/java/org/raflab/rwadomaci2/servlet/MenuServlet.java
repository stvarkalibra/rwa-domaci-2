package org.raflab.rwadomaci2.servlet;

import org.raflab.rwadomaci2.listener.AppInitializer;
import org.raflab.rwadomaci2.model.Day;
import org.raflab.rwadomaci2.model.Order;
import org.raflab.rwadomaci2.service.AppState;
import org.raflab.rwadomaci2.util.HtmlUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/izbor-jela")
public class MenuServlet extends HttpServlet {

    private static final String SESSION_ORDER_DONE = "orderDone";
    private static final String SESSION_ORDER = "savedOrder";
    private static final String SESSION_RESET_VERSION = "resetVersion";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setNoCacheHeaders(resp);

        AppState appState = getAppState();
        HttpSession session = req.getSession();

        syncSessionWithResetVersion(session, appState);

        boolean alreadyOrdered = Boolean.TRUE.equals(session.getAttribute(SESSION_ORDER_DONE));
        Order savedOrder = (Order) session.getAttribute(SESSION_ORDER);

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println(HtmlUtil.pageStart("Odabir jela"));

        // Script koji tera browser da refresha stranu ako je vracena iz Back/Forward cache-a
        out.println("<script>");
        out.println("window.addEventListener('pageshow', function(event) {");
        out.println("  if (event.persisted) {");
        out.println("    window.location.reload();");
        out.println("  }");
        out.println("});");
        out.println("</script>");

        if (alreadyOrdered && savedOrder != null) {
            out.println("<div class='card'>");
            out.println("<div class='error'>Pristup odbijen. U ovoj sesiji je porudžbina već napravljena i nije moguće ponovo birati jela.</div>");
            out.println("<h2>Već odabrana jela</h2>");
            out.println("<table>");
            out.println("<tr><th>Dan</th><th>Jelo</th></tr>");

            for (Day day : Day.values()) {
                out.println("<tr><td>" + HtmlUtil.escape(day.getDisplayName()) + "</td><td>" +
                        HtmlUtil.escape(savedOrder.getMeal(day)) + "</td></tr>");
            }

            out.println("</table>");
            out.println("</div>");
            out.println(HtmlUtil.pageEnd());
            return;
        }

        String error = (String) req.getAttribute("error");
        if (error != null) {
            out.println("<div class='error'>" + HtmlUtil.escape(error) + "</div>");
        }

        out.println("<div class='card'>");
        out.println("<h1>Odabir jela za narednu nedelju</h1>");
        out.println("<p class='small'>Za svaki radni dan moraš odabrati po jedno jelo.</p>");
        out.println("</div>");

        out.println("<div class='card'>");
        out.println("<form method='post' action='" + req.getContextPath() + "/izbor-jela'>");

        for (Day day : Day.values()) {
            List<String> meals = appState.getMealsForDay(day);

            out.println("<label for='" + day.getKey() + "'>" + HtmlUtil.escape(day.getDisplayName()) + "</label>");
            out.println("<select name='" + day.getKey() + "' id='" + day.getKey() + "' required>");
            out.println("<option value=''>-- Izaberi jelo --</option>");

            for (String meal : meals) {
                out.println("<option value='" + HtmlUtil.escape(meal) + "'>" + HtmlUtil.escape(meal) + "</option>");
            }

            out.println("</select>");
        }

        out.println("<button type='submit'>Potvrdi porudžbinu</button>");
        out.println("</form>");
        out.println("</div>");
        out.println(HtmlUtil.pageEnd());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setNoCacheHeaders(resp);

        AppState appState = getAppState();
        HttpSession session = req.getSession();

        syncSessionWithResetVersion(session, appState);

        boolean alreadyOrdered = Boolean.TRUE.equals(session.getAttribute(SESSION_ORDER_DONE));
        if (alreadyOrdered) {
            resp.sendRedirect(req.getContextPath() + "/izbor-jela");
            return;
        }

        Order order = new Order();

        for (Day day : Day.values()) {
            String meal = req.getParameter(day.getKey());

            if (meal == null || meal.trim().isEmpty()) {
                req.setAttribute("error", "Sva polja su obavezna.");
                doGet(req, resp);
                return;
            }

            boolean validMeal = appState.getMealsForDay(day).contains(meal);
            if (!validMeal) {
                req.setAttribute("error", "Neispravan izbor za dan: " + day.getDisplayName());
                doGet(req, resp);
                return;
            }

            order.setMeal(day, meal);
        }

        appState.addOrder(order);

        session.setAttribute(SESSION_ORDER_DONE, true);
        session.setAttribute(SESSION_ORDER, order);
        session.setAttribute(SESSION_RESET_VERSION, appState.getResetVersion());

        resp.sendRedirect(req.getContextPath() + "/uspesno");
    }

    private void syncSessionWithResetVersion(HttpSession session, AppState appState) {
        Integer sessionVersion = (Integer) session.getAttribute(SESSION_RESET_VERSION);
        int appVersion = appState.getResetVersion();

        if (sessionVersion == null || sessionVersion.intValue() != appVersion) {
            session.setAttribute(SESSION_ORDER_DONE, false);
            session.removeAttribute(SESSION_ORDER);
            session.setAttribute(SESSION_RESET_VERSION, appVersion);
        }
    }

    private AppState getAppState() {
        return (AppState) getServletContext().getAttribute(AppInitializer.APP_STATE_KEY);
    }

    private void setNoCacheHeaders(HttpServletResponse resp) {
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);
    }
}