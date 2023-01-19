import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;

public class Utils {
    public static String getWorkdayStr(String txtDate){
        try {
            switch (LocalDate.parse(txtDate).getDayOfWeek()){
                case MONDAY -> {return "Monday";}
                case TUESDAY -> {return  "Tuesday";}
                case WEDNESDAY -> {return "Wednesday";}
                case THURSDAY -> {return "Thursday";}
                case FRIDAY -> {return "Friday";}
                case SATURDAY -> {return "Saturday";}
                case SUNDAY -> {return "Sunday";}
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public static String formatNumberTwoDigits(int number){
        String strNum= "";
        if(number<10)
            strNum += "0";
        strNum+=String.valueOf(number);
        return  strNum;
    }

    public static String getTimeFrame(int startHour, int startMin, int endHour, int endMin){
        return formatNumberTwoDigits(startHour) + ":" + formatNumberTwoDigits(startMin) + " - " + formatNumberTwoDigits(endHour) + ":" + formatNumberTwoDigits(endMin);
    }

    public static boolean checkInWorkTimeTable(Date date, int startHour, int startMin, int durationMin){
        try {
            String text = Files.readString(Paths.get("src/WorkTimeTable.json"));
            JSONObject obj = new JSONObject(text);

            LocalTime startTime = LocalTime.parse(formatNumberTwoDigits(startHour) + ":" + formatNumberTwoDigits(startMin));
            LocalTime endTime = startTime.plusMinutes(durationMin);

            String workday = getWorkdayStr(date.toString());
            JSONArray workdayArr = obj.getJSONArray(workday);
            JSONArray holidayArr = obj.getJSONArray("Holidays");

            if(!checkInWorkTimeTableDaily(workdayArr, startTime, endTime))
                return false;

            if(!checkInWorkTimeTableHoliday(holidayArr, date))
                return false;

            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    private static boolean checkInWorkTimeTableDaily(JSONArray workdayArr, LocalTime startTime, LocalTime endTime){
        for(int i=0; i<workdayArr.length(); i++){
            String startStr =  workdayArr.getJSONObject(i).getString("start");
            String endStr =  workdayArr.getJSONObject(i).getString("end");

            LocalTime sTime = LocalTime.parse(startStr);
            LocalTime eTime = LocalTime.parse(endStr);

            if(sTime.isBefore(startTime) && endTime.isBefore(eTime))
                return true;
        }

        return false;
    }

    private static boolean checkInWorkTimeTableHoliday(JSONArray holidays, Date givenDate){
        for(int i=0; i<holidays.length(); i++){
            String str = holidays.getString(i);
            Date holidayDate = Date.valueOf(str);

            if(holidayDate.equals(givenDate)) return false;
        }
        return true;
    }
}


