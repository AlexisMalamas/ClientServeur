package protocole;

public enum Protocole {

	// connexion
		CONNEXION,
		BIENVENUE,
		REFUS,
		CONNECTE,
		
		// deconnexion
		SORT,
		DECONNEXION,
		
		// debut session
		SESSION,
		VAINQUEUR,
		
		// Phase recherche
		TOUR,
		TROUVE, //aussi dans phase de résolution
		RVALIDE,
		RINVALIDE,
		RATROUVE,
		RFIN,
		
		// Phase de soumission
		SVALIDE,
		SFIN,

		
		//Phase de résultat
		BILAN,
		
		//Chat
		ENVOI,
		PENVOI,
		RECEPTION,
		PRECEPTION,
		
		//Meilleur mot
		MEILLEUR
		
}
