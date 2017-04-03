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
	private long chronometre;
	private boolean endRecherche;

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
		this.nombreTour=1;
		this.currentPhase=PHASE_SESSION;
		this.chronometre=0;
	}

	@Override
	public void run() {
		
		

		while(server.nbPlayer() != 0)// tant que des joueurs sont connectées
		{
			switch(currentPhase){
			case PHASE_SESSION:
				//this.temps(CHRONO_SESSION);
				this.currentPhase = PHASE_RECHERCHE;
				break;

			case PHASE_RECHERCHE:
				this.tour();
				this.endRecherche=false;
				this.temps(CHRONO_RECHERCHE);
				
				if(this.chronometre==0){
					this.rFin();
					this.game.majTourDeJeu();
					this.currentPhase=PHASE_RECHERCHE;
				}else
					this.currentPhase = PHASE_SOUMISSION;
				break;

			case PHASE_SOUMISSION:

				try {
					Thread.sleep(CHRONO_SOUMISSION);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				this.sFin();
				this.currentPhase = PHASE_RESULTAT;
				break;

			case PHASE_RESULTAT:
				this.bilan();

				//this.game.majTourDeJeu();
				try {
					Thread.sleep(CHRONO_RESULTAT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				this.nombreTour++;
				this.currentPhase = PHASE_RECHERCHE;
				break;

			default:
				break;
			}
		}

		publishResultOnTheWeb();

	}

	public void debutSession(){
		this.currentPhase = PHASE_SESSION;
		this.server.sendToAllJoueur(ProtocoleCreateur.create(Protocole.SESSION));
	}

	public void tour(){

		this.server.sendToAllJoueur(ProtocoleCreateur.create(Protocole.TOUR,this.getPlateau(),this.tirageCourant(this.game.getNombreLettreATires())));
	}

	public void researchPhase()
	{
		this.currentPhase = PHASE_RECHERCHE;
	}

	public void sFin(){
		this.server.sendToAllJoueur(ProtocoleCreateur.create(Protocole.SFIN));
	}

	public void submissionPhase()
	{
		this.currentPhase = PHASE_SOUMISSION;


	}

	public void rFin(){
		this.server.sendToAllJoueur(ProtocoleCreateur.create(Protocole.RFIN));
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
	
	public void getMajMeilleurScorePlateauMot(int score,ArrayList<String> reponses, char[][] proposition){
		this.game.majMeilleurScorePlateauMot(score, reponses, proposition);
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

				if(this.endRecherche==true){
					System.out.println("break endrecherche");
					break;
				}
				
				if(this.chronometre ==0)
					break;
			}
		}
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

}

