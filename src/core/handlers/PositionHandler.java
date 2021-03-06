package core.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import core.data.MySqlEngine;

public class PositionHandler implements HttpHandler 
{
	@Override
	public void handle(HttpExchange t) throws IOException 
	{
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(t.getRequestBody()));
		
    	String body = "", line;
    	while((line = inFromClient.readLine()) != null)
    	{
    		body = body + line;
    	}
    	System.out.println("PositionHandler: " + body);
    	
    	String response = "";
    	int code = 200;
    	
        try 
    	{
			JSONObject bodyJson = new JSONObject(body);
			String deviceId = bodyJson.getString("deviceId");
			double lat = bodyJson.getDouble("lat");
			double longit = bodyJson.getDouble("longit");
			double radius = bodyJson.getDouble("radius");
			
			MySqlEngine.getInstance().updateUserLocation(deviceId, lat, longit);
			response = MySqlEngine.getInstance().getNeighbours(deviceId, lat, longit, radius) + "\n";
		} 
    	catch (JSONException e) 
    	{
    		response = "Wrong JSON Format";
    		code = 400;
			e.printStackTrace();
		} 
        catch (SQLException e) 
		{
			response = "SQL error";
			code = 500;
			e.printStackTrace();
		}
		
        t.sendResponseHeaders(code, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
	}
}
