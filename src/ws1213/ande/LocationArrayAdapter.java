package ws1213.ande;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LocationArrayAdapter extends ArrayAdapter<Location>
{

	private Context context;
	private int mResId;  

	public LocationArrayAdapter(Context c, int mResId, List<Location> items)
		{
			super(c,mResId, items);
			this.mResId = mResId;
			this.context = c;
		}
	@Override
	public View getDropDownView(int position, View convertView,
	ViewGroup parent) {
	// TODO Auto-generated method stub
	return getCustomView(position, convertView, parent);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	// TODO Auto-generated method stub
	return getCustomView(position, convertView, parent);
	}

	
public View getCustomView(int position, View convertView, ViewGroup parent)
	{
		TextView tv ;
		Location la = getItem(position);
		if(convertView == null)
		{
			tv = new TextView(context);
			tv.setText(la.getName());
		}
		else
			tv = (TextView)convertView;		
		return tv;	
	}
}
