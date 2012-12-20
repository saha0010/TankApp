/**
 * 
 */
package ws1213.ande;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
		private Context context;

		private int mResId;  

		
		public CarArrayAdapter(Context c, int mResId, List<Car> items)
			{
				super(c,mResId, items);
				this.mResId = mResId;
				this.context = c;
			}
	
		@Override
	public View getView(int position, View convertView, ViewGroup parent)
	  {
		RelativeLayout mv;
	  	Car car = getItem(position);
	  	String name = car.getName();
	  	String uri = car.getImageUrl();
	  	boolean a = car.isActive();
	  	
	  	if(convertView == null)
	  		{
	  			mv = new RelativeLayout(getContext());
	  			String inflater = Context.LAYOUT_INFLATER_SERVICE;
	  			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
	  			vi.inflate(mResId,mv,true);	  		
	  		}
	  	else{
	  		mv = (RelativeLayout)convertView;
	  	}
	  		ImageButton b = (ImageButton)mv.findViewById(R.id.imageButton1);
	  		b.setImageURI(Uri.parse(uri));
	  		
	  		TextView t = (TextView)mv.findViewById(R.id.textView1);
	  		t.setText(name);
	  		
	  		RadioButton r = (RadioButton)mv.findViewById(R.id.radioButton1);
	  		r.setChecked(a);
	  	
	  	
			return mv;
	  }


	}
