/**
 * 
 */
package ws1213.ande;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

/**
 * @author Sascha Hayton
 * 
 */
public class SettingsActivity extends AppActivity implements AdapterView.OnItemClickListener, AdapterView.OnClickListener, AdapterView.OnLongClickListener
{
	private static final int		CAR_ADD_DIALOG_ID			= 0;
	private static final int		LOCATION_ADD_DIALOG_ID		= 1;
	private static final int		TAKE_CAR_CAMERA_REQUEST		= 1;
	private static final int		TAKE_CAR_GALLERY_REQUEST	= 2;
	private static final String		CAMERA_TAG					= "myCamera";
	private SharedPreferences		mSettings;

	private CarArrayAdapter			carAdapter;
	private LocationArrayAdapter	locationAdapter;
	private ListView				carListView;
	private ListView				locationListView;
	private TankDBAdapter			dbAdapter;
	private ArrayList<Car>			cars;
	private ArrayList<Location>		locations;

	private int						onClickPosition				= -1;

	// src BTDTAPP, SamsTeachYourself - Android Application Development
	private Bitmap createScaledBitmapKeepingAspectRatio(Bitmap bitmap, int maxSide)
	{
		int orgHeight = bitmap.getHeight();
		int orgWidth = bitmap.getWidth();

		// scale to no longer any either side than 75px
		int scaledWidth = (orgWidth >= orgHeight) ? maxSide : (int) (maxSide * ((float) orgWidth / (float) orgHeight));
		int scaledHeight = (orgHeight >= orgWidth) ? maxSide : (int) (maxSide * ((float) orgHeight / (float) orgWidth));

		// create the scaled bitmap
		Bitmap scaledGalleryPic = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
		return scaledGalleryPic;
	}// createScaledBitmapKeepingAspectRatio END

	// ! helper method to fill carListView
	private void fillCarList()
	{
		this.dbAdapter.open();
		int resID = R.layout.listview_settings_cars;
		Cursor c = this.dbAdapter.getAllCarsCursor();
		if (c.getCount() != 0)
		{
			while (c.moveToNext())
			{
				Car newCar = new Car(c.getLong(0), c.getString(1), c.getString(2), c.getInt(3));
				this.cars.add(newCar);
			}
			this.carAdapter = new CarArrayAdapter(this, resID, this.cars, this, this);
		}

		this.carListView = (ListView) this.findViewById(R.id.ListView_settings_cartab);
		this.carListView.setAdapter(this.carAdapter);

		this.carListView.setOnItemClickListener(this);

		this.dbAdapter.printCarsOnLog();// DBUG Logs
		this.logActiveCar(); // DBUG Logs
		this.dbAdapter.close();
	} // fillCarList END

	private void fillLocationList()
	{
		this.dbAdapter.open();
		Cursor c = this.dbAdapter.getAllLocationsCursor();

		while (c.moveToNext())
		{
			this.locations.add(new Location(c.getInt(0), c.getString(1)));
		}
		int resID = R.layout.activity_settings;
		this.locationAdapter = new LocationArrayAdapter(this, resID, this.locations);
		this.locationListView = (ListView) this.findViewById(R.id.ListView_settings_locationtab);
		this.locationListView.setAdapter(this.locationAdapter);

		Log.v("DBAapter", this.locationAdapter.getCount() + "");
		/*
		 * locationListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
		 * 
		 * public boolean onItemLongClick(AdapterView<?> parent, View clickedItem, int position, long id) { Log.v("OICL", "onLongItemClick,  " +
		 * parent.getAdapter().toString() + ",   " + clickedItem.toString() + ",  " + position + ",   " + id);
		 * 
		 * return true; }
		 * 
		 * });
		 */

		this.dbAdapter.printLocationsOnLog();// DBUG Logs
		this.dbAdapter.close();
		this.logActiveLocation();
	}// fillLocationList END

	private void logActiveCar()
	{
		if (this.mSettings.contains(SETTINGS_ACTIVECAR))
		{
			String s = this.mSettings.getString(SETTINGS_ACTIVECAR, "keins da");
			Log.v("DBAapter", "Active car in mSettings= " + s);
		}
		if (this.cars.size() > 0)
		{
			for (int i = 0; i < this.cars.size(); i++)
			{
				if (this.cars.get(i).isActive())
					Log.v("DBAapter", "Active car in cars[" + i + "] = " + this.cars.get(i).toString());
			}
		}
	}

