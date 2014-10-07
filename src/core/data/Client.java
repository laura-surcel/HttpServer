package core.data;

public class Client 
{
	double latitude, longitude;
	
	public Client()
	{
		latitude 	= 0.0;
		longitude 	= 0.0;
	}
	
	public void setCoordinates(double lat, double longit)
	{
		latitude = lat;
		longitude = longit;
	}
	
	public double getLatitude()
	{
		return latitude;
	}
	
	public double getLongitude()
	{
		return longitude;
	}
}
