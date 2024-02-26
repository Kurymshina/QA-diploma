package ru.netology.data;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;
import java.security.SecureRandom;

public class DataHelper {
    private static final Random random = new Random();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final Faker faker = new Faker(new Locale("en"));
    public static final Faker faker2 = new Faker(new Locale("ru"));


    public static String getApprovedCardNumber() {
        return "4444444444444441";
    }

    public static String getDeclinedCardNumber() {
        return "4444444444444442";
    }

    public static String generateCardNumber(int length) {
        return faker.number().digits(length);
    }

    public static String generateCardNumberMustBeLessThan() {
        int randomNumberLength = faker.random().nextInt(16);
        return faker.number().digits(randomNumberLength);
    }

    public static String generateRandomText(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

    public static String[] generateValidMonthAndYear() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(5);
        Random random = new Random();
        long startDay = startDate.toEpochDay();
        long endDay = endDate.toEpochDay();
        long randomDay = startDay + random.nextInt((int) (endDay - startDay));
        LocalDate a = LocalDate.ofEpochDay(randomDay);
        int y = a.getYear();
        int m = a.getMonthValue();
        String month = String.format("%02d", m);
        String year = String.format("%02d", y % 100);
        return new String[]{month, year};
    }

    public static String[] generateInvalidMonthAndYear() {
        LocalDate startDate = LocalDate.now().minusYears(6);
        LocalDate endDate = LocalDate.now();
        Random random = new Random();
        long startDay = startDate.toEpochDay();
        long endDay = endDate.toEpochDay();
        long randomDay = startDay + random.nextInt((int) (endDay - startDay));
        LocalDate a = LocalDate.ofEpochDay(randomDay);
        int y = a.getYear();
        int m = a.getMonthValue();
        String month = String.format("%02d", m);
        String year = String.format("%02d", y % 100);
        return new String[]{month, year};
    }

    public static String getCurrentYear() {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        return String.format("%02d", currentYear % 100);
    }

    public static String generateValidYear() {
        return String.format("%02d", faker.number().numberBetween(24, 29));
    }

    public static String getPreviousYear() {
        int currentYear = Integer.parseInt(getCurrentYear());
        int previousYear = currentYear - 1;
        return String.format("%02d", previousYear % 100);
    }

    public static String generateValidMonth() {
        int shift = (int) (Math.random() * 10);
        return LocalDate.now().plusMonths(shift).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String generateInvalidMonthWihLengthThreeDigits() {
        int randomNumber = random.nextInt(Integer.MAX_VALUE - 130) + 131;
        return String.valueOf(randomNumber);
    }

    public static int generateInvalidMonthMoreThen12() {
        Random random = new Random();
        return random.nextInt(89) + 13;
    }

    public static String generateRandomMonth() {
        return String.format("%02d", faker.number().numberBetween(1, 13));
    }

    public static String getPreviousMonth() {
        LocalDate localDate = LocalDate.now();
        if (localDate.getMonthValue() == 1) {
            System.out.println("Текущий месяц январь, не подходит для этого метода.");
            return null;
        }
        return localDate.minusMonths(1).format(DateTimeFormatter.ofPattern("MM"));
    }


    public static String generateName() {
        return faker.name().fullName();
    }

    public static String generateCyrillicName() {
        return faker2.name().fullName();
    }


    public static String generateTreeDigit() {
        return faker.number().digits(3);
    }

    public static String getRandomTwoDigit() {
        return faker.number().digits(2);
    }

    public static String getRandomOneDigit() {
        return faker.number().digits(1);
    }
    //Для всех


    public static String emptyFormField() {
        return "";
    }

    public static String generateSpecialCharacters(int length) {
        String specialCharacters = "!@#$%^&*()_-+=?/{}~";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        int i = 0;
        while (i > length) {
            int index = random.nextInt(specialCharacters.length());
            sb.append(specialCharacters.charAt(index));
            i++;
        }
        return sb.toString();
    }


    public static String getRandomCardNumber() {
        return generateRandomDigits(16);
    }

    public static String getYearWithOneValue() {
        return generateRandomDigits(1);
    }

    public static String getMonthWithOneValue() {
        return generateRandomDigits(1);
    }

    public static String getCurrentYearPlus5() {
        int currentYear = Integer.parseInt(getCurrentYear());
        int yearPlus6 = currentYear + 6;
        return String.format("%02d", yearPlus6 % 100);
    }


    public static String getEmptyMonth() {
        return "";
    }

    public static String getInvalidYear() {
        return "00";
    }

    public static String getEmptyYear() {
        return "";
    }

    public static String getEmptyNumberCard() {
        return "";
    }


    public static String getNextMonth() {
        LocalDate currentDate = LocalDate.now();
        LocalDate nextMonth = currentDate.plusMonths(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM");

        return nextMonth.format(formatter);
    }


    public static String getRandomCvc() {
        return generateRandomDigits(3);
    }


    private static String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

}


