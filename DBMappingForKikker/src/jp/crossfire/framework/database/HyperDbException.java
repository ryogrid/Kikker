package jp.crossfire.framework.database;



public class HyperDbException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HyperDbException(){
		
	}
	
	public HyperDbException(Throwable e){
		super(e);
	}
}