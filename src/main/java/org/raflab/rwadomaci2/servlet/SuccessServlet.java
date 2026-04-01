package org.raflab.rwadomaci2.servlet;

import org.raflab.rwadomaci2.model.Day;
import org.raflab.rwadomaci2.model.Order;
import org.raflab.rwadomaci2.util.HtmlUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/uspesno")
public class SuccessServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setNoCacheHeaders(resp);

        HttpSession session = req.getSession();
        Order order = (Order) session.getAttribute("savedOrder");

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println(HtmlUtil.pageStart("Uspešna potvrda"));

        out.println("<script>");
        out.println("window.addEventListener('pageshow', function(event) {");
        out.println("  if (event.persisted) {");
        out.println("    window.location.reload();");
        out.println("  }");
        out.println("});");
        out.println("</script>");

        out.println("<div class='card'>");
        out.println("<div class='success'>Uspešno si potvrdio izbor jela.</div>");

        if (order != null) {
            out.println("<h2>Tvoja porudžbina</h2>");
            out.println("<table>");
            out.println("<tr><th>Dan</th><th>Jelo</th></tr>");

            for (Day day : Day.values()) {
                out.println("<tr><td>" + HtmlUtil.escape(day.getDisplayName()) + "</td><td>" +
                        HtmlUtil.escape(order.getMeal(day)) + "</td></tr>");
            }

            out.println("</table>");
        }

        out.println("</div>");
        out.println(HtmlUtil.pageEnd());
    }

    private void setNoCacheHeaders(HttpServletResponse resp) {
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);
    }
}