package ws1213.ande;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class Entry
{
	private long		_id;
	private float		pProL;
	private float		newLiter;
	private int			newKilo;
	private Date		date;
	private Location	location;
	private Car			car;

	public Entry(long id, float ppl, float liter, int kilo, long date, Location loc, Car car)
	{
		this._id = id;
		this.pProL = ppl;
		this.newLiter = liter;
		this.newKilo = kilo;
		this.date = new Date(date);
		this.location = loc;
		this.car = car;
	}

	public Entry(float ppl, float liter, int kilo, long date, Location loc, Car car)
	{
		this.pProL = ppl;
		this.newLiter = liter;
		this.newKilo = kilo;
		this.date = new Date(date);
		this.location = loc;
		this.car = car;
	}

	public Entry(float ppl, float liter,int kilo, long date)
	{
		this.pProL = ppl;		
		this.newLiter = liter;
		this.newKilo = kilo;
		this.date = new Date(date);
	}

	@Override
	public String toString()
	{
		return "Entry [id=" + _id + ", pProL=" + pProL + "newLiter=" + newLiter + ", newKilo ="+newKilo + ", date="
				+ new SimpleDateFormat(AppActivity.DATE_FORMAT).format(date) + ", location=" + location.getName() + ", car=" + car.getName()
				+ "]";
	}

	public float getpProL()
	{
		return pProL;
	}

	public void setpProL(float pProL)
	{
		this.pProL = pProL;
	}

	public long get_id()
	{
		return _id;
	}

	public void set_id(long _id)
	{
		this._id = _id;
	}

	public int getNewKilo()
	{
		return newKilo;
	}

	public void setNewKilo(int newKilo)
	{
		this.newKilo = newKilo;
	}

	public float getNewLiter()
	{
		return newLiter;
	}

	public void setNewLiter(float newLiter)
	{
		this.newLiter = newLiter;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(long date)
	{
		this.date = new Date(date);
	}

	public Location getLocation()
	{
		return location;
	}

	public void setLocation(Location location)
	{
		this.location = location;
	}

	public Car getCar()
	{
		return car;
	}

	public void setCar(Car car)
	{
		this.car = car;
	}

}
