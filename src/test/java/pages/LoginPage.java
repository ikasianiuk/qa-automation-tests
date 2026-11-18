package pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byTestId;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class LoginPage {
    private static final String PATH = "/";

    private final SelenideElement usernameInput = $(byTestId("username"));
    private final SelenideElement passwordInput = $(byTestId("password"));
    private final SelenideElement loginButton = $(byTestId("login-button"));

    public LoginPage openPage() {
        open(PATH);
        return this;
    }

    public ProductsPage loginAs(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        loginButton.click();

        return new ProductsPage();
    }
}
