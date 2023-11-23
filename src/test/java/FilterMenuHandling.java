import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class FilterMenuHandling {
    static WebDriver driver;
    static WebDriverWait wait;
    //Assignment - Create generic method to select Filters and also have option to select all filter
    public static void main(String[] args) throws InterruptedException {
        driver = new ChromeDriver();
        driver.get("https://www.t-mobile.com/tablets");
        wait = new WebDriverWait(driver,Duration.ofSeconds(10));
        System.out.println(driver.getTitle());
        Thread.sleep(5000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until((ExpectedCondition<Boolean>) webDriver ->
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

        selectFilter("Deals,Special offer");
        selectFilter("Brands,Apple,Samsung");
        selectFilter("Operating System,iPadOS");
        selectFilter("Deals,all");

        //driver.quit();
    }
    public static void selectFilter(String filtersCriteria){
        String filterHeaderPrefix = "//span[normalize-space()='";
        String filterHeaderSuffix = "']";
        String filterItemPrefix = "/ancestor::mat-expansion-panel//label//span[@class='filter-display-name' and text()=' ";
        String filterItemSuffix = " ']/ancestor::label";
        String filterAllItem = "/ancestor::mat-expansion-panel//label//input[@type='checkbox']";
        String filterRegion = "/ancestor::mat-expansion-panel//div[@role='region']";
        String[] filterArray = filtersCriteria.split(",");
        String filterHeader = filterArray[0];
        String[] filterItems = Arrays.copyOfRange(filterArray, 1, filterArray.length);


        //Click filter header
        String filterHeaderLocator = filterHeaderPrefix+filterHeader+filterHeaderSuffix;
        try {
            boolean isRegionDisplayed = driver.findElement(By.xpath(filterHeaderLocator + filterRegion)).isDisplayed();
            System.out.println("Is Region displayed? " + isRegionDisplayed);
            if (!isRegionDisplayed){
                driver.findElement(By.xpath(filterHeaderLocator)).click();
                System.out.println("Filter Header " + filterHeader + " is clicked.");
            }
            else{
                System.out.println("Filter Header " + filterHeader + " is already opened.");
            }
        }
        catch (NoSuchElementException ex){
            System.out.println("Filter Header "+ filterHeader + " not found.");
            return;
        }

        // Click all filter items
        if(filterItems.length == 1 && filterItems[0].toLowerCase(Locale.ROOT).equals("all")){
            List<WebElement> allFilterItemsElem = driver.findElements(By.xpath(filterHeaderLocator + filterAllItem));

            for(WebElement filterItemElem : allFilterItemsElem){
                String isSelected = filterItemElem.getAttribute("aria-checked");
                System.out.println("All --> IsSelected? " + isSelected);
                try {
                    if (!isSelected.equals("true")) {
                        wait.until(ExpectedConditions.elementToBeClickable(filterItemElem.findElement(By.xpath("ancestor::span")))).click();
                        System.out.println("Filter Item " + filterItemElem.getText() + " is selected.");
                    } else {
                        System.out.println("Filter Item " + filterItemElem.getText() + " is already selected.");
                    }
                }
                catch(TimeoutException ex){
                    System.out.println("TimeoutException occurred.");
                }
            }
        }
        //Click passed Filter item
        else {
            for (String filterItem : filterItems) {
                try {
                    WebElement filterItemElem = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(filterHeaderLocator + filterItemPrefix + filterItem + filterItemSuffix)));
                    String isSelected = filterItemElem.findElement(By.xpath("//input[@type='checkbox']")).getAttribute("aria-checked");
                    if(!isSelected.equals("true")){
                        filterItemElem.click();
                        System.out.println("Filter Item " + filterItem + " is selected.");
                    }
                    else{
                        System.out.println("Filter Item " + filterItem + " is already selected.");
                    }
                }
                catch(TimeoutException ex){
                    System.out.println("Filter Item " + filterItem + " not found or clickable and TimeoutException occurred.");
                }
            }
        }

    }
}
