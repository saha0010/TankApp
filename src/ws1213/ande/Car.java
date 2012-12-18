/**
 * 
 */
package ws1213.ande;

import android.net.Uri;

/**
 * @author Sascha Hayton
 *
 */
public class Car
	{
		private long id;
		private Uri image;
		private String name;
		private boolean active;
		
		public Car()
			{
				super();
			}
		
		public Car(Uri u,String n,boolean a)
			{
				image = u;
				name = n;
				active = a;
			}
		
		public String getName(){return name;}
		public Uri getUri(){return image;}
		public long getId(){return id;}
		public boolean isActive(){return active;}
		
		public void setName(String s){name = s;}
		public void setUri(Uri u){image = u;}
		public void setId(long l){id = l;}
		public void setActive(boolean b){active = b;}
	}
