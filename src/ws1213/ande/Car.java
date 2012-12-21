package ws1213.ande;

public class Car
{
	private long _id;
	private String name;
	private String imageUrl;
	private boolean isActive;
	
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
	public Car(String name, String urlString, boolean b)
	{
		this.name = name;
		this.imageUrl = urlString;
		this.isActive = b;
	}
	public Car(String name, String urlString, int b)
	{
		this.name = name;
		this.imageUrl = urlString;
		this.isActive = (b>0);
	}
	

	public void setActive(boolean b)
	{
		this.isActive = b;
	}
	
	public boolean isActive()
	{
		return isActive;
	}
	
	public void set(Car car)
	{
		this._id = car.get_id();
		this.name = car.getName();
		this.imageUrl = car.getImageUrl();
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