	private void logActiveLocation()
	{
		if (this.mSettings.contains(SETTINGS_LOCATION))
		{
			Map<String, ?> s = this.mSettings.getAll();
			Log.v("DBAapter", "Active location in mSettings= " + s.get(SETTINGS_LOCATION));
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
		case TAKE_CAR_CAMERA_REQUEST :
			Log.v(CAMERA_TAG, "onActivityResul CAMERA REQUST");
			if (resultCode == Activity.RESULT_CANCELED)
			{
				Log.v(CAMERA_TAG, "onActivityResul CAMERA REQUST CANCELD");
			}
			else if (resultCode == Activity.RESULT_OK)
			{
				ImageButton b;
				if (this.onClickPosition != -1)
					b = (ImageButton) this.carListView.getChildAt(this.onClickPosition).findViewById(R.id.imageButton1);
				else
					break;
				Car car = this.carAdapter.getItem(this.onClickPosition);

				Bitmap bit = (Bitmap) data.getExtras().get("data");
				bit = this.createScaledBitmapKeepingAspectRatio(bit, 80);

				ContentValues values = new ContentValues();
				values.put(MediaColumns.TITLE, car.getName() + "pic");
				values.put(ImageColumns.BUCKET_ID, car.get_id() + "pic");
				values.put(MediaColumns.MIME_TYPE, "image/jpeg");
				Uri uri = this.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
				OutputStream out;
				try
				{
					out = this.getContentResolver().openOutputStream(uri);
					bit.compress(CompressFormat.JPEG, 90, out);
					out.close();
				}
				catch (Exception e)
				{
					Log.e(DEBUG_ERR, "Picture Taken error", e);
				}
				Log.v(CAMERA_TAG, " bitmap height: " + bit.getHeight());

				this.dbAdapter.open();
				this.dbAdapter.updateImageFormCar(car.get_id(), uri.toString());
				this.dbAdapter.close();
				b.setImageURI(uri);
			}
			break;
		case TAKE_CAR_GALLERY_REQUEST :
			Log.v(CAMERA_TAG, "onActivityResul GALLERY REQUST");
			if (resultCode == Activity.RESULT_CANCELED)
			{
				Log.v(CAMERA_TAG, "onActivityResul GALLERY REQUST CANCLED");
			}
			else if (resultCode == Activity.RESULT_OK)
			{
				Log.v(CAMERA_TAG, "onActivityResul GALLERY REQUST OK");
				Uri uri = data.getData();
				ImageButton b;
				if (this.onClickPosition != -1)
					b = (ImageButton) this.carListView.getChildAt(this.onClickPosition).findViewById(R.id.imageButton1);
				else
					break;
				Car car = this.carAdapter.getItem(this.onClickPosition);
				this.dbAdapter.open();
				this.dbAdapter.updateImageFormCar(car.get_id(), uri.toString());
				this.dbAdapter.close();
				b.setImageURI(uri);
			}
			this.updateCarList();
			break;
		}
	}// onActivityResult END

