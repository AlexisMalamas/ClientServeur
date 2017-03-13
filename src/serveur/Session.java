package serveur;

public class Session extends Thread{

	private String[] boardGame;
	
	public Session()
	{
		this.boardGame = new String[225];
	}
	
	@Override
	public void run() {
		System.out.println("The game starts");

	}
	
	public void setCaseBoardGame(int c, String letter)
	{
		this.boardGame[c] = letter;
	}

}
