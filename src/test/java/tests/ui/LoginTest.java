package tests;

import config.TestConfig;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import pages.ProductsPage;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;

public class LoginTest extends BaseUiTest {
    @Test
    void userCanLogInAndSeeProductsPage() {
        ProductsPage productsPage = new LoginPage()
                .openPage()
                .loginAs(TestConfig.USERNAME, TestConfig.PASSWORD)
                .shouldBeOpened();

        productsPage.products().shouldHave(sizeGreaterThan(0));
    }
}
