package secondBrain.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class that manages language switching for the application.
 * Supports multiple languages with string lookups.
 */
public class LanguageManager {
	private static LanguageManager instance;
	private String currentLanguage = "English";
	private Map<String, Map<String, String>> translations;
	
	private LanguageManager() {
		initializeTranslations();
	}

	public static synchronized LanguageManager getInstance() {
		if (instance == null) {
			instance = new LanguageManager();
		}
		return instance;
	}

	private void initializeTranslations() {
		translations = new HashMap<>();
		
		Map<String, String> english = new HashMap<>();
		english.put("settings.title", "Settings ⚙️");
		english.put("settings.darkmode.on", "Turn off dark mode");
		english.put("settings.darkmode.off", "Turn on dark mode");
		english.put("settings.delete.account", "Delete account");
		english.put("settings.logout", "Log out");
		english.put("settings.language", "Change a language");
		english.put("settings.about", "About us");
		english.put("settings.delete.confirm", "Are you sure you want to delete your account?\nThis action cannot be undone.");
		english.put("settings.delete.title", "Delete Account");
		english.put("settings.delete.success", "Your account has been successfully deleted.");
		english.put("settings.delete.failed", "Failed to delete account. Please try again.");
		english.put("settings.logout.confirm", "Are you sure you want to log out?");
		english.put("settings.logout.title", "Log Out");
		english.put("settings.theme.changed", "Theme changed successfully.");
		english.put("settings.theme.title", "Theme Changed");
		english.put("settings.language.changed", "Language changed to");
		english.put("settings.language.title", "Language Changed");
		english.put("settings.about.title", "About Us");
		english.put("about.app.name", "SecondBrain KVD");
		english.put("about.version", "Version");
		english.put("about.description", "A powerful knowledge management system\nfor organizing and accessing your information.");
		english.put("about.copyright", "© 2026 SecondBrain. All rights reserved.\n\nFor support, visit: www.secondbrain.com");
		english.put("error.title", "Error");
		english.put("error.database", "Database error");
		english.put("error.language", "Error changing language");
		
		translations.put("English", english);
		
		Map<String, String> spanish = new HashMap<>();
		spanish.put("settings.title", "Configuración ⚙️");
		spanish.put("settings.darkmode.on", "Desactivar modo oscuro");
		spanish.put("settings.darkmode.off", "Activar modo oscuro");
		spanish.put("settings.delete.account", "Eliminar cuenta");
		spanish.put("settings.logout", "Cerrar sesión");
		spanish.put("settings.language", "Cambiar idioma");
		spanish.put("settings.about", "Acerca de nosotros");
		spanish.put("settings.delete.confirm", "¿Está seguro de que desea eliminar su cuenta?\nEsta acción no se puede deshacer.");
		spanish.put("settings.delete.title", "Eliminar Cuenta");
		spanish.put("settings.delete.success", "Su cuenta ha sido eliminada exitosamente.");
		spanish.put("settings.delete.failed", "Error al eliminar la cuenta. Por favor, intente de nuevo.");
		spanish.put("settings.logout.confirm", "¿Está seguro de que desea cerrar sesión?");
		spanish.put("settings.logout.title", "Cerrar Sesión");
		spanish.put("settings.theme.changed", "Tema cambiado exitosamente.");
		spanish.put("settings.theme.title", "Tema Cambiado");
		spanish.put("settings.language.changed", "Idioma cambiado a");
		spanish.put("settings.language.title", "Idioma Cambiado");
		spanish.put("settings.about.title", "Acerca de");
		spanish.put("about.app.name", "SecondBrain KVD");
		spanish.put("about.version", "Versión");
		spanish.put("about.description", "Un poderoso sistema de gestión del conocimiento\npara organizar y acceder a su información.");
		spanish.put("about.copyright", "© 2026 SecondBrain. Todos los derechos reservados.\n\nPara soporte, visite: www.secondbrain.com");
		spanish.put("error.title", "Error");
		spanish.put("error.database", "Error de base de datos");
		spanish.put("error.language", "Error al cambiar el idioma");
		
		translations.put("Español", spanish);
		
		Map<String, String> french = new HashMap<>();
		french.put("settings.title", "Paramètres ⚙️");
		french.put("settings.darkmode.on", "Désactiver le mode sombre");
		french.put("settings.darkmode.off", "Activer le mode sombre");
		french.put("settings.delete.account", "Supprimer le compte");
		french.put("settings.logout", "Se déconnecter");
		french.put("settings.language", "Changer de langue");
		french.put("settings.about", "À propos de nous");
		french.put("settings.delete.confirm", "Êtes-vous sûr de vouloir supprimer votre compte?\nCette action ne peut pas être annulée.");
		french.put("settings.delete.title", "Supprimer le Compte");
		french.put("settings.delete.success", "Votre compte a été supprimé avec succès.");
		french.put("settings.delete.failed", "Échec de la suppression du compte. Veuillez réessayer.");
		french.put("settings.logout.confirm", "Êtes-vous sûr de vouloir vous déconnecter?");
		french.put("settings.logout.title", "Se Déconnecter");
		french.put("settings.theme.changed", "Thème changé avec succès.");
		french.put("settings.theme.title", "Thème Modifié");
		french.put("settings.language.changed", "Langue changée en");
		french.put("settings.language.title", "Langue Modifiée");
		french.put("settings.about.title", "À Propos");
		french.put("about.app.name", "SecondBrain KVD");
		french.put("about.version", "Version");
		french.put("about.description", "Un puissant système de gestion des connaissances\npour organiser et accéder à vos informations.");
		french.put("about.copyright", "© 2026 SecondBrain. Tous droits réservés.\n\nPour le support, visitez: www.secondbrain.com");
		french.put("error.title", "Erreur");
		french.put("error.database", "Erreur de base de données");
		french.put("error.language", "Erreur lors du changement de langue");
		
		translations.put("Français", french);
		
		Map<String, String> german = new HashMap<>();
		german.put("settings.title", "Einstellungen ⚙️");
		german.put("settings.darkmode.on", "Dunkelmodus ausschalten");
		german.put("settings.darkmode.off", "Dunkelmodus einschalten");
		german.put("settings.delete.account", "Konto löschen");
		german.put("settings.logout", "Abmelden");
		german.put("settings.language", "Sprache ändern");
		german.put("settings.about", "Über uns");
		german.put("settings.delete.confirm", "Sind Sie sicher, dass Sie Ihr Konto löschen möchten?\nDiese Aktion kann nicht rückgängig gemacht werden.");
		german.put("settings.delete.title", "Konto Löschen");
		german.put("settings.delete.success", "Ihr Konto wurde erfolgreich gelöscht.");
		german.put("settings.delete.failed", "Fehler beim Löschen des Kontos. Bitte versuchen Sie es erneut.");
		german.put("settings.logout.confirm", "Sind Sie sicher, dass Sie sich abmelden möchten?");
		german.put("settings.logout.title", "Abmelden");
		german.put("settings.theme.changed", "Design erfolgreich geändert.");
		german.put("settings.theme.title", "Design Geändert");
		german.put("settings.language.changed", "Sprache geändert zu");
		german.put("settings.language.title", "Sprache Geändert");
		german.put("settings.about.title", "Über");
		german.put("about.app.name", "SecondBrain KVD");
		german.put("about.version", "Version");
		german.put("about.description", "Ein leistungsstarkes Wissensmanagementsystem\nzum Organisieren und Zugreifen auf Ihre Informationen.");
		german.put("about.copyright", "© 2026 SecondBrain. Alle Rechte vorbehalten.\n\nFür Unterstützung besuchen Sie: www.secondbrain.com");
		german.put("error.title", "Fehler");
		german.put("error.database", "Datenbankfehler");
		german.put("error.language", "Fehler beim Ändern der Sprache");
		
		translations.put("Deutsch", german);
		
		Map<String, String> japanese = new HashMap<>();
		japanese.put("settings.title", "設定 ⚙️");
		japanese.put("settings.darkmode.on", "ダークモードをオフにする");
		japanese.put("settings.darkmode.off", "ダークモードをオンにする");
		japanese.put("settings.delete.account", "アカウントを削除");
		japanese.put("settings.logout", "ログアウト");
		japanese.put("settings.language", "言語を変更");
		japanese.put("settings.about", "について");
		japanese.put("settings.delete.confirm", "本当にアカウントを削除しますか?\nこのアクションは元に戻せません。");
		japanese.put("settings.delete.title", "アカウント削除");
		japanese.put("settings.delete.success", "アカウントが正常に削除されました。");
		japanese.put("settings.delete.failed", "アカウントの削除に失敗しました。もう一度お試しください。");
		japanese.put("settings.logout.confirm", "本当にログアウトしますか?");
		japanese.put("settings.logout.title", "ログアウト");
		japanese.put("settings.theme.changed", "テーマが正常に変更されました。");
		japanese.put("settings.theme.title", "テーマ変更");
		japanese.put("settings.language.changed", "言語が変更されました");
		japanese.put("settings.language.title", "言語変更");
		japanese.put("settings.about.title", "について");
		japanese.put("about.app.name", "SecondBrain KVD");
		japanese.put("about.version", "バージョン");
		japanese.put("about.description", "情報を整理・管理するための\n強力なナレッジマネジメントシステム。");
		japanese.put("about.copyright", "© 2026 SecondBrain. すべての権利を保有しています。\n\nサポートについては、www.secondbrain.com をご覧ください。");
		japanese.put("error.title", "エラー");
		japanese.put("error.database", "データベースエラー");
		japanese.put("error.language", "言語の変更中にエラーが発生しました");
		
		translations.put("日本語", japanese);
		
		Map<String, String> latvian = new HashMap<>();
		latvian.put("settings.title", "Iestatījumi ⚙️");
		latvian.put("settings.darkmode.on", "Ieslēgt tumšu režīmu");
		latvian.put("settings.darkmode.off", "Izslēgt tumšu režīmu");
		latvian.put("settings.delete.account", "Izdzsēst kontu");
		latvian.put("settings.logout", "Iziet no konta");
		latvian.put("settings.language", "Pamainīt valodu");
		latvian.put("settings.about", "Par mums");
		latvian.put("settings.delete.confirm", "Vai tiešām vēlaties dzēst savu kontu?\nŠo darbību nevar atsaukt.");
		latvian.put("settings.delete.title", "Izdzēst kontu");
		latvian.put("settings.delete.success", "Jūsu konts tika veiksmīgi izdzēsts.");
		latvian.put("settings.delete.failed", "Neizdevās izdzēst kontu. Lūdzu, mēģinat vēlreiz.");
		latvian.put("settings.logout.confirm", "Vai tiešām vēlaties iziet no konta?");
		latvian.put("settings.logout.title", "Iziet no konta");
		latvian.put("settings.theme.changed", "Tēma veiksmīgi mainīta.");
		latvian.put("settings.theme.title", "Tēma mainīta.");
		latvian.put("settings.language.changed", "Valoda mainīta uz");
		latvian.put("settings.language.title", "Valoda mainīta.");
		latvian.put("settings.about.title", "Par mums");
		latvian.put("about.app.name", "SecondBrain KVD");
		latvian.put("about.version", "Versija");
		latvian.put("about.description", "Jaudīga zināšanu pārvaldības sistēma\n jūsu informācijas organizēšanai un piekļuvei tai.");
		latvian.put("about.copyright", "© 2026 SecondBrain. Visas tiesības aizsargātas.\n\nLai saņemtu atbalstu, apmeklējiet vietni: www.secondbrain.com");
		latvian.put("error.title", "Kļūda.");
		latvian.put("error.database", "Datubāzes kļūda.");
		latvian.put("error.language", "Kļūda mainot valodu.");
		
		translations.put("Latviešu", latvian);
	}

	public String getString(String key) {
		Map<String, String> currentTranslations = translations.get(currentLanguage);
		if (currentTranslations == null) {
			currentTranslations = translations.get("English");
		}
		return currentTranslations.getOrDefault(key, key);
	}
	

	public void setLanguage(String language) {
		if (translations.containsKey(language)) {
			this.currentLanguage = language;
		} else {
			this.currentLanguage = "English";
		}
	}
	
	public String getCurrentLanguage() {
		return currentLanguage;
	}
}
