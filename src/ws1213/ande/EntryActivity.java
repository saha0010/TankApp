package ws1213.ande;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;

public class EntryActivity extends Activity implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        
        Button saveButton = (Button)findViewById(R.id.button_entry_save);
        saveButton.setOnClickListener(this);
    }

	public void onClick(View v)
	{
		//Intent i = new Intent(EntryActivity.this, addEntry.class);
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
			i.putExtra("activeTab","carTab");
			startActivity(i);
			EntryActivity.this.finish();
			break;
	
		}

		return true;	
	}

}
