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

	public Joueur(Socket socket,Serveur serveur){

		this.socket=socket;
		this.serveur=serveur;
		this.score=0;

		this.estConnecte=true;

		try {
			outchan = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("(Joueur) : Récupération outputStream de "+pseudo+" impossible.");
		}
		try {
			inchan = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			System.out.println("(Joueur) : Récupération inputStream de "+pseudo+" impossible.");
		}

	}
	
	@Override
	public void run() {
		try {
			while(estConnecte){
				informationFromJoueur();
			}
		}catch (Exception e) {
			System.out.println("(Joueur run) Exception : "+e.toString());
		}
	}
	
	public void informationFromJoueur(){
		String msg = "";
			try {
				msg = inchan.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(msg==null) msg = "";
			
			System.out.println("(SERVER) informationFromJoueur reçoit : "+msg);
			
			String[] msgs = msg.split("/");
			String cmd = msgs[0];
			
			if (Protocole.CONNEXION.name().equals(cmd)) {
				try{
					this.pseudo = msgs[1];
					System.out.println("Nouvelle connexion d'un client nomme " + this.pseudo);
				}catch (Exception e) {
					System.err.println("Erreur : Pas de pseudos donné.");
				}
			}else if(Protocole.SORT.name().equals(cmd)){
				if(this.pseudo.equals(msgs[1])){
					estConnecte = false;
					System.out.println("Déonnexion de " + this.pseudo);
				}
			
			}else if(Protocole.TROUVE.name().equals(cmd)){
				// à compléter plus tard
			}else
				System.out.println("L'information reçue ne correspond pas à notre protocole");

		
		
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
