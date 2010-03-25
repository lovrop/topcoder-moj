/** 
 * Preferences.java
 *
 * Description:		Preferences class for FileEdit
 * @author			Tim "Pops" Roberts
 * @version			3.0
 */

package moj;
import com.topcoder.client.contestApplet.common.LocalPreferences;

public class Preferences {

	private static LocalPreferences pref = LocalPreferences.getInstance();
	
	public final static String KEY_TARGETCOMPILER = "moj.config.targetcompiler";
	public final static String KEY_NUMPLACEHOLDERS = "moj.config.numplaceholders";
	public final static String KEY_LANGUAGESWITCHWORKAROUND = "moj.config.languageswitchworkaround";
	public final static String KEY_ENABLEJAVASUPPORT = "moj.config.enablejavasupport";

	public final static String TARGETCOMPILER_GCC = "GCC";
	public final static String TARGETCOMPILER_VC = "Visual C++";
	
	public Preferences() { 
	}
	
	public String getTargetCompiler() {
		return getStringProperty(KEY_TARGETCOMPILER, TARGETCOMPILER_GCC);
	}
	
	public void setTargetCompiler(String compiler) {
		pref.setProperty(KEY_TARGETCOMPILER, compiler);
	}

	public int getNumPlaceholders() {
		return getIntegerProperty(KEY_NUMPLACEHOLDERS, 3);
	}

	public void setNumPlaceholders(int numPlaceholders) {
		pref.setProperty(KEY_NUMPLACEHOLDERS, "" + numPlaceholders);
	}
	
	public boolean getEnableJavaSupport() {
		return getBooleanProperty(KEY_ENABLEJAVASUPPORT, true);
	}
	
	public void setEnableJavaSupport(boolean enabled) {
		pref.setProperty(KEY_ENABLEJAVASUPPORT, enabled ? "true" : "false");
	}
	
	public boolean getLanguageSwitchWorkaround() {
		return getBooleanProperty(KEY_LANGUAGESWITCHWORKAROUND, true);
	}
	
	public void setLanguageSwitchWorkaround(boolean enabled) {
		pref.setProperty(KEY_LANGUAGESWITCHWORKAROUND, enabled ? "true" : "false");
	}
	
	protected String getStringProperty(String key, String defaultValue) {
		String value = pref.getProperty(key);
		return value==null || value.equals("") ? defaultValue : value;
	}
	
	protected boolean getBooleanProperty(String key, boolean defaultValue) {
		String value = pref.getProperty(key);
		return value==null || value.equals("") ? defaultValue : value.equals("true");
	}

	protected int getIntegerProperty(String key, int defaultValue) {
		String value = pref.getProperty(key);
		if(value==null || value.equals("")) return defaultValue;
		
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public void save() throws java.io.IOException { 
		pref.savePreferences(); 
	}
	
}
