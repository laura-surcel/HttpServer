package core.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import core.data.MessageManager;

public class GetMessagesHandler implements HttpHandler 
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
    	
    	//System.out.println("GetMessagesHandler: " + body);
    	
    	String response = "";
    	int code = 200;
    	
        try 
    	{
			JSONObject bodyJson = new JSONObject(body);
			String deviceId = bodyJson.getString("deviceId");
			HashMap<String, List<String>> messagesByClients = MessageManager.getInstance().getMessagesForClient(deviceId);
			
			JSONArray array = new JSONArray();
			for(String id: messagesByClients.keySet())
			{
				JSONObject objForClient = new JSONObject();
				objForClient.put("userId", id);
				JSONArray arrayForClient = new JSONArray();
				for(String msg: messagesByClients.get(id))
				{
					arrayForClient.put(msg);
				}
				objForClient.put("messages", arrayForClient);
				array.put(objForClient);
			}
			response = array.toString();
			MessageManager.getInstance().deleteMessagesFor(deviceId);
		} 
    	catch (JSONException e) 
    	{
    		response = "Wrong JSON Format";
    		code = 400;
			e.printStackTrace();
		}
		
        t.sendResponseHeaders(code, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
	}

}
