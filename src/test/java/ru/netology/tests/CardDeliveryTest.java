package ru.netology.tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.netology.data.UserData;
import ru.netology.utils.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;

public class CardDeliveryTest {

    @BeforeAll
    static void setUp() {
        WebDriverManager.chromedriver().clearDriverCache().clearResolutionCache();
        // Указываем вашу версию браузера
        WebDriverManager.chromedriver().setup();

        Configuration.browser = "chrome";

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        Configuration.browserCapabilities = options;
        Configuration.baseUrl = "http://localhost:9999";
        Configuration.browserSize = "1920x1080";
        Configuration.headless = false;
    }

    @Test
    void shouldRescheduleMeeting() {
        open("/");

        // Генерируем данные для первичной заявки
        UserData user = DataGenerator.generateUser (3);

        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(user.getDate());
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();

        // Клик по кнопке "Запланировать"
        $(byText("Запланировать")).click();

        // Проверяем уведомление об успешном бронировании
        $(".notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("Встреча успешно запланирована на 15.08.2025"));


        // Генерируем новую дату для перепланирования
        String newDate = DataGenerator.generateDate(5);

        // Перепланируем встречу: очищаем и меняем дату, отправляем форму заново
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(newDate);
        $(byText("Запланировать")).click();

        // Проверяем появление окна с предложением перепланировать
        $(".notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("Встреча успешно запланирована на " + newDate));


        // Клик по кнопке "Перепланировать"
        $(byText("Перепланировать"))
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();

        // Проверяем уведомление о успешном перепланировании
        $(".notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("Встреча успешно запланирована на " + newDate));
    }
}


