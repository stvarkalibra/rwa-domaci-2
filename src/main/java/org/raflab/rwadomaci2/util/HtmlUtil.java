package org.raflab.rwadomaci2.util;

public class HtmlUtil {

    private HtmlUtil() {
    }

    public static String escape(String text) {
        if (text == null) {
            return "";
        }

        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    public static String pageStart(String title) {
        return "<!DOCTYPE html>" +
                "<html lang='sr'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>" + escape(title) + "</title>" +
                "<style>" +
                "body{font-family:Arial,sans-serif;max-width:900px;margin:40px auto;padding:20px;background:#f7f7f7;color:#222;}" +
                ".card{background:#fff;border-radius:12px;padding:20px;margin-bottom:20px;box-shadow:0 2px 8px rgba(0,0,0,0.08);}" +
                "h1,h2,h3{margin-top:0;}" +
                "label{font-weight:bold;display:block;margin-bottom:8px;}" +
                "select{width:100%;padding:10px;margin-bottom:15px;border:1px solid #ccc;border-radius:8px;}" +
                "button{background:#2d6cdf;color:white;border:none;padding:12px 18px;border-radius:8px;cursor:pointer;}" +
                "button:hover{background:#1f55b3;}" +
                ".error{background:#ffe0e0;color:#8a1f1f;padding:12px;border-radius:8px;margin-bottom:20px;}" +
                ".success{background:#e1f7e1;color:#1d6b1d;padding:12px;border-radius:8px;margin-bottom:20px;}" +
                "table{width:100%;border-collapse:collapse;margin-top:10px;}" +
                "th,td{border:1px solid #ddd;padding:10px;text-align:left;}" +
                "th{background:#f0f0f0;}" +
                ".small{color:#666;font-size:14px;}" +
                "</style>" +
                "</head><body>";
    }

    public static String pageEnd() {
        return "</body></html>";
    }
}