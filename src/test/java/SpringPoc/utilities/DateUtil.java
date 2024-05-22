package SpringPoc.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtil {

    public static String getCurrentDate(String strFormat) {
        DateFormat dateFormat = new SimpleDateFormat(strFormat);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getCurrentDateTime() {
        String ranNum = "";
        DateFormat formatter = new SimpleDateFormat("MMM");
        SimpleDateFormat monthParse = new SimpleDateFormat("MM");
        DateFormat dformatter = new SimpleDateFormat("DD");
        SimpleDateFormat dateParse = new SimpleDateFormat("DD");
        Calendar cal = Calendar.getInstance();
        String month = Integer.toString(cal.get(Calendar.MONTH) + 1);
        String date = Integer.toString(cal.get(Calendar.DATE) + 1);
        try {
            ranNum = "_" + dformatter.format(dateParse.parse(date))
                    + formatter.format(monthParse.parse(month)) + "_"
                    + Integer.toString(cal.get(Calendar.HOUR_OF_DAY))
                    + Integer.toString(cal.get(Calendar.MINUTE));
        } catch (Exception e) {
            System.out.println(new StringBuilder().append("************Exception:  ")
                    .append(e.getLocalizedMessage())
                    .append("   occured in:")
                    .append(e.getStackTrace()[0])
                    .append("********************"));

        }
        return ranNum;
    }

    public static String getDayAfterTomorrowDate() {
        DateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //Current Date should be go on Next Month and Year
        calendar.add(Calendar.DATE, +2);
        String strNextMonthAndYear = Integer.toString(calendar.get(Calendar.DATE)); //To Convert Integer to String (Next Month and Year)
        Date NextMonthAndYear = calendar.getTime();
        strNextMonthAndYear = dateFormat.format(NextMonthAndYear);
        return strNextMonthAndYear;
    }

    public static String getDate(int inDate, String strDateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(strDateFormat);
        Date today = new Date();
        Date date = new Date(today.getTime() + inDate * (1000 * 60 * 60 * 24));
        System.out.println("Date" + date);
        String strDate = formatter.format(date);
        System.out.println("Date" + strDate);
        return strDate;
    }
}
