package ru.netology.test;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.page.MainPage;
import ru.netology.page.PaymentFormPageCredit;
import ru.netology.sql.SqlRequest;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestCreditBuy {
    private MainPage mainPage;
    private PaymentFormPageCredit paymentFormPageCredit;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    public void setUp() {
        mainPage = open("http://localhost:8080/", MainPage.class);
    }

    @AfterEach
    void cleanDB() {
        SqlRequest.clearDB();
    }


    @Test
    void shouldAllowPurchaseWithApprovedCard() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForSuccessedNotification();
        val expected = DataHelper.getFirstCardStatus();
        val actual = SqlRequest.getCreditPaymentStatus();
        assertEquals(expected, actual);
    }

    @Test
    void shouldDenyPurchaseWithEmptyFields() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getEmptyCardNumber();
        val month = DataHelper.getEmptyMonth();
        val year = DataHelper.getEmptyYear();
        val cardOwner = DataHelper.getEmptyOwner();
        val code = DataHelper.getEmptyCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForMandatoryFieldMessage();
    }

    @Test
    void shouldDenyPurchaseWithEmptyFieldCardNumber() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getEmptyCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForMandatoryFieldMessage();
    }

    @Test
    void shouldDenyPurchaseWithDeclinedCard() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getSecondCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForFailedNotification();
        val expected = DataHelper.getSecondCardStatus();
        val actual = SqlRequest.getCreditPaymentStatus();
        assertEquals(expected, actual);
    }

    @Test
    void shouldDenyPurchaseWithAnotherCard() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getRandomCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForFailedNotification();
    }

    @Test
    void shouldDenyPurchaseCardNumberWith15Digits() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getCardNumberWith15Digits();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseCardNumberWith1Digit() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getCardNumberWith1Digit();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseCardNumberWithTextAndChars() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getCardNumberWithTextAndChars();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForInvalidCharactersMessage();
    }

    @Test
    void shouldDenyPurchaseWithEmptyFieldMonth() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getEmptyMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForMandatoryFieldMessage();
    }

    @Test
    void shouldDenyPurchaseWithMonthOver12() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getMonthOver12();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForWrongCardExpirationMessage();
    }

    @Test
    void shouldDenyPurchaseWithZeroMonth() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getZeroMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForWrongCardExpirationMessage();
    }

    @Test
    void shouldDenyPurchaseWithWrongFormatMonth() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getInvalidFormatMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithTextInMonthField() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getMonthWithText();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForInvalidCharactersMessage();
    }

    @Test
    void shouldDenyPurchaseWithEmptyYearField() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getEmptyYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForMandatoryFieldMessage();
    }

    @Test
    void shouldDenyPurchaseWithPastYear() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getPastYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForCardExpiredMessage();
    }

    @Test
    void shouldDenyPurchaseWithWrongFormatYear() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getInvalidFormatYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithTooFutureYear() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getFutureYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForWrongCardExpirationMessage();
    }

    @Test
    void shouldDenyPurchaseWithTextInYearField() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getYearWithText();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForInvalidCharactersMessage();
    }

    @Test
    void shouldDenyPurchaseWithEmptyCardOwnerField() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getEmptyOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForMandatoryFieldMessage();
    }

    @Test
    void shouldDenyPurchaseWithoutSecondName() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getOnlyNameOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithLowercaseCardOwner() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getLowercaseLettersOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithUppercaseCardOwner() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getUppercaseLettersOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithRedundantDataCardOwner() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getRedundantDataOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithCyrillicDataCardOwner() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getCyrillicDataOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithTwoLanguagesCardOwner() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getTwoAlphabetsDataOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForInvalidCharactersMessage();
    }

    @Test
    void shouldDenyPurchaseWithDigitsCardOwner() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getOwnerWithDigits();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForInvalidCharactersMessage();
    }

    @Test
    void shouldDenyPurchaseWithSpecialCharsCardOwner() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getOwnerWithSpecialChars();
        val code = DataHelper.getValidCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForInvalidCharactersMessage();
    }

    @Test
    void shouldDenyPurchaseWithEmptyCodeField() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getEmptyCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForMandatoryFieldMessage();
    }

    @Test
    void shouldDenyPurchaseWithWrongFormatCode() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getInvalidFormatCode();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithTextInCodeField() {
        paymentFormPageCredit = mainPage.payWithCreditCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getCodeWithText();
        paymentFormPageCredit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageCredit.waitForInvalidCharactersMessage();
    }
}