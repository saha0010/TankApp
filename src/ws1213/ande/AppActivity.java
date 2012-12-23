/**
 * 
 */
package ws1213.ande;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

/**
 * @author Sascha Hayton
 *
 */
public abstract class AppActivity extends Activity
	{
	 public static final String SETTINGS= "settings";
	 public static final String SETTINGS_ACTIVECAR= "car";
	 public static final String SETTINGS_ACTIVECAR_ID ="carId";
	 public static final String SETTINGS_LOCATION = "location";
	 
	 public static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";
	 
	 public static final Uri DEFAULT_CAR_IMAGE_URI = Uri.parse("android.resource://ws1213.ande/drawable/car"); 
	 
	 public static final String DEBUG_ERR = "error";
	}
