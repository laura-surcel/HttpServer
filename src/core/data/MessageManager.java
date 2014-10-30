package core.data;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class MessageManager 
{
	private static MessageManager mInstance = null;
	private HashMap<String, HashMap<String, List<String>>> messages = new HashMap<String, HashMap<String, List<String>>>();
	
	public static MessageManager getInstance()
	{
		if(mInstance == null)
		{
			mInstance = new MessageManager();
		}
		return mInstance;
	}
	
	private MessageManager()
	{
		init();
	}
	
	private void init()
	{
		
	}
	
	public void addMessageForClient(String clientId, String message, String senderId)
	{
		System.out.println("[" + senderId + "] to [" + clientId + "]: " + message);
		if(!messages.containsKey(clientId))
		{
			messages.put(clientId, new HashMap<String, List<String>>());
		}
		
		HashMap<String, List<String>> msg = messages.get(clientId);
		
		if(!msg.containsKey(senderId))
		{
			msg.put(senderId, new Vector<String>());
		}
		
		msg.get(senderId).add(message);
	}
	
	public HashMap<String, List<String>> getMessagesForClient(String clientId)
	{
		return messages.get(clientId);
	}
	
	public void deleteMessagesFor(String clientId)
	{
		messages.remove(clientId);
	}
}
