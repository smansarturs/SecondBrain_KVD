package secondBrain.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton class that manages user preferences like dark mode, language, etc.
 * Saves preferences to a local properties file.
 */
public class UserPreferences {
	private static UserPreferences instance;
	private Properties preferences;
	private File preferencesFile;
	private boolean darkModeEnabled = false;
	private String language = "English";
	private int currentUserId = -1;
	
	private static final String PREFS_FILENAME = "user_preferences.properties";
	private static final String DARK_MODE_KEY = "dark_mode";
	private static final String LANGUAGE_KEY = "language";
	private static final String USER_ID_KEY = "user_id";
	
	private UserPreferences() {
		initializePreferences();
	}
	
	/**
	 * Get singleton instance of UserPreferences
	 */
	public static synchronized UserPreferences getInstance() {
		if (instance == null) {
			instance = new UserPreferences();
		}
		return instance;
	}
	
	/**
	 * Initialize preferences from file or create new
	 */
	private void initializePreferences() {
		preferences = new Properties();
		preferencesFile = new File(System.getProperty("user.home"), PREFS_FILENAME);
		
		// Load existing preferences if file exists
		if (preferencesFile.exists()) {
			try (FileInputStream fis = new FileInputStream(preferencesFile)) {
				preferences.load(fis);
				darkModeEnabled = Boolean.parseBoolean(preferences.getProperty(DARK_MODE_KEY, "false"));
				language = preferences.getProperty(LANGUAGE_KEY, "English");
				currentUserId = Integer.parseInt(preferences.getProperty(USER_ID_KEY, "-1"));
			} catch (IOException e) {
				System.err.println("Error loading preferences: " + e.getMessage());
			}
		} else {
			// Set default preferences
			preferences.setProperty(DARK_MODE_KEY, "false");
			preferences.setProperty(LANGUAGE_KEY, "English");
			savePreferences();
		}
	}
	
	/**
	 * Save preferences to file
	 */
	private void savePreferences() {
		try (FileOutputStream fos = new FileOutputStream(preferencesFile)) {
			preferences.setProperty(DARK_MODE_KEY, String.valueOf(darkModeEnabled));
			preferences.setProperty(LANGUAGE_KEY, language);
			preferences.setProperty(USER_ID_KEY, String.valueOf(currentUserId));
			preferences.store(fos, "SecondBrain User Preferences");
		} catch (IOException e) {
			System.err.println("Error saving preferences: " + e.getMessage());
		}
	}
	
	/**
	 * Check if dark mode is enabled
	 */
	public boolean isDarkModeEnabled() {
		return darkModeEnabled;
	}
	
	/**
	 * Set dark mode enabled/disabled
	 */
	public void setDarkModeEnabled(boolean enabled) {
		this.darkModeEnabled = enabled;
		savePreferences();
	}
	
	/**
	 * Get the current language
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * Set the current language
	 */
	public void setLanguage(String language) {
		this.language = language;
		savePreferences();
	}
	
	/**
	 * Get the current user ID
	 */
	public int getCurrentUserId() {
		return currentUserId;
	}
	
	/**
	 * Set the current user ID
	 */
	public void setCurrentUserId(int userId) {
		this.currentUserId = userId;
		savePreferences();
	}
	
	/**
	 * Clear user session (on logout)
	 */
	public void clearUserSession() {
		this.currentUserId = -1;
		savePreferences();
	}
	
	/**
	 * Clear all user data (on account deletion)
	 */
	public void clearUserData() {
		this.currentUserId = -1;
		this.darkModeEnabled = false;
		this.language = "English";
		preferences.clear();
		preferences.setProperty(DARK_MODE_KEY, "false");
		preferences.setProperty(LANGUAGE_KEY, "English");
		savePreferences();
	}
	
	/**
	 * Reset all preferences to default
	 */
	public void resetPreferences() {
		preferences.clear();
		darkModeEnabled = false;
		language = "English";
		currentUserId = -1;
		savePreferences();
	}
}
