package ru.netology.tests;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.utils.DataGenerator;


import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selectors.byText;

class DeliveryTest {

    @BeforeEach
    void setup() {
        open ("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successfully plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        var validUser  = DataGenerator.Registration.generateUser (1);
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        // Заполнение формы для первичного планирования встречи
        $("[data-test-id='city'] input").setValue(validUser .getCity());
        $("[data-test-id='date'] input")
                .sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(firstMeetingDate);
        $("[data-test-id='name'] input").setValue(validUser .getName());
        $("[data-test-id='phone'] input").setValue(validUser .getPhone());
        $("[data-test-id='agreement']").click();
        $(byText("Запланировать")).click();

        // Проверка успешного планирования встречи на первую дату
        $(".notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("Встреча успешно запланирована на " + firstMeetingDate));

        // Перепланирование: изменяем дату встречи
        $("[data-test-id='date'] input")
                .sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        $("[data-test-id='date'] input").setValue(secondMeetingDate);
        $(byText("Запланировать")).click();

        // Подтверждаем перепланирование и проверяем итоговое уведомление
        $(byText("Перепланировать"))
                .shouldBe(visible, Duration.ofSeconds(5))
                .click();
        $(".notification__content")
                .shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(text("Встреча успешно запланирована на " + secondMeetingDate));
    }
}




