package core.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MySqlEngine 
{	
	private Connection connect = null;
	private static MySqlEngine sInstance = null; 
	
	public static MySqlEngine getInstance()
	{
		if(sInstance == null)
		{
			sInstance = new MySqlEngine();
		}
		return sInstance;
	}
	
	public void setupConnection() throws ClassNotFoundException, SQLException
	{
		String url="jdbc:mysql://localhost/mysql";
        Properties prop=new Properties();
        prop.setProperty("user","root");
        prop.setProperty("password","root");
		connect = DriverManager.getConnection(url, prop);
	}
	
	public void close() throws SQLException
	{		  
		if (connect != null) 
		{
			connect.close();
		}
	}
	
	public void updateUser(String deviceId, boolean active) throws SQLException
	{
		Statement statement = connect.createStatement();
		int isActive = active ? 1 : 0;
		String insert = "INSERT INTO bikesniffer.users " +
				"(`device_id`, `date_updated`, `active`) " +
				"VALUES ('" + deviceId + "', NOW(), "+ isActive + ") " +
				"ON DUPLICATE KEY UPDATE date_updated = NOW(), active = "+isActive;
		try
		{
			statement.executeUpdate(insert);
		}
		catch(SQLException e)
		{
			throw(e);
		}
		finally 
		{
			statement.close();
		}
	}
	
	public void updateUserName(String deviceId, String name) throws SQLException
	{
		Statement statement = connect.createStatement();
		String insert = "INSERT INTO bikesniffer.users " +
				"(`device_id`, `date_updated`, `name`) " +
				"VALUES ('" + deviceId + "', NOW(), '"+ name + "') " +
				"ON DUPLICATE KEY UPDATE date_updated = NOW(), name = '" + name + "'";
		try
		{
			statement.executeUpdate(insert);
		}
		catch(SQLException e)
		{
			throw(e);
		}
		finally 
		{
			statement.close();
		}
	}
	
	public void updateUserLocation(String deviceId, double lat, double longit) throws SQLException
	{
		Statement statement = connect.createStatement();
		String update = "UPDATE bikesniffer.users " +
						"SET latitude = " + lat + ", longitude = " + longit + " " +
						"WHERE device_id = '" + deviceId + "'";
		try
		{
			statement.executeUpdate(update);
		}
		catch(SQLException e)
		{
			throw(e);
		}
		finally 
		{
			statement.close();
		}
	}
	
	public String getNeighbours(String id, double lat, double longit, double radius) throws JSONException, SQLException
	{
		Statement statement = connect.createStatement();
		String select = "SELECT device_id, name, latitude, longitude FROM bikesniffer.users " +
						"WHERE active = 1";
		try
		{
			ResultSet resultSet = statement.executeQuery(select);
			GeoPosition gp = new GeoPosition(lat, longit);
			JSONArray array = new JSONArray();
			while (resultSet.next())
			{
				String idd = resultSet.getString("device_id");
				if(!idd.equals(id))
				{
					JSONObject content = new JSONObject();
					double lat1 = resultSet.getDouble("latitude");
					double longit1 = resultSet.getDouble("longitude");
					if(gp.getDistanceInKmFrom(lat1, longit1) <= radius)
					{
						content.put("id", idd);
						content.put("lat", lat1);
						content.put("longit", longit1);
						content.put("name", resultSet.getString("name"));
						array.put(content);
					}
				}
			}
			
			System.out.println("getNeighbours: " + array.toString());
			return array.toString();	
		}
		catch(SQLException e)
		{
			throw(e);
		}
		finally 
		{
			statement.close();
		}
	}
	
	public String getLocationOfUser(String userId) throws SQLException, JSONException
	{
		Statement statement = connect.createStatement();
		String select = "SELECT latitude, longitude FROM bikesniffer.users " +
						"WHERE device_id = '" + userId + "'";
		try
		{
			ResultSet resultSet = statement.executeQuery(select);
			JSONObject json = new JSONObject();
			
			while (resultSet.next())
			{
				Double lat = resultSet.getDouble("latitude");
				Double longit = resultSet.getDouble("longitude");
				json.put("latitude", lat);
				json.put("longitude", longit);
			}
			
			System.out.println("getLocationOfUser " + userId + ": " + json.toString());
			return json.toString();	
		}
		catch(SQLException | JSONException e)
		{
			throw(e);
		}
		finally 
		{
			statement.close();
		}
	}
	
	public void addMessage(String senderId, String receiverId, int messageType) throws SQLException
	{
		Statement statement = connect.createStatement();
		String insert = "INSERT INTO bikesniffer.messages " +
				"(`sender_id`, `receiver_id`, `type`, `date_created`) " +
				"VALUES ('" + senderId + "', '"+ receiverId + "', "+ messageType + ", NOW())";
		try
		{
			statement.executeUpdate(insert);
		}
		catch(SQLException e)
		{
			throw(e);
		}
		finally 
		{
			statement.close();
		}
	}
	
	public String getMessagesForUser(String userId) throws JSONException, SQLException
	{
		Statement statement = connect.createStatement();
		String select = "SELECT bikesniffer.messages.id, bikesniffer.messages.sender_id, " +
						"bikesniffer.messages.type, bikesniffer.users.name FROM bikesniffer.messages, bikesniffer.users " +
						"WHERE receiver_id = '" + userId + "' " +
						"AND bikesniffer.messages.sender_id = bikesniffer.users.device_id";
		try
		{
			ResultSet resultSet = statement.executeQuery(select);
			JSONArray array = new JSONArray();
			while (resultSet.next())
			{
				JSONObject content = new JSONObject();
				content.put("id", resultSet.getLong("id"));
				content.put("sender_id", resultSet.getString("sender_id"));
				content.put("sender_name", resultSet.getString("name"));
				content.put("type", resultSet.getInt("type"));
				array.put(content);
			}
			
			return array.toString();	
		}
		catch(SQLException e)
		{
			throw(e);
		}
		finally 
		{
			statement.close();
		}
	}
	
	public void addMeeting(String senderId, String receiverId) throws SQLException
	{
		Statement statement = connect.createStatement();
		String insert = "INSERT INTO bikesniffer.meetings " +
				"(`interrogator_id`, `interrogated_id`, `date_created`) " +
				"VALUES ('" + senderId + "', '"+ receiverId + "', NOW())";
		try
		{
			statement.executeUpdate(insert);
		}
		catch(SQLException e)
		{
			throw(e);
		}
		finally 
		{
			statement.close();
		}
	}
	
	public String getMeetingsForUser(String userId) throws JSONException, SQLException
	{
		Statement statement = connect.createStatement();
		String select = "SELECT bikesniffer.meetings.id, bikesniffer.meetings.interrogator_id, " +
						"bikesniffer.users.name, bikesniffer.users.latitude, bikesniffer.users.longitude FROM bikesniffer.meetings, bikesniffer.users " +
						"WHERE interrogated_id = '" + userId + "' " +
						"AND bikesniffer.meetings.interrogator_id = bikesniffer.users.device_id";
		try
		{
			ResultSet resultSet = statement.executeQuery(select);
			JSONArray array = new JSONArray();
			while (resultSet.next())
			{
				JSONObject content = new JSONObject();
				content.put("id", resultSet.getLong("id"));
				content.put("interrogator_id", resultSet.getString("interrogator_id"));
				content.put("interrogator_name", resultSet.getString("name"));
				content.put("lat", resultSet.getDouble("latitude"));
				content.put("longit", resultSet.getDouble("longitude"));
				array.put(content);
			}

			select = "SELECT bikesniffer.meetings.id, bikesniffer.meetings.interrogated_id, " +
					"bikesniffer.users.name, bikesniffer.users.latitude, bikesniffer.users.longitude FROM bikesniffer.meetings, bikesniffer.users " +
					"WHERE interrogator_id = '" + userId + "' " +
					"AND bikesniffer.meetings.interrogated_id = bikesniffer.users.device_id";
			resultSet = statement.executeQuery(select);
			
			while (resultSet.next())
			{
				JSONObject content = new JSONObject();
				content.put("id", resultSet.getLong("id"));
				content.put("interrogated_id", resultSet.getString("interrogated_id"));
				content.put("interrogated_name", resultSet.getString("name"));
				content.put("lat", resultSet.getDouble("latitude"));
				content.put("longit", resultSet.getDouble("longitude"));
				array.put(content);
			}
			
			return array.toString();	
		}
		catch(SQLException e)
		{
			throw(e);
		}
		finally 
		{
			statement.close();
		}
	}
	public void removeMessagesOfUser(String userId, List<Long> ids) throws SQLException
	{
		String idsString = "";
		for (Long id : ids)
		{
			if(idsString.equals(""))
			{
				idsString = idsString + id;
			}
			else
			{
				idsString = idsString + ", " + id;
			}
		}
		Statement statement = connect.createStatement();
		String select = "DELETE FROM bikesniffer.messages " +
						"WHERE receiver_id = '" + userId + "' " +
						"AND id IN ("+ idsString +")";
		try
		{
			statement.executeUpdate(select);			
		}
		catch(SQLException e)
		{
			throw(e);
		}
		finally 
		{
			statement.close();
		}
	}
}
