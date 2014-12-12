package core.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import core.data.MySqlEngine;

public class AcceptMeetingHandler implements HttpHandler 
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
    	System.out.println("AcceptMeetingHandler: " + body);
    	
    	String response = "";
    	int code = 200;
    	
        try 
    	{
			JSONObject bodyJson = new JSONObject(body);
			String deviceId = bodyJson.getString("deviceId");
			String senderId = bodyJson.getString("senderId");
			
			// delete the message
			long messageId = bodyJson.getLong("messageId");
			List<Long> list = new Vector<Long>();
			list.add(new Long(messageId));			
			MySqlEngine.getInstance().removeMessagesOfUser(deviceId, list);
			
			// create the meeting
			MySqlEngine.getInstance().addMeeting(senderId, deviceId);
			response = "Succeeded" + "\n";
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