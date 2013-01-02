package ws1213.ande;

import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EntryActivity extends AppActivity
{

	private SharedPreferences		mSettings;
	private TankDBAdapter			dbAdapter;
	private static final String		TAG	= "entryDEBUG";
	private boolean					OK	= true;
	private Spinner					spinner;
	private ArrayAdapter<String>	adapter;
	private ArrayList<String>		locations;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry);

		mSettings = getSharedPreferences(SETTINGS, MODE_PRIVATE);
		dbAdapter = new TankDBAdapter(this);

		String activeCar;
		if (mSettings.contains(SETTINGS_ACTIVECAR))
		{
			activeCar = mSettings.getString(SETTINGS_ACTIVECAR, getResources().getString(R.string.tost_noactivecar));
		}
		else
		{
			activeCar = getResources().getString(R.string.tost_noactivecar);
			OK = false;
		}

		initLocationSpinner();

		TextView tvCar = (TextView) findViewById(R.id.textView_entry_activeCar);
		tvCar.setText(activeCar);

		Button saveButton = (Button) findViewById(R.id.button_entry_save);
		saveButton.setOnClickListener(new View.OnClickListener()
		{

			public void onClick(View v)
			{
				OK = true;
				try
				{
					float costPerLitre = Float.parseFloat(((EditText) findViewById(R.id.EditText_costs_per_litre)).getText().toString().replace(
							",", "."));
					float newLitre = Float.parseFloat(((EditText) findViewById(R.id.EditText_entry_tankedLitre)).getText().toString().replace(",", "."));
					int distance = Integer.parseInt(((EditText) findViewById(R.id.EditText_entry_kilometerStand)).getText().toString());
					long locId = mSettings.getLong(SETTINGS_LOCATION, -1);
					long carId = -1;
					if (mSettings.contains(SETTINGS_ACTIVECAR_ID))
					{
						carId = mSettings.getLong(SETTINGS_ACTIVECAR_ID, -1);
					}
					if (carId == -1)
					{
						Log.v(TAG, "carID == -1");
						OK = false;
					}

					if (OK)// All EditTexts are filled with 'correct' Data, a car and a location are selected
					{					
						dbAdapter.open();
						Car car = dbAdapter.getCarFromId(carId);
						Location loc = dbAdapter.getLocationFromId(locId);
						Date date = new Date();
						
						Entry e = new Entry(costPerLitre, newLitre, distance, date.getTime(),loc, car);
						
						long entId = dbAdapter.insertEntry(e);

						e = dbAdapter.getEntryFromId(entId);					
						Toast a = Toast.makeText(getBaseContext(), R.string.entry_toast_savesucess, Toast.LENGTH_LONG);
						a.show();
					}
					else{
						OK = true;
						Toast a = Toast.makeText(getBaseContext(), R.string.entry_toast_wrongentry, Toast.LENGTH_LONG);
						a.show();						
					}
				}
				catch (NumberFormatException e)
				{
					OK = false;
					Toast a = Toast.makeText(getBaseContext(), R.string.entry_toast_wrongentry, Toast.LENGTH_LONG);
					a.show();
				}
			}
		});
	}

	private void initLocationSpinner()
	{
		spinner = (Spinner) findViewById(R.id.Spinner_entry_location);
		locations = new ArrayList<String>();
		dbAdapter.open();
		Cursor c = dbAdapter.getAllLocationsCursor();
		

		while (c.moveToNext())
		{
			locations.add(c.getString(1));
		}
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locations);
		c.moveToPosition(0);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		Log.v(TAG, "Counter.count = "+c.getCount());
		for(int i =0;i<spinner.getCount();i++)
		{
			
			Log.v(TAG, "spinner["+i+"] = "+spinner.getItemAtPosition(i).toString());
		}

		if (mSettings.contains(SETTINGS_LOCATION))
		{
			Log.v(TAG, "mSettings.SETTINGS_LOCATION =" + mSettings.getLong(SETTINGS_LOCATION, 0) + "");
			spinner.setSelection(((int)mSettings.getLong(SETTINGS_LOCATION, 0)-1));
			spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
			{
				// Handle spinner selections
				public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId)
				{

					Editor editor = mSettings.edit();
					editor.remove(SETTINGS_LOCATION);
					editor.putLong(SETTINGS_LOCATION, selectedItemPosition+1);
					editor.commit();
				}

				public void onNothingSelected(AdapterView<?> parent)
				{
				}
			});
		}
		else
		// No Loction in database
		{
			OK = false;
			Toast a = Toast.makeText(getBaseContext(), R.string.entry_toast_chooselocation, Toast.LENGTH_LONG);
			a.show();
		}
		dbAdapter.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.entry_optionmenu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent i;
		super.onOptionsItemSelected(item);
		int itemId = item.getItemId();
		switch (itemId)
		{

		case R.id.entry_menuitem_addLocation :

			i = new Intent(EntryActivity.this, SettingsActivity.class);
			i.putExtra("activeTab", "locationTab");
			EntryActivity.this.finish();
			startActivity(i);
			break;
		case R.id.entry_menuitem_changeCar :

			i = new Intent(EntryActivity.this, SettingsActivity.class);
			i.putExtra("activeTab", "carTab");
			startActivity(i);
			EntryActivity.this.finish();
			break;

		}

		return true;
	}

}
