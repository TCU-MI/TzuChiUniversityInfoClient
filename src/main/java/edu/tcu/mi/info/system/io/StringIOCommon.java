package edu.tcu.mi.info.system.io;

public class StringIOCommon {

	public static String extractInfo(String string, String strA01, String strA02){
		int a01 = string.indexOf(strA01) + strA01.length();
		if(a01 >= string.length()) return null;
		int a02 = string.substring(a01).indexOf(strA02) + a01;
		return string.substring(a01, a02);
	}
}
