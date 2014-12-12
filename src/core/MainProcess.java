package core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;

import com.sun.net.httpserver.HttpServer;

import core.data.MySqlEngine;
import core.handlers.*;

public class MainProcess
{
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException 
	{
		HttpServer server = HttpServer.create(new InetSocketAddress(3128), 0);
        server.createContext("/connect", new ConnectionHandler());
        server.createContext("/position", new PositionHandler());
        server.createContext("/get-location-of-user", new GetLocationOfUserHandler());
        server.createContext("/message", new NewMessageHandler());
        server.createContext("/get-messages", new GetMessagesHandler());
        server.createContext("/remove-messages", new RemoveMessagesHandler());
        server.createContext("/accept-meeting", new AcceptMeetingHandler());
        server.createContext("/get-meetings", new GetMeetingsHandler());
        server.createContext("/set-name", new NameHandler());
        server.setExecutor(null); // creates a default executor
        
        try 
        {
			MySqlEngine.getInstance().setupConnection();
	        server.start();
		} 
        catch (ClassNotFoundException | SQLException e) 
        {
        	System.out.println("Connection to MySQL database failed!");
			e.printStackTrace();
		}
	}
}
