package tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class RozetkaEdgeTest {

    WebDriver driver;
    WebDriverWait wait;

    @BeforeClass
    public void setup() {
        System.setProperty("webdriver.edge.driver", "C:\\WebDrivers\\Edge146\\msedgedriver.exe");
        driver = new EdgeDriver();
        driver.manage().window().maximize();

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test
    public void testNavigationAndSearch() {
        try {
            driver.get("https://www.rozetka.com.ua/");

            // Закрываем popup выбора региона
            try {
                WebElement popupClose = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector("button[class*='popup__close']")));
                popupClose.click();
            } catch (Exception ignored) {}

            // Кликаем по элементу "Ноутбуки" через твой CSS селектор
            WebElement laptopsMenu = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("body > rz-app-root > rz-main-page > rz-page-layout > div > div > div > aside > rz-main-page-sidebar > ul > li:nth-child(2) > a")
            ));
            laptopsMenu.click();

            // Ждем появления карточек товаров
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-goods-tile")));

            // Берем первые 5 товаров
            List<WebElement> products = driver.findElements(By.cssSelector("app-goods-tile"));
            System.out.println("Первые 5 товаров категории Ноутбуки:");
            for (int i = 0; i < Math.min(5, products.size()); i++) {
                WebElement title = products.get(i).findElement(By.cssSelector("a.goods-tile__heading"));
                System.out.println((i + 1) + ". " + title.getText());
            }

            // Поиск товара через input с name="search"
            WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("search")));
            searchInput.sendKeys("MacBook");

            // Проверка, что поле ввода содержит текст
            String enteredText = searchInput.getAttribute("value");
            if (enteredText.equals("MacBook")) {
                System.out.println("Поле поиска успешно заполнено: " + enteredText);
            } else {
                System.out.println("Поле поиска не заполнено корректно!");
            }

            searchInput.submit();

            // Ждем появления результатов поиска
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("app-goods-tile")));

            System.out.println("\nРезультаты поиска MacBook:");
            List<WebElement> searchResults = driver.findElements(By.cssSelector("app-goods-tile"));
            for (int i = 0; i < Math.min(5, searchResults.size()); i++) {
                WebElement title = searchResults.get(i).findElement(By.cssSelector("a.goods-tile__heading"));
                System.out.println((i + 1) + ". " + title.getText());
            }

        } catch (Exception e) {
            System.out.println("Произошла ошибка во время теста: " + e.getMessage());
        }
    }

    @AfterClass
    public void teardown() {
        if (driver != null) driver.quit();
    }
}
