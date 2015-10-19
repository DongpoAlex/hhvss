package com.royalstone.util;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.jdom.Element;

/**
   Stores dates and perform date arithmetic.

   This is another date class, but more convenient than 
   <tt>java.util.Date</tt> or <tt>java.util.Calendar</tt>
   <br>
   Day 的原作者为 Cay Horstmann, 为了满足实际开发中的需要,孟罗毅在此类中添加了若干方法.
   @version 1.20 5 Oct 1998
   @author Cay Horstmann
*/

public class Day implements Cloneable, Serializable
{  

	/**
      Constructs sysdate's date
    */  
    
   public Day()
   {  GregorianCalendar sysdatesDate 
         = new GregorianCalendar();
      year = sysdatesDate.get(Calendar.YEAR);
      month = sysdatesDate.get(Calendar.MONTH) + 1;
      day = sysdatesDate.get(Calendar.DAY_OF_MONTH);
   }
   
   /**
    * @param d
    */
    public Day(Day d)
    {
        this.year = d.year;
        this.month = d.month;
        this.day = d.day;
    }

   /**
      Constructs a specific date

      @param yyyy year (full year, e.g., 1996, 
         <i>not</i> starting from 1900)
      @param m month
      @param d day
      @exception IllegalArgumentException if yyyy m d not a 
         valid date
    */
      
   public Day(int yyyy, int m, int d) 
   {  year = yyyy;
      month = m;
      day = d;
      if (!isValid()) 
         throw new IllegalArgumentException();
   }
   
   /**
    * Constructs an instance of Day equivalents to instance of GregorianCalendar.
    * @param gDate	an instance of GregorianCalendar.
    */
    public Day(GregorianCalendar gDate)
    {
        year = gDate.get(Calendar.YEAR);
        month = gDate.get(Calendar.MONTH) + 1;
        day = gDate.get(Calendar.DAY_OF_MONTH);
    }

   public Day( Element elm ) throws InvalidDataException
   {
   	try{
		year = Integer.parseInt( elm.getChild( "year" ).getTextTrim() );
		month = Integer.parseInt( elm.getChild( "month" ).getTextTrim() );
		day = Integer.parseInt( elm.getChild( "day" ).getTextTrim() );
   	}
	catch( Exception e){
		throw new InvalidDataException();
	}

	if (!isValid()) 
	   throw new InvalidDataException();

   }

	/**	将Day对象转换为XML元素.
	 * @return	an instance of Element storing information of date.
	 * @author	Mengluoyi
	 */
	public Element toElement()
	{
		Element elm = new Element( "date" );
		Element y = ( new Element( "year" ) ).addContent( "" + year );
		Element m = ( new Element( "month" ) ).addContent( "" + month );
		Element d = ( new Element( "day" ) ).addContent( "" + day );
		elm.addContent( y );
		elm.addContent( m );
		elm.addContent( d );
		return elm;
	}
   
   /**
      Advances this day by n days. For example. 
      d.advance(30) adds thirdy days to d

      @param n the number of days by which to change this
         day (can be < 0)
    */
   
   public void advance(int n)
   {  fromJulian(toJulian() + n);
   }

   /**
      Gets the day of the month

      @return the day of the month (1...31)
    */

   public int getDay()
   {  return day;
   }

   /**
      Gets the month
      
      @return the month (1...12)
    */

   public int getMonth()

   { return month;
   }

   /**
      Gets the year
      
      @return the year (counting from 0, <i>not</i> from 1900)
    */

   public int getYear()
   { return year;
   }
   
   /**	convert instance of Day to GregorianCalendar.
    * @return	an instance of GregorianCalendar.
    */
    public GregorianCalendar getGregorian()
    {
        return new GregorianCalendar(year, month - 1, day);
    }
   
   /**
      Gets the weekday
      @return the weekday ({@link Day#SUNDAY}, ..., 
         {@link Day#SATURDAY})
    */
    
   public int weekday() 
   {  return (toJulian() + 1) % 7 + 1; 
   }
   
