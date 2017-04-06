#ifndef WINDOW_H_INCLUDED
#define WINDOW_H_INCLUDED

#include <SDL2/SDL.h>
#include "SDL2/SDL_image.h"
#include "SDL2/SDL_ttf.h"
#include "constantes.h"
#include "main.h"
#include <stdio.h>

    int createWindow();
    void initRender();
    void displayLetter(char letter, float x, float y);
    void displayGameBoard(char * tableLetter);
    void displayAvailableLetters(char * availableLetters, int * letterUsed);
    void displayBackground();
    void displayTimer(int temps);
    void displayIHM(int isBestWord);
    void displayMessage(char message[NB_MESSAGE_MAX][SIZE_MESSAGE], int nbMessage);
    void displayScore(char *score);
    void renderImage(int xSrc, int ySrc, int wSrc, int hSrc,int xDest, int yDest, int wDest, int hDest, SDL_Texture *texture);
    void clearRenderer();
    void applyRender();
    void loadTexture();
    void destroyWindow();

    void setCloseWindowByCross(int c);

#endif // WINDOW_H_INCLUDED
