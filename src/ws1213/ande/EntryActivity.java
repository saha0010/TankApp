package ws1213.ande;

import java.util.ArrayList;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class EntryActivity extends AppActivity implements OnClickListener
{

	private SharedPreferences	mSettings;
	private TankDBAdapter		dbAdapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        
        mSettings = getSharedPreferences(SETTINGS, MODE_PRIVATE);
        dbAdapter = new TankDBAdapter(this);
        String activeCar;
        if(mSettings.contains(SETTINGS_ACTIVECAR))
        {
        	 activeCar = mSettings.getString(SETTINGS_ACTIVECAR, "Kein Auto in mSettings");
        }
        else
        {
        	activeCar = "Bitte zu erst ein Auto erstellen!";
        }
       
        
        
        TextView tvCar = (TextView)findViewById(R.id.textView_entry_activeCar);
        tvCar.setText(activeCar);
        
        Button saveButton = (Button)findViewById(R.id.button_entry_save);
        saveButton.setOnClickListener(this);
        
        initLocationSpinner();
    }
	
	//TODO FIX SPINNER
private void initLocationSpinner()
{
	Spinner spinner = (Spinner) findViewById(R.id.Spinner_entry_location);

	ArrayList<Location> locations = new ArrayList<Location>();
	dbAdapter.open();
		Cursor c = dbAdapter.getAllLocationsCursor();
	dbAdapter.close();
	while(c.moveToNext())
	{
		locations.add(new Location(c.getInt(0),c.getString(1)));
	}
	
	
	ArrayAdapter<Location> adapter = new ArrayAdapter<Location>(this, android.R.layout.simple_spinner_item, locations);
	spinner.setAdapter(adapter);
	
	
	if(mSettings.contains(SETTINGS_LOCATION))
	{
		spinner.setSelection(((int)mSettings.getLong(SETTINGS_LOCATION, 0)));
	}
	// Handle spinner selections
    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {

            Editor editor = mSettings.edit();
            editor.remove(SETTINGS_LOCATION);
            editor.putInt(SETTINGS_LOCATION, selectedItemPosition);
            editor.commit();
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    });
}
	
	public void onClick(View v)
	{
		// Intent i = new Intent(EntryActivity.this, addEntry.class);
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
