package protocole;

public class ProtocoleCreateur {
	
	public static String create(Protocole protocole, String... args){
		if(protocole != null){
			String message = protocole + "/";
			if(args != null && args.length>0){
				for(String arg : args){
					message += arg + "/";
				}
			}
			return message;
		}
		return "";
	}
}