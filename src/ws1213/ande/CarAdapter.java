/**
 * 
 */
package ws1213.ande;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * @author Sascha Hayton
 *
 */
public class CarAdapter extends ArrayAdapter<Car>
	{

		Context context;
		private Car data[] = null;
		int layoutResourceId;  

		
		public CarAdapter(Context context, int layoutResourceId, Car[] data)
			{
				super(context,layoutResourceId, data);
				this.layoutResourceId = layoutResourceId;
				this.context = context;
				this.data = data;
			}
	
		@Override
	public View getView(int position, View convertView, ViewGroup parent)
	  {
	  	View row = convertView;
	  	CarHolder holder;
	  	if(row == null)
	  		{
	  			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	  			row = inflater.inflate(layoutResourceId,parent,false);
	  			
	  			holder = new CarHolder();
	  			holder.imgIcon = (ImageButton)row.findViewById(R.id.imageButton1);
	  			holder.txtTitle = (TextView)row.findViewById(R.id.textView1);
	  			holder.radio = (RadioButton)row.findViewById(R.id.radioButton1);
	  			
	  			row.setTag(holder);
	  		}
	  	else{
	  		holder = (CarHolder)row.getTag();
	  	}
	  	
	  	Car car = data[position];
	  	holder.txtTitle.setText(car.getName());
	  	holder.imgIcon.setImageURI(car.getUri());
	  	holder.radio.setChecked(car.isActive());
	  	
			return row;
	  }

		static class CarHolder
		{
			ImageButton imgIcon;
			TextView txtTitle;
			RadioButton radio;
		}
	}
