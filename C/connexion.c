#include "connexion.h"

TCPsocket tcpsock;
pthread_t threadEcoute;
pthread_t threadTerminal;

void *fctThreadEcoute(){
    int result;
    char msg[1000];
	while(getLoop() == 1){
        result=SDLNet_TCP_Recv(tcpsock,msg,1000);
        if(result<=0) {
            printf("ERROR RECEPTION");
        }
        printf("Received: \"%s\"\n",msg);

		char* protocole = strtok(msg,"/");
		char* argOne = strtok(NULL,"/");
		char* argTwo = strtok(NULL,"/");
		char* argThree = strtok(NULL,"/");
		char* argFour = strtok(NULL,"/");
		char* argFive = strtok(NULL,"/");

		if(strcmp(protocole,"BIENVENUE")==0){
			if((argOne==NULL) || (argTwo==NULL) || (argThree==NULL) || (argFour==NULL) || (argFive==NULL))
            {
                printf("Erreur arguments --- Bienvenue\n");
                pthread_exit(NULL);
                endLoop();
            }
            addMessage("Bienvenue !");
            updateGameBoard(argOne);
            updateAvailableLetter(argTwo);
            updateScore(argThree);
            updatePhase(argFour);
            updateTemps(strtol(argFive, NULL, 10));


		} else if(strcmp(protocole,"REFUS") == 0){
            printf("Refus par le serveur\n");
            pthread_exit(NULL);
            endLoop();

		} else if(strcmp(protocole,"CONNECTE")==0){
			if((argOne==NULL))
            {
                printf("Erreur arguments --- CONNECTE\n");
                pthread_exit(NULL);
            }
			char text[SIZE_MESSAGE];
			strcat(text,"Le joueur ");
            strcat(text,argOne);
            strcat(text," vient de se connecter.");
            addMessage(text);

		} else if(strcmp(protocole,"DECONNEXION")==0){
			if((argOne==NULL))
            {
                printf("Erreur arguments --- DECONNEXION\n");
                pthread_exit(NULL);
            }
			char text[SIZE_MESSAGE];
			strcat(text,"Le joueur ");
            strcat(text,argOne);
            strcat(text," vient de se deconnecter.");
            addMessage(text);

		} else if(strcmp(protocole,"SESSION")==0){
			addMessage("Debut d'une nouvelle session.");
			updatePhase("DEB");

		} else if(strcmp(protocole,"VAINQUEUR")==0){
		    if((argOne==NULL))
            {
                printf("Erreur arguments --- VAINQUEUR\n");
                pthread_exit(NULL);
            }
			updateScore(argOne);

		} else if(strcmp(protocole,"TOUR")==0){
		    if((argOne==NULL) || (argTwo == NULL))
            {
                printf("Erreur arguments --- TOUR\n");
                pthread_exit(NULL);
            }
			addMessage("Debut d'un nouveau tour");
			updatePhase("REC");
			updateTemps(300); // 5 min pour la phase de recherche
			updateGameBoard(argOne);
            updateAvailableLetter(argTwo);

		} else if(strcmp(protocole,"RVALIDE")==0){
            addMessage("Validation de la solution par le serveur");
            addMessage("Debut de la phase soumission");
            updatePhase("SOU");
            updateTemps(120);

		} else if(strcmp(protocole,"RINVALIDE")==0){
		    if((argOne==NULL))
            {
                printf("Erreur arguments --- RINVALIDE\n");
                pthread_exit(NULL);
            }
            char text[SIZE_MESSAGE];
            sprintf(text, "Solution refusee. motif: %s", argOne);
            addMessage(text);

		} else if(strcmp(protocole,"RATROUVE")==0){
		    if((argOne==NULL))
            {
                printf("Erreur arguments --- RATROUVE\n");
                pthread_exit(NULL);
            }
			char text[SIZE_MESSAGE];
			sprintf(text, "%s a trouve. Vous entrez en phase de soumission.", argOne);
            updatePhase("SOU");
            updateTemps(120);
            addMessage(text);

		} else if(strcmp(protocole,"RFIN")==0){
			addMessage("Fin du temps imparti");
			addMessage("Fin de la phase de recherche");
			updateTemps(300);

		} else if(strcmp(protocole,"SVALIDE")==0){
			addMessage("Validation de la solution par le serveur\n");

		} else if(strcmp(protocole,"SINVALIDE")==0){
		    if((argOne==NULL))
            {
                printf("Erreur arguments --- SVALIDE\n");
                pthread_exit(NULL);
            }
			char text[SIZE_MESSAGE];
            strcat(text,"Le serveur a refuse votre solution. Motif: ");
            strcat(text,argOne);
            addMessage(text);

		} else if(strcmp(protocole,"SFIN")==0){
			addMessage("Fin de la phase de soumission.");
			addMessage("Debut phase resultat");
			updatePhase("RES");
			resetGameBoard();
			updateTemps(10); // 10 secondes pour la phase de résultat;
			updateBestWord(0);

		} else if(strcmp(protocole,"BILAN")==0){
		    if((argOne==NULL) || (argTwo==NULL) || (argThree==NULL))
            {
                printf("Erreur arguments --- BILAN\n");
                pthread_exit(NULL);
            }
			char text[SIZE_MESSAGE];
			sprintf(text,"Mot: %s. Vainqueur: %s", argOne, argTwo);
            addMessage(text);

            updateScore(argThree);

		} else if(strcmp(protocole,"MEILLEUR")==0){
		    if(argOne == NULL)
            {
                printf("Erreur arguments --- MEILLEUR\n");
                pthread_exit(NULL);
            }
            updateBestWord(argOne[0]);

		} else if(strcmp(protocole,"RECEPTION")==0){
		    if(argOne == NULL)
            {
                printf("Erreur arguments --- RECEPTION\n");
                pthread_exit(NULL);
            }
            addMessage(argOne);

		}else if(strcmp(protocole,"PRECEPTION")==0){
		    if(argOne == NULL && argTwo == NULL)
            {
                printf("Erreur arguments --- PRECEPTION\n");
                pthread_exit(NULL);
            }
            char text[SIZE_MESSAGE];
            sprintf(text, "[%s]: %s", argTwo, argOne);
            addMessage(text);

		}else {
			printf("Commande %s inconnue.\n", protocole);
		}
	}
	printf("Fin d'écoute du server\n");
	return NULL;
}

