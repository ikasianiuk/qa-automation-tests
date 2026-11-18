package pages;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selectors.byTestId;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

public class CartPage {
    private static final String PATH = "/cart.html";

    private final ElementsCollection itemNames = $$(byTestId("inventory-item-name"));

    public CartPage openPage() {
        open(PATH);
        return this;
    }

    public ElementsCollection itemNames() {
        return itemNames;
    }
}
