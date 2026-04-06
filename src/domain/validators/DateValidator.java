package domain.validators;

import java.time.LocalDate;

public class DateValidator {

    public static boolean isValidRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            return true; // Optional fields
        }
        return !end.isBefore(start);
    }

    public static boolean isFutureOrPresent(LocalDate date) {
        if (date == null) {
            return false;
        }
        return !date.isBefore(LocalDate.now());
    }

    public static boolean isPastOrPresent(LocalDate date) {
        if (date == null) {
            return false;
        }
        return !date.isAfter(LocalDate.now());
    }
}
