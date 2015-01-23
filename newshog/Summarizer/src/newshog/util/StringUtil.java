package newshog.util;

public class StringUtil {

	public static String formatStanfOutput(String string) {
		// TODO Auto-generated method stub
		string = string.replaceAll("-LRB-", "(").replaceAll("-RRB-", ")");
		string = string.replaceAll("-LCB-", "{").replaceAll("-RCB-", "}");
		
		//Some common phrases
		return string;
	}
	
	public static String fixFormatting(String string)
	{
		//leading spaces
		string = string.replaceAll(" 's", "'s").replaceAll(" n't", "n't").replaceAll(" 'll","'ll");
		string = string.replaceAll(" ,", ",").replaceAll(" \\.", ".").replaceAll(" !", "!").replaceAll(" \\?", "?");
		string = string.replaceAll("\\( ", "(");
		
		//Trailing spaces
		string = string.replace("\\","").replaceAll("`` ", "``").replaceAll(" ''", "''").replaceAll(" \\)", ")").replace("s '","s'");
		string = string.replaceAll("([a-z 0-9])The ","$1 The ");

		string = string.replaceAll("Continue reading the main story",". ");
		string = string.replaceAll("Re­lated Sto­ries",". ");		

		//Camel case means two separate words
		if(string.contains("Story Highlights")){
				string = string.replaceAll(" ([a-z]+)([A-Z])", " $1.. $2");
				string = string.replaceAll("Story Highlights","Story Highlights- ");
		}
		return string;
	}
}
	