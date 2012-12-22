/**
 * 
 */
package ws1213.ande;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Sascha Hayton
 * 
 */
public class CarArrayAdapter extends ArrayAdapter<Car>
{
	private Context				context;
	private int					mResId;
	private OnClickListener		ocl;
	private OnLongClickListener olcl;


	public CarArrayAdapter(Context c, int mResId, List<Car> items, OnClickListener ocl, OnLongClickListener olcl)
	{
		super(c, mResId, items);
		this.mResId = mResId;
		this.context = c;
		this.ocl = ocl;
		this.olcl = olcl;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		RelativeLayout mv;
		final Car car = getItem(position);
		String name = car.getName();
		String uri = car.getImageUrl();
		boolean a = car.isActive();
		
		if (convertView == null)
		{
			mv = new RelativeLayout(getContext());
			mv.setSelected(a);
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);

			vi.inflate(mResId, mv, true);
		}
		else
		{
			mv = (RelativeLayout) convertView;
		}
		ImageButton b = (ImageButton) mv.findViewById(R.id.imageButton1);
		if(b!=null){
		b.setImageURI(Uri.parse(uri));
		b.setFocusable(false);
		b.setFocusableInTouchMode(false);
		b.setOnClickListener(ocl);
		b.setOnLongClickListener(olcl);
		}
		

		TextView t = (TextView) mv.findViewById(R.id.textView1);
		t.setText(name);
		t.setFocusable(false);
		t.setFocusableInTouchMode(false);

		RadioButton r = (RadioButton) mv.findViewById(R.id.radioButton1);
		r.setChecked(a);
		r.setFocusable(false);
		r.setFocusableInTouchMode(false);
		return mv;
	}



}
