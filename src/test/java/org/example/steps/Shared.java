package org.example.steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.example.pages.TheInternet;

public class Shared {

    TheInternet theInternet;

    @Given("I navigate to {string}")
    public void navigateToURL(String url) {
        theInternet.navigateToPage(url);
    }

    @Then("Verify the heading reads {string}")
    public void verifyHeadingText(String expected) {
        theInternet.verifyPage(expected);
    }
}
