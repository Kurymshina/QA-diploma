package ru.netology.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import ru.netology.data.DataHelper;
import ru.netology.data.DataHelperDB;
import ru.netology.pages.CreditPage;
import ru.netology.pages.PaymentPage;
import ru.netology.pages.TourPage;

import java.util.stream.Stream;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TourServiceTest {
    TourPage tourPage = new TourPage();
    PaymentPage paymentPage = new PaymentPage();
    CreditPage creditPage = new CreditPage();

    @BeforeEach
    void clearDatabaseTables() {
        open("http://localhost:8080/");
        DataHelperDB.clearTables();
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("1 Покупка тура валидной картой с помощью кнопки \"Купить\" со статусом \"APPROVED\"")
    public void shouldBuyWithValidCardAndApproved() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getApprovedCardNumber());
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageSuccessNotification();
        assertEquals("APPROVED", DataHelperDB.findPayStatus());
    }

    @Test
    @DisplayName("2 Покупка тура в кредит с валидной картой с помощью кнопки \"Купить в кредит\" со статусом \"APPROVED\"")
    public void shouldBuyInCreditWithValidCardAndApproved() {
        tourPage.payOnCredit();
        creditPage.setNumber(DataHelper.getApprovedCardNumber());
        creditPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        creditPage.setOwner(DataHelper.generateName());
        creditPage.setCVV(DataHelper.generateTreeDigit());
        creditPage.clickContinueButton();
        creditPage.messageSuccessNotification();
        assertEquals("APPROVED", DataHelperDB.findCreditStatus());
    }

    @Test
    @DisplayName("3.1.1 Ввод невалидного номера карты при покупке тура. (Длинна менее 16 символов)")
    public void shouldGetMessageWhenPurchaseWithInvalidCardNumberShorterThanExpected() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumberMustBeLessThan());
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
    }

    @Test
    @DisplayName("3.1.2 Ввод невалидного номера карты при покупке тура. (Длинна более 16 символов)")
    //d=вводится 16 символов, дальнейший ввод не возможен
    public void shouldBeSuccessWhenPurchaseWithInvalidCardNumberTallerThanExpected() {
        tourPage.pay();
        String a = DataHelper.generateCardNumber(17);
        paymentPage.setNumber(a);
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
        boolean actual = paymentPage.checkNumber(a);
        assertFalse(actual);
    }


    @Test
    @DisplayName("3.1.3 Ввод невалидного номера карты при покупке тура. (Вставка специальных символов)")
    //Вставка невозможна
    public void shouldGetMessageWhenPurchaseWithInvalidCardNumberContainsSpecialCharters() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateSpecialCharacters(17));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
    }

    @Test
    @DisplayName("3.1.4 Ввод невалидного номера карты при покупке тура. (Вставка кириллицы)")
    public void shouldGetMessageWhenPurchaseWithInvalidCardNumberContainsCyrillic() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCyrillicName());
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
    }

    @Test
    @DisplayName("3.1.5 Ввод невалидного номера карты при покупке тура. (Пустое поле)")
    public void shouldGetMessageWhenEmptyFieldNumberCard() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getEmptyNumberCard());
        paymentPage.setMonth(DataHelper.getNextMonth());
        paymentPage.setYear(DataHelper.getCurrentYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
        assertEquals(0, DataHelperDB.getOrderEntityCount());

    }

    @Test
    @DisplayName("3.2.1 Ввод даты с невалидным месяцем действия карты при покупке тура. (Значение в 1 символ")
    public void shouldGetMessageWhenMonthFieldWithOneDigits() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.getMonthWithOneValue());
        paymentPage.setYear(DataHelper.getCurrentYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
    }

    @Test
    @DisplayName("3.2.2 Ввод даты с невалидным месяцем действия карты при покупке тура. (Значение в 3 символа)")
    public void shouldGetMessageWhenMonthFieldWithTreeDigits() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        String a = DataHelper.generateInvalidMonthWihLengthThreeDigits();
        paymentPage.setMonth(a);
        paymentPage.setYear(DataHelper.getCurrentYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidDate();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
        boolean actual = paymentPage.checkNumber(a);
        assertFalse(actual);
    }


    @Test
    @DisplayName("3.2.3 Ввод даты с невалидным месяцем действия карты при покупке тура. (Пустое поле)")
    public void shouldGetMessageWhenMonthFieldEmpty() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.getEmptyMonth());
        paymentPage.setYear(DataHelper.getCurrentYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();

    }

    @Test
    @DisplayName("3.2.4 Ввод даты с невалидным месяцем действия карты при покупке тура. (Ввод прошедшего месяца при вводе текущего года, не действует при текущем месяца - январь)")
    public void shouldGetMessageWhenPurchaseWithPreviouslyMonth() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonth(DataHelper.getPreviousMonth());
        paymentPage.setYear(DataHelper.getCurrentYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidDate();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
    }

    @Test
    @DisplayName("3.2.5 Ввод даты с невалидным месяцем действия карты при покупке тура. (Значение больше 12)")
    public void shouldGetMessageWhenMonthFieldMoreThenTwelve() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getApprovedCardNumber());
        paymentPage.setMonth(String.valueOf(DataHelper.generateInvalidMonthMoreThen12()));
        paymentPage.setYear(DataHelper.generateValidYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidDate();
    }

    @ParameterizedTest
    @MethodSource("provideStringsForValidation")
    @DisplayName("3.2.6 Ввод даты с невалидным месяцем действия карты при покупке тура. (Ввод значений: c длиной в 1 символ, двухзначные больше 12, со значением \"00\", со значением \"0\" )")
    public void shouldGetMessageWhenMonthFieldWithInvalidMonth(String month, String year) {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonth(month);
        paymentPage.setYear(year);
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        String[] split = month.split("");
        {
            if
            (split.length > 1) {
                paymentPage.messageInvalidDate();
            } else {
                paymentPage.messageInvalidFormat();
            }

        }
    }

    private static Stream<Arguments> provideStringsForValidation() {
        return Stream.of(
                Arguments.of("0", "23"),
                Arguments.of("0", "24"),
                Arguments.of("0", "25"),
                Arguments.of("9", "23"),
                Arguments.of("9", "24"),
                Arguments.of("13", "23"),
                Arguments.of("13", "24"),
                Arguments.of("13", "25"),
                Arguments.of("00", "23"),
                Arguments.of("00", "24"),
                Arguments.of("00", "25")
        );
    }

    @Test
    @DisplayName("3.2.7 Ввод даты с невалидным месяцем действия карты при покупке тура. (Вставка специальных символов)")
    //Вставка невозможна
    public void shouldGetMessageWhenPurchaseWithMonthContainsSpecialCharters() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.generateSpecialCharacters(2));
        paymentPage.setYear(DataHelper.generateValidYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
    }

    @Test
    @DisplayName("3.2.8 Ввод даты с невалидным месяцем действия карты при покупке тура. (Вставка кириллицы)")
    //Вставка невозможна
    public void shouldGetMessageWhenPurchaseWithMonthContainsCyrillic() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.generateCyrillicName());
        paymentPage.setYear(DataHelper.generateValidYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();

    }

    @Test
    @DisplayName("3.3.1 Ввод даты с невалидным годом действия карты при покупке тура. (Ввод данных в поле Год с пустым значением)")
    public void shouldGetMessageWhenYearFieldIsEmpty() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.getNextMonth());
        paymentPage.setYear(DataHelper.getEmptyYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();

    }

    @Test
    @DisplayName("3.3.2 Ввод даты с невалидным годом действия карты при покупке тура. (Значение в 1 символ)")
    public void shouldGetMessageWhenYearFieldWithOneDigits() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.getNextMonth());
        paymentPage.setYear(DataHelper.getYearWithOneValue());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
    }

    @Test
    @DisplayName("3.3.3 Ввод даты с невалидным годом действия карты при покупке тура. (Значение в 3 символа)")
    public void shouldGetMessageWhenYearFieldWithThreeDigits() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.generateValidMonth());
        String a = DataHelper.generateInvalidMonthWihLengthThreeDigits();
        paymentPage.setYear(a);
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
        boolean actual = paymentPage.checkNumber(a);
        assertFalse(actual);
    }

    @Test
    @DisplayName("3.3.4 Ввод даты с невалидным годом действия карты при покупке тура.(Ввод данных с меньшим значением чем текущий год")
    public void shouldGetMessageWhenPurchaseWithInvalidLastYear() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.getNextMonth());
        paymentPage.setYear(DataHelper.getPreviousYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageExpiredDate();

    }

    @Test
    @DisplayName("3.3.5 Ввод даты с невалидным годом действия карты при покупке тура. (Год со значением 6+ лет)")
    public void shouldGetMessageWhenPurchaseWithInvalidNextYear() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.getNextMonth());
        paymentPage.setYear(DataHelper.getCurrentYearPlus5());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidDate();

    }

    @Test
    @DisplayName("3.3.6 Ввод даты с невалидным годом действия карты при покупке тура. (Год со значением 00)")
    public void shouldGetMessageWhenPurchaseWithYearIsZero() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.getNextMonth());
        paymentPage.setYear(DataHelper.getInvalidYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageExpiredDate();
    }

    @Test
    @DisplayName("3.3.7 Ввод даты с невалидным годом действия карты при покупке тура. (Вставка специальных символов)")
    //Вставка невозможна
    public void shouldGetMessageWhenPurchaseWithYearContainsSpecialCharters() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.generateValidYear());
        paymentPage.setYear(DataHelper.generateSpecialCharacters(2));
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
    }

    @Test
    @DisplayName("3.3.8 Ввод даты с невалидным годом действия карты при покупке тура. (Вставка кириллицы)")
    //Вставка невозможна
    public void shouldGetMessageWhenPurchaseWithYearContainsCyrillic() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.generateValidYear());
        paymentPage.setYear(DataHelper.generateCyrillicName());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();

    }


    @Test
    @DisplayName("3.4 Ввод карты c истекшим сроком действия при покупке тура. (Ввод даты раньше текущей даты)")
    public void shouldGetMessageWhenPurchaseWithExpiredCard() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateInvalidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageExpiredDate();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
    }

    @Test
    @DisplayName("3.5.1 Ввод невалидного имени владельца карты при покупке тура.(Ввод данных в поле Владелец пользователя с именем на кириллице)")
