package secondBrain.data;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

/**
 * Singleton class that manages theme switching across all GUI windows.
 * Supports dark mode and light mode with real-time updates to all open windows.
 */
public class ThemeManager {
	private static ThemeManager instance;
	private boolean darkModeEnabled = false;
	private WeakHashMap<JFrame, Boolean> managedFrames = new WeakHashMap<>();
	
	// Color scheme for dark mode
	private static final Color DARK_BACKGROUND = new Color(30, 30, 30);
	private static final Color DARK_FOREGROUND = new Color(240, 240, 240);
	private static final Color DARK_PANEL = new Color(45, 45, 45);
	private static final Color DARK_BUTTON = new Color(60, 60, 60);
	
	// Color scheme for light mode
	private static final Color LIGHT_BACKGROUND = new Color(255, 255, 255);
	private static final Color LIGHT_FOREGROUND = new Color(0, 0, 0);
	private static final Color LIGHT_PANEL = new Color(240, 240, 240);
	
	private ThemeManager() {
		this.darkModeEnabled = UserPreferences.getInstance().isDarkModeEnabled();
	}
	
	/**
	 * Get singleton instance of ThemeManager
	 */
	public static synchronized ThemeManager getInstance() {
		if (instance == null) {
			instance = new ThemeManager();
		}
		return instance;
	}
	
	/**
	 * Register a JFrame to be managed by the theme manager
	 */
	public void registerFrame(JFrame frame) {
		managedFrames.put(frame, true);
	}
	
	/**
	 * Apply current theme to a specific JFrame
	 */
	public void applyTheme(JFrame frame) {
		registerFrame(frame);
		if (darkModeEnabled) {
			applyDarkMode(frame);
		} else {
			applyLightMode(frame);
		}
	}
	
	/**
	 * Set dark mode and update all registered frames
	 */
	public void setDarkMode(boolean enabled) {
		this.darkModeEnabled = enabled;
		
		// Update all managed frames
		for (JFrame frame : managedFrames.keySet()) {
			if (frame != null && frame.isDisplayable()) {
				if (darkModeEnabled) {
					applyDarkMode(frame);
				} else {
					applyLightMode(frame);
				}
			}
		}
		
		// Save preference
		UserPreferences.getInstance().setDarkModeEnabled(enabled);
	}
	
	/**
	 * Apply dark mode colors to all components in a frame
	 */
	private void applyDarkMode(JFrame frame) {
		JPanel contentPane = (JPanel) frame.getContentPane();
		contentPane.setBackground(DARK_BACKGROUND);
		contentPane.setForeground(DARK_FOREGROUND);
		
		applyThemeToComponents(contentPane, true);
		frame.repaint();
	}
	
	/**
	 * Apply light mode colors to all components in a frame
	 */
	private void applyLightMode(JFrame frame) {
		JPanel contentPane = (JPanel) frame.getContentPane();
		contentPane.setBackground(LIGHT_BACKGROUND);
		contentPane.setForeground(LIGHT_FOREGROUND);
		
		applyThemeToComponents(contentPane, false);
		frame.repaint();
	}
	
	/**
	 * Recursively apply theme to all components
	 */
	private void applyThemeToComponents(Component component, boolean isDarkMode) {
		if (component instanceof JLabel) {
			component.setForeground(isDarkMode ? DARK_FOREGROUND : LIGHT_FOREGROUND);
		} else if (component instanceof JButton) {
			JButton button = (JButton) component;
			if (isDarkMode) {
				button.setBackground(DARK_BUTTON);
				button.setForeground(DARK_FOREGROUND);
			} else {
				button.setBackground(UIManager.getColor("Button.background"));
				button.setForeground(LIGHT_FOREGROUND);
			}
			button.setOpaque(true);
		} else if (component instanceof JToggleButton) {
			JToggleButton toggleButton = (JToggleButton) component;
			if (isDarkMode) {
				toggleButton.setBackground(DARK_BUTTON);
				toggleButton.setForeground(DARK_FOREGROUND);
			} else {
				toggleButton.setBackground(UIManager.getColor("Button.background"));
				toggleButton.setForeground(LIGHT_FOREGROUND);
			}
			toggleButton.setOpaque(true);
		} else if (component instanceof JComboBox) {
			component.setBackground(isDarkMode ? DARK_BUTTON : LIGHT_BACKGROUND);
			component.setForeground(isDarkMode ? DARK_FOREGROUND : LIGHT_FOREGROUND);
		} else if (component instanceof JPanel) {
			component.setBackground(isDarkMode ? DARK_PANEL : LIGHT_PANEL);
			component.setForeground(isDarkMode ? DARK_FOREGROUND : LIGHT_FOREGROUND);
		}
		
		if (component instanceof JPanel) {
			for (Component child : ((JPanel) component).getComponents()) {
				applyThemeToComponents(child, isDarkMode);
			}
		}
	}
	
	/**
	 * Check if dark mode is currently enabled
	 */
	public boolean isDarkModeEnabled() {
		return darkModeEnabled;
	}
	
	/**
	 * Clear all registered frames (useful on logout)
	 */
	public void clearFrames() {
		managedFrames.clear();
	}
}
