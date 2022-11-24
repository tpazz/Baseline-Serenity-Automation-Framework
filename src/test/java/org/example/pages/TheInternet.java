package org.example.pages;

import org.example.base.PageObjectExtension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class TheInternet extends PageObjectExtension {

    @FindBy(xpath = "//h1")
    WebElement heading;

    public void navigateToPage(String url) {
        getDriver().get(url);
        waitForPageLoadedJS();
    }

    public void verifyPage(String expected) {
        actual = executeAction(Action.GET_TEXT, heading);
        verify(expected, actual);
    }

}
