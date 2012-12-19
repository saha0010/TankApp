package ws1213.ande;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

/**
 * @author Sascha Hayton
 * 
 */
public class MenuActivity extends AppActivity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_menu);
		ListView menuList = (ListView) findViewById(R.id.ListView_Menu);

		String[ ] items = { getResources().getString(R.string.entry), getResources().getString(R.string.settings),
				getResources().getString(R.string.log), getResources().getString(R.string.stats) };

		ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, R.layout.listview_menu_row, items);
		menuList.setAdapter(adapt);

		menuList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{

			public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id)
			{
				TextView textView = (TextView) itemClicked;
				String txt = textView.getText().toString();

				if (txt.equalsIgnoreCase(getResources().getString(R.string.entry)))
				{
					startActivity(new Intent(MenuActivity.this, EntryActivity.class));
				}
				else if (txt.equalsIgnoreCase(getResources().getString(R.string.settings)))
				{
					startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
				}
				else if (txt.equalsIgnoreCase(getResources().getString(R.string.log)))
				{
					startActivity(new Intent(MenuActivity.this, LogActivity.class));
				}
				else if (txt.equalsIgnoreCase(getResources().getString(R.string.stats)))
				{
					startActivity(new Intent(MenuActivity.this, StatsActivity.class));
				}

			}
		});
	}

}
