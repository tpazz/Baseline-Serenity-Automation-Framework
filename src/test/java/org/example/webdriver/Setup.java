package org.example.webdriver;

import cucumber.api.java.en.Then;
import org.example.base.Constants;
import org.example.base.PageObjectExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Setup extends PageObjectExtension {

    String browserVersion;
    String fullBrowserVersion;
    String driverVersion;
    String fullDriverVersion;
    String chromeDriverZIP = "chromedriver_win32.zip";

    @Then("Update ChromeDriver")
    public void updateChromeDriver() {
        compareBrowserDriverVersion();
        downloadCompatibleChromeDriver();
        installChromeDriver();
    }

    public void downloadCompatibleChromeDriver() {
        getDriver().get(Constants.CHROMEDRIVER_DOWNLOADS);
        locator = By.xpath("//strong[contains(text(),'ChromeDriver " + browserVersion + "')]");
        element = generateElement(locator);
        executeAction(Action.CLICK, element);
        switchTabs(1);
        locator = xPathBuilder("a", "text()", chromeDriverZIP);
        element = generateElement(locator);
        executeAction(Action.CLICK, element);
        for (int i = 0; i < 10; i++) {
            if (verifyFileDownloaded(chromeDriverZIP, false)) break;
            else threadSleep(1000);
        }
    }

    public void installChromeDriver() {
        File dir = new File(Constants.PROJECT_RESOURCES_WINDOWS + "\\test");
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(Constants.LOCAL_DOWNLOADS + chromeDriverZIP);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(Constants.PROJECT_RESOURCES_WINDOWS + "\\test" + File.separator + fileName);
                System.out.println("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean compareBrowserDriverVersion() {
        WebDriver webDriver;
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, "src/test/resources/webdriver/windows/chromedriver.exe");
        Capabilities caps = DesiredCapabilities.chrome();
        webDriver = new ChromeDriver(caps);
        caps = ((RemoteWebDriver) webDriver).getCapabilities();
        Map<String, String> a = (Map<String, String>) caps.getCapability("chrome");
        fullDriverVersion = a.get("chromedriverVersion").substring(0,20).split(" ")[0];
        driverVersion = fullDriverVersion.split("\\.")[0];
        fullBrowserVersion = caps.getVersion();
        browserVersion = fullBrowserVersion.split("\\.")[0];
        logger.info("Driver version: " + fullDriverVersion + " (" + driverVersion + ")");
        logger.info("Browser version: " + fullBrowserVersion + " (" + browserVersion + ")");
        webDriver.close();
        if (driverVersion.equalsIgnoreCase(browserVersion)) return true;
        else return false;
    }
}
