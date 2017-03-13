package serveur;

public class Session extends Thread{

	private String[] boardGame;
	private Serveur server;
	private String currentPhase;
	
	public Session(Serveur server)
	{
		this.boardGame = new String[225];
		this.server = server;
	}
	
	@Override
	public void run() {
		
		while(server.nbPlayer() != 0)// tant que des joueurs sont connectées
		{
			researchPhase();
			submissionPhase();
			resultPhase();
			
		}
		
		publishResultOnTheWeb();
		
	}
	
	public void researchPhase()
	{
		this.currentPhase = "researchPhase";
	}
	
	public void submissionPhase()
	{
		this.currentPhase = "submissionPhase";
	}
	
	public void resultPhase()
	{
		this.currentPhase = "resultPhase";
	}
	
	
	public void publishResultOnTheWeb()
	{
		
	}
	
	public void setCaseBoardGame(int c, String letter)
	{
		this.boardGame[c] = letter;
	}
	
	public String getCurrentPhase()
	{
		return this.currentPhase;
	}

}
