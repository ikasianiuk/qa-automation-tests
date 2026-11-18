package tests.ui;

import config.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pages.CartPage;
import pages.LoginPage;
import pages.ProductsPage;

import java.util.Comparator;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;

public class ProductCartTest extends BaseUiTest {
    private ProductsPage productsPage;

    @BeforeEach
    void logIn() {
        productsPage = new LoginPage()
                .openPage()
                .loginAs(TestConfig.USERNAME, TestConfig.PASSWORD)
                .shouldBeOpened();
        productsPage.products().shouldHave(sizeGreaterThan(0));
    }

    // Assumes prices look like "$29.99" (single currency, no thousands separators).
    private static double toNumber(String price) {
        return Double.parseDouble(price.replace("$", ""));
    }

    @Test
    void sortedProductCanBeAddedToCart() {
        List<String> expectedPrices = productsPage.prices().texts().stream()
                .sorted(Comparator.comparingDouble(ProductCartTest::toNumber))
                .toList();

        productsPage.sortBy("lohi");
        productsPage.prices().shouldHave(exactTexts(expectedPrices));

        String productName = productsPage.addFirstProductToCart();

        CartPage cartPage = productsPage.header().openCart();

        cartPage.itemNames().shouldHave(exactTexts(productName));
    }

    @ParameterizedTest
    @ValueSource(strings = {"az", "za", "lohi", "hilo"})
    void productsSortedCorrectly(String sortBy) {
        List<String> titles = productsPage.titles().texts();
        List<String> prices = productsPage.prices().texts();

        productsPage.sortBy(sortBy);

        switch (sortBy) {
            case "az":
                productsPage.titles().shouldHave(exactTexts(titles.stream().sorted().toList()));
                break;
            case "za":
                productsPage.titles().shouldHave(exactTexts(titles.stream().sorted(Comparator.reverseOrder()).toList()));
                break;
            case "lohi":
                productsPage.prices().shouldHave(exactTexts(prices.stream().sorted(Comparator.comparingDouble(ProductCartTest::toNumber)).toList()));
                break;
            case "hilo":
                productsPage.prices().shouldHave(exactTexts(prices.stream().sorted(Comparator.comparingDouble(ProductCartTest::toNumber).reversed()).toList()));
                break;
            default:
                throw new IllegalArgumentException("Unknown sort option: " + sortBy);
        }
    }
}