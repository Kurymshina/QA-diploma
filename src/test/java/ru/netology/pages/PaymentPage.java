package ru.netology.pages;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaymentPage {
    private SelenideElement header = $$("h3").findBy(Condition.text("Оплата по карте"));
    private SelenideElement cardNumberField = $("input[placeholder='0000 0000 0000 0000']");
    private SelenideElement monthField = $$(".input__inner").findBy(text("Месяц")).$(".input__control");
    private SelenideElement yearField = $("input[placeholder='22']");
    private SelenideElement holderField = $$(".input__inner").findBy(text("Владелец")).$(".input__control");
    private SelenideElement cvcField = $("input[placeholder='999']");
    private SelenideElement continueButton = $$("button").findBy(Condition.text("Продолжить"));


    public void setNumber(String number) {
        header.shouldBe(visible);
        cardNumberField.setValue(number);
    }

    public boolean checkNumber(String number) {
        return cardNumberField.getValue().equals(number);
    }

    public boolean checkCvc(String number) {
        return cvcField.getValue().equals(number);
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

    public void messageFieldAreRequired() {
        $$(".input__sub").shouldHave(CollectionCondition.size(1)).shouldHave(CollectionCondition.texts("Поле обязательно для заполнения"));
    }

    public void messageDeclined() {
        $$(".input__sub").shouldHave(CollectionCondition.size(1)).shouldHave(CollectionCondition.texts("Карта отклонена, обратитесь в ваш банк или попробуйте другую карту"));
    }
}