package ws1213.ande;

public class Car
{
	private long _id;
	private String name;
	private String imageUrl;
	private boolean isActive;
	
	public Car(long rowIndex, String name, String urlString, int bool)
	{
		this._id = rowIndex;
		this.name = name;
		this.imageUrl = urlString;
		this.isActive = (bool > 0);
	}
	
	
	
	@Override
	public String toString()
	{
		return "Car: [_id=" + _id + ", name=" + name + ", imageUrl=" + imageUrl + ", isActive=" + isActive + "]";
	}



	public Car(long rowIndex, String name, String urlString)
	{
		this._id = rowIndex;
		this.name = name;
		this.imageUrl = urlString;
		this.isActive = false;
	}
	public Car(String name, String urlString)
	{
		this.name = name;
		this.imageUrl = urlString;
		this.isActive = false;
	}
	public Car(String name, String urlString, int bool)
	{
		this.name = name;
		this.imageUrl = urlString;
		this.isActive = (bool)>0;
	}

	

	public void setActive(boolean b)
	{
		this.isActive = b;
	}
	
	public boolean isActive()
	{
		return isActive;
	}
	
	public long get_id()
	{
		return _id;
	}

	public void set_id(long _id)
	{
		this._id = _id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getImageUrl()
	{
		return imageUrl;
	}

	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}
	
}
