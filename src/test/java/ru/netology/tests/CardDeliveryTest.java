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

public class CardDeliveryTest {

    @BeforeAll
    static void setUp() {
        WebDriverManager.chromedriver().clearDriverCache().clearResolutionCache();
        WebDriverManager.chromedriver().browserVersion("115.0.5790.102").setup();

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
        UserData user = DataGenerator.generateUser(3);

        $("[data-test-id=city] input").setValue(user.getCity());
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(user.getDate());
        $("[data-test-id=name] input").setValue(user.getName());
        $("[data-test-id=phone] input").setValue(user.getPhone());
        $("[data-test-id=agreement]").click();
        // Клик по кнопке "Запланировать"
        $$("span.button__text").findBy(Condition.text("Запланировать")).parent().click();

        // Проверяем уведомление об успешном бронировании
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + user.getDate()), Duration.ofSeconds(15));

        // Генерируем новую дату для перепланирования
        String newDate = DataGenerator.generateDate(5);

        // Перепланируем встречу: очищаем и меняем дату, отправляем форму заново
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.BACK_SPACE);
        $("[data-test-id=date] input").setValue(newDate);
        $$("span.button__text").findBy(Condition.text("Запланировать")).parent().click();

        // Проверяем появление окна с предложением перепланировать
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + newDate), Duration.ofSeconds(15));

        // Клик по кнопке "Перепланировать"
        $$("span.button__text")
                .findBy(Condition.text("Перепланировать"))
                .parent()
                .shouldBe(Condition.visible, Duration.ofSeconds(5))
                .click();

        // Проверяем уведомление о успешном перепланировании
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно запланирована на " + newDate), Duration.ofSeconds(15));
    }
}
