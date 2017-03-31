package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import protocole.Protocole;

public class Joueur extends Thread {


	private String pseudo;
	private int score;

	private Socket socket;
	private PrintWriter outchan;
	private BufferedReader inchan;
	private Serveur serveur;

	private boolean estConnecte; 
	private boolean enAttente;

	public Joueur(Serveur serveur){
		this.serveur=serveur;
		this.score=0;
		this.estConnecte=true;

	}

	@Override
	public void run() {
		Socket s = null;
		while(true){
			
			
			synchronized (this.serveur)
		      {
				//si personnne dans la file d'attente, on attend
				if(this.serveur.getNbwait()==0)
		          try { this.serveur.wait(); } catch(InterruptedException e) {e.printStackTrace();}
		        s = this.serveur.removeFirstSocket();
			  }

			try {
				inchan = new BufferedReader(new InputStreamReader(s.getInputStream()));
			} catch (IOException e) {
				System.out.println("(Joueur) : Récupération inputStream impossible.");
			}
			try {
				outchan = new PrintWriter(s.getOutputStream());
			} catch (IOException e) {
				System.out.println("(Joueur) : Récupération outputStream impossible.");
			}

			this.socket=s;
			try{
				while(estConnecte){
					informationFromJoueur();
				}
				this.serveur.removeJoueur(this);

			}catch (Exception e) {
				System.out.println("(Joueur run) Exception : "+e.toString());
				synchronized(this.serveur)
				{
					this.serveur.removeJoueur(this);
				}
				try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public void informationFromJoueur(){
		String message = "";
		while(message.isEmpty() || !message.contains("/")){
			try {
				message = inchan.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(message==null) message = "";
		}

		System.out.println("(SERVER) informationFromJoueur re�oit : "+message);

		String[] infoMessages = message.split("/");
		String cmd = infoMessages[0];

		if (Protocole.CONNEXION.name().equals(cmd)) {
			try{

				//if(serveur.pseudoAlreadyUsed(infoMessages[1]))
				//serveur.envoiRefus(infoMessages[1]);

				this.pseudo = infoMessages[1];
				System.out.println("Nouvelle connexion d'un client nomme " + this.pseudo);
				synchronized (serveur){this.serveur.addJoueur(this);}
			}catch (Exception e) {
				System.err.println("Erreur : CONNEXION.");
				System.out.println(e);
			}
		}else if(Protocole.SORT.name().equals(cmd)){
			if(this.pseudo.equals(infoMessages[1])){
				this.estConnecte = false;
				synchronized(this.serveur)
				{
					this.serveur.removeJoueur(this);
				}
				System.out.println("Déonnexion de " + this.pseudo);
				
			}

		}else if(Protocole.TROUVE.name().equals(cmd)){
			// � compl�ter plus tard
		}else
			System.out.println("L'information re�ue ne correspond pas � notre protocole");

	}

	synchronized public void sendToJoueur(String message) throws IOException{
		if(outchan!=null){
			outchan.println(message);
			outchan.flush();
			if(outchan.checkError()){
				System.out.println("(sendToJoueur) "+pseudo+" est parti...");
				throw new IOException();
			}
		}
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public PrintWriter getOutchan() {
		return outchan;
	}

	public void setOutchan(PrintWriter outchan) {
		this.outchan = outchan;
	}

	public BufferedReader getInchan() {
		return inchan;
	}

	public void setInchan(BufferedReader inchan) {
		this.inchan = inchan;
	}

	public Serveur getServeur() {
		return serveur;
	}

	public void setServeur(Serveur serveur) {
		this.serveur = serveur;
	}

	public boolean getEstConnecte() {
		return estConnecte;
	}

	public void setEstConnecte(boolean isHere) {
		this.estConnecte = isHere;
	}

	public boolean getEnAttente() {
		return enAttente;
	}

	public void setEnAttente(boolean isWaiting) {
		this.enAttente = isWaiting;
	}


}
