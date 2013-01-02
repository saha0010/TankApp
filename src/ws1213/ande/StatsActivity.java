package ws1213.ande;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.lang.ref.WeakReference;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Sascha Hayton
 * 
 *         "chart.googleapis.com/chart ?chf=a,s,000000 &chxs=0,FFFFFF,11.5 &chxt=x &chs=300x180 &cht=p3 &chco=7777CC|76A4FB|3399CC|3366CC &chd=s:CGBD
 *         &chdl=Waldmohr|Homburg|Esso|Shell"
 *         "http://chart.apis.google.com/chart?chxr=0,0,158&chxs=0,676767,11.5,0,_,676767&chxt=y&chbh=a&chs="
	            + "WIDTH_GRAPH"
	            + "x"
	            + "HEIGHT_GRAPH"
	            + "&cht=bvg&chco=0000FF,FF0000&chds=0,158,0,158&chd=t:"
	            + 79
	            + "|" + 30 + "&chdl=Tuo|Media&chdlp=b";
 */

public class StatsActivity extends AppActivity
{

	public static final String	TAG				= "stats";
	public static final String	URL				= "http://chart.googleapis.com/chart?chf=a,s,000000&chxs=0,FFFFFF,11.5&chxt=x&chs=300x180&cht=p3&chco=7777CC|76A4FB|3399CC|3366CC&chd=s:CGBD&chdl=Waldmohr|Homburg|Esso|Shell";

	TankDBAdapter				dbAdapter;
	SharedPreferences			mSettings;
	DecimalFormat				verbrauchFormat	= new DecimalFormat("##.##");

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		dbAdapter = new TankDBAdapter(this);
		mSettings = getSharedPreferences(SETTINGS, MODE_PRIVATE);

		TableRow statsTableRow1 = (TableRow) findViewById(R.id.tableRow_stats_FirstRow);
		TableRow statsTableRow2 = (TableRow) findViewById(R.id.tableRow_stats_SecondRow);
		TableRow statsTableRow3 = (TableRow) findViewById(R.id.tableRow_stats_ThirdRow);
		TableRow statsTableRow4 = (TableRow) findViewById(R.id.tableRow_stats_ForthRow);



		String row1Currency = getResources().getString(R.string.stats_litre);
		String row2Currency = getResources().getString(R.string.stats_length_notaion);
		String row3Currency = getResources().getString(R.string.stats_currency);
		String row4Currency = row3Currency;
		Log.v(TAG, "Active Car mSettings: " + mSettings.getLong(SETTINGS_ACTIVECAR_ID, -1) + "");

		dbAdapter.open();
		long activeCarId = mSettings.getLong(SETTINGS_ACTIVECAR_ID, -1);
		Cursor c = dbAdapter.getAllEntriesFormCarByIdCursor(activeCarId);
		if (c != null && c.getCount() > 0)
		{
			Log.v(TAG, "Anzahl einträge von Auto mit id:" + activeCarId + " = " + c.getCount());
			Entry lastEnt = dbAdapter.getLastEntryFromCarById(activeCarId);
			float liter = 0.0f;
			float tempPreisProLiter = 0.0f;
			int km = lastEnt.getNewKilo();
			while (c.moveToNext())
			{
				liter += c.getFloat(2);
				tempPreisProLiter += c.getFloat(1);

			}

			c = dbAdapter.getAllEntriesFormCarByIdGroupedByDateCursor(activeCarId);
			Log.v(TAG, "getAllEntriesFormCarByIdGroupedByDateCursor Colum count:=" + c.getColumnCount());
			Log.v(TAG, "getAllEntriesFormCarByIdGroupedByDateCursor cursor count:=" + c.getCount());
			Log.v(TAG, "LastEntry=" + lastEnt.toString());
			while (c.moveToNext())
			{

				Log.v(TAG, "--------------------" + (dbAdapter.getEntryFromId(c.getLong(0)).toString()));
			}
			float verbrauchPro100Km = liter / km * 100;
			int kmProMonat = km / 12;
			float preisProLiter = tempPreisProLiter / c.getCount();
			float monatLicheKosten = liter * tempPreisProLiter / 12;

			addTextToRowWithValues(statsTableRow1, verbrauchFormat.format(verbrauchPro100Km), row1Currency);
			addTextToRowWithValues(statsTableRow2, verbrauchFormat.format(kmProMonat), row2Currency);
			addTextToRowWithValues(statsTableRow3, verbrauchFormat.format(preisProLiter), row3Currency);
			addTextToRowWithValues(statsTableRow4, verbrauchFormat.format(monatLicheKosten), row4Currency);
			dbAdapter.close();

		}
		else
		{
			Toast a = Toast.makeText(this, getResources().getString(R.string.stats_toast_nostats), Toast.LENGTH_LONG);
			a.show();
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		
		ImageView ivGraph = (ImageView) findViewById(R.id.ImageView_stats);
		LoadingRemoteGraph task = new LoadingRemoteGraph(StatsActivity.this, ivGraph);
		task.execute(URL);
	}

	private void addTextToRowWithValues(final TableRow tableRow, String value, String currency)
	{

		int textColor = getResources().getColor(R.color.normal_green);
		float textSize = getResources().getDimension(R.dimen.label_size);

		TextView textView = new TextView(this);

		textView.setText(value + " " + currency);
		textView.setTextSize(textSize);
		textView.setTextColor(textColor);
		textView.setGravity(Gravity.RIGHT);
		tableRow.addView(textView);
	}

	private class LoadingRemoteGraph extends AsyncTask<String, Void, Bitmap>
	{
		private final WeakReference<ImageView>	imageViewReference;
		ProgressDialog							dialog	= null;
		private Context							context	= null;
		private ImageView						ivT;
		int										vHeight;
		int										vWidth;

		public LoadingRemoteGraph(Context con, final ImageView imgView)
		{
			context = con;
			imageViewReference = new WeakReference<ImageView>(imgView);
			ivT = imgView;
		}

		@Override
		protected void onPreExecute()
		{
			dialog = ProgressDialog.show(context, "", "Test", true);
		}

		@Override
		protected Bitmap doInBackground(String... params)
		{
			Bitmap bmGraphT = null;
			URL img = null;

			ViewTreeObserver vto = ivT.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener()
			{

				public void onGlobalLayout()
				{
					vHeight = ivT.getHeight();
					vWidth = ivT.getWidth();

				}

			});
			String url = params[0].replace("WIDTH_GRAPH", String.valueOf(vWidth));
			url = url.replace("HEIGHT_GRAPH", String.valueOf(vHeight));
			URL test = null; 
			try
			{
				img = new URL(url);
			}
			catch (MalformedURLException e)
			{
				e.printStackTrace();
			}

			try
			{
				bmGraphT = BitmapFactory.decodeStream(img.openStream());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			Log.v(TAG,img.getFile().toString()+", "+img.getPath()+", "+img.getHost());
			return bmGraphT;
		}

		@Override
		protected void onPostExecute(Bitmap bmT)
		{
			if (isCancelled())
			{
				bmT = null;
			}
			if (imageViewReference != null)
			{
				ImageView imageView = imageViewReference.get();
				if (imageView != null)
				{
					imageView.setImageBitmap(bmT);
				}
			}
			dialog.dismiss();
		}
	}
}
