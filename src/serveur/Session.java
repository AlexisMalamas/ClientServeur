package serveur;

import java.util.ArrayList;
import java.util.HashMap;

import game.Game;
import protocole.Protocole;
import protocole.ProtocoleCreateur;

public class Session extends Thread{

	private Game game;
	private Serveur server;
	private int currentPhase;
	private int nombreTour;
	private long chronometre;
	private boolean endRecherche;
	
	// ajout pour qu'un client déco puisse être dans le tableau des scores finales
	private HashMap<String,Integer> joueurs;

	//Chrono en milliseconds
	private final static int CHRONO_SESSION = 20;
	private final static int CHRONO_RECHERCHE= 300; 
	private final static int CHRONO_SOUMISSION= 120;
	private final static int CHRONO_RESULTAT= 10;

	public final static int PHASE_SESSION = 0;
	public final static int PHASE_RECHERCHE = 1;
	public final static int PHASE_SOUMISSION = 2;
	public final static int PHASE_RESULTAT = 3;
	public final static int PAS_PHASE = 4;

	public Session(Serveur server)
	{
		this.game = new Game();
		this.server = server;
		this.nombreTour=0;
		this.currentPhase=PHASE_SESSION;
		this.chronometre=CHRONO_SESSION;
		
		this.joueurs = new HashMap<String,Integer>();
	}

	@Override
	public void run() {
		while(this.game.nbLettresRestantes()>=7)// tant que des joueurs sont connectées
		{
			synchronized(this)
			{
				if(this.server.getNbConnected()==0)
				{
					break;
				}
			}
			
			switch(currentPhase){
			case PHASE_SESSION:
				debutSession();
				this.endRecherche = false;
				this.chronometre = CHRONO_SESSION;
				this.temps(CHRONO_SESSION);
				this.currentPhase = PHASE_RECHERCHE;
				break;

			case PHASE_RECHERCHE:
				this.nombreTour++;
				this.tour();
				this.endRecherche=false;
				this.temps(CHRONO_RECHERCHE);

				if(this.chronometre==0){
					this.rFin();
					this.game.majTourDeJeu();
					this.currentPhase=PHASE_RECHERCHE;
				}else
				{
					this.currentPhase = PHASE_SOUMISSION;
					this.endRecherche = false;
				}
				break;

			case PHASE_SOUMISSION:
				this.endRecherche = false;
				this.temps(CHRONO_SOUMISSION);

				this.sFin();
				this.currentPhase = PHASE_RESULTAT;
				break;

			case PHASE_RESULTAT:
				for(Joueur j:this.server.getJoueurs())
				{
					j.setScore(j.getScore()+j.getScoreTour());
					j.setScoreTour(0);
					joueurs.put(j.getPseudo(), j.getScore());
				}
				
				this.bilan();
				this.game.majTourDeJeu();
				this.endRecherche = false;
				this.temps(CHRONO_RESULTAT);


				this.currentPhase = PHASE_RECHERCHE;
				break;

			default:
				break;
			}
		}
		
		this.vainqueur();
		this.server.saveResults(this.joueurs ,this.nombreTour);
	}

	public void debutSession(){
		this.currentPhase = PHASE_SESSION;
		this.server.sendToAllJoueur(ProtocoleCreateur.create(Protocole.SESSION));
	}

	public void tour(){

		this.server.sendToAllJoueur(ProtocoleCreateur.create(Protocole.TOUR,this.getPlateau(),this.tirageCourant(7)));
	}

	public void sFin(){
		this.server.sendToAllJoueur(ProtocoleCreateur.create(Protocole.SFIN));
	}

	public void rFin(){
		this.server.sendToAllJoueur(ProtocoleCreateur.create(Protocole.RFIN));
	}

	public void bilan()
	{
		this.server.sendToAllJoueur(ProtocoleCreateur.create(Protocole.BILAN,this.game.getMeilleurMot(),this.game.getMeilleurJoueur(),this.scoreAllJoueur()));
	}

	public void vainqueur(){
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
		String tirage = " ";

		if(this.game.getTirageCourant()==null)
			return tirage;

		for(Character c : this.game.getTirageCourant()){
			tirage += c;
		}
		return tirage;
	}

	public ArrayList<String> getWords(char[][] proposition){
		return this.game.createdWords(proposition);
	}

	public char[][] getStringtoPlateau(String plateauString){
		return this.game.stringToPlateau(plateauString);
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

	public int getCalculScore(ArrayList<String> reponses){
		return this.game.calculScore(reponses);
	}

	public boolean getMajMeilleurScorePlateauMot(int score,ArrayList<String> reponses, char[][] proposition, String pseudo){
		return this.game.majMeilleurScorePlateauMot(score, reponses, proposition, pseudo);
	}

	public void temps(int temps){
		long actualTimer;
		long lastTimeTimer = 0;
		this.chronometre = temps;

		while(true){
			actualTimer=System.currentTimeMillis();
			if(actualTimer - lastTimeTimer > 1000) // toutes les 1 sec
			{
				if(this.chronometre>0){
					this.chronometre--;
				}
				lastTimeTimer = System.currentTimeMillis();
				synchronized(this){
					if(this.endRecherche || this.server.getNbConnected()==0){
						break;
					}
				}

				if(this.chronometre ==0)
					break;
			}
		}
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

	public long getChronometre() {
		return chronometre;
	}


	public void setChronometre(int chronometre) {
		this.chronometre = chronometre;
	}


	public boolean isEndRecherche() {
		return endRecherche;
	}

	public void setEndRecherche(boolean endRecherche) {
		this.endRecherche = endRecherche;
	}
	
	public void addJoueurs(String s)
	{
		this.joueurs.put(s, 0);
	}

}

