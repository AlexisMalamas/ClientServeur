package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {

	public static void main(String args[])
	{
		BufferedReader inchan;
		PrintWriter outchan;
		ServerSocket serv;
		Socket client;

		try
		{
			System.out.println("---- SERVEUR ----");
			
			int port = Integer.parseInt(args[0]);
			System.out.println("port:"+port);
			serv = new ServerSocket(port);
			String command = "";

			while(true)
			{
				client = serv.accept();
				try
				{
					inchan = new BufferedReader(new InputStreamReader(client.getInputStream()));
					outchan = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
					while (true)
					{
						command = inchan.readLine();
						System.out.println("Commande recu "+command);
						if(command.equals("fin")) 
						{ 
							System.out.println("Fin de connexion.");
							client.close();
							serv.close();
							break;
						}
						System.out.println("evoie de "+command.toUpperCase());
						outchan.println(command.toUpperCase());
						outchan.flush();
					}
				}
				catch(IOException e) 
				{ 
					System.err.println("I/O Error"); e.printStackTrace();
				}
			}
			
		}
		catch(Throwable t) {t.printStackTrace(System.err); }
	}

}
