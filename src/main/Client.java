package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
	public static void main(String args[])
	{
		try
		{
			System.out.println("--- CLIENT ---");
			
			int port = Integer.parseInt(args[0]);
			Socket sock = new Socket(InetAddress.getLocalHost(),port);
			
			BufferedReader inchan = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			PrintStream canalEcriture= new PrintStream(sock.getOutputStream());
			BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
			
			String ligne = new String();
			
			while(true)
			{
				System.out.println("?");
				ligne = console.readLine();
				

				if(ligne == ""){System.out.println("Connexion terminee");sock.close();break;}
				System.out.println("envoie de "+ligne);
				canalEcriture.println(ligne);
				canalEcriture.flush();
				ligne = inchan.readLine();

				System.out.println("!"+ligne);
				
				
			}
			
		}
		catch(Throwable t) {t.printStackTrace(System.err); }
	}
}
