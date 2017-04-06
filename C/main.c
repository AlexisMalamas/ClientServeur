#include "main.h"

int loop = 1;

int isBestWord = 0;
int nbMessage = 0;
char userMessage[NB_MESSAGE_MAX][SIZE_MESSAGE];
char userName[SIZE_USER_NAME];
char gameBoardByServer[NB_LETTER_HEIGHT*NB_LETTER_WIDTH];
char gameBoardByClient[NB_LETTER_HEIGHT*NB_LETTER_WIDTH];
char availableLetter[NB_LETTER_AVAILABLE];
int letterUsed[NB_LETTER_AVAILABLE];
int currentPickLetter = -1;
char score[1000];
char phase[30];
int temps = 0;

void updateBestWord(char c)
{
    isBestWord = (int)c;
}

void addMessage(char *message)
{
    if(nbMessage>=NB_MESSAGE_MAX)
        nbMessage = 0;
    strcpy(userMessage[nbMessage], message);
    nbMessage++;
}

void clearBuffer()
{
    int c = 0;
    while (c != '\n' && c != EOF)
    {
        c = getchar();
    }
}

int readTerminal(char *chaine, int longueur)
{
    char *positionEntree = NULL;

    if (fgets(chaine, longueur, stdin) != NULL)
    {
        positionEntree = strchr(chaine, '\n');
        if (positionEntree != NULL)
        {
            *positionEntree = '\0';
        }
        else
        {
            clearBuffer();
        }
        return 1;
    }
    else
    {
        clearBuffer();
        return 0;
    }
}

void treatClickMouse(int x, int y)
{
    // on click sur une lettre disponible
    if(x>=(WINDOW_WIDTH - 150) && x<=(WINDOW_WIDTH - 150 + LETTER_WIDTH) &&
        y>=50 && y<=LETTER_HEIGHT*(NB_LETTER_AVAILABLE+1) && letterUsed[(y-50)/LETTER_HEIGHT]==0)
        {
            if(currentPickLetter != -1)
                letterUsed[currentPickLetter] = 0;

            currentPickLetter = (y-50)/LETTER_HEIGHT;
            letterUsed[currentPickLetter] = 1;
        }

    //on place la lettre sur le plateau
    if(x>= 10 && x<= 10 + 675 && y>=30 && y<=30+675)
    {
        int line = (y-30)/(675/NB_LETTER_HEIGHT)+1;
        int column = (x-10)/(675/NB_LETTER_WIDTH)+1;

        if(gameBoardByClient[(line-1)*NB_LETTER_WIDTH+(column-1)]=='0' && currentPickLetter!=-1)
        {
            gameBoardByClient[(line-1)*NB_LETTER_WIDTH+(column-1)] = availableLetter[currentPickLetter];
            currentPickLetter = -1;
        }
    }
    //on clique sur le boutton reset
    if(x>=700 && x<=770 && y>=30 && y<=100)
    {
        resetGameBoard();
    }

    if(x>=900 && x<=1040 && y>=30 && y<=107)
    {
        sendProposition();
        resetGameBoard();
    }

}

void sendProposition()
{
    if(strcmp(phase, "REC")==0 || strcmp(phase, "SOU")==0)
    {
        addMessage("Proposition envoyee au serveur");
        char message[300];
        sprintf(message, "TROUVE/%s/\n", gameBoardByClient);
        sendToServer(message);
    }
    else
        addMessage("Ce n'est pas le bon moment pour donner une solution");

}

void resetGameBoard()
{
    int i;
    for(i=0;i<NB_LETTER_AVAILABLE;i++)
        letterUsed[i] = 0;
    currentPickLetter = -1;

    strcpy(gameBoardByClient,gameBoardByServer);
}

void keyEvent()
{
    SDL_Event event;
    while(SDL_PollEvent(&event))
    {
        switch (event.type)
        {
            case SDL_QUIT:
                loop = 0;
                break;

            case SDL_KEYDOWN:
                if ( event.key.keysym.scancode == SDL_SCANCODE_ESCAPE )
                {
                    loop = 0;
                }
                break;
            case SDL_MOUSEBUTTONDOWN:
                treatClickMouse(event.button.x, event.button.y);

                break;
        }
    }
}

void updateGameBoard(char * gameBoard)
{
    strcpy(gameBoardByServer,gameBoard);
    strcpy(gameBoardByClient,gameBoard);
}

void updateAvailableLetter(char * al)
{
    strcpy(availableLetter,al);
}

void updateScore(char * s)
{
    strcpy(score, s);
}

void updateTemps(int tps)
{
    temps = tps;
}

void updatePhase(char * p)
{
    strcpy(phase, p);
}

void gameLoop()
{


    initRender();
    loadTexture();

    int startTime, endTime;
    int actualTimeTimer = 0;
    int lastTimeTimer = 0;

    while(loop)
    {
        startTime = SDL_GetTicks();

        if(startTime - endTime > 16)
        {
            clearRenderer();

            displayBackground();
            displayIHM(isBestWord);
            displayGameBoard(gameBoardByClient);
            displayAvailableLetters(availableLetter, letterUsed);
            displayTimer(temps);

            displayMessage(userMessage, nbMessage);
            displayScore(score);

            applyRender();
            keyEvent();
            endTime = SDL_GetTicks();
        }
        actualTimeTimer = SDL_GetTicks();
        if(actualTimeTimer - lastTimeTimer > 1000) // toutes les 1 sec
        {
            if(temps>0)
                temps--;
            lastTimeTimer = SDL_GetTicks();
        }
    }
}

int main(int argc, char** argv)
{
    int i;
    for(i = 0; i < NB_LETTER_WIDTH*NB_LETTER_HEIGHT; i++)
        gameBoardByServer[i]='0';

    for(i = 0; i < NB_LETTER_AVAILABLE; i++){
        letterUsed[i]=0;
        availableLetter[i]='0';
    }

    printf("Veuillez saisir votre nom de joueur\n");
    readTerminal(userName, SIZE_USER_NAME);

    connexion();
    createWindow();
    destroyWindow();

    for(i=0 ; i<NB_MESSAGE_MAX; i++)
        free(userMessage[i]);

    endThread();
    printf("ENVOI DU MESSAGE SORT");
    char message[100];
    sprintf(message, "SORT/%s/\n", userName);
    sendToServer(message);

    endConnexion();

    SDL_Delay(1000);
    return 0;
}

void endLoop()
{
    loop = 0;
}

int getLoop()
{
    return loop;
}

char * getUserName()
{
    return userName;
}
