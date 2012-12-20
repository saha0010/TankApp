/**
 * 
 */
package ws1213.ande;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author Sascha Hayton
 * 
 */
public class SettingsActivity extends AppActivity
{
	private static final int	CAR_ADD_DIALOG_ID			= 0;
	private static final int	LOCATION_ADD_DIALOG_ID		= 1;
	private static final int	TAKE_AVATAR_CAMERA_REQUEST	= 1;
	private static final int	TAKE_AVATAR_GALLERY_REQUEST	= 2;
	private SharedPreferences	mSettings;

	private CarArrayAdapter		carAdapter;
	private ListView			carListView;
	private TankDBAdapter		dbAdapter;
	private ArrayList<Car>		cars;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

 // Generate TabLayout
		mSettings = getSharedPreferences(SETTINGS, MODE_PRIVATE);
		
		TabHost host = (TabHost) findViewById(android.R.id.tabhost);
		host.setup();

		TabSpec carTab = host.newTabSpec("carTab");
		((TabSpec) carTab).setIndicator(getResources().getString(R.string.settings_car), getResources().getDrawable(R.drawable.car));
		carTab.setContent(R.id.scrollview_settings_cartab);

		TabSpec locationTab = host.newTabSpec("locationTab");
		locationTab.setIndicator(getResources().getString(R.string.settings_location), getResources().getDrawable(R.drawable.map));
		locationTab.setContent(R.id.scrollview_settings_locationtab);

		host.addTab(carTab);
		host.addTab(locationTab);

		if(getIntent().getExtras()!=null){
		if (getIntent().getExtras().containsKey("activeTab"))
			host.setCurrentTabByTag(getIntent().getExtras().getString("activeTab"));
		}
		// Open Database
		dbAdapter = new TankDBAdapter(this);
		dbAdapter.open();		
		
//CAR
		cars = new ArrayList<Car>();		
		cars = dbAdapter.getAllCars();
		
		int resID = R.layout.listview_settings_cars;
		carListView = (ListView)findViewById(R.id.ListView_settings_car);
		carAdapter = new CarArrayAdapter(this,resID , cars);
		carListView.setAdapter(carAdapter);
		
		

	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		dbAdapter.close();
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
			showDialog(CAR_ADD_DIALOG_ID);
			return true;
		case R.id.settings_menuitem_editCar :
		case R.id.settings_menuitem_removeCar :
		case R.id.settings_menuitem_addLocation :
			showDialog(LOCATION_ADD_DIALOG_ID);
			return true;
		case R.id.settings_menuitem_editLocation :
		case R.id.settings_menuitem_removeLocation :

			Toast toast = Toast.makeText(getApplicationContext(), "Dieses Feature wurde noch nicht Implementiert", Toast.LENGTH_LONG);
			toast.show();
		}

		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		LayoutInflater inflater;
		View layout;
		AlertDialog.Builder builder;
		switch (id)
		{
		case CAR_ADD_DIALOG_ID :
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.dialog_caradd, (ViewGroup) findViewById(R.id.carRoot));
			
			builder = new AlertDialog.Builder(this);
			builder.setView(layout);
			builder.setTitle(R.string.settings_dialog_addcar);
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					SettingsActivity.this.removeDialog(CAR_ADD_DIALOG_ID);
				}
			});
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					String sCarName;
					EditText carName = (EditText) findViewById(R.id.EditText_Dialog_CarAdd);
					if(carName != null)
					 sCarName = carName.getText().toString();
					else 
					 sCarName = "aasdgfsv";
					
					Car c = new Car(sCarName,DEFAULT_CAR_IMAGE_URL,true);
					dbAdapter.insert(c);
					
					Editor editor = mSettings.edit();
					editor.putString(SETTINGS_ACTIVECAR, sCarName);
					editor.commit();

					SettingsActivity.this.removeDialog(CAR_ADD_DIALOG_ID);
				}
			});
			AlertDialog carAdderDialog = builder.create();
			return carAdderDialog;
			
case LOCATION_ADD_DIALOG_ID :
			
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.dialog_caradd, (ViewGroup) findViewById(R.id.carRoot));
			final EditText locationName = (EditText) findViewById(R.id.EditText_Dialog_LocationAdd);

			builder = new AlertDialog.Builder(this);

			builder.setView(layout);
			builder.setTitle(R.string.settings_dialog_addlocation);
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					SettingsActivity.this.removeDialog(LOCATION_ADD_DIALOG_ID);
				}
			});
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int whichButton)
				{
					String sLocationName = locationName.getText().toString();
					Editor editor = mSettings.edit();
					editor.putString(SETTINGS_LOCATION, sLocationName);
					editor.commit();
					SettingsActivity.this.removeDialog(LOCATION_ADD_DIALOG_ID);
				}
			});
			AlertDialog locationAdderDialog = builder.create();
			return locationAdderDialog;
		}
		return null;
	}
}
