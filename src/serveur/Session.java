package serveur;

import java.util.ArrayList;

import game.Game;
import protocole.Protocole;
import protocole.ProtocoleCreateur;

public class Session extends Thread{

	private Game game;
	private Serveur server;
	private int currentPhase;
	private int nombreTour;
	
	private final static int CHRONO_SESSION = 20;
	private final static int CHRONO_RECHERCHE=300; 
	private final static int CHRONO_SOUMISSION=120;
	private final static int CHRONO_RESULTAT=10;
	
	public final static int PHASE_SESSION = 0;
	public final static int PHASE_RECHERCHE = 1;
	public final static int PHASE_SOUMISSION = 2;
	public final static int PHASE_RESULTAT = 3;
	public final static int PAS_PHASE = 4;

	public Session(Serveur server)
	{
		this.game = new Game();
		this.server = server;
	}
	
	@Override
	public void run() {
		
		while(server.nbPlayer() != 0)// tant que des joueurs sont connectées
		{
			/*DEBUT SESSION*/
			
			//this.server.sendToAllJoueur(ProtocoleCreateur.create(Protocole.SESSION));
			
			//researchPhase();
			//submissionPhase();
			//resultPhase();
			//resultPhase();
			//bilan();
		}
		
		publishResultOnTheWeb();
		
	}
	
	public void debutSession(){
		this.currentPhase = PHASE_SESSION;
	}
	
	public void researchPhase()
	{
		this.currentPhase = PHASE_RECHERCHE;
	}
	
	public void submissionPhase()
	{
		this.currentPhase = PHASE_SOUMISSION;
	}
	
	public void resultPhase()
	{
		this.currentPhase = PHASE_RESULTAT;
		this.server.sendToAllJoueur(ProtocoleCreateur.create(Protocole.BILAN,this.game.getMeilleurMot(),this.game.getMeilleurJoueur(),this.scoreAllJoueur()));
		
	}
	
	public void bilan(){
		this.server.sendToAllJoueur(ProtocoleCreateur.create(Protocole.VAINQUEUR,this.scoreAllJoueur()));
		
	}
	
	public String scoreAllJoueur() {
		String score = "" + this.nombreTour + "*";
		for (Joueur j : this.server.getJoueurs()) {
			if(j.getPseudo()!=null)
				score += j.getPseudo() + "*" + j.getScore() + "*";
		}
		return score;
	}
	
	public String tirageCourant(int nombreLettre) {
			String tirage = "";
			for(Character c : this.game.tirage(nombreLettre)){
				tirage += c;
			}
			return tirage;
	}
	
	public String getTirageCourant(){
		String tirage = "";
		
		if(this.game.getTirageCourant()==null)
			return tirage;
		
		for(Character c : this.game.getTirageCourant()){
			tirage += c;
		}
		return tirage;
	}
	
	public String stringCurrentPhase(){
		if(this.currentPhase == PHASE_RECHERCHE)
			return "REC";
		else if(this.currentPhase == PHASE_RESULTAT)
			return "RES";
		else if(this.currentPhase == PHASE_SOUMISSION)
			return "SOU";
		return "DEB";
	}
	
	public void publishResultOnTheWeb()
	{
		
	}

	public int getCurrentPhase()
	{
		return this.currentPhase;
	}

	public int getNombreTour() {
		return nombreTour;
	}

	public void setNombreTour(int nombreTour) {
		this.nombreTour = nombreTour;
	}
	
	public String getPlateau(){
		return this.game.plateauToString(this.game.getPlateau());
	}
	
	public int chrono(){
		int time=0;
		
		System.out.println("phase courante :"+this.currentPhase);
		
		switch (this.currentPhase) {
		case PHASE_SESSION:
			time = CHRONO_SESSION;
			break;
		case PHASE_RECHERCHE:
			time = CHRONO_RECHERCHE;
			break;
		case PHASE_SOUMISSION:
			time = CHRONO_SOUMISSION;
			break;
		case PHASE_RESULTAT:
			time = CHRONO_RESULTAT;
			break;
		case PAS_PHASE:
			time=0;
			break;
		default:
			break;
		}
		return time;
	}
}
