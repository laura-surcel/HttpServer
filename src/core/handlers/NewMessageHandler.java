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

public class NewMessageHandler implements HttpHandler 
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
    	System.out.println("NewMessageHandler: " + body);
    	
    	String response = "";
    	int code = 201;
    	
        try 
    	{
			JSONObject bodyJson = new JSONObject(body);
			String senderId = bodyJson.getString("senderId");
			String receiverId = bodyJson.getString("receiverId");
			int messageType = bodyJson.getInt("msgType");
			MySqlEngine.getInstance().addMessage(senderId, receiverId, messageType);
			response = "Created";
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
