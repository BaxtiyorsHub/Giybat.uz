package api.giybat.uz.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneCheck {
    // Universal O'zbekiston raqamlari regex
    private static final Pattern UZ_PHONE_PATTERN = Pattern.compile(
            "^(?:\\+998|998|0)?(90|91|93|94|95|97|98|99|33|88)\\d{7}$"
    );

    /**
     * Telefon raqamini tekshiradi va standart formatga keltiradi (+998XXXXXXXXX)
     *
     * @param phoneNumber foydalanuvchidan kelgan raqam
     * @return standart formatdagi raqam (+998XXXXXXXXX)
     * @throws IllegalArgumentException noto'g'ri raqam bo'lsa
     */
    public static String normalize(String phoneNumber) {
        if (phoneNumber == null) {
            throw new IllegalArgumentException("Telefon raqam bo'sh bo'lishi mumkin emas");
        }

        // Bo'sh joylar, chiziqlar va boshqa belgilarni olib tashlaymiz
        String cleaned = phoneNumber.replaceAll("[^\\d+]", "");

        // Agar boshida +998 bo'lmasa, qo'shamiz
        if (!cleaned.startsWith("+998")) {
            // Agar boshida 998 bo'lsa, faqat + qo'shamiz
            if (cleaned.startsWith("998")) {
                cleaned = "+" + cleaned;
            }
            // Agar 0 bilan boshlansa, olib tashlab +998 qo'shamiz
            else if (cleaned.startsWith("0")) {
                cleaned = "+998" + cleaned.substring(1);
            }
            // Agar to'g'ridan-to'g'ri kod bilan boshlansa (masalan: 90...), +998 qo'shamiz
            else if (cleaned.matches("^(90|91|93|94|95|97|98|99|33|88)\\d{7}$")) {
                cleaned = "+998" + cleaned;
            }
        }

        // Endi regex bilan tekshiramiz
        Matcher matcher = UZ_PHONE_PATTERN.matcher(cleaned);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Noto'g'ri telefon raqam formati: " + phoneNumber);
        }

        return cleaned;
    }
}
