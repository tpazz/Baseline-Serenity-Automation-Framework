package org.example.base;

import net.serenitybdd.core.pages.PageObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;

public class PageObjectExtension extends PageObject {

    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);

    public String actual;
    public WebElement element;
    public List<WebElement> elements = new ArrayList<>();
    public By locator;

    // ************************************************** WAITS ********************************************************

    public enum WaitType { DONT_WAIT, CLICKABLE, DISABLED, ENABLED, NOT_VISIBLE, VISIBLE, PRESENT }

    public void waitForElement(WebElement webElement, WaitType waitType) {
        switch (waitType) {
            case CLICKABLE   : waitFor(webElement).waitUntilClickable();
            case DISABLED    : waitFor(webElement).waitUntilDisabled();
            case ENABLED     : waitFor(webElement).waitUntilEnabled();
            case NOT_VISIBLE : waitFor(webElement).waitUntilNotVisible();
            case VISIBLE     : waitFor(webElement).waitUntilVisible();
            case PRESENT     : waitFor(webElement).waitUntilPresent();
            case DONT_WAIT   : {}
        }
    }

    public void waitForElement(WebElement webElement) {
        waitFor(webElement).waitUntilPresent();
    }

    public void setExplicitWait(int seconds, WebElement webElement, WaitType waitType) {
        WebDriverWait webDriverWait = new WebDriverWait(getDriver(), seconds);
        switch (waitType) {
            case VISIBLE   : webDriverWait.until(ExpectedConditions.visibilityOf(webElement));
            case CLICKABLE : webDriverWait.until(ExpectedConditions.elementToBeClickable(webElement));
            // add more cases...
        }
    }

    // ************************************************* HANDLERS ******************************************************

    public void handleAlert(String option) {
        WebDriverWait webDriverWait = new WebDriverWait(getDriver(), 10);
        webDriverWait.until(ExpectedConditions.alertIsPresent());
        if (option.equalsIgnoreCase("OK")) getDriver().switchTo().alert().accept();
        else getDriver().switchTo().alert().dismiss();
    }

    public void handleStaleClick(By locator) {
        new WebDriverWait(getDriver(), 5)
                .ignoring(StaleElementReferenceException.class)
                .until((WebDriver d) -> {
                    d.findElement(locator).click();
                    return true;
                });
    }

    public void handleStaleEnterText(By locator, String value) {
        new WebDriverWait(getDriver(), 5)
                .ignoring(StaleElementReferenceException.class)
                .until((WebDriver d) -> {
                    d.findElement(locator).sendKeys(value);
                    return true;
                });
    }

    public String handleStaleGetTextByLocator(By locator, String value) {
        AtomicReference<String> text = new AtomicReference<>("");
        new WebDriverWait(getDriver(), 5)
                .ignoring(StaleElementReferenceException.class)
                .until((WebDriver d) -> {
                    text.set(d.findElement(locator).getText());
                    return true;
                });
        return text.get();
    }

    public String handleStaleGetTextElement(WebElement webElement) {
        AtomicReference<String> text = new AtomicReference<>("");
        new WebDriverWait(getDriver(), 5)
                .ignoring(StaleElementReferenceException.class)
                .until((WebDriver d) -> {
                    text.set(webElement.getText());
                    return true;
                });
        return text.get();
    }

    // ********************************************* INTERACTIONS ******************************************************

    public void selectFromDropdown(WebElement webElement, String text) {
        selectFromDropdown(webElement, text, WaitType.CLICKABLE);
    }

    public void selectFromDropdown(WebElement webElement, String text, WaitType waitType) {
        waitForElement(webElement, waitType);
        Select select = new Select(webElement);
        select.selectByVisibleText(text);
    }

    public void enterText(WebElement webElement, String text) {
        enterText(webElement, text, WaitType.CLICKABLE);
    }

    public void enterText(WebElement webElement, String text, WaitType waitType) {
        waitForElement(webElement, waitType);
        webElement.clear();
        webElement.sendKeys(text);
    }

    public void click(WebElement webElement) {
        click(webElement, WaitType.CLICKABLE);
    }

    public void click(WebElement webElement, WaitType waitType) {
        waitForElement(webElement, waitType);
        webElement.click();
    }

    public String getCSS(WebElement webElement, String css) {
        return getCSS(webElement, css, WaitType.PRESENT);
    }

    public String getCSS(WebElement webElement, String css, WaitType waitType) {
        waitForElement(webElement, waitType);
        return webElement.getCssValue(css);
    }

    public String getAttribute(WebElement webElement, String att) {
        return getAttribute(webElement, att, WaitType.PRESENT);
    }

    public String getAttribute(WebElement webElement, String att, WaitType waitType) {
        waitForElement(webElement, waitType);
        return webElement.getAttribute(att);
    }

    public String getDropDownText(WebElement webElement) {
        return getDropDownText(webElement, WaitType.VISIBLE);
    }

    public String getDropDownText(WebElement webElement, WaitType waitType) {
        waitForElement(webElement, waitType);
        String[] split = getText(webElement).split("\\r?\\n");
        return split[0].trim();
    }

    public String getText(WebElement webElement) {
        return getText(webElement, WaitType.VISIBLE);
    }

    public String getText(WebElement webElement, WaitType waitType) {
        waitForElement(webElement, waitType);
        return webElement.getText();
    }

    public void enterKey(WebElement webElement, Keys keys) {
        enterKey(webElement, keys, WaitType.CLICKABLE);
    }

    public void enterKey(WebElement webElement, Keys keys, WaitType waitType) {
        waitForElement(webElement, waitType);
        waitForElement(webElement);
        webElement.sendKeys(keys);
    }

    public void selectElementWithText(String elementType, String text) {
        locator = By.xpath("//" + elementType + "[text()='" + text + "']");
        element = generateElement(locator);
        click(element);
    }

    public void selectElementWithValue(String elementType, String attribute, String value) {
        locator = By.xpath("//" + elementType + "[contains(@" + attribute + ", '" + value + "')]");
        handleStaleClick(locator);
        waitForPageLoadedJS();
    }

    // ************************************************* JS ************************************************************

    public String extractBackgroundColourJS(String id) {
        String script = "return window.getComputedStyle(document.querySelector('#" + id + "'),':before').getPropertyValue('background-color')";
        JavascriptExecutor js = (JavascriptExecutor)getDriver();
        return (String) js.executeScript(script);
    }

    public String extractPropertyValueJS(WebElement webElement) {
        String script = "return arguments[0].value";
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        return (String) js.executeScript(script, webElement);
    }

    public void makeVisibleJS(WebElement webElement) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].style.display='block';", webElement);
    }

    public void handleUpload(WebElement webElement, String fileName) {
        makeVisibleJS(webElement);
        webElement.sendKeys(Constants.BASE_DIRECTORY + fileName);
    }

    public void openNewTabJS() {
        String script = "window.open()";
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript(script);
        switchTabs(1);
    }

    public void clickJS(WebElement webElement) {
        JavascriptExecutor js = (JavascriptExecutor) getDriver();
        js.executeScript("arguments[0].click();", webElement);
    }

    public void waitForPageToBeLoadedJS() {
        JavascriptExecutor js = (JavascriptExecutor)getDriver();
        for (int wait=0; wait<30; wait++) {
            try { Thread.sleep(1000); }
            catch (InterruptedException e) {}
            if (js.executeScript("return document.readyState").toString().equals("complete")) { break; }
        }
    }

    public void waitForPageLoadedJS() {
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
            }
        };
        try {
            Thread.sleep(1000);
            WebDriverWait wait = new WebDriverWait(getDriver(), 10);
            wait.until(expectation);
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for Page Load Request to complete.");
        }
    }


    // ************************************************* GENERIC *******************************************************

    public void switchTabs(int tabNo) {
        ArrayList<String> tabs2 = new ArrayList<> (getDriver().getWindowHandles());
        getDriver().switchTo().window(tabs2.get(tabNo));
        //getDriver().manage().window().setSize(new Dimension(1920, 1080));
    }

    public void moveToElementClick(WebElement webElement) {
        Actions actions = new Actions(getDriver());
        actions.moveToElement(webElement).click().build().perform();
    }

    public String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd");
        Date date = new Date();
        return formatter.format(date);
    }

    public void initLogger() {
        logger.info("~TESTS STARTED~");
    }

    public void endLogger() { logger.info("~TEST FINISHED~");}

    public WebElement generateElement(By type) {
        return getDriver().findElement(type);
    }

    public List<WebElement> generateElements(By type) {
        return getDriver().findElements(type);
    }

    // ***************************************** VERIFY METHODS ********************************************************

    @FindBy(className = "feedback-message-text")
    WebElement feedbackMessage;

    public void verifyFeedbackMessage(String expected) {
        locator = By.className("<feedback-message>");
        element = generateElement(locator);
        setExplicitWait(30, element, WaitType.VISIBLE);
        actual = getText(feedbackMessage);
        if (actual.equalsIgnoreCase(getText(element))) expected = actual;
        verify(expected, actual);
    }

    public boolean verifyFileDownloaded(String fileName) {
        String expected = "File Successfully Downloaded";
        actual = "";
        File dir = new File(Constants.BASE_DOWNLOADS);
        File[] dirContents = dir.listFiles();
        for (int i = 0; i < dirContents.length; i++) {
            if (dirContents[i].getName().equals(fileName)) {
                actual = expected;
                // File has been found, it can now be deleted:
                dirContents[i].delete();
                verify(expected, actual);
                return true;
            }
        }
        return false;
    }

    public void verifyURL(String expected) {
        String url = getDriver().getCurrentUrl();
        if (url.equalsIgnoreCase(expected))
            actual = expected;
        else
            actual = url;
        verify(expected, actual);
    }

    public void verifyPageTitle(String expected) {
        actual = getDriver().getTitle();
        verify(expected, actual);
    }

    public void verifyElementText(WebElement webElement, String expected) {
        try { actual = getText(webElement); }
        catch (Exception e) { actual = e.toString(); }
        verify(expected, actual);
    }

    public void verifyTextOnPage(String elementType, String expected) {
        actual = expected;
        locator = By.xpath("//" + elementType + "[text()='" + expected + "']");
        try { generateElement(locator); }
        catch (Exception e) { actual = e.toString(); }
        verify(expected, actual);
    }

    public void verifyTextDoesNotExistOnPage(String elementType, String notExpected) {
        locator = By.xpath("//" + elementType + "[text()='" + notExpected + "']");
        elements = generateElements(locator);
        actual = String.valueOf(elements.size());
        verify("0", actual);
    }

    public void verifyElementState(WebElement webElement, boolean enabled) {
        actual = String.valueOf(webElement.isEnabled());
        if (enabled) verify("true", actual);
        else verify("false", actual);
    }

    public void verify(String expected, String actual) {
        String message = "***** FAIL: [" + expected + " =/= " + actual + "] *****";
        if (expected.equalsIgnoreCase(actual))
            logger.info("***TEST SUCCEEDED***");
        else
            logger.error(message);
        assertEquals(message, expected, actual);
    }

}