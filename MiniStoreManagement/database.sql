CREATE DATABASE MiniStoreDB;
GO

USE MiniStoreDB;
GO

-- PHẦN 1: BẢNG TÀI KHOẢN (Cho LoginFrame)
CREATE TABLE Users (
    Username VARCHAR(50) PRIMARY KEY,
    Password VARCHAR(50)
);
GO

-- Thêm nick Admin mặc định
INSERT INTO Users VALUES ('admin', '123');
GO

-- PHẦN 2: BẢNG SẢN PHẨM (Cho MainFrame)
CREATE TABLE Products (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    Name NVARCHAR(100),
    Category VARCHAR(50),
    Price FLOAT,
    Stock INT,
    ImageFile NVARCHAR(100) -- Cột lưu tên ảnh
);
GO

-- Thêm 8 sản phẩm mẫu (Đã khớp tên file ảnh PNG)
INSERT INTO Products (Name, Category, Price, Stock, ImageFile) VALUES 
(N'Mì Hảo Hảo Tôm Chua Cay', 'Food', 4500, 200, 'haohao.png'),
(N'Snack Oishi Tôm', 'Food', 6000, 50, 'oishi.png'),
(N'Pepsi Zero Calorie', 'Drink', 12000, 100, 'pepsi.png'),
(N'Sting Dâu', 'Drink', 11000, 120, 'sting.png'),
(N'Vở Campus 200 trang', 'Stationery', 18000, 30, 'notebook.png'),
(N'Bút Bi Thiên Long', 'Stationery', 5000, 300, 'pen.png'),
(N'KĐR Colgate MaxFresh', 'Household', 35000, 40, 'colgate.png'),
(N'Nước Rửa Tay Lifebuoy', 'Household', 65000, 25, 'lifebuoy.png');
GO

-- Kiểm tra lại dữ liệu
SELECT * FROM Users;
SELECT * FROM Products;