package ec.tec.ami.views.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ReadableDateFormat {

    public static String toHumanFormat(Date date){
        Calendar today = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        long days = TimeUnit.DAYS.convert(Math.abs(date.getTime() - calendar.getTime().getTime()),TimeUnit.MILLISECONDS);

        int [] todayArr = {today.get(Calendar.YEAR), today.get(Calendar.MONTH),today.get(Calendar.DAY_OF_MONTH),
                           today.get(Calendar.HOUR_OF_DAY),today.get(Calendar.MINUTE),today.get(Calendar.SECOND)};
        int [] calendarArr = {calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),
                              calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND)};

    }
}
