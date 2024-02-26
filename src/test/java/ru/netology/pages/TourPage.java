package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;

public class TourPage {
    private final SelenideElement header = $$("h2").findBy(Condition.text("Путешествие дня"));
    private final SelenideElement buyButton = $$("button").findBy(Condition.text("Купить"));
    private final SelenideElement creditButton = $$("button").findBy(Condition.text("Купить в кредит"));

    public void pay() {
        header.shouldBe(visible);
        buyButton.click();

    }

    public void payOnCredit() {
        header.shouldBe(visible);
        creditButton.click();
    }
}