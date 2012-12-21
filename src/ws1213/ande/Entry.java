package ws1213.ande;

import java.sql.Date;

public class Entry
{
	private int			_id;
	private float		pProL;
	private int			newKilo;
	private int			newLiter;
	private Date		date;
	private Location	location;
	private Car			car;

	public Entry(int id,float ppl, int kilo, int liter, Date date, Location loc, Car car)
	{
		this._id = id;
		this.pProL = ppl;
		this.newKilo = kilo;
		this.newLiter = liter;
		this.date = date;
		this.location.set(loc);
		this.car.set(car);
	}

	
	
	@Override
	public String toString()
	{
		return "Entry [id=" + _id + ", pProL=" + pProL + ", newKilo=" + newKilo + ", newLiter=" + newLiter + ", date=" + date + ", location="
				+ location.getName() + ", car=" + car.getName() + "]";
	}



	public float getpProL()
	{
		return pProL;
	}

	public void setpProL(float pProL)
	{
		this.pProL = pProL;
	}

	public int get_id()
	{
		return _id;
	}

	public void set_id(int _id)
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

	public int getNewLiter()
	{
		return newLiter;
	}

	public void setNewLiter(int newLiter)
	{
		this.newLiter = newLiter;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
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
