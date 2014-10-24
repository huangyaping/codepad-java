package date;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by huangyp on 2014/10/24.
 */
public class MyDateFormat {

    public static Date parseDate(String str) {
        return parseDate(str, new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd"});
    }
    public static Date parseDate(String str, String...parsePatterns) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(str, parsePatterns);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
