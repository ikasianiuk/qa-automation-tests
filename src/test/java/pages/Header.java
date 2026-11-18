package pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byTestId;
import static com.codeborne.selenide.Selenide.$;

public class Header {
    private final SelenideElement cartLink = $(byTestId("shopping-cart-link"));

    public CartPage openCart() {
        cartLink.click();
        return new CartPage();
    }
}
