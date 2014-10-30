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

public class ConnectionHandler implements HttpHandler 
{
    public void handle(HttpExchange t) throws IOException 
    {
    	BufferedReader inFromClient = new BufferedReader(new InputStreamReader(t.getRequestBody()));
		
    	String body = "", line;
    	while((line = inFromClient.readLine()) != null)
    	{
    		body = body + line;
    	}
    	System.out.println("ConnectionHandler: " + body);
    	
    	String response = "Client connected";
    	int code = 200;
    	
        try 
    	{
			JSONObject bodyJson = new JSONObject(body);
			MySqlEngine.getInstance().updateUser(bodyJson.getString("deviceId"), bodyJson.getBoolean("connect"));
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
