/**
 * 
 */
package ws1213.ande;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

/**
 * @author Sa
 * 
 */
public class SettingsActivity extends AppActivity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		TabHost host = (TabHost)findViewById(android.R.id.tabhost);
		host.setup();
		
		TabSpec carTab = host.newTabSpec("carTab");
		 ((TabSpec) carTab).setIndicator(getResources().getString(R.string.settings_car),
							 getResources().getDrawable(R.drawable.car));
		 carTab.setContent(R.id.scrollview_settings_cartab);
		 

		 TabSpec locationTab = host.newTabSpec("locationTab");
		 locationTab.setIndicator(getResources().getString(R.string.settings_location),
				 getResources().getDrawable(R.drawable.map));
		 locationTab.setContent(R.id.scrollview_settings_locationtab);
		 
		 host.addTab(carTab);
		 host.addTab(locationTab);
		 
		 host.setCurrentTabByTag("carTab");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		

		getMenuInflater().inflate(R.menu.settings_optionmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		int itemId = item.getItemId();
		switch (itemId)
		{

		case R.id.settings_menuitem_addCar :
		case R.id.settings_menuitem_editCar :
		case R.id.settings_menuitem_removeCar :
		case R.id.settings_menuitem_addLocation :
		case R.id.settings_menuitem_editLocation :
		case R.id.settings_menuitem_removeLocation :

			Toast toast = Toast.makeText(getApplicationContext(), "Dieses Feature wurde noch nicht Implementiert", Toast.LENGTH_LONG);
			toast.show();
		}

		return true;
	}

}
