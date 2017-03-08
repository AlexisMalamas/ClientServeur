package serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Serveur {


	private ServerSocket serverSocket;
	private ArrayList<String> joueurs;
	public final static int port=2017;

	public Serveur(){
		this.joueurs = new ArrayList<>();
		this.startServeur();
	}


	public void startServeur(){
		Runnable server = new Runnable() {

			@Override
			public void run() {
				try {
					serverSocket = new ServerSocket(port);
					System.out.println("Attente connexion joueurs.");

					while(true){
						Socket clientSocket = serverSocket.accept();
						new Thread().start(); // à completer
					}
					
				} catch (IOException e) {
					System.err.println("Erreur : impossible de traiter le joueur");
					e.printStackTrace();
					
				}finally {
					if(serverSocket!=null && !serverSocket.isClosed()){
						try {
							serverSocket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		Thread serverThread = new Thread(server);
        serverThread.start();
	}

}
