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
	public final static int capacite=2;
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

	synchronized public void addJoueur(Joueur j){

		if(pseudoAlreadyUsed(j.getPseudo()))
		{
			try {
				j.sendToJoueur(ProtocoleCreateur.create(Protocole.REFUS));
				// ptete stopper le thread Joueur
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else
		{
			this.joueurs.add(j); // on ajoute notre joueur connect�
			
			if(this.joueurs.size()==1)
			{
				this.session = new Session(this);
				session.start();
			}

			try {
				j.sendToJoueur(ProtocoleCreateur.create(Protocole.BIENVENUE,j.getPseudo())); //A modif j.getPseudo par les vraies arguments placement/tirage/scores
			} catch (IOException e) {
				e.printStackTrace();
			}

			sendToAllJoueurButMe(ProtocoleCreateur.create(Protocole.CONNECTE,j.getPseudo()), j.getPseudo());
		}
	}

	public boolean pseudoAlreadyUsed(String pseudo)
	{
		for(Joueur j : this.joueurs)
			if(j.getPseudo().equals(pseudo))
				return true;
		return false;
	}

	public void removeJoueur(Joueur j){
		this.joueurs.remove(j);
		sendToAllJoueurButMe(ProtocoleCreateur.create(Protocole.DECONNEXION,j.getPseudo()), j.getPseudo());
	}

	public void sendToAllJoueurButMe(String message, String joueurCourant){
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
		for(Joueur j:this.joueurs){
			try {
				if(!j.getPseudo().equals(joueurCourant))
					j.sendToJoueur(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public int nbPlayer()
	{
		return joueurs.size();
	}

}
