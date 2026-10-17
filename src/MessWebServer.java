import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

/**
 * High-performance lightweight HTTP server for mobile QR meal scanning.
 * Serves modern responsive web interfaces to students' mobile browsers.
 *
 * Developed by Abhishek Pathak (https://github.com/abhishekpathak6941)
 */
public class MessWebServer {

    private HttpServer server;
    private int port;

    public MessWebServer(int port) throws IOException {
        this.port = port;
        server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/meal",    new MealFormHandler());
        server.createContext("/login",   new LoginHandler());
        server.createContext("/records", new RecordsHandler());
        server.setExecutor(null);
    }

    public void start() { server.start(); }
    public void stop()  { server.stop(0); }
    public int  getPort() { return port; }

    public static String getLocalIP() {
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = ifaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;
                Enumeration<InetAddress> addrs = iface.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if (addr instanceof Inet4Address) return addr.getHostAddress();
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "127.0.0.1";
    }

    // ── GET /meal?type=BREAKFAST&admin=1 ─────────────────────────────────────
    static class MealFormHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            String query    = ex.getRequestURI().getQuery();
            String mealType = parseParam(query, "type");
            String adminStr = parseParam(query, "admin");
            if (mealType == null) mealType = "MEAL";
            if (adminStr == null) adminStr = "0";

            mealType = mealType.toUpperCase();
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy"));
            String color = mealHex(mealType);
            String icon  = mealIcon(mealType);

            String html = "<!DOCTYPE html><html lang='en'><head>" +
                "<meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no'>" +
                "<title>SmartMess • " + mealType + " Verification</title><style>" +
                ":root{--primary:" + color + ";--bg:#0f172a;--card:#1e293b;--text:#f8fafc;--sub:#94a3b8;--border:#334155;}" +
                "*{box-sizing:border-box;margin:0;padding:0;-webkit-tap-highlight-color:transparent;}" +
                "body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;" +
                "background:linear-gradient(135deg,#090d16 0%,#0f172a 100%);color:var(--text);display:flex;" +
                "align-items:center;justify-content:center;min-height:100vh;padding:20px;}" +
                ".wrapper{width:100%;max-width:390px;}" +
                ".card{background:rgba(30,41,59,0.85);backdrop-filter:blur(16px);-webkit-backdrop-filter:blur(16px);" +
                "border:1px solid rgba(255,255,255,0.08);border-radius:22px;overflow:hidden;box-shadow:0 20px 40px rgba(0,0,0,0.45);}" +
                ".header{background:linear-gradient(180deg,rgba(255,255,255,0.04) 0%,rgba(255,255,255,0) 100%);" +
                "padding:28px 24px 20px;text-align:center;border-bottom:1px solid var(--border);position:relative;}" +
                ".accent-bar{position:absolute;top:0;left:0;right:0;height:4px;background:" + color + ";" +
                "box-shadow:0 0 16px " + color + ";}" +
                ".header h2{font-size:21px;font-weight:700;letter-spacing:-0.4px;color:#fff;margin-top:4px;}" +
                ".header .date{color:var(--sub);font-size:12px;margin-top:6px;font-weight:500;text-transform:uppercase;letter-spacing:0.8px;}" +
                ".meal-badge{display:inline-flex;align-items:center;gap:6px;background:rgba(255,255,255,0.08);" +
                "border:1px solid " + color + ";color:#fff;padding:6px 18px;border-radius:50px;font-weight:700;" +
                "font-size:13px;margin-top:14px;letter-spacing:0.5px;box-shadow:0 4px 12px rgba(0,0,0,0.2);}" +
                ".form{padding:26px 24px;}" +
                ".field{margin-bottom:18px;}" +
                "label{display:block;font-size:12px;font-weight:600;color:#cbd5e1;text-transform:uppercase;letter-spacing:0.7px;margin-bottom:8px;}" +
                "input{width:100%;padding:13px 15px;background:#0f172a;border:1px solid var(--border);border-radius:12px;" +
                "font-size:15px;color:#fff;outline:none;transition:all 0.2s ease;}" +
                "input:focus{border-color:" + color + ";box-shadow:0 0 0 3px rgba(255,255,255,0.1);}" +
                ".btn{width:100%;padding:14px;background:" + color + ";color:#fff;border:none;border-radius:12px;" +
                "font-size:15px;font-weight:700;letter-spacing:0.5px;cursor:pointer;margin-top:8px;" +
                "box-shadow:0 8px 20px rgba(0,0,0,0.3);transition:transform 0.15s ease,filter 0.15s ease;}" +
                ".btn:active{transform:scale(0.98);filter:brightness(0.9);}" +
                ".footer{text-align:center;color:var(--sub);font-size:11px;padding:16px 20px;letter-spacing:0.4px;}" +
                "</style></head><body>" +
                "<div class='wrapper'><div class='card'>" +
                "<div class='accent-bar'></div>" +
                "<div class='header'>" +
                "<h2>SmartMess Digital Pass</h2>" +
                "<p class='date'>" + today + "</p>" +
                "<div class='meal-badge'>" + icon + " " + mealType + "</div>" +
                "</div>" +
                "<form class='form' method='POST' action='/login'>" +
                "<input type='hidden' name='meal' value='" + mealType + "'>" +
                "<input type='hidden' name='admin' value='" + adminStr + "'>" +
                "<div class='field'>" +
                "<label>Student Roll Number</label>" +
                "<input type='text' name='roll' placeholder='e.g. 2026CS101' autocomplete='off' autocorrect='off' autocapitalize='characters' required>" +
                "</div>" +
                "<div class='field'>" +
                "<label>Mess Password</label>" +
                "<input type='password' name='pass' placeholder='••••••••' required>" +
                "</div>" +
                "<button class='btn' type='submit'>VERIFY & RECORD MEAL</button>" +
                "</form>" +
                "<div class='footer'>Campus Dining Management System • Secure Verification</div>" +
                "</div></div></body></html>";

