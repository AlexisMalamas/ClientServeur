#ifndef MAIN_H_INCLUDED
#define MAIN_H_INCLUDED

#include <stdio.h>
#include "constantes.h"
#include "window.h"
#include "connexion.h"
#include <string.h>

    void updateBestWord(char c);
    void addMessage(char *message);
    void clearBuffer();
    int readTerminal(char *chaine, int longueur);
    void treatClickMouse(int x, int y);
    void sendProposition();
    void resetGameBoard();
    void keyEvent();
    void updateGameBoard(char * gameBoard);
    void updateAvailableLetter(char * al);
    void updateScore(char * s);
    void updateTemps(int tps);
    void updatePhase(char * p);
    void gameLoop();
    void endLoop();
    int getLoop();
    char * getUserName();


#endif // MAIN_H_INCLUDED
