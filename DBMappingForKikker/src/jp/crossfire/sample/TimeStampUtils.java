package jp.crossfire.sample;

import java.sql.Timestamp;
import java.util.Calendar;


/**
 * 
 * @author iizuka
 *
 */
public class TimeStampUtils{
	/**
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static Timestamp createTimeStamp(int year, int month, int day){
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		long val = cal.getTimeInMillis();
		Timestamp tm = new Timestamp(val);
		
		return tm;
	}
	
	/**
	 * 
	 * @param tm
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static Timestamp getTimestampAfter(Timestamp tm, int year, int month, int day){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(tm.getTime());
		cal.add(Calendar.YEAR, year);
		cal.add(Calendar.MONTH, month);
		cal.add(Calendar.DATE, day);
		
		return new Timestamp(cal.getTimeInMillis());
	}
	
	/**
	 * Get current time by timestamp
	 * @return
	 */
	public static Timestamp getCurrentTimestamp(){
		Calendar cal = Calendar.getInstance();
		long val = cal.getTimeInMillis();
		Timestamp tm = new Timestamp(val);
		
		return tm;
	}
	
	
}