//баг
    public void shouldGetMessageWhenOwnerOnCyrillic() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateCyrillicName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();

    }

    @Test
    @DisplayName("3.5.2 Ввод невалидного имени владельца карты при покупке тура.(Ввод данных в поле Владелец в цифровом значении")
    //баг
    public void shouldGetMessageWhenOwnerOnDigits() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateCardNumber(21));
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();

    }

    @Test
    @DisplayName("3.5.3 Ввод невалидного имени владельца карты при покупке тура.(Ввод данных в поле Владелец с пустым значением)")
    public void shouldGetMessageWhenOwnerEmpty() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.emptyFormField());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageRequiredField();

    }

    @Test
    @DisplayName("3.5.3 Ввод невалидного имени владельца карты при покупке тура.(Ввод данных в поле Владелец со спецсимволами")
    public void shouldGetMessageWhenOwnerOnSymbols() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateSpecialCharacters(15));
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageFieldAreRequired();


    }

    @Test
    @DisplayName("3.5.4 Ввод невалидного имени владельца карты при покупке тура.(Значение длинной более 25 символов в верхнем и нижнем регистре")
    public void shouldGetMessageWhenOwnerLargest() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        String a = DataHelper.generateRandomText(26);
        paymentPage.setOwner(a);
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
        boolean actual = paymentPage.checkNumber(a);
        assertFalse(actual);


    }

    @Test
    @DisplayName("3.6.1 Ввод невалидного CVV-кода при покупке тура. (Ввод данных длиной в 1 символ")
    public void shouldGetMessageWhenTheFieldCvcHasOneValue() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomOneDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();


    }

    @Test
    @DisplayName("3.6.2 Ввод невалидного CVV-кода при покупке тура. (Ввод данных длиной в 2 символа")
    public void shouldGetMessageWhenTheFieldCvcHasTwoValue() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomTwoDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
    }

    @Test
    @DisplayName("3.6.3 Ввод невалидного CVV-кода при покупке тура. (Ввод данных длиной в 4 символа")
    public void shouldGetMessageWhenTheFieldCvcHasFourValue() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        String a = DataHelper.generateCardNumber(4);
        paymentPage.setCVV(a);
        paymentPage.clickContinueButton();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
        boolean actual = paymentPage.checkCvc(a);
        assertFalse(actual);
    }

    @Test
    @DisplayName("3.6.4 Ввод невалидного CVV-кода при покупке тура. (Ввод данных с пустым значением")
    public void shouldGetMessageWhenTheFieldCvcIsEmpty() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.emptyFormField());
        paymentPage.clickContinueButton();
        paymentPage.messageFieldAreRequired();


    }

    @ParameterizedTest
    @CsvFileSource(files = "src/test/resources/ValidCvc.csv")
    @DisplayName("3.6.5 Ввод валидного CVV-кода при покупке тура. (Ввод пограничных значений: c длиной в 3 символа")
    public void shouldBeSuccessWhenValidCvc(String a) {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getApprovedCardNumber());
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(a);
        paymentPage.clickContinueButton();
        paymentPage.messageSuccessNotification();
    }


    @Test
    @DisplayName("4 Попытка оплаты валидной картой со статусом \"DECLINED\" ")
    public void shouldNotBuyWithValidCardAndDeclined() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getDeclinedCardNumber());
        paymentPage.setMonth(DataHelper.getNextMonth());
        paymentPage.setYear(DataHelper.getCurrentYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageDeclined();
        assertEquals("DECLINED", DataHelperDB.findPayStatus());

    }

    @Test
    @DisplayName("5.1.1 Ввод невалидного номера карты при покупке тура в кредит. (Длинна менее 16 символов)")
    public void shouldGetMessageWhenPurchaseOnCreditWithInvalidCardNumberShorterThanExpected() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumberMustBeLessThan());
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
    }

    @Test
    @DisplayName("5.1.2 Ввод невалидного номера карты при покупке тура в кредит. (Длинна более 16 символов)")
    //d=вводится 16 символов, дальнейший ввод не возможен
    public void shouldBeSuccessWhenPurchaseOnCreditWithInvalidCardNumberTallerThanExpected() {
        tourPage.pay();
        String a = DataHelper.generateCardNumber(17);
        paymentPage.setNumber(a);
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
        boolean actual = paymentPage.checkNumber(a);
        assertFalse(actual);
    }


    @Test
    @DisplayName("5.1.3 Ввод невалидного номера карты при покупке тура в кредит. (Вставка специальных символов)")
    //Вставка невозможна
    public void shouldGetMessageWhenPurchaseOnCreditWithInvalidCardNumberContainsSpecialCharters() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateSpecialCharacters(17));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
    }

    @Test
    @DisplayName("5.1.4 Ввод невалидного номера карты при покупке тура в кредит. (Вставка кириллицы)")
    //Вставка невозможна
    public void shouldGetMessageWhenPurchaseOnCreditWithInvalidCardNumberContainsCyrillic() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCyrillicName());
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
    }

    @Test
    @DisplayName("5.1.5 Ввод невалидного номера карты при покупке тура в кредит. (Пустое поле)")
    public void shouldGetMessageWhenPurchaseOnCreditWithEmptyFieldNumberCard() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getEmptyNumberCard());
        paymentPage.setMonth(DataHelper.getNextMonth());
        paymentPage.setYear(DataHelper.getCurrentYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
        assertEquals(0, DataHelperDB.getOrderEntityCount());

    }

    @Test
    @DisplayName("5.2.1 Ввод даты с невалидным месяцем действия карты при покупке тура в кредит. (Значение в 1 символ")
    public void shouldGetMessageWhenPurchaseOnCreditWithMonthFieldWithOneDigits() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.getMonthWithOneValue());
        paymentPage.setYear(DataHelper.getCurrentYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
    }

    @Test
    @DisplayName("5.2.2 Ввод даты с невалидным месяцем действия карты при покупке тура в кредит. (Значение в 3 символа)")
    public void shouldGetMessageWhenPurchaseOnCreditWithMonthFieldWithTreeDigits() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        String a = DataHelper.generateInvalidMonthWihLengthThreeDigits();
        paymentPage.setMonth(a);
        paymentPage.setYear(DataHelper.getCurrentYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidDate();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
        boolean actual = paymentPage.checkNumber(a);
        assertFalse(actual);
    }


    @Test
    @DisplayName("5.2.3 Ввод даты с невалидным месяцем действия карты при покупке тура в кредит. (Пустое поле)")
    public void shouldGetMessageWhenPurchaseOnCreditWithMonthFieldEmpty() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.getEmptyMonth());
        paymentPage.setYear(DataHelper.getCurrentYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();

    }

    @Test
    @DisplayName("5.2.4 Ввод даты с невалидным месяцем действия карты при покупке тура в кредит. (Ввод прошедшего месяца при вводе текущего года, не действует при текущем месяца - январь)")
    public void shouldGetMessageWhenPurchaseOnCreditWithPreviouslyMonth() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonth(DataHelper.getPreviousMonth());
        paymentPage.setYear(DataHelper.getCurrentYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidDate();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
    }

    @Test
    @DisplayName("5.2.5 Ввод даты с невалидным месяцем действия карты при покупке тура в кредит. (Значение больше 12)")
    public void shouldGetMessageWhenPurchaseOnCreditWithMonthFieldMoreThenTwelve() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getApprovedCardNumber());
        paymentPage.setMonth(String.valueOf(DataHelper.generateInvalidMonthMoreThen12()));
        paymentPage.setYear(DataHelper.generateValidYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidDate();
    }

    @ParameterizedTest
    @MethodSource("provideStringsForValidation")
    @DisplayName("5.2.6 Ввод даты с невалидным месяцем действия карты при покупке тура в кредит. (Ввод значений: c длиной в 1 символ, двухзначные больше 12, со значением \"00\", со значением \"0\" )")
    public void shouldGetMessageWhenPurchaseOnCreditWithMonthFieldWithInvalidMonth(String month, String year) {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonth(month);
        paymentPage.setYear(year);
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        String[] split = month.split("");
        {
            if
            (split.length > 1) {
                paymentPage.messageInvalidDate();
            } else {
                paymentPage.messageInvalidFormat();
            }

        }
    }


    @Test
    @DisplayName("5.2.7 Ввод даты с невалидным месяцем действия карты при покупке тура в кредит. (Вставка специальных символов)")
    //Вставка невозможна
    public void shouldGetMessageWhenPurchaseOnCreditWithMonthContainsSpecialCharters() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.generateSpecialCharacters(2));
        paymentPage.setYear(DataHelper.generateValidYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
    }

    @Test
    @DisplayName("5.2.8 Ввод даты с невалидным месяцем действия карты при покупке тура в кредит. (Вставка кириллицы)")
    //Вставка невозможна
    public void shouldGetMessageWhenPurchaseOnCreditWithMonthContainsCyrillic() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.generateCyrillicName());
        paymentPage.setYear(DataHelper.generateValidYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();

    }

    @Test
    @DisplayName("5.3.1 Ввод даты с невалидным годом действия карты при покупке тура в кредит. (Ввод данных в поле Год с пустым значением)")
    public void shouldGetMessageWhenPurchaseOnCreditWithYearFieldIsEmpty() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.getNextMonth());
        paymentPage.setYear(DataHelper.getEmptyYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();

    }

    @Test
    @DisplayName("5.3.2 Ввод даты с невалидным годом действия карты при покупке тура в кредит. (Значение в 1 символ)")
    public void shouldGetMessageWhenPurchaseOnCreditWithYearFieldWithOneDigits() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.getNextMonth());
        paymentPage.setYear(DataHelper.getYearWithOneValue());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
    }

    @Test
    @DisplayName("5.3.3 Ввод даты с невалидным годом действия карты при покупке тура в кредит. (Значение в 3 символа)")
    public void shouldGetMessageWhenPurchaseOnCreditWithYearFieldWithThreeDigits() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.generateValidMonth());
        String a = DataHelper.generateInvalidMonthWihLengthThreeDigits();
        paymentPage.setYear(a);
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidDate();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
        boolean actual = paymentPage.checkNumber(a);
        assertFalse(actual);
    }

    @Test
    @DisplayName("5.3.4 Ввод даты с невалидным годом действия карты при покупке тура в кредит.(Ввод данных с меньшим значением чем текущий год")
    public void shouldGetMessageWhenPurchaseOnCreditWithInvalidLastYear() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.getNextMonth());
        paymentPage.setYear(DataHelper.getPreviousYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageExpiredDate();

    }

    @Test
    @DisplayName("5.3.5 Ввод даты с невалидным годом действия карты при покупке тура в кредит. (Год со значением 6+ лет)")
    public void shouldGetMessageWhenPurchaseOnCreditWithInvalidNextYear() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.getNextMonth());
        paymentPage.setYear(DataHelper.getCurrentYearPlus5());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidDate();

    }

    @Test
    @DisplayName("5.3.6 Ввод даты с невалидным годом действия карты при покупке тура в кредит. (Год со значением 00)")
    public void shouldGetMessageWhenPurchaseOnCreditWithYearIsZero() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.getNextMonth());
        paymentPage.setYear(DataHelper.getInvalidYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageExpiredDate();
    }

    @Test
    @DisplayName("5.3.7 Ввод даты с невалидным годом действия карты при покупке тура в кредит. (Вставка специальных символов)")
    //Вставка невозможна
    public void shouldGetMessageWhenPurchaseOnCreditWithYearContainsSpecialCharters() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.generateValidYear());
        paymentPage.setYear(DataHelper.generateSpecialCharacters(2));
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
    }

    @Test
    @DisplayName("5.3.8 Ввод даты с невалидным годом действия карты при покупке тура в кредит. (Вставка кириллицы)")
    //Вставка невозможна
    public void shouldGetMessageWhenPurchaseOnCreditWithYearContainsCyrillic() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getRandomCardNumber());
        paymentPage.setMonth(DataHelper.generateValidYear());
        paymentPage.setYear(DataHelper.generateCyrillicName());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();

    }


    @Test
    @DisplayName("5.4 Ввод карты c истекшим сроком действия при покупке тура в кредит. (Ввод даты раньше текущей даты)")
    public void shouldGetMessageWhenPurchaseOnCreditWithExpiredCard() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateInvalidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageExpiredDate();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
    }

    @Test
    @DisplayName("5.5.1 Ввод невалидного имени владельца карты при покупке тура в кредит.(Ввод данных в поле Владелец пользователя с именем на кириллице)")
