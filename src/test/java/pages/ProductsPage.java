package pages;


import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byTestId;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

public class ProductsPage {
    private static final String PATH = "/inventory.html";

    private final SelenideElement title = $(byTestId("title"));
    private final ElementsCollection products = $$(byTestId("inventory-item"));

    public ProductsPage shouldBeOpened() {
        webdriver().shouldHave(urlContaining(PATH));
        title.shouldBe(visible);
        return this;
    }

    public ElementsCollection products() {
        return products;
    }
}
