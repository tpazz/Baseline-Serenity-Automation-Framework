# Baseline-Serenity-Automation-Framework

### This is a template test automation framework that you can clone to make a start on your automated testing.
* Gradle project
* Serenity with Cucumber
* Log4j2
* Java 1.8
* Selenium 

### The key to make your Selenium tests more robust 
* Use explicit waits: Instead of using the default implicit wait, use explicit waits to tell the test to wait for a certain condition to be met before proceeding. This will make your tests less prone to flakiness caused by elements taking longer to load than expected.

* Use retry logic: Add retry logic to your tests so that they will automatically retry a failed test a certain number of times before giving up. This can help to mitigate flakiness caused by intermittent failures.

* Use a stable test environment: Make sure that your test environment is as stable as possible. This includes things like having a reliable internet connection, a fast CPU, and enough memory to run your tests.

* Use exception handling: Use try-except blocks to handle exceptions that may occur during the execution of your tests. This can help to prevent your tests from failing due to unexpected errors.
---
### How to handle exceptions
Believe it or not, systematic testing can lead to unpredictable results. In an ideal world, test results should be consistent if nothing changes, but unfortunately this is not the case. (Selenium) Exception handling is perhaps the most crucial aspect towards a robust testing suite, and there are several ways to do it. Most Exceptions arise from interacting with elements in the DOM. These are the most common types:

* **NoSuchElementException**

This exception is raised when Selenium is unable to locate an element on the page using the provided search criteria.

* **StaleElementReferenceException**

This exception is raised when an element that was previously found on the page is no longer attached to the DOM.

* **InvalidElementStateException**

This exception is raised when an element is in an invalid state (e.g., disabled) for the requested operation.

* **ElementNotVisibleException**

This exception is raised when an element is present on the DOM, but is not visible and so cannot be interacted with.

* **ElementNotInteractableException** 

Element is present on the page, but is not able to be interacted with. This can happen for several reasons, such as:
* The element is hidden or overlapped by another element
* The element is disabled
* The element is not visible (e.g., it has a style of display: none)
