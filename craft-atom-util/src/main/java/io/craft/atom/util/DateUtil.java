package io.craft.atom.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Date Util
 * 
 * @author  mindwind
 * @version 1.0, May 15, 2012
 * @deprecated use <code>org.apache.commons.lang3.time.DateUtils</code> as an alternative
 */
public class DateUtil {
	
	/** 
     * Get the first day of the month specified by date argument.
     * 
     * @param date  
     * @return the first day of the month
     */  
    public static Date getMonthHead(Date date){  
        if(date == null) { return null; }
        
        Calendar c = Calendar.getInstance();  
        c.setTime(date);  
        c.set(Calendar.DAY_OF_MONTH, 1);  
        c.set(Calendar.HOUR, 0);  
        c.set(Calendar.MINUTE, 0);  
        c.set(Calendar.SECOND, 0);  
        return c.getTime();       
    }  
      
    /** 
     * Get today morning
     * 
     * @return today morning date
     */  
    public static Date getTodayMorning() {  
        return getMorning(new Date());  
    }  
    
    /** 
     * Get morning of specified date.
     * 
     * @param date 
     * @return morning date
     */  
    public static Date getMorning(Date date) {  
        Calendar c = Calendar.getInstance();  
        c.setTime(date);  
        c.set(Calendar.HOUR_OF_DAY, 0);  
        c.set(Calendar.MINUTE, 0);  
        c.set(Calendar.SECOND, 0);  
        return c.getTime();  
    }  

}
