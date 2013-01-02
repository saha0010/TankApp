package ws1213.ande;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

		this.setContentView(R.layout.activity_menu);
		ListView menuList = (ListView) this.findViewById(R.id.ListView_Menu);

		String[ ] items = { this.getResources().getString(R.string.entry), this.getResources().getString(R.string.settings),
				this.getResources().getString(R.string.log), this.getResources().getString(R.string.stats) };

		ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, R.layout.listview_menu_row, items);
		menuList.setAdapter(adapt);

		menuList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{

			public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id)
			{
				TextView textView = (TextView) itemClicked;
				String txt = textView.getText().toString();

				if (txt.equalsIgnoreCase(MenuActivity.this.getResources().getString(R.string.entry)))
				{
					MenuActivity.this.startActivity(new Intent(MenuActivity.this, EntryActivity.class));
				}
				else if (txt.equalsIgnoreCase(MenuActivity.this.getResources().getString(R.string.settings)))
				{
					MenuActivity.this.startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
				}
				else if (txt.equalsIgnoreCase(MenuActivity.this.getResources().getString(R.string.log)))
				{
					MenuActivity.this.startActivity(new Intent(MenuActivity.this, LogActivity.class));
				}
				else if (txt.equalsIgnoreCase(MenuActivity.this.getResources().getString(R.string.stats)))
				{
					MenuActivity.this.startActivity(new Intent(MenuActivity.this, StatsActivity.class));
				}

			}
		});
	}
}