void *fctThreadTerminal()
{
    while(1)
    {
        char input[SIZE_MESSAGE];
        readTerminal(input, SIZE_MESSAGE);
		char* argOne = strtok(input, "/");
		char* argTwo = strtok(NULL, "/");

		if(argOne != NULL && argTwo == NULL)  // send message public
        {
            char message[100];
            sprintf(message, "ENVOI/%s/\n", argOne);
            sendToServer(message);
        }
        else if(argOne != NULL && argTwo != NULL)
        {
            char message[100];
            sprintf(message, "PENVOI/%s/%s/\n", argOne, argTwo);
            sendToServer(message);
        }
    }
    return NULL;
}

void connexion()
{
    if(SDLNet_Init()==-1) {
        printf("SDLNet_Init: %s\n", SDLNet_GetError());
        exit(1);
    }

    IPaddress ip;


    if(SDLNet_ResolveHost(&ip,"127.0.0.1",2017)==-1) {
        printf("SDLNet_ResolveHost: %s\n", SDLNet_GetError());
        exit(1);
    }

    tcpsock=SDLNet_TCP_Open(&ip);
    if(!tcpsock) {
        printf("SDLNet_TCP_Open: %s\n", SDLNet_GetError());
        exit(2);
    }

    if(pthread_create(&threadEcoute, NULL, fctThreadEcoute, NULL)) {
        perror("pthread_create");
        exit(3);
    }
    if(pthread_create(&threadTerminal, NULL, fctThreadTerminal, NULL)) {
        perror("pthread_create");
        exit(4);
    }

    char message[100];
    if(strcmp(getUserName(), "") &&(strstr(getUserName(),"/")))
    {
        printf("Nom invalide");
        return exit(5);
    }
    else
    {
        sprintf(message, "CONNEXION/%s/\n", getUserName());
        sendToServer(message);
    }
}

void endConnexion()
{
    SDLNet_TCP_Close(tcpsock);
    SDLNet_Quit();
}

void endThread()
{
    pthread_cancel(threadTerminal);
    pthread_cancel(threadEcoute);
}

void sendToServer(char* message){
	int len,result;

    len=strlen(message);
    result=SDLNet_TCP_Send(tcpsock,message,len);
    if(result<len) {
        printf("SDLNet_TCP_Send: %s\n", SDLNet_GetError());
        endConnexion();
    }
}
