package jp.crossfire.framework.database;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Values extends ConditionContainer {

	public Values(String name) {
		super(name);
	}
	public String toString(){
		List valList = new LinkedList();
		List colList = new LinkedList();
		Iterator it = this.conditions.iterator();
		while(it.hasNext()){
			ConditionContainer colsCondition = (ConditionContainer)it.next();
			colList = colsCondition.getConditions();
			
			ConditionContainer valuesCondition = (ConditionContainer)it.next();
			valList = valuesCondition.getConditions();
		}
		
		StringBuffer vbuf = new StringBuffer();
		StringBuffer cbuf = new StringBuffer();
		boolean first = true;
		for(int i = 0; i < valList.size(); i++){
			Condition vc = (Condition)valList.get(i);
			Condition cc = (Condition)colList.get(i);
			
			if(!vc.isOk()){continue;}
			
			if(!first){
				vbuf.append(", ");
				cbuf.append(", ");
			}
			first = false;
			
			vbuf.append(vc.toString());
			cbuf.append(cc.toString());
			
		}
		
		return "(" + cbuf.toString()+ ") VALUES(" + vbuf.toString() + ")";
	}
}
