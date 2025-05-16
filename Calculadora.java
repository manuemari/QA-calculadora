package com.example;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Calculadora {
    private AndroidDriver driver;

    private final String DEVICENAME = "R5CT20Q8CBV";
    private final String PLATFORMNAME = "Android";
    private final String CALCULATORPACKAGE = "com.sec.android.app.popupcalculator";
    private final String APPIUMURL = "http://192.168.1.14:4723/";
    private final String DRIVERNOTINICIALIZEDERROR = "El driver no está inicializado.";

    private final String CALCULATOREQUALSBUTTON = "com.sec.android.app.popupcalculator:id/calc_keypad_btn_equal";
    private final String CALCULATORACBUTTON = "com.sec.android.app.popupcalculator:id/calc_keypad_btn_clear";
    private final String CALCULATORPLUSBUTTON = "com.sec.android.app.popupcalculator:id/calc_keypad_btn_add";
    private final String CALCULATORMULTIPLYBUTTON = "com.sec.android.app.popupcalculator:id/calc_keypad_btn_mul";
    private final String CALCULATORDIVISIONBUTTON = "com.sec.android.app.popupcalculator:id/calc_keypad_btn_div";
    private final String CALCULATORPOINTBUTTON = "com.sec.android.app.popupcalculator:id/calc_keypad_btn_dot";

    private final String CALCULATORNUMBERSBASE = "com.sec.android.app.popupcalculator:id/calc_keypad_btn_";
    private final String CALCULATORRESULT = "com.sec.android.app.popupcalculator:id/calc_edt_formula";

    private AndroidDriver createConnection() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setDeviceName(DEVICENAME);
        options.setPlatformName(PLATFORMNAME);
        options.setUdid(DEVICENAME);
        URL appiumServerURL = URI.create(APPIUMURL).toURL();
        return new AndroidDriver(appiumServerURL, options);
    }

    @BeforeEach
    public void setUp() throws MalformedURLException, InterruptedException {
        driver = createConnection();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }

    private void validateInitializedDriver() {
        if (driver == null) {
            throw new IllegalStateException(DRIVERNOTINICIALIZEDERROR);
        }
    }

    private Boolean openApp(String appPackage) {
        validateInitializedDriver();
        driver.activateApp(appPackage);
        waitSeconds(1);
        Boolean equalsButtonDisplayed = driver.findElement(AppiumBy.id(CALCULATOREQUALSBUTTON)).isDisplayed();
        return equalsButtonDisplayed;
    }

    private void waitSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void cleanCalculator() {
        driver.findElement(AppiumBy.id(CALCULATORACBUTTON)).click();
    }

   private void inputNumbers(String numbers) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    
    for (int i = 0; i < numbers.length(); i++) {
        char c = numbers.charAt(i);
        
        try {
            if (c == '.') {
                wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.id(CALCULATORPOINTBUTTON)
                )).click();
            } else {
                // se cambia el formato de numero de 1 a dos digitos
                String twoDigitNum = String.format("%02d", Character.getNumericValue(c));
                String buttonId = "com.sec.android.app.popupcalculator:id/calc_keypad_btn_" + twoDigitNum;
                
                wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.id(buttonId)
                )).click();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to click button for character: " + c, e);
        }
        
        waitSeconds((int) 0.3); // pausas entre los clicks
    }
}

    private void plusButton() {
        driver.findElement(AppiumBy.id(CALCULATORPLUSBUTTON)).click();
    }

    private void multiplyButton() {
        driver.findElement(AppiumBy.id(CALCULATORMULTIPLYBUTTON)).click();
    }

    private void divisionButton() {
        driver.findElement(AppiumBy.id(CALCULATORDIVISIONBUTTON)).click();
    }

    private String getResult() {
        return driver.findElement(AppiumBy.id(CALCULATORRESULT)).getText();
    }

    private void equalsButton() {
        driver.findElement(AppiumBy.id(CALCULATOREQUALSBUTTON)).click();
    }

    @Test
    @Order(1)
    public void calculator() {
        assertTrue(openApp(CALCULATORPACKAGE));
        waitSeconds(1);

        // Suma
        inputNumbers("45");
        plusButton();
        inputNumbers("15");
        equalsButton();
        assertTrue(getResult().equals("60 Resultado del cálculo"));
        cleanCalculator();
        
        // Multiplicacion
        inputNumbers("3");
        multiplyButton();
        inputNumbers("5");
        equalsButton();
        assertTrue(getResult().equals("15 Resultado del cálculo"));
        cleanCalculator();
        
        // Division entera
        inputNumbers("10");
        divisionButton();
        inputNumbers("2");
        equalsButton();
        assertTrue(getResult().equals("5 Resultado del cálculo"));
        cleanCalculator();
        
        // Division decimal
        inputNumbers("15.6");
        divisionButton();
        inputNumbers("2.4");
        equalsButton();
        assertTrue(getResult().equals("6.5 Resultado del cálculo"));
        cleanCalculator();
        
        // Multiplicacion decimal
        inputNumbers("23.9");
        multiplyButton();
        inputNumbers("7.1");
        equalsButton();
        assertTrue(getResult().equals("169.69 Resultado del cálculo"));
        cleanCalculator();
        
        // Suma decimal
        inputNumbers("7.1");
        plusButton();
        inputNumbers("7.1");
        equalsButton();
        assertTrue(getResult().equals("14.2 Resultado del cálculo"));
        cleanCalculator();
        
        // Multiples operaciones
        inputNumbers("105");
        divisionButton();
        inputNumbers("4");
        multiplyButton();
        inputNumbers("6");
        plusButton();
        inputNumbers("8");
        equalsButton();
        assertTrue(getResult().equals("165.5 Resultado del cálculo"));
        cleanCalculator();
    }

}
