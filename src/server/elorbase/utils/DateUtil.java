package server.elorbase.utils;

import java.util.Calendar;

public class DateUtil {
	
    public static int getCurrentWeek() {
        Calendar calendar = Calendar.getInstance();

        // 2 de septiembre de 2024 (lunes) es el primer día
        Calendar startOfClasses = Calendar.getInstance();
        startOfClasses.set(2024, Calendar.SEPTEMBER, 2);

        // 30 de mayo de 2025 (viernes) es el último día de clases
        Calendar endOfClasses = Calendar.getInstance();
        endOfClasses.set(2025, Calendar.MAY, 30);

        // Si la fecha actual está fuera del rango de clases
        if (calendar.before(startOfClasses) || calendar.after(endOfClasses)) {
            return -1;
        }

        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        long diffInMillis = calendar.getTimeInMillis() - startOfClasses.getTimeInMillis();
        long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

        return (int) (diffInDays / 7) + 1;
    }
    
    public static int getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SUNDAY) {
        	dayOfWeek = 7;
        }
        
        return dayOfWeek;
    }

}
