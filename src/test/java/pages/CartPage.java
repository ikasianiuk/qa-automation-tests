package pages;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Selectors.byTestId;
import static com.codeborne.selenide.Selenide.$$;

public class CartPage {
    private final ElementsCollection itemNames = $$(byTestId("inventory-item-name"));

    public ElementsCollection itemNames() {
        return itemNames;
    }
}
