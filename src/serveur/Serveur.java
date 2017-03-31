package serveur;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

import protocole.*;

public class Serveur {


	private ServerSocket serverSocket;
	private  ArrayList<Joueur> joueurs;
	private Vector<Socket> sockets;
	private  Socket	joueur;
	public final static int port=2017;
	public final static int capacite=2;

	private int nbwait;

	private Session session;

	public Serveur(){
		this.joueurs = new ArrayList<Joueur>(capacite);
		this.sockets = new Vector<Socket>();

		for (int i = 0; i < capacite; i++)
		{
			Joueur j = new Joueur(this);
			this.joueurs.add(j);
			j.start();
		}
		this.nbwait=0;
	}



	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Attente connexion joueurs.");

			while(true){
				joueur = serverSocket.accept();
				synchronized (this)
				{
					 
					sockets.add(joueur);
					this.nbwait++;
					this.notify();
				}

			}

		} catch (IOException e) {
			System.err.println("Erreur : impossible de traiter le joueur");
			e.printStackTrace();
		}
	}
	public Socket removeFirstSocket()
	{
		Socket ret = sockets.get(0);
		sockets.removeElementAt(0);
		return ret;
	}

	public int getNbwait(){ return nbwait;}

	public void envoiRefus(Joueur j){
		try {
			j.sendToJoueur(ProtocoleCreateur.create(Protocole.REFUS));
			// ptete stopper le thread Joueur
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void addJoueur(Joueur j){
		if(this.joueurs.size()==0)
		{
			this.session = new Session(this);
			session.start();
		}

		else
		{
			this.joueurs.add(j); // on ajoute notre joueur connecté
			
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

		} 

		sendToAllJoueurButMe(ProtocoleCreateur.create(Protocole.CONNECTE,j.getPseudo()), j.getPseudo());
		nbwait--;
	}

	public  boolean pseudoAlreadyUsed(String pseudo)
	{
		for(Joueur j : joueurs){
			if(j.getPseudo().equals(pseudo)){
				System.out.println("Pseudo :"+pseudo+", j.get :"+j.getPseudo());
				return true;
			}
		}

		return false;
	}

	public void removeJoueur(Joueur j){
		sendToAllJoueurButMe(ProtocoleCreateur.create(Protocole.DECONNEXION,j.getPseudo()), j.getPseudo());
	}

	public void sendToAllJoueurButMe(String message, String joueurCourant){
		for(Joueur j:this.joueurs){
			try {
				if(j.getPseudo()!=null && !j.getPseudo().equals(joueurCourant))
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
