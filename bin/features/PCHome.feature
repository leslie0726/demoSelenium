Feature: test PCHome
    Scenario: 在PCHome買東西
        Given 打開Google搜尋
        And 輸入PCHome後搜尋
        And 按下第二筆查詢結果進到PCHome
        When 輸入牙膏後搜尋
        Then 找出最便宜的牙膏
        And 導頁至付費資訊