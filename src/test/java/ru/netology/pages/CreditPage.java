package ru.netology.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class CreditPage {
    private final SelenideElement header = $$("h3").findBy(Condition.text("Кредит по данным карты"));
    private final SelenideElement cardNumberField = $("input[placeholder='0000 0000 0000 0000']");
    private final SelenideElement monthField = $("input[placeholder='08']");
    private final SelenideElement yearField = $("input[placeholder='22']");
    private final SelenideElement holderField = $(byXpath("/html/body/div/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input"));
    private final SelenideElement cvcField = $("input[placeholder='999']");
    private final SelenideElement continueButton = $$("button").findBy(Condition.text("Продолжить"));


    public void setNumber(String number) {
        header.shouldBe(visible);
        cardNumberField.setValue(number);
    }


    public void setMonthAndYear(String[] monthAndYear) {
        String month = monthAndYear[0];
        String year = monthAndYear[1];
        monthField.setValue(month);
        yearField.setValue(year);
    }

    public void setMonth(String month) {
        monthField.setValue(month);
    }


    public void setYear(String year) {
        yearField.setValue(year);
    }


    public void setOwner(String owner) {
        holderField.setValue(owner);
    }


    public void setCVV(String cvc) {
        cvcField.setValue(cvc);
    }


    public void clickContinueButton() {
        continueButton.click();
    }

    public void messageSuccessNotification() {
        $(".notification_status_ok").shouldBe(Condition.visible, Duration.ofMillis(15000));
    }

    public void messageDeclineNotification() {
        $("notification_status_error").shouldBe(Condition.visible, Duration.ofMillis(15000));
    }

    public void messageInvalidFormat() {
        $(".input__sub").shouldHave(exactText("Неверный формат")).shouldBe(visible);
    }

    public void messageRequiredField() {
        $(".input__sub").shouldHave(exactText("Поле обязательно для заполнения")).shouldBe(visible);

    }

    public void messageInvalidDate() {
        $(".input__sub").shouldHave(exactText("Неверно указан срок действия карты")).shouldBe(visible);
    }

    public void messageExpiredDate() {
        $(".input__sub").shouldHave(exactText("Истёк срок действия карты")).shouldBe(visible);
    }

    public void messageInvalidName() {
        $(".input__sub").shouldHave(exactText("Введите полное имя и фамилию")).shouldBe(visible);
    }

    public void messageLongName() {
        $(".input__sub").shouldHave(exactText("Значение поля не может содержать более 100 символов")).shouldBe(visible);
    }

    public void messageInvalidDataName() {
        $(".input__sub").shouldHave(exactText("Значение поля может содержать только буквы и дефис")).shouldBe(visible);
    }

    public void messageShortName() {
        $(".input__sub").shouldHave(exactText("Значение поля должно содержать больше одной буквы")).shouldBe(visible);
    }

    public void messageInvalidCvc() {
        $(".input__sub").shouldHave(exactText("Значение поля должно содержать 3 цифры")).shouldBe(visible);
    }

    public void messageFieldsAreRequired() {
        $$(".input__sub").shouldHave(CollectionCondition.size(5)).shouldHave(CollectionCondition.texts("Поле обязательно для заполнения"));
    }
}