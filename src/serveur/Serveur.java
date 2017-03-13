package serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import protocole.*;

public class Serveur {


	private ServerSocket serverSocket;
	private ArrayList<Joueur> joueurs;
	public final static int port=2017;
	private Session session;

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
						new Joueur(clientSocket, Serveur.this).start(); 
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
		server.run();
	}

	 public void addJoueur(Joueur j){
		this.joueurs.add(j); // on ajoute notre joueur connecté

		try {
			j.sendToJoueur(ProtocoleCreateur.create(Protocole.BIENVENUE,j.getPseudo())); //A modif j.getPseudo par les vraies arguments placement/tirage/scores
			if(joueurs.size() == 1){}
				//this.session.start();  //A remettre quand session sera fait
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		sendToAllJoueurButMe(ProtocoleCreateur.create(Protocole.CONNECTE,j.getPseudo()), j.getPseudo());
	}
	 
	 public void removeJoueur(Joueur j){
		 this.joueurs.remove(j);
		 sendToAllJoueurButMe(ProtocoleCreateur.create(Protocole.DECONNEXION,j.getPseudo()), j.getPseudo());
	 }

	public void sendToAllJoueurButMe(String message, String joueurCourant){
		System.out.println("oui");
		for(Joueur j:this.joueurs){
			try {
				if(!j.getPseudo().equals(joueurCourant))
					j.sendToJoueur(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public void sendToAllJoueur(String message, String joueurCourant){
		System.out.println("oui");
		for(Joueur j:this.joueurs){
			try {
				if(!j.getPseudo().equals(joueurCourant))
					j.sendToJoueur(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
