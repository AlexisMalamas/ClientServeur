#include "window.h"

SDL_Window* pWindow = NULL;
SDL_Texture* pTexture = NULL;
SDL_Surface* pSprite = NULL;
SDL_Texture* pTextureBackground = NULL;
SDL_Surface* pSpriteBackground = NULL;
SDL_Texture* pTextureGrid = NULL;
SDL_Surface* pSpriteGrid = NULL;
SDL_Texture* pTextureReset = NULL;
SDL_Surface* pSpriteReset = NULL;
SDL_Texture* pTextureSend = NULL;
SDL_Surface* pSpriteSend = NULL;
SDL_Texture* pTextureGreenCircle = NULL;
SDL_Surface* pSpriteGreenCircle = NULL;
SDL_Texture* pTextureRedCircle = NULL;
SDL_Surface* pSpriteRedCircle = NULL;
SDL_Renderer *pRenderer = NULL;
TTF_Font *font = NULL;
SDL_Rect solidRect;

int createWindow()
{
    if (SDL_Init(SDL_INIT_VIDEO) != 0)
    {
        fprintf(stdout,"Echec initialisation de la SDL (%s)\n",SDL_GetError());
        return 0;
    }
    else
    {
        pWindow = SDL_CreateWindow("Jeu de lettre",SDL_WINDOWPOS_UNDEFINED, SDL_WINDOWPOS_UNDEFINED, WINDOW_WIDTH, WINDOW_HEIGHT, SDL_WINDOW_SHOWN);

        if( pWindow )
        {
            if(TTF_Init() == -1)
            {
                printf("Erreur d'initialisation de TTF_Init\n");
                return 0;
            }
            font = TTF_OpenFont("font/arial.ttf", 18);
            gameLoop();
            return 1;
        }
        else
        {
            fprintf(stderr,"Erreur de creation de la fenêtre: %s\n",SDL_GetError());
            return 0;
        }
    }

}

void initRender()
{
    pRenderer = SDL_CreateRenderer(pWindow,-1,SDL_RENDERER_ACCELERATED);
    if (!pRenderer)
    {
        fprintf(stdout,"Échec de creation du renderer (%s)\n",SDL_GetError());
    }
}

void displayLetter(char letter, float x, float y)
{
    if(letter != '0'){

        int l = letter - 65; // A->65, Z-> 90 ASCII
        int line = l/6;

        SDL_Rect srcRect;
        SDL_Rect destRect;
        srcRect.x = 200 * (l%6);
        srcRect.y = 200 * line;
        srcRect.w = 200;
        srcRect.h = 200;
        destRect.w = LETTER_WIDTH;
        destRect.h = LETTER_HEIGHT;
        destRect.x = x;
        destRect.y = y;
        SDL_RenderCopy(pRenderer,pTexture,&srcRect,&destRect);
    }
}

void displayGameBoard(char * tableLetter)
{
    if(tableLetter != NULL)
    {
        int letter = 0;
        int i, j;

        for(i=0; i<NB_LETTER_WIDTH; i++)
            for(j=0; j<NB_LETTER_HEIGHT; j++)
            {
                int posX = j*LETTER_WIDTH +14 + j*4.9;
                int posY = i*LETTER_HEIGHT + 33 + i*4.95;
                displayLetter(tableLetter[letter], posX, posY);
                letter +=1;
            }
    }
}

void displayAvailableLetters(char * availableLetters, int * letterUsed)
{
    if (availableLetters)
    {
        int i;
        for(i=0; i<NB_LETTER_AVAILABLE; i++)
        {
            if(letterUsed[i]==0)
                displayLetter(availableLetters[i], WINDOW_WIDTH-150, i*LETTER_HEIGHT+50 );
        }
    }
}

void renderImage(int xSrc, int ySrc, int wSrc, int hSrc,int xDest, int yDest, int wDest, int hDest, SDL_Texture *texture)
{
    SDL_Rect srcRect;
    SDL_Rect destRect;
    srcRect.x = xSrc;
    srcRect.y = ySrc;
    srcRect.w = wSrc;
    srcRect.h = hSrc;
    destRect.w = wDest;
    destRect.h = hDest;
    destRect.x = xDest;
    destRect.y = yDest;
    SDL_RenderCopy(pRenderer,texture,&srcRect,&destRect);
}

void displayBackground()
{
    renderImage(0,0,1280,720,0,0,WINDOW_WIDTH, WINDOW_HEIGHT, pTextureBackground);
}

void displayIHM(int isBestWord)
{
    renderImage(0,0,675,675,10,30,675,675,pTextureGrid);
    renderImage(0,0,140,140,700,30,70,70,pTextureReset);
    renderImage(0,0,140,67,900,30,140,67,pTextureSend);
    if(isBestWord)
        renderImage(0,0,373,373,700,150,50,50,pTextureGreenCircle);
    else
        renderImage(0,0,373,373,700,150,50,50,pTextureRedCircle);
}

void displayMessage(char message[NB_MESSAGE_MAX][SIZE_MESSAGE], int nbMessage)
{
    SDL_Color textColor = {0, 0, 0, 255};

    int i;
    for(i=0; i<nbMessage; i++)
    {
        SDL_Surface* solid = TTF_RenderUTF8_Blended(font, message[i], textColor);
        SDL_Texture* solidTexture = SDL_CreateTextureFromSurface(pRenderer, solid);
        SDL_FreeSurface(solid);

        SDL_QueryTexture( solidTexture, NULL, NULL, &solidRect.w, &solidRect.h );
        solidRect.x = WINDOW_WIDTH - 550;
        solidRect.y = WINDOW_HEIGHT - 225 + i * 20;

        SDL_RenderCopy( pRenderer, solidTexture, NULL, &solidRect);
        SDL_DestroyTexture(solidTexture);
    }
}

