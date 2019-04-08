package demoSelenium;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import cucumber.api.java8.En;
import junit.framework.Assert;

public class PCHomeStepdefs implements En {
	private WebDriver driver;

    public PCHomeStepdefs() {
    	
    	Before(()->{
    		System.setProperty("webdriver.gecko.driver", "C:\\Program Files (x86)\\Silk\\Silk WebDriver\\ng\\WebDriversSWD\\Windows\\Gecko\\0.23.0\\geckodriver.exe");
            driver = new FirefoxDriver();
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            
    	});
    	
        Given("打開Google搜尋", () -> {
        	driver.get("https://google.com");
        });

        Given("輸入PCHome後搜尋", () -> {
        	driver.get("https://www.google.com/");
    		WebElement element = driver.findElement(By.name("q"));
    		element.sendKeys("pchome");
    		element.submit();
        });

        Given("按下第二筆查詢結果進到PCHome", () -> {
        	 //因為有確認到Google轉頁時會有 "約有XX筆項結果" 所以先抓這個元素
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("resultStats")));
            
            //這邊就是觀察了google的網頁，然後用XPath去定位，可以抓出該頁面出現結果的超連結
            List<WebElement> resultElements = driver.findElements(By.xpath("//div[@id='rso']//div[@class='r']//h3"));
            if(resultElements.size()>0) {
                resultElements.get(1).click();
            }else {
                Assert.fail("查無結果");
            }
        });

        When("輸入牙膏後搜尋", () -> {
            driver.findElement(By.id("keyword")).sendKeys("牙膏");
            driver.findElement(By.id("doSearch")).click();
        });

        Then("找出最便宜的牙膏", () -> {
        	driver.findElement(By.linkText("價錢由低至高")).click();

            //取得價格
            List<WebElement> priceElement = driver.findElements(By.xpath("//div[@id='ItemContainer']//span[@class='value']"));

            //加入購物車按鈕
            List<WebElement> cartElement = driver.findElements(By.xpath("//button[@class='unblock']"));

            //商品名稱
            List<WebElement> productElement = driver.findElements(By.xpath("//div[@id='ItemContainer']//h5[@class='prod_name']"));
            int position = 0;
            if(priceElement.size()>0) {
                //確認是否可以馬上購買、以及是否為牙膏
                for(int i = 0 ; i<priceElement.size() ; i++) {
                    if (cartElement.get(i).getText().contains("加入")&&productElement.get(i).getText().contains("牙膏")) {
                        position = i;
                        break;
                    }
                }
                WebElement element = productElement.get(position);

                //執行可拉動卷軸，避免#goTOP擋住想點的連結
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
                element.click();

                //換分頁
                Iterator<String> iterator = driver.getWindowHandles().iterator();
                String subWindowHandler = null;
                while (iterator.hasNext()){
                    subWindowHandler = iterator.next();
                }
                driver.switchTo().window(subWindowHandler);

                //購物車
                WebDriverWait wait = new WebDriverWait(driver, 10);
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@id=\"ButtonContainer\"]/button[text()=\"加入24h購物車\"]"))).click();
                driver.findElement(By.id("ico_cart")).click();
            }

        });

        Then("導頁至付費資訊", () -> {
        	driver.findElement(By.id("loginEmail")).sendKeys("妳的帳號");
            driver.findElement(By.id("loginPwd")).sendKeys("你的密碼");
            //driver.findElement(By.id("captchaInput")).sendKeys("驗證碼");
            driver.findElement(By.id("captchaInput")).click();
        	
            while (true) {
                try {
                    Thread.sleep(5*1000);
                    if (driver.findElement(By.id("captchaInput")).getAttribute("value").length()==6) {
                        break;
                    }
                }catch(Exception e) {}
            }
        	
            WebDriverWait wait = new WebDriverWait(driver, 10);
            driver.findElement(By.id("btnLogin")).click();
            wait.until(ExpectedConditions.invisibilityOf(driver.findElement(By.className("overlay-shadow"))));
            driver.findElement(By.linkText("貨到付款")).click();
            wait.until(ExpectedConditions.invisibilityOf(driver.findElement(By.id("mask_background"))));
            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("BuyerName")))).clear();
            driver.findElement(By.id("BuyerName")).sendKeys("購買人");
            driver.findElement(By.id("BuyerMobile")).clear();
            driver.findElement(By.id("BuyerMobile")).sendKeys("123456789");
            driver.findElement(By.id("BuyerTel")).sendKeys("123456789");
            new Select(driver.findElement(By.id("BuyerAddrCity"))).selectByVisibleText("臺北市");
            new Select(driver.findElement(By.id("BuyerAddrRegion"))).selectByVisibleText("中正區");
            driver.findElement(By.id("BuyerAddr")).sendKeys("地址");
        });
        
        After(()->{
        	if(driver!=null) {
        		driver.quit();
        	}
        });
    }
    
    

}