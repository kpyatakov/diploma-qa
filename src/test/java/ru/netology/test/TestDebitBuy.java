package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.page.MainPage;
import ru.netology.page.PaymentFormPageDebit;
import ru.netology.sql.SqlRequest;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDebitBuy {
    private MainPage mainPage;
    private PaymentFormPageDebit paymentFormPageDebit;

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
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForSuccessedNotification();
        val expected = DataHelper.getFirstCardStatus();
        val actual = SqlRequest.getDebitPaymentStatus();
        assertEquals(expected, actual);
    }

    @Test
    void shouldDenyPurchaseWithEmptyFields() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getEmptyCardNumber();
        val month = DataHelper.getEmptyMonth();
        val year = DataHelper.getEmptyYear();
        val cardOwner = DataHelper.getEmptyOwner();
        val code = DataHelper.getEmptyCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForMandatoryFieldMessage();
    }

    @Test
    void shouldDenyPurchaseWithEmptyCardNumberField() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getEmptyCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForMandatoryFieldMessage();
    }

    @Test
    void shouldDenyPurchaseWithDeclinedCard() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getSecondCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForFailedNotification();
        val expected = DataHelper.getSecondCardStatus();
        val actual = SqlRequest.getDebitPaymentStatus();
        assertEquals(expected, actual);
    }

    @Test
    void shouldDenyPurchaseWithAnotherCard() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getRandomCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForFailedNotification();
    }

    @Test
    void shouldDenyPurchaseCardNumberWith15Digits() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getCardNumberWith15Digits();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseCardNumberWith1Digit() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getCardNumberWith1Digit();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseCardNumberWithTextAndChars() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getCardNumberWithTextAndChars();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForInvalidCharactersMessage();
    }

    @Test
    void shouldDenyPurchaseWithEmptyMonthField() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getEmptyMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForMandatoryFieldMessage();
    }

    @Test
    void shouldDenyPurchaseWithMonthOver12() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getMonthOver12();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForWrongCardExpirationMessage();
    }

    @Test
    void shouldDenyPurchaseWithZeroMonth() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getZeroMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForWrongCardExpirationMessage();
    }

    @Test
    void shouldDenyPurchaseWithWrongFormatMonth() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getInvalidFormatMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithTextInMonthField() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getMonthWithText();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForInvalidCharactersMessage();
    }

    @Test
    void shouldDenyPurchaseWithEmptyYearField() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getEmptyYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForMandatoryFieldMessage();
    }

    @Test
    void shouldDenyPurchaseWithPastYear() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getPastYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForCardExpiredMessage();
    }

    @Test
    void shouldDenyPurchaseWithWrongFormatYear() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getInvalidFormatYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithTooFutureYear() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getFutureYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForWrongCardExpirationMessage();
    }

    @Test
    void shouldDenyPurchaseWithTextInYearField() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getYearWithText();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForInvalidCharactersMessage();
    }

    @Test
    void shouldDenyPurchaseWithEmptyCardOwnerField() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getEmptyOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForMandatoryFieldMessage();
    }

    @Test
    void shouldDenyPurchaseWithoutSecondName() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getOnlyNameOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithLowercaseCardOwner() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getLowercaseLettersOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithUppercaseCardOwner() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getUppercaseLettersOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithRedundantDataCardOwner() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getRedundantDataOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithCyrillicDataCardOwner() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getCyrillicDataOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithTwoLanguagesCardOwner() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getTwoAlphabetsDataOwner();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForInvalidCharactersMessage();
    }

    @Test
    void shouldDenyPurchaseWithDigitsCardOwner() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getOwnerWithDigits();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForInvalidCharactersMessage();
    }

    @Test
    void shouldDenyPurchaseWithSpecialCharsCardOwner() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getOwnerWithSpecialChars();
        val code = DataHelper.getValidCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForInvalidCharactersMessage();
    }

    @Test
    void shouldDenyPurchaseWithEmptyCodeField() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getEmptyCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForMandatoryFieldMessage();
    }

    @Test
    void shouldDenyPurchaseWithWrongFormatCode() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getInvalidFormatCode();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForWrongFormatMessage();
    }

    @Test
    void shouldDenyPurchaseWithTextInCodeField() {
        paymentFormPageDebit = mainPage.payWithDebitCard()
                .clear();
        val cardNumber = DataHelper.getFirstCardNumber();
        val month = DataHelper.getValidMonth();
        val year = DataHelper.getValidYear();
        val cardOwner = DataHelper.getValidOwner();
        val code = DataHelper.getCodeWithText();
        paymentFormPageDebit.fillForm(cardNumber, month, year, cardOwner, code);
        paymentFormPageDebit.waitForInvalidCharactersMessage();
    }
}