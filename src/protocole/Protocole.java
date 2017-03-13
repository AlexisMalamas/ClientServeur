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
		
		// Phase de soumission
		ENCHERE,
		VALIDATION,
		ECHEC,
		NOUVELLEENCHERE,
		FINENCHERE,
		
		// Phase de résolution
		SVALIDE,
		SINVALIDE,
		FIN,
		
		//Phase de résultat
		BILAN
		
		
}
