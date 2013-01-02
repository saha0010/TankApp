package ws1213.ande;

import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TankDBAdapter
{

	private static final String	TAG						= "DBAapter";

	public static final String	ENTRY_KEY_ROWID			= "_id";
	public static final String	ENTRY_KEY_PPROL			= "pProL";
	public static final String	ENTRY_KEY_NEWKILO		= "newKilo";
	public static final String	ENTRY_KEY_NEWLITER		= "newLiter";
	public static final String	ENTRY_KEY_DATE			= "date";
	public static final String	ENTRY_KEY_LOCATION		= "lNr";
	public static final String	ENTRY_KEY_CAR			= "cNr";
	

	public static final String	LOCATION_KEY_ROWID		= "_id";
	public static final String	LOCATION_KEY_NAME		= "name";
	public static final int		LOCATION_NAME_COL_NUM	= 1;
	public static final int		LOCATION_ID_COL_NUM		= 0;


	public static final String	CAR_KEY_ROWID			= "_id";
	public static final String	CAR_KEY_NAME			= "name";
	public static final String	CAR_KEY_IMAGEURL		= "imageUrl";
	public static final String	CAR_KEY_ACTIVE			= "active";
	public static final int		CAR_ID_COL_NUM			= 0;
	public static final int		CAR_NAME_COL_NUM		= 1;
	public static final int		CAR_IMAGEURL_COL_NUM	= 2;
	public static final int		CAR_ACTIVE_COL_NUM		= 3;

	public static final String	DB_NAME					= "TankDB";
	private static final String	DB_ENTRY_TABLE			= "entry";
	private static final String	DB_LOCATION_TABLE		= "location";
	private static final String	DB_CAR_TABLE			= "car";
	private static final int	DB_VERSION				= 1;

	private static final String	DB_CREATE_CAR		= "CREATE TABLE IF NOT EXISTS car(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR not null, imageUrl VARCHAR not null, active INTEGER not null)";
	
	
	
	  private static final String	DB_CREATE_ENTRY		= "create table if not exists entry(_id INTEGER PRIMARY KEY AUTOINCREMENT, pProL FLOAT not null, newLiter FLOAT not null,"
															+ "newKilo INTEGER not null, date DATETIME not null, lNr INTEGER REFERENCES location(_id) not null, cNr INTEGER REFERENCES car(_id) not null)";
	  
	private static final String	DB_CREATE_LOCATION	= "CREATE TABLE IF NOT EXISTS location(_id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR not null)";

	private SQLiteDatabase		db;
	private final Context 		context;
	private DBOpenHelper		dbHelper;
	private static class DBOpenHelper extends SQLiteOpenHelper
	{


		public DBOpenHelper(Context context, String name, CursorFactory factory, int version)
		{
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			TankDBAdapter.onCreate(db);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.v(TAG, "Upgrading from verion " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS" + "DB_CAR_TABLE");
			db.execSQL("DROP TABLE IF EXISTS" + "DB_LOCATION_TABLE");
			db.execSQL("DROP TABLE IF EXISTS" + "DB_ENTRY_TABLE");
			onCreate(db);
		}
		
	}
	public TankDBAdapter(Context c)
	{
		this.context = c;
		dbHelper = new DBOpenHelper(context,DB_NAME,null,DB_VERSION);	
	}
	
	public static void onCreate(SQLiteDatabase db)
	{
		db.execSQL(DB_CREATE_CAR);
		db.execSQL(DB_CREATE_LOCATION);
		db.execSQL(DB_CREATE_ENTRY);
	}
	public void open() throws SQLiteException
	{
		try
		{
			db = dbHelper.getWritableDatabase();
			Log.v(TAG, "WRITABLE DB CREATED");
		}
		catch (SQLiteException ex)
		{
			db = dbHelper.getReadableDatabase();
			Log.v(TAG, "READABLE DB CREATED");
		}
	}

	public void close()
	{
		db.close();
	}

	public long insertCar(Car car)
	{	
		ContentValues newCarValues = new ContentValues();
		newCarValues.put(CAR_KEY_NAME, car.getName());
		newCarValues.put(CAR_KEY_IMAGEURL, car.getImageUrl());
		newCarValues.put(CAR_KEY_ACTIVE, car.isActive());	

		
		deactivateCar();
		
		long insertedRowIndex = db.insert(DB_CAR_TABLE, null, newCarValues);
		Log.v(TAG, "INSERT CAR " + insertedRowIndex + car.toString());
		return insertedRowIndex;
	}	
	public long getActiveCarId()
	{
		Cursor c = getAllCarsCursor();
		while(c.moveToNext())
		{
			if(c.getInt(CAR_ACTIVE_COL_NUM) == 1)
				Log.v(TAG, "getActiveCarID() @return"+ c.getInt(CAR_ID_COL_NUM));
				return c.getInt(CAR_ID_COL_NUM);
		}
		Log.v(TAG, "getActiveCarID() @return -1");
		 return -1; 	
	}
	
	public boolean deactivateCar()
	{
		long actveCarId = getActiveCarId();
		ContentValues carValues = new ContentValues();
		carValues.put(CAR_KEY_ACTIVE, 0);
		return (db.update(DB_CAR_TABLE, carValues, CAR_ID_COL_NUM + "=" + actveCarId, null)>0);
	}
	public boolean deactivateAllCars()
	{
		Cursor c = getAllCarsCursor();
		ContentValues carValues = new ContentValues();
		carValues.put(CAR_KEY_ACTIVE, 0);
		while(c.moveToNext())
		{
			db.update(DB_CAR_TABLE, carValues, CAR_ID_COL_NUM + "=" + c.getPosition(), null);
		}
		return true;
	}
	

	public long insertLocation(Location loc)
	{
		ContentValues newLocationValues = new ContentValues();
		newLocationValues.put(LOCATION_KEY_NAME, loc.getName());
		long insertedRowIndex = db.insert(DB_LOCATION_TABLE, null, newLocationValues);
		Log.v(TAG, "INSERT LOCATION " + insertedRowIndex + loc.toString());
		return insertedRowIndex;
	}	

	public long insertEntry(Entry ent)
	{
		ContentValues newEntryValues = new ContentValues();
		new SimpleDateFormat(AppActivity.DATE_FORMAT);
		newEntryValues.put(ENTRY_KEY_PPROL, ent.getpProL());
		newEntryValues.put(ENTRY_KEY_NEWLITER, ent.getNewLiter());
		newEntryValues.put(ENTRY_KEY_NEWKILO, ent.getNewKilo());
		newEntryValues.put(ENTRY_KEY_DATE, ent.getDate().getTime());
		newEntryValues.put(ENTRY_KEY_LOCATION, ent.getLocation().get_id());
		newEntryValues.put(ENTRY_KEY_CAR, ent.getCar().get_id());

		long insertedRowIndex = db.insert(DB_ENTRY_TABLE, null, newEntryValues);
		Log.v(TAG, "INSERT ENTRY " + insertedRowIndex + ent.toString());
		return insertedRowIndex;
	}

	public boolean removeEntrybyIndex(long rowIndex)
	{
		return (db.delete(DB_ENTRY_TABLE, ENTRY_KEY_ROWID + " = " + rowIndex, null) > 0);
	}

	
	public boolean updateImageFormCar(long key, String uri)
	{
		Log.v(TAG,"updateCar(long "+key+"),  Sting "+uri);
		ContentValues values  = new ContentValues();
		values.put(CAR_KEY_IMAGEURL, uri);
		
		return (db.update(DB_CAR_TABLE, values, CAR_KEY_ROWID + "=" + key, null) > 0);	
	}
	
	public boolean updateCar(long rowIndex, String name, String uri, boolean active)
	{
		Log.v(TAG,"updateCar(long "+rowIndex+"), String "+name+", Sting "+uri+", boolen "+active);
		int act;
		if(active)
			act = 1;
		else act = 0;
		
		ContentValues newValue = new ContentValues();
		newValue.put(CAR_KEY_NAME, name);
		newValue.put(CAR_KEY_IMAGEURL, uri);
		newValue.put(CAR_KEY_ACTIVE, act);
		
		return (db.update(DB_CAR_TABLE, newValue, CAR_KEY_ROWID + "=" + rowIndex, null) > 0);	
	}

	public boolean updateLocation(long rowIndex, String name)
	{
		ContentValues newValue = new ContentValues();
		newValue.put(LOCATION_KEY_NAME, name);
		return (db.update(DB_LOCATION_TABLE, newValue, LOCATION_KEY_ROWID + "=" + rowIndex, null) > 0);
	}
	
	public Cursor getAllEntriesCursor()
	{
		return db.query(DB_ENTRY_TABLE, new String[ ] { ENTRY_KEY_ROWID, ENTRY_KEY_PPROL,  ENTRY_KEY_NEWLITER, ENTRY_KEY_NEWKILO,ENTRY_KEY_DATE,
				ENTRY_KEY_LOCATION, ENTRY_KEY_CAR }, null, null, null, null, null);
	}

	public Cursor getAllCarsCursor()
	{
		return db.query(DB_CAR_TABLE, new String[ ] { CAR_KEY_ROWID, CAR_KEY_NAME, CAR_KEY_IMAGEURL, CAR_KEY_ACTIVE }, null, null, null, null, null);
	}

	public Cursor getAllLocationsCursor()
	{
		return db.query(DB_LOCATION_TABLE, new String[ ] { LOCATION_KEY_ROWID, LOCATION_KEY_NAME }, null, null, null, null, null);
	}
	
	public Cursor setCursorCarToIndex(long rowIndex)
	{
		Cursor rs = db.query(
				true, DB_CAR_TABLE, new String[ ] { CAR_KEY_ROWID, CAR_KEY_NAME, CAR_KEY_IMAGEURL, CAR_KEY_ACTIVE }, CAR_KEY_ROWID + "=" + rowIndex, null, null,
				null, null, null);
		if ((rs.getCount() == 0 || !rs.moveToFirst())) { throw new SQLException("No Car found for row: " + rowIndex); }
		return rs;
	}
	public Cursor setCursorLocationToIndex(long rowIndex)
	{
		Cursor rs = db.query(true, DB_LOCATION_TABLE, new String[ ]{LOCATION_KEY_ROWID, LOCATION_KEY_NAME}, LOCATION_KEY_ROWID +"="+rowIndex, null, null, null, null, null);
		if((rs.getCount() == 0 || !rs.moveToFirst())) {throw new SQLException("No Location found for row: "+ rowIndex); }
		return rs;
	}
	
	public Cursor setCursorEntryToIndex(long rowIndex)
	{
		Cursor rs = db.query(true, DB_ENTRY_TABLE, new String[ ]{ENTRY_KEY_ROWID, ENTRY_KEY_PPROL, ENTRY_KEY_NEWLITER,ENTRY_KEY_NEWKILO,
				ENTRY_KEY_DATE,ENTRY_KEY_LOCATION,ENTRY_KEY_CAR }, ENTRY_KEY_ROWID + "=" + rowIndex, null, null, null, null, null);
		if((rs.getCount() == 0 || !rs.moveToFirst())){throw new SQLException("No Entry found for row: "+ rowIndex); }
		return rs;
	}
	
	public void printLocationsOnLog()
	{
		Cursor c = getAllLocationsCursor();
		if(c.getCount()!=0)
		{
			while(c.moveToNext())
			{
				Log.v(TAG,"location "+c.getPosition()+": "+c.getColumnName(0)+"="+c.getInt(0)+", "+
														   c.getColumnName(1)+"="+c.getString(1));
			}
		}
		else
			Log.v(TAG,"Keine Einträge in Locations");
	}
	
	public Car getCarFromId(long rowId)
	{
		Cursor c = setCursorCarToIndex(rowId);
		return(new Car(c.getLong(0),c.getString(1),c.getString(2),c.getInt(3)));
	}
	
	public Location getLocationFromId(long rowId)
	{
		Cursor c = setCursorLocationToIndex(rowId);
		return (new Location(c.getLong(LOCATION_ID_COL_NUM),c.getString(LOCATION_NAME_COL_NUM)));
	}
	
	public Entry getEntryFromId(long rowId)
	{
		Cursor c = setCursorEntryToIndex(rowId);
		Car car = getCarFromId(c.getLong(6));
		Location loc = getLocationFromId(c.getLong(5));
		
		return (new Entry(c.getLong(0),c.getFloat(1),c.getFloat(2),c.getInt(3),c.getLong(4), loc, car));	
	}
	
	public void printCarsOnLog()
	{
		Cursor c = getAllCarsCursor();
		if(c.getCount() != 0){
			while(c.moveToNext()){
				Log.v(TAG, "car "+c.getPosition()+": "+c.getColumnName(0)+"="+c.getInt(0)+ ", " +
													   c.getColumnName(1)+"="+ c.getString(1)+", "+
													   c.getColumnName(2)+"="+ c.getString(2)+", "+
													   c.getColumnName(3)+"="+ c.getString(3));
			}
		}
		else
			Log.v(TAG,"Keine Einträge in CARS");
	}
	
	public Entry getLastEntryFromCarById(long rowId)
	{
		Cursor c = 	db.query(true, DB_ENTRY_TABLE, new String[ ] { ENTRY_KEY_ROWID, ENTRY_KEY_PPROL,ENTRY_KEY_NEWLITER, ENTRY_KEY_NEWKILO,  ENTRY_KEY_DATE,
				ENTRY_KEY_LOCATION, ENTRY_KEY_CAR }, ENTRY_KEY_CAR +"="+rowId, null, null, null, ENTRY_KEY_DATE+" DESC", "1");
		c.moveToFirst();
		Car car = getCarFromId(c.getLong(6));
		Location loc = getLocationFromId(c.getLong(5));
		
	
		return (new Entry(c.getLong(0),c.getFloat(1),c.getFloat(2),c.getInt(3),c.getLong(4), loc, car));	
	}
	
	public Cursor getAllEntriesFormCarByIdCursor(long rowId)
	{
		return db.query(DB_ENTRY_TABLE, new String[ ] { ENTRY_KEY_ROWID, ENTRY_KEY_PPROL, ENTRY_KEY_NEWLITER, ENTRY_KEY_NEWKILO, ENTRY_KEY_DATE,
				ENTRY_KEY_LOCATION, ENTRY_KEY_CAR }, ENTRY_KEY_CAR + "=" + rowId, null, null, null, ENTRY_KEY_DATE+" DESC");
	}
	
	public Cursor getAllEntriesFormCarByIdGroupedByDateCursor(long rowId)
	{
		return db.query(true, DB_ENTRY_TABLE, new String[ ] { ENTRY_KEY_ROWID, ENTRY_KEY_PPROL, ENTRY_KEY_NEWLITER, ENTRY_KEY_NEWKILO, ENTRY_KEY_DATE,
				ENTRY_KEY_LOCATION, ENTRY_KEY_CAR }, ENTRY_KEY_CAR+"="+rowId, null, "date", null, ENTRY_KEY_DATE+" DESC", null);
	
	}
	
	
}
