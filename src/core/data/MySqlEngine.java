package core.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.jdbc.PreparedStatement;

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
		String select = "SELECT device_id, latitude, longitude FROM bikesniffer.users " +
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
	
	public JSONArray searchFilesByCategory(String CatName) throws SQLException, JSONException
	{
		Statement statement  = connect.createStatement();
		ResultSet resultSet = statement.executeQuery("select * from ipv.file where Category='"+CatName+"'");
	    
		JSONArray array = new JSONArray();
		
		while (resultSet.next())
		{
			JSONObject json = new JSONObject();
			json.put("id", resultSet.getInt("File_Id"));
			json.put("FileName",resultSet.getString("FileName"));
			json.put("status",resultSet.getString("Status"));
			json.put("description",resultSet.getString("Description"));
			json.put("last_upd",resultSet.getString("Last_Upd."));
			json.put("mod_by", resultSet.getString("mod_by"));
			json.put("station", resultSet.getString("station"));
			array.put(json);
		}		
		System.out.println("send: "+array.toString());
		return array;
		
	}

	public JSONObject sendSchedule() throws SQLException, JSONException
	{
		Statement statement = connect.createStatement();
		Statement statement2=connect.createStatement();
		ResultSet resultSet = statement.executeQuery("select * from ipv.schedule");
	    ResultSet count= statement2.executeQuery("select count(*) from ipv.schedule");
	    count.next();
		JSONObject array = new JSONObject();
		System.out.println(count.getInt(1));
		array.append("count",count.getInt(1));
		
		while (resultSet.next())
		{
			JSONObject json = new JSONObject();
			json.put("id", resultSet.getString("Conf_id"));
			json.put("day", resultSet.getString("day"));
			json.put("hour", resultSet.getString("hour"));
			array.put("array", json);
		}	
		System.out.println("send: "+array.toString());
		return array;
	}
	
	public List<String> getAgentsByIds(List<String> ids) throws SQLException
	{
		Statement statement=connect.createStatement();
		List<String> list = new Vector<String>();
		
		for(int i = 0; i < ids.size(); i++)
		{
			ResultSet resultSet = statement.executeQuery("select Name from ipv.agents, ipv.accounts where username='"+ids.get(i)+"' and SSN=Id_Ag_Log");
			if(resultSet.next())
			{
				list.add(resultSet.getString(1));
			}
		}
		
		return list;		
	}
	
	public void insertAgent(String agData) throws JSONException, SQLException{
		JSONObject data=new JSONObject(agData);
		PreparedStatement insert= (PreparedStatement) connect.prepareStatement("Insert into ipv.agents(SSN,Name,Age,Rank_Id,Rank,Station_id) values (?,?,?,?,?,?)");
		insert.setString(1, data.getString("SSN"));
		insert.setString(2, data.getString("agName"));
		insert.setInt(3, data.getInt("age"));
		insert.setInt(4, data.getInt("rankId"));
		insert.setString(5, data.getString("rank"));
		insert.setInt(6, data.getInt("stationId"));
		insert.execute();
	}
	
	public void close() throws SQLException
	{		  
		if (connect != null) 
		{
			connect.close();
		}
	}

	public void addStation(String stationData) throws SQLException, JSONException{
		JSONObject data=new JSONObject(stationData);
		ResultSet count= connect.createStatement().executeQuery("select count(*) from ipv.address");
		count.next();
		int rowNumber=count.getInt(1);
		System.out.println(rowNumber);
		PreparedStatement statement = (PreparedStatement) connect.prepareStatement("INSERT INTO ipv.address(Id_Address,Street,No,Lat,Longit) VALUES (? , ?, ?, ?, ?)");
		statement.setInt(1,  ++rowNumber);
		statement.setString(2, data.getString("address"));
		statement.setInt(3, data.getInt("no"));
		statement.setString(4, data.getString("latitude"));
		statement.setString(5, data.getString("longit"));
		statement.execute();
		
		ResultSet count2= connect.createStatement().executeQuery("select count(*) from ipv.station");
		count2.next();
		int rowNumber2=count.getInt(1);
		PreparedStatement st = (PreparedStatement) connect.prepareStatement("INSERT INTO ipv.station(Station_id,Description,Station_Name,address)" +
				" VALUES (?,?,?,?)");
		st.setInt(1,  ++rowNumber2);
		st.setString(2, data.getString("description"));
		st.setString(3, data.getString("name"));
		st.setString(4, Integer.toString(rowNumber));
		st.execute();
	}
}
