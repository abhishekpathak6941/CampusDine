import java.awt.*;
import java.util.List;

/**
 * Modern UI Helper and Design Theme for Smart Mess Card System.
 * Developed by Abhishek Pathak (https://github.com/abhishekpathak6941)
 */
public class UIHelper {

    // ── Primary Theme Palette (Slate & Modern Accents) ──────────────────────
    public static final Color COL_NAVY    = new Color(15,  23,  42);  // Slate 900
    public static final Color COL_HEADER  = new Color(30,  41,  59);  // Slate 800
    public static final Color COL_GREEN   = new Color(16, 185, 129);  // Emerald 500
    public static final Color COL_RED     = new Color(239,  68,  68);  // Red 500
    public static final Color COL_ORANGE  = new Color(245, 158,  11);  // Amber 500
    public static final Color COL_BLUE    = new Color(14, 165, 233);  // Sky 500
    public static final Color COL_PURPLE  = new Color(139,  92, 246);  // Violet 500
    public static final Color COL_BG      = new Color(241, 245, 249);  // Slate 100
    public static final Color COL_WHITE   = Color.WHITE;
    public static final Color COL_DARK    = new Color(30,  41,  59);  // Slate 800
    public static final Color COL_MUTED   = new Color(100, 116, 139);  // Slate 500
    public static final Color COL_BORDER  = new Color(226, 232, 240);  // Slate 200

    public static Color mealColor(String meal) {
        switch (meal.toUpperCase()) {
            case "BREAKFAST": return COL_ORANGE;
            case "LUNCH":     return COL_GREEN;
            case "SNACKS":    return COL_BLUE;
            case "DINNER":    return COL_PURPLE;
            default:          return COL_NAVY;
        }
    }

    public static FlatButton styledButton(String text, Color bg) {
        FlatButton b = new FlatButton(text, bg, Color.WHITE);
        b.setPreferredSize(new Dimension(175, 40));
        return b;
    }

    public static FlatButton styledButton(String text, Color bg, Dimension size) {
        FlatButton b = new FlatButton(text, bg, Color.WHITE);
        b.setPreferredSize(size);
        b.setSize(size);
        return b;
    }

    public static Label headingLabel(String text) {
        Label l = new Label(text, Label.CENTER);
        l.setFont(new Font("Arial", Font.BOLD, 20));
        l.setForeground(COL_DARK);
        return l;
    }

    public static TextField styledTextField(int cols) {
        TextField tf = new TextField(cols);
        tf.setFont(new Font("Arial", Font.PLAIN, 14));
        return tf;
    }

    public static Panel headerPanel(String title, String subtitle) {
        int rows = subtitle.isEmpty() ? 1 : 2;
        Panel p = new Panel(new GridLayout(rows, 1));
        p.setBackground(COL_HEADER);
        p.setPreferredSize(new Dimension(900, subtitle.isEmpty() ? 56 : 74));

        Label t = new Label(title, Label.CENTER);
        t.setFont(new Font("Arial", Font.BOLD, 22));
        t.setForeground(Color.WHITE);
        p.add(t);

        if (!subtitle.isEmpty()) {
            Label s = new Label(subtitle, Label.CENTER);
            s.setFont(new Font("Arial", Font.PLAIN, 13));
            s.setForeground(new Color(203, 213, 225));
            p.add(s);
        }
        return p;
    }

    public static void centreFrame(Frame f) {
        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation((scr.width - f.getWidth()) / 2, (scr.height - f.getHeight()) / 2);
    }

    public static class ImageCanvas extends Canvas {
        private java.awt.image.BufferedImage img;
        private final int W, H;
        public ImageCanvas(int w, int h) {
            W = w; H = h;
            setPreferredSize(new Dimension(w, h));
            setSize(w, h);
        }
        public void setImage(java.awt.image.BufferedImage i) { img = i; repaint(); }
        public void paint(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, W, H);
            if (img != null) g.drawImage(img, 0, 0, W, H, this);
            g.setColor(new Color(203, 213, 225));
            g.drawRect(0, 0, W - 1, H - 1);
        }
    }

    public static TextArea recordTextArea() {
        TextArea ta = new TextArea("", 10, 60, TextArea.SCROLLBARS_VERTICAL_ONLY);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setEditable(false);
        ta.setBackground(new Color(248, 250, 252));
        return ta;
    }

    public static String formatMealRecords(List<String[]> records) {
        if (records.isEmpty()) return "   No meal records found.";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  %-14s  %-14s  %-12s%n", "MEAL", "DATE", "TIME"));
        sb.append("  " + "─".repeat(46) + "\n");
        for (String[] r : records)
            sb.append(String.format("  %-14s  %-14s  %-12s%n", r[0], r[1], r[2]));
        return sb.toString();
    }

    public static String formatAllLogs(List<String[]> logs) {
        if (logs.isEmpty()) return "   No meal logs found.";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("  %-14s  %-20s  %-12s  %-12s  %-10s%n",
                "ROLL NUMBER", "STUDENT NAME", "MEAL TYPE", "DATE", "TIME"));
        sb.append("  " + "─".repeat(76) + "\n");
        for (String[] r : logs)
            sb.append(String.format("  %-14s  %-20s  %-12s  %-12s  %-10s%n",
                    r[0], r[1], r[2], r[3], r[4]));
        return sb.toString();
    }
}