//баг
    public void shouldGetMessageWhenPurchaseOnCreditWithOwnerOnCyrillic() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateCyrillicName());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();

    }

    @Test
    @DisplayName("5.5.2 Ввод невалидного имени владельца карты при покупке тура в кредит.(Ввод данных в поле Владелец в цифровом значении")
    //баг
    public void shouldGetMessageWhenPurchaseOnCreditWithOwnerOnDigits() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateCardNumber(21));
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();

    }

    @Test
    @DisplayName("5.5.3 Ввод невалидного имени владельца карты при покупке тура в кредит.(Ввод данных в поле Владелец с пустым значением)")
    public void shouldGetMessageWhenPurchaseOnCreditWithOwnerEmpty() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.emptyFormField());
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageRequiredField();

    }

    @Test
    @DisplayName("5.5.3 Ввод невалидного имени владельца карты при покупке тура в кредит.(Ввод данных в поле Владелец со спецсимволами")
    public void shouldGetMessageWhenPurchaseOnCreditWithOwnerOnSymbols() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateSpecialCharacters(15));
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageFieldAreRequired();


    }

    @Test
    @DisplayName("5.5.4 Ввод невалидного имени владельца карты при покупке тура в кредит.(Значение длинной более 25 символов в верхнем и нижнем регистре")
    public void shouldGetMessageWhenPurchaseOnCreditWithOwnerLargest() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        String a = DataHelper.generateRandomText(26);
        paymentPage.setOwner(a);
        paymentPage.setCVV(DataHelper.generateTreeDigit());
        paymentPage.clickContinueButton();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
        boolean actual = paymentPage.checkNumber(a);
        assertFalse(actual);


    }

    @Test
    @DisplayName("5.6.1 Ввод невалидного CVV-кода при покупке тура в кредит. (Ввод данных длиной в 1 символ")
    public void shouldGetMessageWhenPurchaseOnCreditWithTheFieldCvcHasOneValue() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomOneDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();


    }

    @Test
    @DisplayName("5.6.2 Ввод невалидного CVV-кода при покупке тура в кредит. (Ввод данных длиной в 2 символа")
    public void shouldGetMessageWhenPurchaseOnCreditWithTheFieldCvcHasTwoValue() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomTwoDigit());
        paymentPage.clickContinueButton();
        paymentPage.messageInvalidFormat();
    }

    @Test
    @DisplayName("5.6.3 Ввод невалидного CVV-кода при покупке тура в кредит. (Ввод данных длиной в 4 символа")
    public void shouldGetMessageWhenPurchaseOnCreditWithTheFieldCvcHasFourValue() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        String a = DataHelper.generateCardNumber(4);
        paymentPage.setCVV(a);
        paymentPage.clickContinueButton();
        assertEquals(0, DataHelperDB.getOrderEntityCount());
        boolean actual = paymentPage.checkCvc(a);
        assertFalse(actual);
    }

    @Test
    @DisplayName("5.6.4 Ввод невалидного CVV-кода при покупке тура в кредит. (Ввод данных с пустым значением)")
    public void shouldGetMessageWhenPurchaseOnCreditWithTheFieldCvcIsEmpty() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.generateCardNumber(16));
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.emptyFormField());
        paymentPage.clickContinueButton();
        paymentPage.messageFieldAreRequired();


    }

    @ParameterizedTest
    @CsvFileSource(files = "src/test/resources/ValidCvc.csv")
    @DisplayName("5.6.5 Ввод валидного CVV-кода при покупке тура в кредит. (Ввод пограничных значений: c длиной в 3 символа)")
    public void shouldBeSuccessWhenPurchaseOnCreditWithValidCvc(String a) {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getApprovedCardNumber());
        paymentPage.setMonthAndYear(DataHelper.generateValidMonthAndYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(a);
        paymentPage.clickContinueButton();
        paymentPage.messageSuccessNotification();
    }


    @Test
    @DisplayName("6 Попытка оплаты в кредит валидной картой со статусом \"DECLINED\" ")
    public void shouldNotBuyOnCreditWithValidCardAndDeclined() {
        tourPage.pay();
        paymentPage.setNumber(DataHelper.getDeclinedCardNumber());
        paymentPage.setMonth(DataHelper.getNextMonth());
        paymentPage.setYear(DataHelper.getCurrentYear());
        paymentPage.setOwner(DataHelper.generateName());
        paymentPage.setCVV(DataHelper.getRandomCvc());
        paymentPage.clickContinueButton();
        paymentPage.messageDeclined();
        assertEquals("DECLINED", DataHelperDB.findPayStatus());

    }


}







