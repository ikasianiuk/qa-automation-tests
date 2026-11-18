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
    private final SelenideElement sortDropdown = $(byTestId("product-sort-container"));

    private final ElementsCollection products = $$(byTestId("inventory-item"));
    private final ElementsCollection prices = $$(byTestId("inventory-item-price"));
    private final ElementsCollection titles = $$(byTestId("inventory-item-name"));

    public ProductsPage shouldBeOpened() {
        webdriver().shouldHave(urlContaining(PATH));
        title.shouldBe(visible);
        return this;
    }

    public ElementsCollection products() {
        return products;
    }

    public ElementsCollection prices() {
        return prices;
    }

    public ElementsCollection titles() {
        return titles;
    }

    public ProductsPage sortBy(String optionValue) {
        sortDropdown.selectOptionByValue(optionValue);
        return this;
    }

    public String addFirstProductToCart() {
        SelenideElement firstProduct = products.first();
        String firstProductName = firstProduct.$(byTestId("inventory-item-name")).getText();
        System.out.println(firstProductName);
        firstProduct.$("button[data-test^='add-to-cart']").click();
        return firstProductName;
    }
}
