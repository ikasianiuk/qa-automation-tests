package tests.ui;

import org.junit.jupiter.api.AfterEach;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public abstract class BaseUiTest {

    @AfterEach
    void closeBrowser() {
        closeWebDriver();
    }
}