void displayScore(char *score)
{
    if(score!=NULL)
    {
        char tmp[1000];
        strcpy(tmp, score);

        int i=1;
        char *tour = strtok(tmp, "*");
        SDL_Color textColor = {0, 0, 0, 255};
        char message[30];
        sprintf(message, "Tour numero %s", tour);
        SDL_Surface* solid = TTF_RenderUTF8_Blended(font, message, textColor);
        SDL_Texture* solidTexture = SDL_CreateTextureFromSurface(pRenderer, solid);
        SDL_QueryTexture( solidTexture, NULL, NULL, &solidRect.w, &solidRect.h );
        SDL_FreeSurface(solid);
        solidRect.x = 900;
        solidRect.y = 150;
        SDL_RenderCopy( pRenderer, solidTexture, NULL, &solidRect);
        SDL_DestroyTexture(solidTexture);
        char *rep;
        while((rep = strtok(NULL, "*"))!=NULL)
        {
            char mess[1000];
            sprintf(mess, "%s a un score de %s", rep, strtok(NULL, "*"));
            SDL_Surface* sol = TTF_RenderUTF8_Blended(font, mess, textColor);
            SDL_Texture* texture = SDL_CreateTextureFromSurface(pRenderer, sol);
            SDL_QueryTexture( texture, NULL, NULL, &solidRect.w, &solidRect.h );
            SDL_FreeSurface(sol);
            solidRect.x = 900;
            solidRect.y = 150 + i * 20;

            SDL_RenderCopy( pRenderer, texture, NULL, &solidRect);
            SDL_DestroyTexture(texture);
            i+=1;
        }
    }
}

void displayTimer(int temps)
{
    SDL_Color textColor = {0, 0, 0, 255};

    char message[20];
    sprintf(message, "Temps restant: %d", temps);
    SDL_Surface* solid = TTF_RenderUTF8_Blended(font, message, textColor);
    SDL_Texture* solidTexture = SDL_CreateTextureFromSurface(pRenderer, solid);
    SDL_FreeSurface(solid);

    SDL_QueryTexture( solidTexture, NULL, NULL, &solidRect.w, &solidRect.h );
    solidRect.x = WINDOW_WIDTH - 550;
    solidRect.y = 300;

    SDL_RenderCopy( pRenderer, solidTexture, NULL, &solidRect);
    SDL_DestroyTexture(solidTexture);
}

void applyRender()
{
    SDL_RenderPresent(pRenderer);
}

void clearRenderer()
{
    SDL_RenderClear(pRenderer);
}

void loadTexture()
{
    pSprite = SDL_LoadBMP("./sprite/letter.bmp");
    pSpriteBackground = IMG_Load("./sprite/background.jpg");
    pSpriteGrid = IMG_Load("./sprite/grid.png");
    pSpriteReset = IMG_Load("./sprite/reset.png");
    pSpriteSend = IMG_Load("./sprite/send.png");
    pSpriteGreenCircle = IMG_Load("./sprite/greenCircle.png");
    pSpriteRedCircle = IMG_Load("./sprite/redCircle.png");
    if(pSprite && pSpriteBackground && pSpriteGrid && pSpriteReset && pSpriteSend && pSpriteGreenCircle && pSpriteRedCircle)
    {
        pTexture = SDL_CreateTextureFromSurface(pRenderer,pSprite);
        pTextureSend = SDL_CreateTextureFromSurface(pRenderer, pSpriteSend);
        pTextureBackground = SDL_CreateTextureFromSurface(pRenderer,pSpriteBackground);
        pTextureGrid = SDL_CreateTextureFromSurface(pRenderer,pSpriteGrid);
        pTextureReset = SDL_CreateTextureFromSurface(pRenderer,pSpriteReset);
        pTextureGreenCircle = SDL_CreateTextureFromSurface(pRenderer,pSpriteGreenCircle);
        pTextureRedCircle = SDL_CreateTextureFromSurface(pRenderer,pSpriteRedCircle);
        if(pTexture && pTextureBackground && pTextureGrid && pTextureReset && pTextureSend && pTextureGreenCircle && pTextureRedCircle)
        {
            printf("chargement des textures termine\n");
        }
        else
        {
            fprintf(stdout,"echec de creation de la texture (%s)\n",SDL_GetError());
        }
    }
    else
    {
        fprintf(stdout,"echec chargement du sprite (%s)\n",SDL_GetError());
    }
}

void destroyWindow()
{
    SDL_DestroyTexture(pTexture);
    SDL_DestroyTexture(pTextureBackground);
    SDL_DestroyTexture(pTextureGrid);
    SDL_DestroyTexture(pTextureReset);
    SDL_DestroyTexture(pTextureSend);
    SDL_DestroyTexture(pTextureGreenCircle);
    SDL_DestroyTexture(pTextureRedCircle);
    SDL_FreeSurface(pSprite);
    SDL_FreeSurface(pSpriteSend);
    SDL_FreeSurface(pSpriteBackground);
    SDL_FreeSurface(pSpriteGrid);
    SDL_FreeSurface(pSpriteReset);
    SDL_FreeSurface(pSpriteGreenCircle);
    SDL_FreeSurface(pSpriteRedCircle);
    SDL_DestroyRenderer(pRenderer);
    TTF_CloseFont(font);
    TTF_Quit();
    SDL_Quit();
}
