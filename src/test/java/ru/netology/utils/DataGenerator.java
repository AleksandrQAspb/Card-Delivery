package ru.netology.utils;

import com.github.javafaker.Faker;
import ru.netology.data.UserData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataGenerator {

    public static DataGenerator Registration;

    private DataGenerator() {
        // приватный конструктор, чтобы класс нельзя было инстанцировать
    }

    private static final Faker faker = new Faker(new Locale("ru"));

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static String generateCity() {
        // Можно сделать список городов и выбирать случайный из них
        String[] cities = {"Москва", "Санкт-Петербург", "Казань", "Новосибирск", "Екатеринбург"};
        int index = faker.number().numberBetween(0, cities.length);
        return cities[index];
    }

    public static String generateDate(int daysAhead) {
        return LocalDate.now().plusDays(daysAhead).format(formatter);
    }

    public static String generateName() {
        return faker.name().fullName();
    }

    public static String generatePhone() {
        // Генерируем телефон в формате +7XXXXXXXXXX
        return faker.phoneNumber().phoneNumber().replaceAll("[^\\d]", "");
    }

    public static UserData generateUser(int daysAhead) {
        return new UserData(
                generateCity(),
                generateDate(daysAhead),
                generateName(),
                generatePhone()
        );
    }
}

