package pl.polsl.workinghours.data.model;

import android.os.Build;
import android.text.format.DateFormat;
import android.text.format.Time;

import androidx.annotation.RequiresApi;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class WorkHours {
    public Integer id;
    public String started;
    public String  finished;
    public User user;

    public static int stringToTimestampJust(String str){
        String[] times = str.split(":");
        int hours = Integer.parseInt(times[0]);
        int minutes = Integer.parseInt(times[1]);
        int seconds = Integer.parseInt(times[2]);
        return seconds + minutes * 60 + hours * 60 * 60;
    }

    public static int stringToTimestamp(String str){
        str = str.split("T")[1];
        return stringToTimestampJust(str);
    }

    public static String timeStampToString(int time){
        StringBuilder str = new StringBuilder();
        str.append(time/3600);
        str.append(":");
        str.append((time % 3600) / 60);
        str.append(":");
        str.append(time%60);
        return str.toString();
    }


    public int timeSpendWork(){
        return  stringToTimestamp(finished) - stringToTimestamp(started);
    }

    public String timeSpendWorkString()
    {
        return timeStampToString(timeSpendWork());
    }


//    private String getDate(long time) {
//        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//        cal.setTimeInMillis(time * 1000);
//        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
//        return date;
//    }
}
