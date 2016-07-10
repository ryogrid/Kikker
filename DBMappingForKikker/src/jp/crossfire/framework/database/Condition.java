package jp.crossfire.framework.database;


import java.util.Hashtable;
import java.util.StringTokenizer;


/**
 * 
 * @author iizuka
 *
 */
public class Condition  implements ICondition{
	private String body;
	private Hashtable properties = new Hashtable();
	
	/**
	 * @return Returns the properties.
	 */
	public Hashtable getProperties() {
		return properties;
	}
	public Condition(String script){
		this.body = script;
	}

	public String toString(){
		int propCounts = 0;
		StringBuffer out = new StringBuffer();
		StringTokenizer tokenizer = new StringTokenizer(this.body, " \n\t$'=():/@", true);
		while(tokenizer.hasMoreTokens()){
			String token = (String)tokenizer.nextToken();
			
			if(token.equals("$") && tokenizer.hasMoreTokens()){
				String propName = tokenizer.nextToken();
				if(propName.equals("$")){
					out.append("$");
					continue;
				}

				out.append(properties.get(propName).toString());
				propCounts++;
				continue;
			}
			if(token.equals("@") && tokenizer.hasMoreTokens()){
				String propName = tokenizer.nextToken();
				if(propName.equals("@")){
					out.append("@");
					continue;
				}
				out.append(properties.get("@" + propName));
				continue;
			}
			out.append(token);
		}

		return out.toString();
	}
	/**
	 * @see jp.crossfire.framework.database.ICondition#isOk()
	 */
	public boolean isOk() {
		// count token
		StringTokenizer tokenizer = new StringTokenizer(this.body, " \n\t$'=():/", true);
		while(tokenizer.hasMoreTokens()){
			String token = (String)tokenizer.nextToken();
			if(token.equals("$") && tokenizer.hasMoreTokens()){
				String propName = tokenizer.nextToken();
				// Check Property is not null
				if(this.properties.get(propName) == null){return false;}
			}
		}
		
		return true;
	}
}