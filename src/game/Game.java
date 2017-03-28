package game;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

	private char[][] plateau;
	private static int taille = 15;
	private int nombreLettreDisponible;

	/*position 0 : A donc position 0 : A a pour score 1 etc...
	 * Pas de Joker
	 * */
	private int[] alphabetScore = {1,3,3,2,1,4,2,4,1,8,10,1,2,1,1,3,8,1,1,1,1,4,10,10,10,10};
	private int[] alphabetNombre = {9,2,2,3,15,2,2,2,8,1,1,5,3,6,6,2,1,6,6,6,6,2,1,1,1,1};

	public Game(){
		this.plateau = new char[15][15];
		nombreLettreDisponible=100;
		for(int i=0;i<taille;i++)
			for(int j=0; j<taille;j++)
				this.plateau[i][j]='0';
	}

	public String plateauToString(){
		String plateauString=null;

		for(int i=0;i<taille;i++)
			for(int j=0;j<taille;j++)
				plateauString+=this.plateau[i][j];

		return plateauString;		
	}

	public char[][] stringToPlateau(String plateauString){
		String [] chaine = plateauString.split("");

		for(int i=0;i<15;i++){
			System.arraycopy(chaine, (i*taille), this.plateau[i], 0, taille);
		}

		return this.plateau;
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

	public char[] tirage(int nombreLettres){

		if(nombreLettres>this.nombreLettreDisponible){
			nombreLettres=this.nombreLettreDisponible;
		}

		char[] lettresTires= new char[nombreLettres];
		int lettre;

		for(int i=0; i<nombreLettres;i++){

			do{
				lettre=ThreadLocalRandom.current().nextInt(65, 90 + 1);
			}while(this.alphabetNombre[lettre-65]==0);

			lettresTires[i]=(char)lettre;
			this.alphabetNombre[lettre-65]--;
		}

		return lettresTires;
	}


	//verif si les mots sont dans le dico
	public boolean motsValide(ArrayList<String> mots){
		try{
			InputStream flux=new FileInputStream("dictionnaire/dictionnaire.txt"); 
			InputStreamReader lecture=new InputStreamReader(flux);
			BufferedReader buff=new BufferedReader(lecture);
			String ligne;

			while ((ligne=buff.readLine())!=null){
				for(String mot: mots){
					if(!mot.equals(ligne))
						return false;
				}
			}
			buff.close(); 
		}catch (Exception e){
			System.out.println(e.toString());
		}
		
		return true;
	}
	
	public boolean plateauVide(){
		for(int i=0;i<taille;i++)
			for(int j=0;j<taille;j++)
				if(!(this.plateau[i][j]=='0'))
					return false;
		return true;
					
	}
	
	public boolean placementValide(char[][] proposition){
		
		ArrayList<String> mots = new ArrayList<String>();
		String motTrouve="";
		
		if(plateauVide())
			return true;
		
		for(int i=0;i<taille;i++){
			for(int j=0;j<taille;j++){
				
				if(proposition[i][j]!=this.plateau[i][j]){
					motTrouve+=proposition[i][j];
					
				}
				
				if(proposition[i][j]=='0' && motTrouve!=""){
					
				}
					
			}
		}
		
		if(motsValide(mots))
			return true;
		return true;
	}

	/*
	 * A faire 
	 * trouvermot()
	 * 
	 */
}
