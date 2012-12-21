/**
 * 
 */
package ws1213.ande;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;

/**
 * @author Sascha Hayton
 * 
 */
public class SettingsActivity extends AppActivity
{
	private static final int		CAR_ADD_DIALOG_ID			= 0;
	private static final int		LOCATION_ADD_DIALOG_ID		= 1;
	private static final int		TAKE_AVATAR_CAMERA_REQUEST	= 1;
	private static final int		TAKE_AVATAR_GALLERY_REQUEST	= 2;
	private SharedPreferences		mSettings;

	private CarArrayAdapter			carAdapter;
	private LocationArrayAdapter	locationAdapter;
	private ListView				carListView;
	private ListView				locationListView;
	private TankDBAdapter			dbAdapter;
	private ArrayList<Car>			cars;
	private ArrayList<Location>		locations;

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

		// ! Check if this activity was called by EntryActivity, depening on value a different tab will be set as current
		if (getIntent().getExtras() != null)
		{
			if (getIntent().getExtras().containsKey("activeTab"))
				host.setCurrentTabByTag(getIntent().getExtras().getString("activeTab"));
		}
		dbAdapter = new TankDBAdapter(this);
		locations = new ArrayList<Location>();
		cars = new ArrayList<Car>();
		fillCarList();
		fillLocationList();
	}

	// ! helper method to fill carListView
	private void fillCarList()
	{
		dbAdapter.open();
		int resID = R.layout.listview_settings_cars;
		Cursor c = dbAdapter.getAllCarsCursor();
		if (c.getCount() != 0)
		{
			while (c.moveToNext())
			{
				Car newCar = new Car(c.getLong(0), c.getString(1), c.getString(2), c.getInt(3));
				cars.add(newCar);
			}
			carAdapter = new CarArrayAdapter(this, resID, cars);
		}

		carListView = (ListView) findViewById(R.id.ListView_settings_car);
		carListView.setAdapter(carAdapter);
		carListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View clickedItem, int position, long id)
			{
				Log.v("OICL", "onItemClick,  " + parent.getAdapter().toString() + ",   " + clickedItem.toString() + ",  " + position + ",   " + id);

				CarArrayAdapter caa = (CarArrayAdapter) parent.getAdapter();
				Car car = (Car) caa.getItem(position);

				dbAdapter.open();
				dbAdapter.deactivateAllCars();
				dbAdapter.updateCar(car.get_id(), car.getName(), car.getImageUrl(), true);
				dbAdapter.open();

				Editor editor = mSettings.edit();
				editor.remove(SETTINGS_ACTIVECAR);
				editor.putString(SETTINGS_ACTIVECAR, car.getName());
				editor.commit();
				car.setActive(true);
				RadioButton r = (RadioButton) clickedItem.findViewById(R.id.radioButton1);
				r.setChecked(true);
				updateCarList();
			}
		});

		dbAdapter.printCarsOnLog();// DBUG Logs
		logActiveCar(); // DBUG Logs
		dbAdapter.close();
	}

	private void fillLocationList()
	{
		dbAdapter.open();
		Cursor c = dbAdapter.getAllLocationsCursor();

		while (c.moveToNext())
		{
			locations.add(new Location(c.getInt(0), c.getString(1)));
		}
		int resID = R.layout.activity_settings;
		locationAdapter = new LocationArrayAdapter(this, resID, locations);
		locationListView = (ListView) findViewById(R.id.ListView_settings_locations);
		locationListView.setAdapter(locationAdapter);

		Log.v("DBAapter", locationAdapter.getCount() + "");
		locationListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{

			public boolean onItemLongClick(AdapterView<?> parent, View clickedItem, int position, long id)
			{
				Log.v("OICL", "onLongItemClick,  " + parent.getAdapter().toString() + ",   " + clickedItem.toString() + ",  " + position + ",   "
						+ id);

				return true;
			}

		});

		dbAdapter.printLocationsOnLog();// DBUG Logs
		dbAdapter.close();
		logActiveLocation();
	}

	private void updateCarList()
	{
		carListView = null;
		carAdapter = null;
		cars.clear();
		fillCarList();
	}

	private void updateLocationList()
	{
		locationListView = null;
		locationAdapter = null;
		locations.clear();
		fillLocationList();
	}

	private void logActiveCar()
	{
		if (mSettings.contains(SETTINGS_ACTIVECAR))
		{
			String s = mSettings.getString(SETTINGS_ACTIVECAR, "keins da");
			Log.v("DBAapter", "Active car in mSettings= " + s);
		}
		if (cars.size() > 0)
		{
			for (int i = 0; i < cars.size(); i++)
			{
				if (cars.get(i).isActive())
					Log.v("DBAapter", "Active car in cars[" + i + "] = " + cars.get(i).toString());
			}
		}
	}

	private void logActiveLocation()
	{
		if (mSettings.contains(SETTINGS_LOCATION))
		{
			long s = mSettings.getLong(SETTINGS_LOCATION, 0);
			Log.v("DBAapter", "Active location in mSettings= " + s);
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
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

		case R.id.settings_menuitem_addLocation :
			showDialog(LOCATION_ADD_DIALOG_ID);
			return true;

		case R.id.settings_menuitem_editLocation :
		case R.id.settings_menuitem_removeLocation :
		case R.id.settings_menuitem_editCar :
		case R.id.settings_menuitem_removeCar :

			Toast toast = Toast.makeText(getApplicationContext(), "Dieses Feature wurde noch nicht Implementiert", Toast.LENGTH_LONG);
			toast.show();
		}

		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout;
		AlertDialog.Builder builder;
		switch (id)
		{
		case CAR_ADD_DIALOG_ID :
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
					// ! Get carname from EditText
					String carName = ((EditText) layout.findViewById(R.id.EditText_Dialog_CarAdd)).getText().toString();
					// ! create the new car with name, default Image, and mark it as active
					Car c = new Car(carName, DEFAULT_CAR_IMAGE_URI.toString(), 1);

					// ! open database, mark all Cars as not active, insert the new active car, close db
					dbAdapter.open();
					dbAdapter.deactivateAllCars();
					dbAdapter.insertCar(c);
					dbAdapter.close();

					// ! open an editor for sharedPreferences, remove the active car, and set the id of the new active car, commit the Preferences
					Editor editor = mSettings.edit();
					editor.remove(SETTINGS_ACTIVECAR);
					editor.putString(SETTINGS_ACTIVECAR, c.getName());
					editor.commit();

					SettingsActivity.this.removeDialog(CAR_ADD_DIALOG_ID);
					updateCarList();

				}
			});
			AlertDialog carAdderDialog = builder.create();
			return carAdderDialog;

		case LOCATION_ADD_DIALOG_ID :
			layout = inflater.inflate(R.layout.dialog_locationadd, (ViewGroup) findViewById(R.id.LocationRoot));

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
					String name = ((EditText) layout.findViewById(R.id.EditText_Dialog_LocationAdd)).getText().toString();
					dbAdapter.open();
					long id = dbAdapter.insertLocation(new Location(name));
					dbAdapter.close();

					Editor editor = mSettings.edit();
					editor.remove(SETTINGS_LOCATION);
					editor.putLong(SETTINGS_LOCATION, id);
					editor.commit();
					SettingsActivity.this.removeDialog(LOCATION_ADD_DIALOG_ID);
					updateLocationList();
				}
			});
			AlertDialog locationAdderDialog = builder.create();
			return locationAdderDialog;
		}
		return null;
	}

}
