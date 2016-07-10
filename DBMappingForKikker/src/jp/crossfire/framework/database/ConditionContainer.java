package jp.crossfire.framework.database;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;



/**
 * @author iizuka
 *
 */
public class ConditionContainer  implements ICondition{
	protected String name;
	protected List conditions = new LinkedList();
	
	public ConditionContainer(String name){
		this.name = name;
	}
	
	public void addChild(ICondition cnd){
		this.conditions.add(cnd);
	}
	
	public List getConditions() {
		return conditions;
	}

	public String toString(){
		StringBuffer buff = new StringBuffer();
		boolean first = true;
		Iterator it = this.conditions.iterator();
		while(it.hasNext()){
			ICondition condition = (ICondition)it.next();
			if(condition.isOk()){
				if(first){
					buff.append(condition.toString());
					first = false;
				}else{
					buff.append(" " + this.name + " " + condition.toString());
				}
				
			}
			
		}
		
		if(this.name.equals("OR") && !buff.toString().equals("")){
			return "(" + buff.toString() + ")";
		}
		if(this.name.equals(",") && !buff.toString().equals("")){
			return "(" + buff.toString() + ")";
		}
		return buff.toString();
	}

	/**
	 * @see jp.crossfire.framework.database.ICondition#isOk()
	 */
	public boolean isOk() {
		int ok = 0;
		Iterator it = this.conditions.iterator();
		while(it.hasNext()){
			ICondition condition = (ICondition)it.next();
			if(condition.isOk()){
				ok = ok + 1;
			}
		}
		if(ok > 0){
			return true;
		}
		return false;
	}
}
