
import android.text.Spannable;
import android.text.SpannableStringBuilder;

/**
 * Created on 9/26/2017.
 */

public class Utils {
    public static String getTAG(Object o) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();

        int position = 0;
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].getFileName().contains(o.getClass().getSimpleName())
                    && !elements[i+1].getFileName().contains(o.getClass().getSimpleName())){
                position = i;
                break;
            }
        }

        StackTraceElement element = elements[position];
        String className = element.getFileName().replace(".java", "");
        return "[" + className + "](" + element.getMethodName() + ":" + element.getLineNumber() + ")";
    }
    public static void setSpan(SpannableStringBuilder stringBuilder, String text, String secondaryText, Object Span) {
        try {
            stringBuilder.setSpan(
                    Span,
                    text.indexOf(secondaryText),
                    text.indexOf(secondaryText) + String.valueOf(secondaryText).length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
}
