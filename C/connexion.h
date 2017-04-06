#ifndef CONNEXION_H_INCLUDED
#define CONNEXION_H_INCLUDED

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>
#include "main.h"
#include "SDL2/SDL_net.h"
#include "constantes.h"

    void connexion();
    void endConnexion();
    void *fctThreadEcoute();
    void *fctThreadTerminal();
    void endThread();
    void sendToServer(char* message);

#endif // CONNEXION_H_INCLUDED
