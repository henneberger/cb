package cb;

public class AttributeUtils {
    public static String getValueOrDefault(String value, String defaultValue) {
        return value != null && !value.trim().equals("") ? value : defaultValue;
    }

    // if phone number is short, add global code of +1
    public static String cleanPhoneNumber(String number) {
        String numbersOnly = number.replaceAll("\\D+", "");
        return numbersOnly.length() == 10 ? "1" + numbersOnly : numbersOnly;
    }
}
