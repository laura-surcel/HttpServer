package core;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import core.handlers.*;

public class BikeSnifferServer
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
        server.setExecutor(null); // creates a default executor
        server.start();
	}
}
