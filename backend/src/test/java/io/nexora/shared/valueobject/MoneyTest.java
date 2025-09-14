package io.nexora.shared.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive unit tests for Money value object.
 * 
 * These tests verify all validation logic, behavior, and edge cases
 * implemented in the Money value object.
 * 
 * Test Coverage:
 * - Constructor validation and behavior
 * - Money arithmetic operations (add, subtract)
 * - Currency validation and matching
 * - Edge cases and error conditions
 * - Value object equality and immutability
 * - String representation
 * 
 * Design Principles Applied:
 * - Test-Driven Development: Tests verify expected behavior
 * - Comprehensive Coverage: All validation rules and edge cases
 * - Clear Test Structure: Organized with nested classes for clarity
 * - Value Object Testing: Focus on validation and immutability
 */
@DisplayName("Money Value Object Tests")
class MoneyTest {

    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency GBP = Currency.getInstance("GBP");

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create money successfully with valid amount and currency")
        void shouldCreateMoneySuccessfullyWithValidAmountAndCurrency() {
            // Given
            BigDecimal amount = new BigDecimal("100.00");
            Currency currency = USD;

            // When
            Money money = new Money(amount, currency);

            // Then
            assertThat(money).isNotNull();
            assertThat(money.amount()).isEqualTo(amount);
            assertThat(money.currency()).isEqualTo(currency);
        }

        @Test
        @DisplayName("Should create money with zero amount")
        void shouldCreateMoneyWithZeroAmount() {
            // Given
            BigDecimal amount = BigDecimal.ZERO;
            Currency currency = USD;

            // When
            Money money = new Money(amount, currency);

            // Then
            assertThat(money.amount()).isEqualTo(BigDecimal.ZERO);
            assertThat(money.currency()).isEqualTo(currency);
        }

        @Test
        @DisplayName("Should create money with very small amount")
        void shouldCreateMoneyWithVerySmallAmount() {
            // Given
            BigDecimal amount = new BigDecimal("0.01");
            Currency currency = USD;

            // When
            Money money = new Money(amount, currency);

            // Then
            assertThat(money.amount()).isEqualTo(amount);
            assertThat(money.currency()).isEqualTo(currency);
        }

        @Test
        @DisplayName("Should create money with very large amount")
        void shouldCreateMoneyWithVeryLargeAmount() {
            // Given
            BigDecimal amount = new BigDecimal("999999999.99");
            Currency currency = USD;

            // When
            Money money = new Money(amount, currency);

            // Then
            assertThat(money.amount()).isEqualTo(amount);
            assertThat(money.currency()).isEqualTo(currency);
        }

