package cop4331.taskflow.settings;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.JSONObject;

/**
 * Manages application theme (Light/Dark mode).
 * 
 * <p>This singleton service manages theme preferences and applies them
 * to the Swing UI components. Theme preferences are persisted to a JSON file.
 * 
 * <p><b>Postconditions:</b> Theme changes are applied to the UI and saved to disk.
 * 
 * @author TaskFlow Team
 * @version 1.0
 */
public class ThemeManager {

    /**
     * Theme modes available in the application.
     */
    public enum Theme {
        LIGHT,
        DARK
    }

    private static final ThemeManager INSTANCE = new ThemeManager();
    private static final Path THEME_FILE = Paths.get("taskflow_theme.json");
    private Theme currentTheme = Theme.LIGHT;

    /**
     * Private constructor for singleton pattern.
     */
    private ThemeManager() {
        loadTheme();
    }

    /**
     * Gets the singleton instance of ThemeManager.
     * 
     * @return the ThemeManager instance (never null)
     */
    public static ThemeManager getInstance() {
        return INSTANCE;
    }

    /**
     * Gets the current theme.
     * 
     * @return the current theme (never null)
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Toggles between light and dark theme.
     * 
     * <p><b>Postconditions:</b> Theme is toggled and saved to disk
     */
    public void toggleTheme() {
        currentTheme = (currentTheme == Theme.LIGHT) ? Theme.DARK : Theme.LIGHT;
        applyTheme();
        saveTheme();
    }

    /**
     * Sets the theme to the specified mode.
     * 
     * <p><b>Preconditions:</b> theme must be non-null
     * 
     * <p><b>Postconditions:</b> Theme is set and saved to disk
     * 
     * @param theme the theme to set (required, non-null)
     * @throws IllegalArgumentException if theme is null
     */
    public void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme must be non-null");
        }
        currentTheme = theme;
        applyTheme();
        saveTheme();
    }

    /**
     * Applies the current theme to the UI.
     * 
     * <p><b>Postconditions:</b> UIManager properties are updated with theme colors
     */
    public void applyTheme() {
        if (currentTheme == Theme.DARK) {
            // Dark theme colors
            UIManager.put("Panel.background", new Color(45, 45, 45));
            UIManager.put("Button.background", new Color(60, 60, 60));
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Label.foreground", Color.WHITE);
            UIManager.put("TextField.background", new Color(60, 60, 60));
            UIManager.put("TextField.foreground", Color.WHITE);
            UIManager.put("TextArea.background", new Color(60, 60, 60));
            UIManager.put("TextArea.foreground", Color.WHITE);
            UIManager.put("Table.background", new Color(50, 50, 50));
            UIManager.put("Table.foreground", Color.WHITE);
            UIManager.put("TableHeader.background", new Color(40, 40, 40));
            UIManager.put("TableHeader.foreground", Color.WHITE);
        } else {
            // Light theme (default Swing colors)
            UIManager.put("Panel.background", UIManager.getColor("Panel.background"));
            UIManager.put("Button.background", UIManager.getColor("Button.background"));
            UIManager.put("Button.foreground", UIManager.getColor("Button.foreground"));
            UIManager.put("Label.foreground", UIManager.getColor("Label.foreground"));
            UIManager.put("TextField.background", UIManager.getColor("TextField.background"));
            UIManager.put("TextField.foreground", UIManager.getColor("TextField.foreground"));
            UIManager.put("TextArea.background", UIManager.getColor("TextArea.background"));
            UIManager.put("TextArea.foreground", UIManager.getColor("TextArea.foreground"));
            UIManager.put("Table.background", UIManager.getColor("Table.background"));
            UIManager.put("Table.foreground", UIManager.getColor("Table.foreground"));
            UIManager.put("TableHeader.background", UIManager.getColor("TableHeader.background"));
            UIManager.put("TableHeader.foreground", UIManager.getColor("TableHeader.foreground"));
        }
    }

    /**
     * Saves the current theme preference to disk.
     * 
     * <p><b>Postconditions:</b> Theme preference is written to JSON file
     */
    private void saveTheme() {
        try {
            JSONObject json = new JSONObject();
            json.put("theme", currentTheme.name());
            Files.writeString(THEME_FILE, json.toString(2));
        } catch (IOException e) {
            // Silently fail - theme will default to LIGHT
        }
    }

    /**
     * Loads the theme preference from disk.
     * 
     * <p><b>Postconditions:</b> Theme is loaded from JSON file if it exists
     */
    private void loadTheme() {
        try {
            if (Files.exists(THEME_FILE)) {
                String content = Files.readString(THEME_FILE);
                JSONObject json = new JSONObject(content);
                String themeName = json.getString("theme");
                currentTheme = Theme.valueOf(themeName);
            }
        } catch (Exception e) {
            // Default to LIGHT theme if loading fails
            currentTheme = Theme.LIGHT;
        }
    }
}

