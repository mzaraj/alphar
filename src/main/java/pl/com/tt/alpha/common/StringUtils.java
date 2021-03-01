package pl.com.tt.alpha.common;

public class StringUtils {
    public static boolean hasValue(Object value){
        return value instanceof String && ((String) value).isEmpty();
    }

    public static String changeFloatValueForDatabase(String value){
        String result = value.replace('.', ',');
        if(result.matches("^(0,\\d*)$")){
            return result.substring(1);
        }
        return result;
    }

	public static String replaceAccentChars(String text) {
		return text.replace("ł", "l")
				   .replace("Ł", "L")
				   .replace("ś", "s")
				   .replace("Ś", "S")
				   .replace("ć", "c")
				   .replace("Ć", "C")
				   .replace("ż", "z")
				   .replace("Ż", "Z")
				   .replace("ź", "z")
				   .replace("Ź", "Z")
				   .replace("ń", "n")
				   .replace("Ń", "N")
				   .replace("ó", "o")
				   .replace("Ó", "O")
				   .replace("ą", "a")
				   .replace("Ą", "A")
				   .replace("ę", "e")
				   .replace("Ę", "E");
	}
}
