package core.data;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClientManager 
{
	private static ClientManager mInstance = null;
	private HashMap<String, Client> clients = new HashMap<String, Client>();
	
	public static ClientManager getInstance()
	{
		if(mInstance == null)
		{
			mInstance = new ClientManager();
		}
		return mInstance;
	}
	
	private ClientManager()
	{
		init();
	}
	
	public void init()
	{
		addClient("1111");
		updateClient("1111", 44.40, 23.80);
	}
	
	public HashMap<String, Client> getClients()
	{
		return clients;
	}
	
	public void addClient(String agentId)
	{
		clients.put(agentId, new Client());
	}
	
	public void updateClient(String agentId, double lat, double longit)
	{
		Client client = clients.get(agentId);
		if(client != null)
		{
			client.setCoordinates(lat, longit);
		}
		else
		{
			System.out.println("Client doesn't exist!");
		}
	}
	
	public String getNeighbours(String id) throws JSONException
	{
		HashMap<String, Client> neighbours = getClients();
		JSONArray array = new JSONArray();
		
		for(String clientId : neighbours.keySet())
		{
			if (!clientId.equals(id))
			{	
				Client client = neighbours.get(clientId);
				JSONObject content = new JSONObject();
				content.put("id", clientId);
				content.put("lat", client.getLatitude());
				content.put("longit", client.getLongitude());
				array.put(content);
			}
		}
		System.out.println("sending: " + array.toString());
		return array.toString();
	}
}
