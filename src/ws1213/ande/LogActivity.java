/**
 * 
 */
package ws1213.ande;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

/**
 * @author Sascha Hayton
 * 
 */
public class LogActivity extends AppActivity
{
	private TankDBAdapter 			dbAdapter;
	private SharedPreferences		mSettings;
	private SimpleDateFormat		sdf;
	private static final String 	TAG = "LOG";
	@Override
	public void onCreate(Bundle savedInstaceState)
	{
		super.onCreate(savedInstaceState);
		setContentView(R.layout.activity_log);
		TableLayout logTable = (TableLayout) findViewById(R.id.TableLayout_Log);
		initHeaderRow(logTable);

		dbAdapter = new TankDBAdapter(this);
		mSettings = getSharedPreferences(SETTINGS, MODE_PRIVATE);
		sdf = new SimpleDateFormat(DATE_FORMAT);
		getGetAllEntries(logTable);
	}

	private void initHeaderRow(TableLayout logTable)
	{
		TableRow logHeader = new TableRow(this);
		int textColor = getResources().getColor(R.color.normal_blue);
		float textSize = getResources().getDimension(R.dimen.label_size);
		//logHeader.setBackgroundColor(getResources().getColor(R.color.dark_blue));
		addTextToHeaderWithValues(logHeader, getResources().getString(R.string.log_date), textColor, textSize);
		addTextToHeaderWithValues(logHeader, getResources().getString(R.string.log_cost_per_litre), textColor, textSize);
		addTextToHeaderWithValues(logHeader, getResources().getString(R.string.log_litre), textColor, textSize);
		addTextToHeaderWithValues(logHeader, getResources().getString(R.string.log_km), textColor, textSize);
		addTextToHeaderWithValues(logHeader, getResources().getString(R.string.log_location), textColor, textSize);
		logTable.addView(logHeader);
	}

	private void addTextToHeaderWithValues(final TableRow tableRow, String text, int textColor, float textSize)
	{
		TextView textView = new TextView(this);
		textView.setTextSize(textSize);
		textView.setTextColor(textColor);
		textView.setText(text);
		textView.setBackgroundColor(getResources().getColor(android.R.color.black));
		textView.setGravity(android.view.Gravity.CENTER);
		tableRow.addView(textView);
		Log.v(TAG, "addedTextField to Row:"+tableRow.getId()+", String: "+text );
	}	
	
	private void addTextToRowWithValues(final TableRow tableRow, String text, int textColor, float textSize)
	{
		TextView textView = new TextView(this);
		textView.setTextSize(textSize);
		textView.setTextColor(textColor);
		textView.setText(text);
		textView.setGravity(android.view.Gravity.RIGHT);
		textView.setPadding(10, 0, 15, 0);
		textView.setBackgroundColor(getResources().getColor(android.R.color.black));
		tableRow.addView(textView);
		tableRow.setPadding(0, 2, 0,0);
		tableRow.setBackgroundColor(getResources().getColor(R.color.dark_blue));
		Log.v(TAG, "addedTextField to Row:"+tableRow.getId()+", String: "+text );
	}
	
	private void getGetAllEntries(TableLayout logTable)
	{
		dbAdapter.open();
		long carId = -1;
		if(mSettings.contains(SETTINGS_ACTIVECAR_ID))
			carId = mSettings.getLong(SETTINGS_ACTIVECAR_ID, -1);
		if(carId !=-1)
		{
			Cursor c = dbAdapter.getAllEntriesFormCarByIdCursor(carId);		
			int textColor = getResources().getColor(android.R.color.white);
			float textSize = getResources().getDimension(R.dimen.log_size);
			
			while(c.moveToNext())
			{
				Entry ent = dbAdapter.getEntryFromId(c.getLong(0));
				TableRow logRow = new TableRow(this);
				addTextToRowWithValues(logRow, sdf.format(ent.getDate()) ,textColor,textSize);
				addTextToRowWithValues(logRow, Float.toString(ent.getpProL()).replace(".", ","), textColor,textSize);
				addTextToRowWithValues(logRow, Float.toString(ent.getNewLiter()).replace(".", ",") ,textColor,textSize);
				addTextToRowWithValues(logRow, Integer.toString(ent.getNewKilo()) ,textColor,textSize);
				addTextToRowWithValues(logRow, ent.getLocation().getName() ,textColor,textSize);	
				logTable.addView(logRow);
			}
			
		}
		else{
			Toast a = Toast.makeText(getBaseContext(), R.string.tost_noactivecar, Toast.LENGTH_LONG);
			a.show();	
		}
			
		dbAdapter.close();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.log_optionmenu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		
		final int itemId = item.getItemId();
		switch(itemId)
		{
		case R.id.log_menuitem_save:
			saveLogOnSD();
			return true;
		}
		
		return false;
	}

	private int saveLogOnSD()
	{		
		String fileName = "TankAppLog.txt";
		File f = new File(getExternalFilesDir(null),fileName);
		
		FileOutputStream fos;
		
		
		byte[] data = new String("data to Write").getBytes();
		 try {
	            File myFile = new File("/sdcard/tankAppLog.txt");
	            myFile.createNewFile();
	            FileOutputStream fOut = new FileOutputStream(myFile);
	            OutputStreamWriter myOutWriter =new OutputStreamWriter(fOut);
	            myOutWriter.append("data to Write");
	            myOutWriter.close();
	            fOut.close();
	            Toast.makeText(getBaseContext(),"Done writing SD 'tankAppLog.txt'", Toast.LENGTH_SHORT).show();
	           // txtData.setText("");
	        } 
	        catch (Exception e) 
	        {
	            Toast.makeText(getBaseContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
	        }
		
		dbAdapter.close();
		return -1;
	}
}
