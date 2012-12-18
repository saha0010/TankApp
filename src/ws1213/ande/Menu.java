package ws1213.ande;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

/**
 * @author Sascha Hayton
 * 
 */
public class Menu extends AppActivity
	{

		private ListView listView1;
		
		@Override
		public void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);

				setContentView(R.layout.menu);
				initTabs();
				
				Car carData[] = new Car[0];
				
				
				 CarAdapter adapter = new CarAdapter(this,R.layout.listview_item_settings,carData);
				
				listView1 = (ListView)findViewById(R.id.ListView_Menu);
				listView1.setAdapter(adapter);
			}

		// !Create Tabs and add Content
		private void initTabs()
			{
				TabHost host = (TabHost) findViewById(R.id.TabHost1);
				host.setup();
				// !Eintrag Tab aufbauen und hinzufügen
				TabSpec entryTab = host.newTabSpec("entry");
				entryTab.setIndicator(getResources().getString(R.string.main_menu_tab_entry), 
						getResources().getDrawable(android.R.drawable.ic_menu_add));
				entryTab.setContent(R.id.ScrollViewEntry);
				host.addTab(entryTab);
				// !Set Default Tab
				host.setCurrentTabByTag("entry");

				// !Verwaltung Tab aufbauen und hinzufügen
				TabSpec settingsTab = host.newTabSpec("settings");
				settingsTab.setIndicator(getResources().getString(R.string.main_menu_tab_settings), 
						getResources().getDrawable(android.R.drawable.ic_menu_manage));
				settingsTab.setContent(R.id.ListView_Menu);
				host.addTab(settingsTab);

				// !Log Tab aufbauen und hinzufügen
				TabSpec logTab = host.newTabSpec("log");
				logTab.setIndicator(getResources().getString(R.string.main_menu_tab_log), 
						getResources().getDrawable(android.R.drawable.ic_menu_sort_alphabetically));
				logTab.setContent(R.id.TableLayout_log);
				host.addTab(logTab);

				// !Statistic Tab aufbauen und hinzufügen
				TabSpec statsTab = host.newTabSpec("stats");
				statsTab.setIndicator(getResources().getString(R.string.main_menu_tab_stats), getResources().getDrawable(
				    android.R.drawable.ic_menu_sort_by_size));
				statsTab.setContent(R.id.TableLayout_stats);
				host.addTab(statsTab);
			}
		
		
			public static final String CAMERA_IMAGE_BUCKET_NAME =
        Environment.getExternalStorageDirectory().toString()
        + "/DCIM/Camera";
			public static final String CAMERA_IMAGE_BUCKET_ID =
        getBucketId(CAMERA_IMAGE_BUCKET_NAME);
			public static String getBucketId(String path) 
				{
					return String.valueOf(path.toLowerCase().hashCode());
				}
	
			public static List<String> getCameraImages(Context context) {
		    final String[] projection = { MediaStore.Images.Media.DATA };
		    final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
		    final String[] selectionArgs = { CAMERA_IMAGE_BUCKET_ID };
		    final Cursor cursor = context.getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI, 
		            projection, 
		            selection, 
		            selectionArgs, 
		            null);
		    ArrayList<String> result = new ArrayList<String>(cursor.getCount());
		    if (cursor.moveToFirst()) {
		        final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		        do {
		            final String data = cursor.getString(dataColumn);
		            result.add(data);
		        } while (cursor.moveToNext());
		    }
		    cursor.close();
		    return result;
		}

		@Override
		public boolean onCreateOptionsMenu(android.view.Menu menu)
			{
				super.onCreateOptionsMenu(menu);
				getMenuInflater().inflate(R.menu.entrymenu, menu);
				return true;
			}

		@Override
		public boolean onOptionsItemSelected(MenuItem item)
			{
				super.onOptionsItemSelected(item);
				if (item.getItemId() == R.id.entry_menu_item)
					{
						Toast toast = Toast.makeText(getApplicationContext(), "Dieses Feature wurde noch nicht Implementiert", Toast.LENGTH_LONG);
						toast.show();
					}
				return true;
			}
	}
