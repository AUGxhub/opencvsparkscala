import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by augta on 2016/5/22.
 */
public class test {

    public static void main(String[] args) {

        String temp = "C:\\Users\\augta\\Documents\\Diablo III\\D3Prefs.txt";
        String p = "\\\\";
        Pattern r = Pattern.compile(p);
        Matcher m = r.matcher(temp);
        String x = m.replaceAll("\\\\");
        System.out.println(x);
    }
}
