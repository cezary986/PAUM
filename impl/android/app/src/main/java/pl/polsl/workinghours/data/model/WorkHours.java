package pl.polsl.workinghours.data.model;

public class WorkHours implements Comparable<WorkHours>{
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
    public static String extractDateFromDateTTime(String str) {
        return str.split("T")[0];
    }

    public static int stringToTimestamp(String str){
        str = str.split("T")[1];
        return stringToTimestampJust(str);
    }

    public String stringToTime(String str){
        return timeStampToString(stringToTimestamp(str));
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

    @Override
    public int compareTo(WorkHours o) {
        if (stringToTimestamp(this.started)<stringToTimestamp(o.started))
            return -1;
        else
            return 1;
    }

}
