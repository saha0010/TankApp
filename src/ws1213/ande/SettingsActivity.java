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
public class SettingsActivity extends AppActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, AdapterView.OnClickListener, AdapterView.OnLongClickListener
{
	private static final int		CAR_ADD_DIALOG_ID			= 0;
	private static final int		LOCATION_ADD_DIALOG_ID		= 1;
	private static final int		TAKE_CAR_CAMERA_REQUEST		= 1;
	private static final int		TAKE_CAR_GALLERY_REQUEST	= 2;
	private static final String		CAMERA_TAG					= "myCamera";
	private static final String		ONLONGCLICK					= "olc";
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
		final int orgHeight = bitmap.getHeight();
		final int orgWidth = bitmap.getWidth();

		// scale to no longer any either side than 75px
		final int scaledWidth = (orgWidth >= orgHeight) ? maxSide : (int) (maxSide * ((float) orgWidth / (float) orgHeight));
		final int scaledHeight = (orgHeight >= orgWidth) ? maxSide : (int) (maxSide * ((float) orgHeight / (float) orgWidth));

		// create the scaled bitmap
		final Bitmap scaledGalleryPic = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
		return scaledGalleryPic;
	}// createScaledBitmapKeepingAspectRatio END

	// ! helper method to fill carListView
	private void fillCarList()
	{
		dbAdapter.open();
		final int resID = R.layout.listview_settings_cars;
		final Cursor c = dbAdapter.getAllCarsCursor();
		if (c.getCount() != 0)
		{
			while (c.moveToNext())
			{
				final Car newCar = new Car(c.getLong(0), c.getString(1), c.getString(2), c.getInt(3));
				cars.add(newCar);
			}
			carAdapter = new CarArrayAdapter(this, resID, cars, this, this);
		}

		carListView = (ListView) findViewById(R.id.ListView_settings_cartab);
		carListView.setAdapter(carAdapter);

		carListView.setOnItemClickListener(this);
		carListView.setOnItemLongClickListener(this);

		dbAdapter.printCarsOnLog();// DBUG Logs
		logActiveCar(); // DBUG Logs
		dbAdapter.close();
	} // fillCarList END

	private void fillLocationList()
	{
		dbAdapter.open();
		final Cursor c = dbAdapter.getAllLocationsCursor();

		while (c.moveToNext())
			locations.add(new Location(c.getInt(0), c.getString(1)));
		final int resID = R.layout.activity_settings;
		locationAdapter = new LocationArrayAdapter(this, resID, locations);
		locationListView = (ListView) findViewById(R.id.ListView_settings_locationtab);
		locationListView.setAdapter(locationAdapter);

		dbAdapter.printLocationsOnLog();// DBUG Logs
		dbAdapter.close();
		logActiveLocation();
	}// fillLocationList END

	private void logActiveCar()
	{
		if (mSettings.contains(SETTINGS_ACTIVECAR))
		{
			final String s = mSettings.getString(SETTINGS_ACTIVECAR, "keins da");
			Log.v("DBAapter", "Active car in mSettings= " + s);
		}
		if (cars.size() > 0)
			for (int i = 0; i < cars.size(); i++)
				if (cars.get(i).isActive())
					Log.v("DBAapter", "Active car in cars[" + i + "] = " + cars.get(i).toString());
	}

	private void logActiveLocation()
	{
		if (mSettings.contains(SETTINGS_LOCATION))
		{
			final Map<String, ?> s = mSettings.getAll();
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
				Log.v(CAMERA_TAG, "onActivityResul CAMERA REQUST CANCELD");
			else if (resultCode == Activity.RESULT_OK)
			{
				ImageButton b;
				if (onClickPosition != -1)
					b = (ImageButton) carListView.getChildAt(onClickPosition).findViewById(R.id.imageButton1);
				else
					break;
				final Car car = carAdapter.getItem(onClickPosition);

				Bitmap bit = (Bitmap) data.getExtras().get("data");
				bit = createScaledBitmapKeepingAspectRatio(bit, 80);

				final ContentValues values = new ContentValues();
				values.put(MediaColumns.TITLE, car.getName() + "pic");
				values.put(ImageColumns.BUCKET_ID, car.get_id() + "pic");
				values.put(MediaColumns.MIME_TYPE, "image/jpeg");
				final Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
				OutputStream out;
				try
				{
					out = getContentResolver().openOutputStream(uri);
					bit.compress(CompressFormat.JPEG, 90, out);
					out.close();
				}
				catch (final Exception e)
				{
					Log.e(DEBUG_ERR, "Picture Taken error", e);
				}
				Log.v(CAMERA_TAG, " bitmap height: " + bit.getHeight());

				dbAdapter.open();
				dbAdapter.updateImageFormCar(car.get_id(), uri.toString());
				dbAdapter.close();
				b.setImageURI(uri);
			}
			break;
		case TAKE_CAR_GALLERY_REQUEST :
			Log.v(CAMERA_TAG, "onActivityResul GALLERY REQUST");
			if (resultCode == Activity.RESULT_CANCELED)
				Log.v(CAMERA_TAG, "onActivityResul GALLERY REQUST CANCLED");
			else if (resultCode == Activity.RESULT_OK)
			{
				Log.v(CAMERA_TAG, "onActivityResul GALLERY REQUST OK");
				final Uri uri = data.getData();
				ImageButton b;
				if (onClickPosition != -1)
					b = (ImageButton) carListView.getChildAt(onClickPosition).findViewById(R.id.imageButton1);
				else
					break;
				final Car car = carAdapter.getItem(onClickPosition);
				dbAdapter.open();
				dbAdapter.updateImageFormCar(car.get_id(), uri.toString());
				dbAdapter.close();
				b.setImageURI(uri);
			}
			updateCarList();
			break;
		}
	}// onActivityResult END

	// ! Used for lunching ActionImageCaputre Intents
	public void onClick(View v)
	{
		final String promt = getResources().getString(R.string.settings_intent_makepicture);
		final Intent pictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(Intent.createChooser(pictureIntent, promt), TAKE_CAR_CAMERA_REQUEST);
		final ListView lv = (ListView) v.getParent().getParent().getParent();
		onClickPosition = lv.getPositionForView(v);
	}// onClick END

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_settings);

		// Generate TabLayout
		mSettings = getSharedPreferences(SETTINGS, MODE_PRIVATE);

		final TabHost host = (TabHost) findViewById(android.R.id.tabhost);
		host.setup();

		final TabSpec carTab = host.newTabSpec("carTab");
		carTab.setIndicator(getResources().getString(R.string.settings_car), getResources().getDrawable(R.drawable.car));
		carTab.setContent(R.id.ListView_settings_cartab);

		final TabSpec locationTab = host.newTabSpec("locationTab");
		locationTab.setIndicator(getResources().getString(R.string.settings_location), getResources().getDrawable(R.drawable.map));
		locationTab.setContent(R.id.ListView_settings_locationtab);

		host.addTab(carTab);
		host.addTab(locationTab);

		// ! Check if this activity was called by EntryActivity, depening on value a different tab will be set as current
		if (getIntent().getExtras() != null)
			if (getIntent().getExtras().containsKey("activeTab"))
				host.setCurrentTabByTag(getIntent().getExtras().getString("activeTab"));
		dbAdapter = new TankDBAdapter(this);
		locations = new ArrayList<Location>();
		cars = new ArrayList<Car>();
		fillCarList();
		fillLocationList();
	} // onCreate END

	@Override
	protected Dialog onCreateDialog(int id)
	{
		final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
					final String carName = ((EditText) layout.findViewById(R.id.EditText_Dialog_CarAdd)).getText().toString();
					if (carName.length() == 0)
					{
						final Toast a = Toast.makeText(SettingsActivity.this.getBaseContext(), SettingsActivity.this.getResources().getString(
								R.string.settings_toast_notext), Toast.LENGTH_LONG);
						a.show();
					}
					// ! create the new car with name, default Image, and mark it as active
					final Car c = new Car(carName, DEFAULT_CAR_IMAGE_URI.toString(), 1);

					// ! open database, mark all Cars as not active, insert the new active car, close db
					dbAdapter.open();
					dbAdapter.deactivateAllCars();
					long id = dbAdapter.insertCar(c);
					dbAdapter.close();

					// ! open an editor for sharedPreferences, remove the active car, and set the id of the new active car, commit the Preferences
					final Editor editor = mSettings.edit();
					editor.remove(SETTINGS_ACTIVECAR);
					editor.remove(SETTINGS_ACTIVECAR_ID);
					editor.putString(SETTINGS_ACTIVECAR, c.getName());
					editor.putLong(SETTINGS_ACTIVECAR_ID, id);
					editor.commit();

					SettingsActivity.this.removeDialog(CAR_ADD_DIALOG_ID);
					SettingsActivity.this.updateCarList();

				}
			});
			final AlertDialog carAdderDialog = builder.create();
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

					final String name = ((EditText) layout.findViewById(R.id.EditText_Dialog_LocationAdd)).getText().toString();
					if (name.length() == 0)
					{
						final Toast a = Toast.makeText(SettingsActivity.this.getBaseContext(), SettingsActivity.this.getResources().getString(
								R.string.settings_toast_notext), Toast.LENGTH_LONG);
						a.show();
					}
					dbAdapter.open();
					final long id = dbAdapter.insertLocation(new Location(name));
					dbAdapter.close();

					final Editor editor = mSettings.edit();
					editor.remove(SETTINGS_LOCATION);
					editor.putLong(SETTINGS_LOCATION, id);
					editor.commit();
					SettingsActivity.this.removeDialog(LOCATION_ADD_DIALOG_ID);
					SettingsActivity.this.updateLocationList();
				}
			});
			final AlertDialog locationAdderDialog = builder.create();
			return locationAdderDialog;
		}
		return null;
	}// onCreateDialog END

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.settings_optionmenu, menu);
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
		final CarArrayAdapter caa = (CarArrayAdapter) parent.getAdapter();
		final Car car = caa.getItem(position);

		dbAdapter.open();
		dbAdapter.deactivateAllCars();
		dbAdapter.updateCar(car.get_id(), car.getName(), car.getImageUrl(), true);
		dbAdapter.open();

		final Editor editor = mSettings.edit();
		editor.remove(SETTINGS_ACTIVECAR);
		editor.remove(SETTINGS_ACTIVECAR_ID);
		editor.putString(SETTINGS_ACTIVECAR, car.getName());
		editor.putLong(SETTINGS_ACTIVECAR_ID, car.get_id());
		editor.commit();
		car.setActive(true);
		final RadioButton r = (RadioButton) clickedItem.findViewById(R.id.radioButton1);
		r.setChecked(true);

		clickedItem.findViewById(R.id.imageButton1);
		updateCarList();
	}

	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		Log.v(ONLONGCLICK, "onItemLongClick");

		return false;
	}

	public boolean onLongClick(View v)
	{
		Log.v(CAMERA_TAG, "onLongClick");
		final String strAvatarPrompt = getResources().getString(R.string.settings_intent_choosepicture);
		final Intent pickPhoto = new Intent(Intent.ACTION_PICK);
		pickPhoto.setType("image/*");
		startActivityForResult(Intent.createChooser(pickPhoto, strAvatarPrompt), TAKE_CAR_GALLERY_REQUEST);
		final ListView lv = (ListView) v.getParent().getParent().getParent();
		onClickPosition = lv.getPositionForView(v);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		final int itemId = item.getItemId();
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

			final Toast toast = Toast.makeText(getApplicationContext(), "Dieses Feature wurde noch nicht Implementiert", Toast.LENGTH_LONG);
			toast.show();
		}

		return false;
	}// onOptionsItemSelected END

	private void updateCarList()
	{
		carListView = null;
		carAdapter = null;
		cars.clear();
		fillCarList();
		onClickPosition = -1;
	}

	private void updateLocationList()
	{
		locationListView = null;
		locationAdapter = null;
		locations.clear();
		fillLocationList();
	}

}
