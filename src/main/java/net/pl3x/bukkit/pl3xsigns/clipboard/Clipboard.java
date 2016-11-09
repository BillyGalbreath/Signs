package net.pl3x.bukkit.pl3xsigns.clipboard;

import org.bukkit.block.Sign;

public class Clipboard {
    private final String[] lines = new String[4];

    public Clipboard(Sign sign) {
        this(sign.getLines());
    }

    public Clipboard(String[] lines) {
        System.arraycopy(lines, 0, this.lines, 0, 4);
    }

    public String getLine(int i) {
        if (lines == null || lines.length < i || lines[i] == null) {
            return "";
        }
        return lines[i];
    }

    public String[] getLines() {
        return lines;
    }
}