            sendHtml(ex, 200, html);
        }
    }

    // ── POST /login ──────────────────────────────────────────────────────────
    static class LoginHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            if (!"POST".equals(ex.getRequestMethod())) { ex.sendResponseHeaders(405,-1); return; }
            String body     = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            String roll     = parseParam(body, "roll");
            String pass     = parseParam(body, "pass");
            String meal     = parseParam(body, "meal");
            String adminStr = parseParam(body, "admin");

            if (roll == null) roll = "";
            if (pass == null) pass = "";
            if (meal == null) meal = "MEAL";
            int adminId = 0;
            try { adminId = Integer.parseInt(adminStr); } catch (Exception ignored) {}

            roll = roll.toUpperCase().trim();
            String color = mealHex(meal);
            String html;

            if (!DatabaseManager.isRegistered(roll, adminId)) {
                html = resultPage("#ef4444", "ACCESS DENIED", roll,
                    "Roll number <b>" + roll + "</b> is not registered under this mess section.<br><br>" +
                    "Please contact your mess supervisor or administrator.", meal, adminId, false, false, "✕");
            } else {
                String name = DatabaseManager.validateStudent(roll, pass, adminId);
                if (name == null) {
                    html = resultPage("#f59e0b", "INVALID CREDENTIALS", roll,
                        "Incorrect password entered for roll number <b>" + roll + "</b>.<br>Please re-verify your password.",
                        meal, adminId, true, false, "⚠");
                } else if (DatabaseManager.hasMealToday(roll, adminId, meal)) {
                    html = resultPage("#f59e0b", "ALREADY CHECKED IN", roll,
                        "Welcome back, <b>" + name + "</b>!<br>Your <b>" + meal + "</b> has already been logged for today.",
                        meal, adminId, false, true, "ℹ");
                } else {
                    DatabaseManager.logMeal(roll, adminId, meal);
                    html = resultPage(color, "MEAL CONFIRMED", roll,
                        "Welcome, <b>" + name + "</b>!<br>Your <b>" + meal + "</b> entry has been recorded successfully.",
                        meal, adminId, false, true, "✓");
                }
            }
            sendHtml(ex, 200, html);
        }
    }

    // ── GET /records?roll=XXX&admin=1 ────────────────────────────────────────
    static class RecordsHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            String query    = ex.getRequestURI().getQuery();
            String roll     = parseParam(query, "roll");
            String adminStr = parseParam(query, "admin");
            if (roll == null) { ex.sendResponseHeaders(400,-1); return; }
            int adminId = 0;
            try { adminId = Integer.parseInt(adminStr); } catch (Exception ignored) {}
            roll = roll.toUpperCase().trim();

            java.util.List<String[]> recs = DatabaseManager.getMealRecords(roll, adminId);
            StringBuilder rows = new StringBuilder();
            for (String[] r : recs) {
                String badgeColor = mealHex(r[0]);
                rows.append("<tr>")
                    .append("<td><span class='badge' style='background:").append(badgeColor).append("'>").append(r[0]).append("</span></td>")
                    .append("<td>").append(r[1]).append("</td>")
                    .append("<td>").append(r[2]).append("</td></tr>");
            }
            if (recs.isEmpty()) {
                rows.append("<tr><td colspan='3' style='text-align:center;padding:24px;color:#94a3b8;'>No prior meal records found.</td></tr>");
            }

            String html = "<!DOCTYPE html><html lang='en'><head>" +
                "<meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1.0'>" +
                "<title>Meal History • " + roll + "</title><style>" +
                "*{box-sizing:border-box;margin:0;padding:0;}" +
                "body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;" +
                "background:#0f172a;color:#f8fafc;padding:20px;min-height:100vh;display:flex;align-items:center;justify-content:center;}" +
                ".card{background:#1e293b;border:1px solid #334155;border-radius:20px;padding:24px;width:100%;" +
                "max-width:440px;box-shadow:0 16px 36px rgba(0,0,0,0.4);}" +
                "h2{font-size:20px;color:#fff;margin-bottom:4px;font-weight:700;}" +
                ".roll-tag{color:#38bdf8;font-size:13px;font-weight:600;margin-bottom:18px;display:inline-block;" +
                "background:rgba(56,189,248,0.1);padding:4px 10px;border-radius:6px;border:1px solid rgba(56,189,248,0.2);}" +
                "table{width:100%;border-collapse:collapse;font-size:13px;margin-top:10px;}" +
                "th{background:#0f172a;color:#94a3b8;padding:10px 12px;text-align:left;font-size:11px;" +
                "text-transform:uppercase;letter-spacing:0.6px;border-radius:6px;}" +
                "td{padding:10px 12px;border-bottom:1px solid rgba(255,255,255,0.06);color:#e2e8f0;}" +
                ".badge{display:inline-block;color:#fff;font-size:11px;font-weight:700;padding:3px 8px;border-radius:4px;}" +
                ".back-btn{display:block;text-align:center;margin-top:20px;padding:12px;background:#334155;color:#fff;" +
                "text-decoration:none;border-radius:10px;font-size:13px;font-weight:600;}" +
                "</style></head><body>" +
                "<div class='card'>" +
                "<h2>Personal Meal History</h2>" +
                "<div class='roll-tag'>Roll No: " + roll + "</div>" +
                "<table><tr><th>Meal</th><th>Date</th><th>Time</th></tr>" +
                rows + "</table>" +
                "</div></body></html>";
            sendHtml(ex, 200, html);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String resultPage(String color, String heading, String roll,
                                     String message, String meal, int adminId,
                                     boolean showRetry, boolean showRecords, String symbol) {
        String retryLink = showRetry
            ? "<a href='/meal?type=" + meal + "&admin=" + adminId + "' class='action-btn' style='background:rgba(255,255,255,0.18);'>" +
              "Try Again</a>" : "";
        String recLink = showRecords
            ? "<a href='/records?roll=" + roll + "&admin=" + adminId + "' class='action-btn' style='background:rgba(255,255,255,0.18);'>" +
              "View Attendance History</a>" : "";

        return "<!DOCTYPE html><html lang='en'><head>" +
            "<meta charset='UTF-8'><meta name='viewport' content='width=device-width,initial-scale=1.0'>" +
            "<title>" + heading + "</title><style>*{box-sizing:border-box;margin:0;padding:0;}" +
            "body{font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;" +
            "background:#0f172a;display:flex;align-items:center;justify-content:center;min-height:100vh;padding:20px;}" +
            ".card{background:linear-gradient(145deg," + color + " 0%,#0f172a 140%);" +
            "border:1px solid rgba(255,255,255,0.15);border-radius:24px;width:100%;max-width:380px;" +
            "box-shadow:0 24px 48px rgba(0,0,0,0.5);padding:36px 26px;text-align:center;color:#fff;}" +
            ".icon-circle{width:64px;height:64px;border-radius:50%;background:rgba(255,255,255,0.2);" +
            "display:flex;align-items:center;justify-content:center;margin:0 auto 18px;font-size:28px;font-weight:bold;}" +
            "h2{font-size:22px;font-weight:800;letter-spacing:-0.3px;margin-bottom:12px;}" +
            "p{font-size:14px;line-height:1.6;color:rgba(255,255,255,0.9);margin-bottom:20px;}" +
            ".action-btn{display:block;margin-top:10px;color:#fff;text-decoration:none;padding:12px;border-radius:12px;" +
            "font-size:14px;font-weight:700;backdrop-filter:blur(8px);transition:opacity 0.2s;}" +
            ".action-btn:active{opacity:0.8;}" +
            "</style></head><body><div class='card'>" +
            "<div class='icon-circle'>" + symbol + "</div>" +
            "<h2>" + heading + "</h2>" +
            "<p>" + message + "</p>" +
            retryLink + recLink +
            "</div></body></html>";
    }

    static String parseParam(String query, String key) {
        if (query == null) return null;
        for (String part : query.split("&")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                try { return URLDecoder.decode(kv[1], "UTF-8"); }
                catch (Exception e) { return kv[1]; }
            }
        }
        return null;
    }

    static void sendHtml(HttpExchange ex, int code, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        ex.sendResponseHeaders(code, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }

    static String mealHex(String meal) {
        switch (meal.toUpperCase()) {
            case "BREAKFAST": return "#f59e0b"; // Warm Amber
            case "LUNCH":     return "#10b981"; // Vibrant Emerald
            case "SNACKS":    return "#0ea5e9"; // Sky Blue
            case "DINNER":    return "#8b5cf6"; // Royal Violet
            default:          return "#6366f1"; // Indigo
        }
    }

    static String mealIcon(String meal) {
        switch (meal.toUpperCase()) {
            case "BREAKFAST": return "🌅";
            case "LUNCH":     return "☀️";
            case "SNACKS":    return "☕";
            case "DINNER":    return "🌙";
            default:          return "🍽️";
        }
    }
}