   /**
      The number of days between this and day parameter 
      
      @param b any date
      @return the number of days between this and day parameter 
         and b (> 0 if this day comes after b)
    */
   
   public int daysBetween(Day b)
   {  return toJulian() - b.toJulian();
   }

   /**
      A string representation of the day
      
      @return a string representation of the day
    */
   
   public String toString()
   {  
      //  modified by Mengluoyi. 2004.05.23
	DecimalFormat df = new DecimalFormat( "00" );
	return "" + year + "-" + df.format( month) + "-" + df.format( day );
   }

   /**
      Makes a bitwise copy of a Day object
      @return a bitwise copy of a Day object
    */
   
   public Object clone()
   {  try
      {  return super.clone();
      } catch (CloneNotSupportedException e)
      {  // this shouldn't happen, since we are Cloneable
         return null;
      }   
   }

   /**
      Compares this Day against another object
      @param obj another object
      @return true if the other object is identical to this Day object
    */

   public boolean equals(Object obj)
   {  if (!getClass().equals(obj.getClass())) return false;
      Day b = (Day)obj;
      return day == b.day && month == b.month && year == b.year;
   }

   /**
      Computes the number of days between two dates
      @return true iff this is a valid date
    */
    
   private boolean isValid()
   {  Day t = new Day();
      t.fromJulian(this.toJulian());
      return t.day == day && t.month == month 
         && t.year == year;
   }

   /**
      @return The Julian day number that begins at noon of 
      this day
      Positive year signifies A.D., negative year B.C. 
      Remember that the year after 1 B.C. was 1 A.D.

      A convenient reference point is that May 23, 1968 noon
      is Julian day 2440000.

      Julian day 0 is a Monday.

      This algorithm is from Press et al., Numerical Recipes
      in C, 2nd ed., Cambridge University Press 1992
    */

   private int toJulian()
   {  int jy = year;
      if (year < 0) jy++;
      int jm = month;
      if (month > 2) jm++;
      else
      {  jy--;
         jm += 13;
      }
      int jul = (int) (Math.floor(365.25 * jy)
      + Math.floor(30.6001*jm) + day + 1720995.0);

      int IGREG = 15 + 31*(10+12*1582);
         // Gregorian Calendar adopted Oct. 15, 1582

      if (day + 31 * (month + 12 * year) >= IGREG)
         // change over to Gregorian calendar
      {  int ja = (int)(0.01 * jy);
         jul += 2 - ja + (int)(0.25 * ja);
      }
      return jul;
   }

   /**
      Converts a Julian day to a calendar date

      This algorithm is from Press et al., Numerical Recipes
      in C, 2nd ed., Cambridge University Press 1992
      
      @param j  the Julian date
    */

   private void fromJulian(int j)
   {  int ja = j;
   
      int JGREG = 2299161;
         /* the Julian date of the adoption of the Gregorian
            calendar    
         */   

      if (j >= JGREG)
      /* cross-over to Gregorian Calendar produces this 
         correction
      */   
      {  int jalpha = (int)(((float)(j - 1867216) - 0.25) 
             / 36524.25);
         ja += 1 + jalpha - (int)(0.25 * jalpha);
      }
      int jb = ja + 1524;
      int jc = (int)(6680.0 + ((float)(jb-2439870) - 122.1)
          /365.25);
      int jd = (int)(365 * jc + (0.25 * jc));
      int je = (int)((jb - jd)/30.6001);
      day = jb - jd - (int)(30.6001 * je);
      month = je - 1;
      if (month > 12) month -= 12;
      year = jc - 4715;
      if (month > 2) --year;
      if (year <= 0) --year;
   }

   public static int SUNDAY = 1;
   public static int MONDAY = 2;
   public static int TUESDAY = 3;
   public static int WEDNESDAY = 4;
   public static int THURSDAY = 5;
   public static int FRIDAY = 6;
   public static int SATURDAY = 7;

   /** @serial */
   private int day;
   /** @serial */
   private int month;
   /** @serial */
   private int year;
   private static final long serialVersionUID = 20060719L;
}
