package ec.tec.ami.views.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ReadableDateFormat {

    public static String toHumanFormat(Date date){
        Calendar today = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar yesterday = (Calendar) today.clone();
        yesterday.add(Calendar.DATE,-1);

        long millis = Math.abs(date.getTime() - today.getTime().getTime());
        long days = TimeUnit.DAYS.convert(millis,TimeUnit.MILLISECONDS);
        long minutes = TimeUnit.MINUTES.convert(millis,TimeUnit.MILLISECONDS);
        long seconds = TimeUnit.SECONDS.convert(millis,TimeUnit.MILLISECONDS);
        long hours = TimeUnit.HOURS.convert(millis,TimeUnit.MILLISECONDS);


        if(seconds > 0 && seconds <60){
            return "A moment ago";
        }
        if(days == 1 && yesterday.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)){
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return "Yesterday at "+format.format(date);
        }
        if(minutes >= 1 && minutes < 60){
            return String.format("%2dmin",minutes);
        }
        if(hours>=1 && hours < 24){
            return String.format("%2dh",hours);
        }
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        return format.format(date);

    }
}
