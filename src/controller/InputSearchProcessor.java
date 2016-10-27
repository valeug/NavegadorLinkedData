package controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputSearchProcessor {

	public static int isValid(String input){
		return 0;
	} 
	
	public static boolean isUri(String input){
		String regex = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input);
		
		if(m.find()) return true;
		return false;
	}
}
