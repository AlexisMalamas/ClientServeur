package serveur;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;


import game.Game;
import protocole.*;

public class Serveur {

	private ServerSocket serverSocket;
	private ArrayList<Joueur> joueurs;
	private Vector<Socket> sockets;
	private Socket	joueur;
	public final static int port=2017;
	public final static int capacite=2;
	
	private int nbwait;
	private int nbConnected;
	private int numeroSession;
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
		this.numeroSession = 1;
		this.nbConnected=0;
		
		//Partie HTML
		/*
		ArrayList<String> nom = new ArrayList<String>();
		ArrayList<Integer> scores = new ArrayList<Integer>();
		nom.add("jean");
		nom.add("bob");
		scores.add(10);
		scores.add(20);
		saveResults(42,nom,scores);
		*/
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

	public void bienvenue(Joueur j){
		try {
			j.sendToJoueur(ProtocoleCreateur.create(Protocole.BIENVENUE,this.session.getPlateau(),this.session.getTirageCourant()
					,this.session.scoreAllJoueur(),this.session.stringCurrentPhase(),String.valueOf(this.session.getChronometre())));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	
	public void connecte(Joueur j){
		sendToAllJoueurButMe(ProtocoleCreateur.create(Protocole.CONNECTE,j.getPseudo()), j.getPseudo());
	}

	public void addJoueur(Joueur j){
		this.nbConnected++;
		
		if(nbConnected==1){
			this.session = new Session(this);
			session.start();
		}
		bienvenue(j);
		connecte(j);

		this.nbwait--;
	}

	public void propositionRechercheValide(Joueur j){
		try {
			j.sendToJoueur(ProtocoleCreateur.create(Protocole.RVALIDE));
			sendToAllJoueurButMe(ProtocoleCreateur.create(Protocole.RATROUVE,j.getPseudo()), j.getPseudo());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public  boolean pseudoAlreadyUsed(String pseudo)
	{
		for(Joueur j : joueurs){
			if(j.getPseudo()!=null && j.getPseudo().equals(pseudo)){
				return true;
			}
		}

		return false;
	}

	public void removeJoueur(Joueur j){
		sendToAllJoueurButMe(ProtocoleCreateur.create(Protocole.DECONNEXION,j.getPseudo()), j.getPseudo());
		this.nbConnected--;
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

	public void sendToAllJoueur(String message){
		for(Joueur j:this.joueurs){
			try {
				if(j.getPseudo()!=null)
					j.sendToJoueur(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public void arreterRecherche() {
		synchronized (this) {
			this.notify();
		}
	}
	
	public void saveResults(int nbTour, ArrayList<String> nom,  ArrayList<Integer> scores)
	{
		String myOutputString	= "";
		
		RandomAccessFile myInputFile = null;
		String myLine = null;
		
		try{
			// --- Open the file
			myInputFile = new RandomAccessFile("resultats.html", "r");
			
			// --- Read line per line
			while (  (myLine = myInputFile.readLine() ) != null){
				if(myLine.equals("<h1>Resultats des sessions</h1>"))
					myLine += "\n"+generateHtml(nbTour, nom, scores);
				
				myOutputString += myLine+"\n";
			}
			
			File f = new File("resultats.html");
			FileWriter ffw=new FileWriter(f);
			
			ffw.write(myOutputString);
			ffw.close();

			
		}catch (IOException e){
			System.err.println("IOException : "+e.getMessage());
		}finally {
			try{
				myInputFile.close();
			}catch ( Exception e ){

			}
		}
	}
	public String generateHtml(int nbTour, ArrayList<String> nom,  ArrayList<Integer> scores)
	{
		String s = "<div class=resultats>\n"
				+"<h2>Session "+this.numeroSession+"</h2>\n"
				+"<span>Nombre de Tour : "+nbTour+"</span>\n"
				+"<table>\n"
				+"<tr><th>Nom du joueur</th><th>score</th></tr>\n";
		for(int i=0; i<nom.size(); i++)
		{
			s+="<tr><td>"+nom.get(i)+"</td><td>"+scores.get(i)+"</td></tr>\n";
		}
		
		s+="</table>\n"
			+"</div>\n";
	
		
		return s;
	}
	
	public int nbPlayer()
	{
		return joueurs.size();
	}

	public ArrayList<Joueur> getJoueurs() {
		return joueurs;
	}

	public void setJoueurs(ArrayList<Joueur> joueurs) {
		this.joueurs = joueurs;
	}


	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

}