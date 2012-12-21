package ws1213.ande;

public class Location
{
	private long	_id;
	private String	name;

	public Location(long rowIndex, String name)
	{
		this._id = rowIndex;
		this.name = name;
	}

	public Location(String name)
	{
		this.name = name;
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
	
}
