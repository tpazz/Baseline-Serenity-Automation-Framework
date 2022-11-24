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

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PageObjectExtension extends PageObject {

    public static Logger logger = LogManager.getLogger(PageObjectExtension.class);

    public String actual;
    public WebElement element;
    public List<WebElement> elements = new ArrayList<>();
    public By locator;
    public WebDriverWait wait;

    public enum Action { CLICK, ENTER_TEXT, SELECT_FROM_DROPDOWN, ENTER_KEY, GET_CSS, GET_ATTRIBUTE, GET_DROPDOWN_TEXT, GET_TEXT }
    public enum WaitType { ENABLED, CLICKABLE, DISABLED, PRESENT, VISIBLE, NOT_VISIBLE }

    // Ignore Selenium exceptions by repeatedly attempting desired action until successful (default time out = 5s)
    // waitUntilClickable is the same as ignoring ElementClickInterceptedException + ElementNotVisibleException
    public String executeAction(Action action, WebElement webElement, String value) {
        return executeAction(action, webElement, value, 5);
    }

    public String executeAction(Action action, WebElement webElement) {
        return executeAction(action, webElement, "", 5);
    }

    public String executeAction(Action action, WebElement webElement, String value, int timeOut) {
        final String[] returnValue = {""};
        wait = new WebDriverWait(getDriver(), timeOut);
        wait.ignoring(StaleElementReferenceException.class)
            .ignoring(ElementNotInteractableException.class)
            .ignoring(ElementClickInterceptedException.class)
            .ignoring(ElementNotSelectableException.class)
            .ignoring(ElementNotVisibleException.class)
            .until((WebDriver d) -> {
                switch (action) {
                    case CLICK                : webElement.click();                              break;
                    case ENTER_TEXT           : enterText(webElement, value);                    break;
                    case ENTER_KEY            : enterKey(webElement, value);                     break;
                    case SELECT_FROM_DROPDOWN : selectFromDropdown(webElement, value);           break;
                    case GET_CSS              : returnValue[0] = webElement.getCssValue(value);  break;
                    case GET_ATTRIBUTE        : returnValue[0] = webElement.getAttribute(value); break;
                    case GET_DROPDOWN_TEXT    : returnValue[0] = getDropDownText(webElement);    break;
                    case GET_TEXT             : returnValue[0] = webElement.getText();           break;
                }
                return true;
            });
        return returnValue[0];
    }

    public void enterKey(WebElement webElement, String keys) {
        switch (keys) {
            case "DOWN_ARROW"  : webElement.sendKeys(Keys.ARROW_DOWN);  break;
            case "UP_ARROW"    : webElement.sendKeys(Keys.ARROW_UP);    break;
            case "LEFT_ARROW"  : webElement.sendKeys(Keys.ARROW_LEFT);  break;
            case "RIGHT_ARROW" : webElement.sendKeys(Keys.ARROW_RIGHT); break;
            case "ESCAPE"      : webElement.sendKeys(Keys.ESCAPE);      break;
            case "ENTER"       : webElement.sendKeys(Keys.ENTER);       break;
            // add more cases
        }
    }

    // Set explicit wait for element condition
    public void waitForElement(WebElement webElement, WaitType waitType, int timeOut) {
        wait = new WebDriverWait(getDriver(), timeOut);
        switch (waitType) {
            case ENABLED     : waitFor(webElement).waitUntilEnabled();    break;
            case CLICKABLE   : waitFor(webElement).waitUntilClickable();  break;
            case DISABLED    : waitFor(webElement).waitUntilDisabled();   break;
            case PRESENT     : waitFor(webElement).waitUntilPresent();    break;
            case VISIBLE     : waitFor(webElement).waitUntilVisible();    break;
            case NOT_VISIBLE : waitFor(webElement).waitUntilNotVisible(); break;
        }
    }

    public WebElement generateElement(By locator) {
        wait = new WebDriverWait(getDriver(), 5);
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        return getDriver().findElement(locator);
    }

    public List<WebElement> generateElements(By type) {
        return getDriver().findElements(type);
    }

    public void selectFromDropdown(WebElement webElement, String text) {
        Select select = new Select(webElement);
        select.selectByVisibleText(text);
    }

    public void enterText(WebElement webElement, String text) {
        webElement.clear();
        webElement.sendKeys(text);
    }

    public String getDropDownText(WebElement webElement) {
        String[] split = webElement.getText().split("\\r?\\n");
        return split[0].trim();
    }

    public void selectElementWithText(String elementType, String text) {
        locator = By.xpath("//" + elementType + "[text()='" + text + "']");
        element = generateElement(locator);
        executeAction(Action.CLICK, element, "");
    }

    public void selectElementWithValue(String elementType, String attribute, String value) {
        locator = By.xpath("//" + elementType + "[contains(@" + attribute + ", '" + value + "')]");
        element = generateElement(locator);
        executeAction(Action.CLICK, element, "");
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

    public void handleAlert(String option) {
        wait = new WebDriverWait(getDriver(), 5);
        wait.until(ExpectedConditions.alertIsPresent());
        if (option.equalsIgnoreCase("OK")) getDriver().switchTo().alert().accept();
        else getDriver().switchTo().alert().dismiss();
    }

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

    // ******************************************* FILE I/O ************************************************************

    public List<List<String>> getFileIntoArray() {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/test/resources/csvFile.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (Exception e) { logger.error("Error converting file into array"); }
        return records;
    }

    public String readFromFile(String key) {
        final String[] value = {""};
        getFileIntoArray().forEach(r -> {
            if (r.get(0).equals(key))
                value[0] = r.get(1);
        });
        logger.info(value[0]);
        return value[0];
    }

    public void getArrayIntoFile(List<List<String>> records) {
        try (FileWriter writer = new FileWriter("src/test/resources/csvFile.csv")) {
            records.forEach(r -> {
                try {
                    writer.write(r.get(0) + "," + r.get(1) + "\n");
                } catch (IOException e) {
                    logger.error("Error writing to File");
                }
            });
        } catch (Exception e) { logger.error("Error converting array into file"); }
    }

    public void writeToFile(String key, String value) {
        List<List<String>> records = getFileIntoArray();
        records.forEach(r -> {
            if (r.get(0).equals(key))
                r.set(1,value);
            getArrayIntoFile(records);
        });
    }

    // ****************************************** LOGGER ***************************************************************

    public void startLogger() { logger.info("~TESTS STARTED~"); }

    public void endLogger() { logger.info("~TEST FINISHED~"); }

    // ***************************************** VERIFY METHODS ********************************************************

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
        try { actual = executeAction(Action.GET_TEXT, webElement, ""); }
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
        if (expected.equalsIgnoreCase(actual)) logger.info("***TEST SUCCEEDED***");
        else logger.error(message);
        assertEquals(message, expected, actual);
    }

}