	// ! Used for lunching ActionImageCaputre Intents
	public void onClick(View v)
	{
		String promt = this.getResources().getString(R.string.settings_intent_makepicture);
		Intent pictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		this.startActivityForResult(Intent.createChooser(pictureIntent, promt), TAKE_CAR_CAMERA_REQUEST);
		ListView lv = (ListView) v.getParent().getParent().getParent();
		this.onClickPosition = lv.getPositionForView(v);
	}// onClick END

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_settings);

		// Generate TabLayout
		this.mSettings = this.getSharedPreferences(SETTINGS, MODE_PRIVATE);

		TabHost host = (TabHost) this.findViewById(android.R.id.tabhost);
		host.setup();

		TabSpec carTab = host.newTabSpec("carTab");
		carTab.setIndicator(this.getResources().getString(R.string.settings_car), this.getResources().getDrawable(R.drawable.car));
		carTab.setContent(R.id.ListView_settings_cartab);

		TabSpec locationTab = host.newTabSpec("locationTab");
		locationTab.setIndicator(this.getResources().getString(R.string.settings_location), this.getResources().getDrawable(R.drawable.map));
		locationTab.setContent(R.id.ListView_settings_locationtab);

		host.addTab(carTab);
		host.addTab(locationTab);

		// ! Check if this activity was called by EntryActivity, depening on value a different tab will be set as current
		if (this.getIntent().getExtras() != null)
		{
			if (this.getIntent().getExtras().containsKey("activeTab"))
				host.setCurrentTabByTag(this.getIntent().getExtras().getString("activeTab"));
		}
		this.dbAdapter = new TankDBAdapter(this);
		this.locations = new ArrayList<Location>();
		this.cars = new ArrayList<Car>();
		this.fillCarList();
		this.fillLocationList();
	} // onCreate END

	@Override
	protected Dialog onCreateDialog(int id)
	{
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout;
		AlertDialog.Builder builder;
		switch (id)
		{
		case CAR_ADD_DIALOG_ID :
			layout = inflater.inflate(R.layout.dialog_caradd, (ViewGroup) this.findViewById(R.id.carRoot));

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
					if (carName.length() == 0)
					{
						Toast a = Toast.makeText(SettingsActivity.this.getBaseContext(), SettingsActivity.this.getResources().getString(
								R.string.settings_toast_notext), Toast.LENGTH_LONG);
						a.show();
					}
					// ! create the new car with name, default Image, and mark it as active
					Car c = new Car(carName, DEFAULT_CAR_IMAGE_URI.toString(), 1);

					// ! open database, mark all Cars as not active, insert the new active car, close db
					SettingsActivity.this.dbAdapter.open();
					SettingsActivity.this.dbAdapter.deactivateAllCars();
					SettingsActivity.this.dbAdapter.insertCar(c);
					SettingsActivity.this.dbAdapter.close();

					// ! open an editor for sharedPreferences, remove the active car, and set the id of the new active car, commit the Preferences
					Editor editor = SettingsActivity.this.mSettings.edit();
					editor.remove(SETTINGS_ACTIVECAR);
					editor.putString(SETTINGS_ACTIVECAR, c.getName());
					editor.commit();

					SettingsActivity.this.removeDialog(CAR_ADD_DIALOG_ID);
					SettingsActivity.this.updateCarList();

				}
			});
			AlertDialog carAdderDialog = builder.create();
			return carAdderDialog;

		case LOCATION_ADD_DIALOG_ID :
			layout = inflater.inflate(R.layout.dialog_locationadd, (ViewGroup) this.findViewById(R.id.LocationRoot));

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
					if (name.length() == 0)
					{
						Toast a = Toast.makeText(SettingsActivity.this.getBaseContext(), SettingsActivity.this.getResources().getString(
								R.string.settings_toast_notext), Toast.LENGTH_LONG);
						a.show();
					}
					SettingsActivity.this.dbAdapter.open();
					long id = SettingsActivity.this.dbAdapter.insertLocation(new Location(name));
					SettingsActivity.this.dbAdapter.close();

					Editor editor = SettingsActivity.this.mSettings.edit();
					editor.remove(SETTINGS_LOCATION);
					editor.putLong(SETTINGS_LOCATION, id);
					editor.commit();
					SettingsActivity.this.removeDialog(LOCATION_ADD_DIALOG_ID);
					SettingsActivity.this.updateLocationList();
				}
			});
			AlertDialog locationAdderDialog = builder.create();
			return locationAdderDialog;
		}
		return null;
	}// onCreateDialog END

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		this.getMenuInflater().inflate(R.menu.settings_optionmenu, menu);
		return true;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	public void onItemClick(AdapterView<?> parent, View clickedItem, int position, long id)
	{

		Log.v("OICL", "onItemClick,  " + "prarentChildCount: " + parent.getChildCount() + "Position:" + position + ",  id: " + id);

		CarArrayAdapter caa = (CarArrayAdapter) parent.getAdapter();
		final Car car = caa.getItem(position);

		this.dbAdapter.open();
		this.dbAdapter.deactivateAllCars();
		this.dbAdapter.updateCar(car.get_id(), car.getName(), car.getImageUrl(), true);
		this.dbAdapter.open();

		Editor editor = this.mSettings.edit();
		editor.remove(SETTINGS_ACTIVECAR);
		editor.putString(SETTINGS_ACTIVECAR, car.getName());
		editor.commit();
		car.setActive(true);
		RadioButton r = (RadioButton) clickedItem.findViewById(R.id.radioButton1);
		r.setChecked(true);

		clickedItem.findViewById(R.id.imageButton1);
		this.updateCarList();
	}

	public boolean onLongClick(View v)
	{
		Log.v(CAMERA_TAG, "onLongClick");
		String strAvatarPrompt = this.getResources().getString(R.string.settings_intent_choosepicture);
		Intent pickPhoto = new Intent(Intent.ACTION_PICK);
		pickPhoto.setType("image/*");
		this.startActivityForResult(Intent.createChooser(pickPhoto, strAvatarPrompt), TAKE_CAR_GALLERY_REQUEST);
		ListView lv = (ListView) v.getParent().getParent().getParent();
		this.onClickPosition = lv.getPositionForView(v);
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
			this.showDialog(CAR_ADD_DIALOG_ID);
			return true;

		case R.id.settings_menuitem_addLocation :
			this.showDialog(LOCATION_ADD_DIALOG_ID);
			return true;

		case R.id.settings_menuitem_editLocation :
		case R.id.settings_menuitem_removeLocation :
		case R.id.settings_menuitem_editCar :
		case R.id.settings_menuitem_removeCar :

			Toast toast = Toast.makeText(this.getApplicationContext(), "Dieses Feature wurde noch nicht Implementiert", Toast.LENGTH_LONG);
			toast.show();
		}

		return false;
	}// onOptionsItemSelected END

	private void updateCarList()
	{
		this.carListView = null;
		this.carAdapter = null;
		this.cars.clear();
		this.fillCarList();
		this.onClickPosition = -1;
	}

	private void updateLocationList()
	{
		this.locationListView = null;
		this.locationAdapter = null;
		this.locations.clear();
		this.fillLocationList();
	}

}
