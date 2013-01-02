/**
 * 
 */
package ws1213.ande;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
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
	private final int					mResId;
	private final OnClickListener		ocl;
	private final OnLongClickListener	olcl;

	public CarArrayAdapter(Context c, int mResId, List<Car> items, OnClickListener ocl, OnLongClickListener olcl)
	{
		super(c, mResId, items);
		this.mResId = mResId;
		this.ocl = ocl;
		this.olcl = olcl;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		RelativeLayout mv;
		final Car car = getItem(position);
		final String name = car.getName();
		final String uri = car.getImageUrl();
		final boolean a = car.isActive();

		if (convertView == null)
		{
			mv = new RelativeLayout(getContext());
			mv.setSelected(a);
			final String inflater = Context.LAYOUT_INFLATER_SERVICE;
			final LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);

			vi.inflate(mResId, mv, true);
		}
		else
			mv = (RelativeLayout) convertView;

		final ImageButton b = (ImageButton) mv.findViewById(R.id.imageButton1);
		if (b != null)
		{
			final File f = new File(uri);
			if (f.exists())
				b.setImageURI(Uri.parse(uri));
			else
				b.setImageURI(AppActivity.DEFAULT_CAR_IMAGE_URI);

			b.setFocusable(false);
			b.setFocusableInTouchMode(false);
			b.setOnClickListener(ocl);
			b.setOnLongClickListener(olcl);
		}

		final TextView t = (TextView) mv.findViewById(R.id.textView1);
		t.setText(name);
		t.setFocusable(false);
		t.setFocusableInTouchMode(false);

		final RadioButton r = (RadioButton) mv.findViewById(R.id.radioButton1);
		r.setChecked(a);
		r.setFocusable(false);
		r.setFocusableInTouchMode(false);
		return mv;
	}

}