        @Test
        @DisplayName("Should throw exception when amount is null")
        void shouldThrowExceptionWhenAmountIsNull() {
            // When & Then
            assertThatThrownBy(() -> new Money(null, USD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Amount and currency must not be null");
        }

        @Test
        @DisplayName("Should throw exception when currency is null")
        void shouldThrowExceptionWhenCurrencyIsNull() {
            // Given
            BigDecimal amount = new BigDecimal("100.00");

            // When & Then
            assertThatThrownBy(() -> new Money(amount, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Amount and currency must not be null");
        }

        @Test
        @DisplayName("Should throw exception when both amount and currency are null")
        void shouldThrowExceptionWhenBothAmountAndCurrencyAreNull() {
            // When & Then
            assertThatThrownBy(() -> new Money(null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Amount and currency must not be null");
        }

        @Test
        @DisplayName("Should throw exception when amount is negative")
        void shouldThrowExceptionWhenAmountIsNegative() {
            // Given
            BigDecimal negativeAmount = new BigDecimal("-100.00");

            // When & Then
            assertThatThrownBy(() -> new Money(negativeAmount, USD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Amount must not be negative");
        }

        @ParameterizedTest
        @ValueSource(strings = {"-0.01", "-1.00", "-100.00", "-999999.99"})
        @DisplayName("Should throw exception for various negative amounts")
        void shouldThrowExceptionForVariousNegativeAmounts(String negativeAmountStr) {
            // Given
            BigDecimal negativeAmount = new BigDecimal(negativeAmountStr);

            // When & Then
            assertThatThrownBy(() -> new Money(negativeAmount, USD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Amount must not be negative");
        }
    }

    @Nested
    @DisplayName("Arithmetic Operations Tests")
    class ArithmeticOperationsTests {

        @Test
        @DisplayName("Should add money successfully with same currency")
        void shouldAddMoneySuccessfullyWithSameCurrency() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"), USD);
            Money money2 = new Money(new BigDecimal("50.00"), USD);

            // When
            Money result = money1.add(money2);

            // Then
            assertThat(result.amount()).isEqualTo(new BigDecimal("150.00"));
            assertThat(result.currency()).isEqualTo(USD);
        }

        @Test
        @DisplayName("Should subtract money successfully with same currency")
        void shouldSubtractMoneySuccessfullyWithSameCurrency() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"), USD);
            Money money2 = new Money(new BigDecimal("30.00"), USD);

            // When
            Money result = money1.subtract(money2);

            // Then
            assertThat(result.amount()).isEqualTo(new BigDecimal("70.00"));
            assertThat(result.currency()).isEqualTo(USD);
        }

        @Test
        @DisplayName("Should add money with zero amount")
        void shouldAddMoneyWithZeroAmount() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"), USD);
            Money money2 = new Money(BigDecimal.ZERO, USD);

            // When
            Money result = money1.add(money2);

            // Then
            assertThat(result.amount()).isEqualTo(new BigDecimal("100.00"));
            assertThat(result.currency()).isEqualTo(USD);
        }

        @Test
        @DisplayName("Should subtract money resulting in zero")
        void shouldSubtractMoneyResultingInZero() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"), USD);
            Money money2 = new Money(new BigDecimal("100.00"), USD);

            // When
            Money result = money1.subtract(money2);

            // Then
            assertThat(result.amount()).isEqualTo(BigDecimal.ZERO);
            assertThat(result.currency()).isEqualTo(USD);
        }

        @Test
        @DisplayName("Should handle decimal precision correctly in addition")
        void shouldHandleDecimalPrecisionCorrectlyInAddition() {
            // Given
            Money money1 = new Money(new BigDecimal("0.10"), USD);
            Money money2 = new Money(new BigDecimal("0.20"), USD);

            // When
            Money result = money1.add(money2);

            // Then
            assertThat(result.amount()).isEqualTo(new BigDecimal("0.30"));
        }

        @Test
        @DisplayName("Should handle decimal precision correctly in subtraction")
        void shouldHandleDecimalPrecisionCorrectlyInSubtraction() {
            // Given
            Money money1 = new Money(new BigDecimal("0.30"), USD);
            Money money2 = new Money(new BigDecimal("0.10"), USD);

            // When
            Money result = money1.subtract(money2);

            // Then
            assertThat(result.amount()).isEqualTo(new BigDecimal("0.20"));
        }

        @Test
        @DisplayName("Should throw exception when adding money with different currencies")
        void shouldThrowExceptionWhenAddingMoneyWithDifferentCurrencies() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"), USD);
            Money money2 = new Money(new BigDecimal("50.00"), EUR);

            // When & Then
            assertThatThrownBy(() -> money1.add(money2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Currencies must match");
        }

        @Test
        @DisplayName("Should throw exception when subtracting money with different currencies")
        void shouldThrowExceptionWhenSubtractingMoneyWithDifferentCurrencies() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"), USD);
            Money money2 = new Money(new BigDecimal("50.00"), EUR);

            // When & Then
            assertThatThrownBy(() -> money1.subtract(money2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Currencies must match");
        }
    }

    @Nested
    @DisplayName("Currency Tests")
    class CurrencyTests {

        @Test
        @DisplayName("Should work with USD currency")
        void shouldWorkWithUsdCurrency() {
            // Given
            Money money = new Money(new BigDecimal("100.00"), USD);

            // When & Then
            assertThat(money.currency()).isEqualTo(USD);
            assertThat(money.currency().getCurrencyCode()).isEqualTo("USD");
        }

        @Test
        @DisplayName("Should work with EUR currency")
        void shouldWorkWithEurCurrency() {
            // Given
            Money money = new Money(new BigDecimal("100.00"), EUR);

            // When & Then
            assertThat(money.currency()).isEqualTo(EUR);
            assertThat(money.currency().getCurrencyCode()).isEqualTo("EUR");
        }

        @Test
        @DisplayName("Should work with GBP currency")
        void shouldWorkWithGbpCurrency() {
            // Given
            Money money = new Money(new BigDecimal("100.00"), GBP);

            // When & Then
            assertThat(money.currency()).isEqualTo(GBP);
            assertThat(money.currency().getCurrencyCode()).isEqualTo("GBP");
        }

        @Test
        @DisplayName("Should work with JPY currency (no decimal places)")
        void shouldWorkWithJpyCurrency() {
            // Given
            Currency JPY = Currency.getInstance("JPY");
            Money money = new Money(new BigDecimal("100"), JPY);

            // When & Then
            assertThat(money.currency()).isEqualTo(JPY);
            assertThat(money.currency().getCurrencyCode()).isEqualTo("JPY");
        }
    }

    @Nested
    @DisplayName("Value Object Behavior Tests")
    class ValueObjectBehaviorTests {

        @Test
        @DisplayName("Should be equal when amount and currency are the same")
        void shouldBeEqualWhenAmountAndCurrencyAreTheSame() {
            // Given
            BigDecimal amount = new BigDecimal("100.00");
            Currency currency = USD;
            Money money1 = new Money(amount, currency);
            Money money2 = new Money(amount, currency);

            // When & Then
            assertThat(money1).isEqualTo(money2);
            assertThat(money1.hashCode()).isEqualTo(money2.hashCode());
        }

        @Test
        @DisplayName("Should not be equal when amounts are different")
        void shouldNotBeEqualWhenAmountsAreDifferent() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"), USD);
            Money money2 = new Money(new BigDecimal("200.00"), USD);

            // When & Then
            assertThat(money1).isNotEqualTo(money2);
        }

        @Test
        @DisplayName("Should not be equal when currencies are different")
        void shouldNotBeEqualWhenCurrenciesAreDifferent() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"), USD);
            Money money2 = new Money(new BigDecimal("100.00"), EUR);

            // When & Then
            assertThat(money1).isNotEqualTo(money2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            // Given
            Money money = new Money(new BigDecimal("100.00"), USD);

            // When & Then
            assertThat(money).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Should not be equal to different type")
        void shouldNotBeEqualToDifferentType() {
            // Given
            Money money = new Money(new BigDecimal("100.00"), USD);
            String stringValue = "100.00";

            // When & Then
            assertThat(money).isNotEqualTo(stringValue);
        }

        @Test
        @DisplayName("Should be equal when amounts are mathematically equal")
        void shouldBeEqualWhenAmountsAreMathematicallyEqual() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"), USD);
            Money money2 = new Money(new BigDecimal("100.000"), USD);

            // When & Then
            assertThat(money1).isEqualTo(money2);
            assertThat(money1.hashCode()).isEqualTo(money2.hashCode());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Conditions")
    class EdgeCasesAndErrorConditions {

        @Test
        @DisplayName("Should handle very small decimal amounts")
        void shouldHandleVerySmallDecimalAmounts() {
            // Given
            BigDecimal verySmallAmount = new BigDecimal("0.000001");
            Money money = new Money(verySmallAmount, USD);

            // When & Then
            assertThat(money.amount()).isEqualTo(verySmallAmount);
        }

        @Test
        @DisplayName("Should handle amounts with many decimal places")
        void shouldHandleAmountsWithManyDecimalPlaces() {
            // Given
            BigDecimal amountWithManyDecimals = new BigDecimal("123.456789");
            Money money = new Money(amountWithManyDecimals, USD);

            // When & Then
            assertThat(money.amount()).isEqualTo(amountWithManyDecimals);
        }

        @Test
        @DisplayName("Should handle large amounts with precision")
        void shouldHandleLargeAmountsWithPrecision() {
            // Given
            BigDecimal largeAmount = new BigDecimal("999999999999.999999");
            Money money = new Money(largeAmount, USD);

            // When & Then
            assertThat(money.amount()).isEqualTo(largeAmount);
        }

        @Test
        @DisplayName("Should handle arithmetic with large numbers")
        void shouldHandleArithmeticWithLargeNumbers() {
            // Given
            Money money1 = new Money(new BigDecimal("999999999.99"), USD);
            Money money2 = new Money(new BigDecimal("0.01"), USD);

            // When
            Money result = money1.add(money2);

            // Then
            assertThat(result.amount()).isEqualTo(new BigDecimal("1000000000.00"));
        }

        @Test
        @DisplayName("Should handle arithmetic with very small numbers")
        void shouldHandleArithmeticWithVerySmallNumbers() {
            // Given
            Money money1 = new Money(new BigDecimal("0.000001"), USD);
            Money money2 = new Money(new BigDecimal("0.000002"), USD);

            // When
            Money result = money1.add(money2);

            // Then
            assertThat(result.amount()).isEqualTo(new BigDecimal("0.000003"));
        }
    }

    @Nested
    @DisplayName("Immutability Tests")
    class ImmutabilityTests {

        @Test
        @DisplayName("Should be immutable - values cannot be changed after creation")
        void shouldBeImmutable() {
            // Given
            BigDecimal originalAmount = new BigDecimal("100.00");
            Currency originalCurrency = USD;
            Money money = new Money(originalAmount, originalCurrency);

            // When & Then
            // The record is immutable by design, so we can't modify the values
            // This test verifies that the value object behaves as expected
            assertThat(money.amount()).isEqualTo(originalAmount);
            assertThat(money.currency()).isEqualTo(originalCurrency);
        }

        @Test
        @DisplayName("Should create new instance when values are different")
        void shouldCreateNewInstanceWhenValuesAreDifferent() {
            // Given
            Money money1 = new Money(new BigDecimal("100.00"), USD);
            Money money2 = new Money(new BigDecimal("200.00"), USD);

            // When & Then
            assertThat(money1).isNotSameAs(money2);
            assertThat(money1).isNotEqualTo(money2);
        }

        @Test
        @DisplayName("Should create new instance for arithmetic operations")
        void shouldCreateNewInstanceForArithmeticOperations() {
            // Given
            Money originalMoney = new Money(new BigDecimal("100.00"), USD);
            Money addend = new Money(new BigDecimal("50.00"), USD);

            // When
            Money result = originalMoney.add(addend);

            // Then
            assertThat(result).isNotSameAs(originalMoney);
            assertThat(result).isNotSameAs(addend);
            assertThat(originalMoney.amount()).isEqualTo(new BigDecimal("100.00")); // Original unchanged
        }
    }

    @Nested
    @DisplayName("String Representation Tests")
    class StringRepresentationTests {

        @Test
        @DisplayName("Should return meaningful string representation")
        void shouldReturnMeaningfulStringRepresentation() {
            // Given
            Money money = new Money(new BigDecimal("100.00"), USD);

            // When
            String result = money.toString();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).contains("100.00");
            assertThat(result).contains("USD");
        }
    }
}
