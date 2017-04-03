package game;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

	private char[][] plateau;
	private static int taille = 15;
	private char[] tirageCourant;
	private int nombreLettreDisponible;
	private int scoreMax;
	private String meilleurMot;
	private String meilleurJoueur;
	private char[][] meilleurPlateau;

	/*position 0 : A donc position 0 : A a pour score 1 etc...
	 * Pas de Joker
	 * */
	private int[] alphabetScore = {1,3,3,2,1,4,2,4,1,8,10,1,2,1,1,3,8,1,1,1,1,4,10,10,10,10};
	private int[] alphabetNombre = {9,2,2,3,15,2,2,2,8,1,1,5,3,6,6,2,1,6,6,6,6,2,1,1,1,1};

	public Game(){
		this.plateau = new char[15][15];
		this.nombreLettreDisponible=100;
		this.scoreMax=0;
		this.meilleurMot="";
		this.meilleurJoueur="";
		for(int i=0;i<taille;i++)
			for(int j=0; j<taille;j++)
				this.plateau[i][j]='0';
	}
	
	public int nbLettresRestantes()
	{
		int compteur = 0;
		for(int i = 0; i < alphabetNombre.length; i++)
			compteur+=alphabetNombre[i];
		return compteur;
	}

	public String plateauToString(char[][] plateau){
		String plateauString="";

		for(int i=0;i<taille;i++)
			for(int j=0;j<taille;j++)
				plateauString+=plateau[i][j];

		return plateauString;		
	}

	public char[][] stringToPlateau(String plateauString){
		char[][] p = new char[this.taille][this.taille];
		for(int i = 0; i < this.taille; i++){
				for(int j = 0; j < this.taille; j++){
					p[i][j] = plateauString.charAt(i * this.taille + j);
				}
			 }
		
		return p;
	}

	public int charToInt(char lettre){
		return ((int) lettre)-65;
	}

	public int calculScore(ArrayList<String> reponses){

		String [] motPropose;
		int score =0;
		for(String mot : reponses){
			motPropose = mot.split("");

			for(int i=0; i<motPropose.length;i++){
				score+= alphabetScore[charToInt(motPropose[i].charAt(0))];
			}
		}

		return score;
	}
	
	public boolean majMeilleurScorePlateauMot(int score,ArrayList<String> reponses, char[][] proposition, String pseudo){
		
		if(score>this.scoreMax){
			this.scoreMax=score;
			this.meilleurJoueur = pseudo;
			this.meilleurMot="";
			for(String r : reponses){
				if(reponses.size()==1)
					this.meilleurMot+=r;
				else
					this.meilleurMot+=r;
			}
			this.meilleurPlateau=proposition;
			return true;
		}
		return false;
	}
	
	public void majTourDeJeu(){
		this.scoreMax=0;
		this.meilleurJoueur="";
		this.meilleurMot="";
		
		if(this.meilleurPlateau!=null)
			this.plateau=this.meilleurPlateau;
		this.meilleurPlateau=null;
	}

	public char[] tirage(int nombreLettres){

		if(nombreLettres>this.nombreLettreDisponible){
			nombreLettres=this.nombreLettreDisponible;
		}

		this.tirageCourant= new char[nombreLettres];
		int lettre;

		for(int i=0; i<nombreLettres;i++){

			do{
				lettre=ThreadLocalRandom.current().nextInt(65, 90 + 1);
			}while(this.alphabetNombre[lettre-65]==0);

			this.tirageCourant[i]=(char)lettre;
			this.alphabetNombre[lettre-65]--;
		}

		return this.tirageCourant;
	}


	//verif si les mots sont dans le dico
	public boolean motsValide(ArrayList<String> mots){
		int compt=0;
		try{
			BufferedReader buff;
			
			try{
				URL url = new URL("http://www.pallier.org/ressources/dicofr/liste.de.mots.francais.frgut.txt");
				buff=new BufferedReader(new InputStreamReader(url.openStream()));
			}
			catch(MalformedURLException e)
			{
				System.out.println(e);
				InputStream flux=new FileInputStream("dictionnaire/dictionnaire.txt");
				InputStreamReader lecture=new InputStreamReader(flux);
				buff=new BufferedReader(lecture);
				
			}
			
			String ligne;
			
			
			for(String r:mots)
				System.out.println(r);

			while ((ligne=buff.readLine())!=null){
				for(String mot: mots){
					ligne=Normalizer.normalize(ligne, Normalizer.Form.NFD);
					ligne=ligne.toUpperCase();
					if(mot.equals(ligne)){
						compt++;
					}
					
				}
			}
			buff.close(); 
		}catch (Exception e){
			System.out.println(e.toString());
		}
		System.out.println(compt);
		if(compt==mots.size())
			return true;
		else
			return false;
	}
	
	public boolean plateauVide(){
		for(int i=0;i<taille;i++)
			for(int j=0;j<taille;j++)
				if(!(this.plateau[i][j]=='0'))
					return false;
		return true;
					
	}
	
	// return list of created word or NULL if word wrong placed or don't exist
	public ArrayList<String> createdWords(char[][] proposition)
	{
		ArrayList<String> wordsCreated = new ArrayList<String>();
		boolean wordHorizontaly;
		
		int posI = -1;
		int posJ = -1;
		for(int i=0;i<taille;i++)
			for(int j=0;j<taille;j++)
				if(proposition[i][j]!=this.plateau[i][j] && posI ==-1 && posJ ==-1)
				{
					posI = i;
					posJ = j;
				}
		
		if(posI ==-1 || posJ ==-1){
			return null;
		}
		
		if(proposition[posI][posJ+1] != this.plateau[posI][posJ+1])
			wordHorizontaly = true;
		else
			wordHorizontaly = false;
		
		String wordCreatedSameDirection = null;
		if(!wordHorizontaly)
		{
			wordCreatedSameDirection = getWordCreatedSameDirectionVerticaly(posI, posJ, proposition);
			
			for(int i= posI; proposition[i][posJ] !='0'; i++){
				String s = null;
				s = getWordCreatedSameDirectionHorizontaly(i, posJ, proposition);
				if(s!=null && s.length()>1)
					wordsCreated.add(s);
			}
		}
		else
		{
			wordCreatedSameDirection = getWordCreatedSameDirectionHorizontaly(posI, posJ, proposition);
			
			for(int j= posJ; proposition[posI][j] !='0'; j++){
				
				String s = null;
				s = getWordCreatedSameDirectionVerticaly(posI, j, proposition);
				if(s!=null && s.length()>1)
					wordsCreated.add(s);
			}
		}
		if(wordCreatedSameDirection != null)
			wordsCreated.add(wordCreatedSameDirection);
		
		if(motsValide(wordsCreated))
			return wordsCreated;
		else{
			System.out.println("ici pas bon 2");
			return null;
		}
	}
	
	public String getWordCreatedSameDirectionHorizontaly( int posI, int posJ, char[][] proposition)
	{
		String word = "";
		int j = posJ;
		while(posJ>=0 && proposition[posI][posJ]!='0')
		{
			j=posJ;
			posJ--;
		}
		
		while(j<taille && proposition[posI][j] != '0')
		{
			if(proposition[posI][j]!='0')
				word+=proposition[posI][j];
			else
				word+=proposition[posI][j];
			j++;
		}
		return word;
	}
	
	public String getWordCreatedSameDirectionVerticaly( int posI, int posJ, char[][] proposition)
	{
		String word = "";
		int i = posI;
		while(posI>=0 && proposition[posI][posJ]!='0')
		{
			i=posI;
			posI--;
		}
		
		while(i<taille && proposition[i][posJ] != '0')
		{
			if(proposition[i][posJ]!='0')
				word+=proposition[i][posJ];
			else
				word+=proposition[i][posJ];
			i++;
		}
		return word;
	}
	
	public int getScoreMax() {
		return scoreMax;
	}

	public void setScoreMax(int scoreMax) {
		this.scoreMax = scoreMax;
	}

	public String getMeilleurMot() {
		return meilleurMot;
	}

	public void setMeilleurMot(String meilleurMot) {
		this.meilleurMot = meilleurMot;
	}

	public String getMeilleurJoueur() {
		return meilleurJoueur;
	}

	public void setMeilleurJoueur(String meilleurJoueur) {
		this.meilleurJoueur = meilleurJoueur;
	}
	
	public char[][] getPlateau() {
		return plateau;
	}

	public void setPlateau(char[][] plateau) {
		this.plateau = plateau;
	}
	
	public char[] getTirageCourant() {
		return tirageCourant;
	}

	public void setTirageCourant(char[] tirageCourant) {
		this.tirageCourant = tirageCourant;
	}

	public char[][] getMeilleurPlateau() {
		return meilleurPlateau;
	}

	public void setMeilleurPlateau(char[][] meilleurPlateau) {
		this.meilleurPlateau = meilleurPlateau;
	}
}