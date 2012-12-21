/**
 * 
 */
package ws1213.ande;

import android.app.Activity;
import android.net.Uri;

/**
 * @author Sascha Hayton
 *
 */
public abstract class AppActivity extends Activity
	{
	 public static final String SETTINGS= "settings";
	 public static final String SETTINGS_ACTIVECAR= "car";
	 public static final String SETTINGS_LOCATION = "location";
	 
	 
	 public static final Uri DEFAULT_CAR_IMAGE_URI = Uri.parse("android.resource://ws1213.ande/drawable/car"); 
	 
	}
