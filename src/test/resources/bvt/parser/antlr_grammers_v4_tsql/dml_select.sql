-----------------------------------------------------------------------
-- SELECT https://msdn.microsoft.com/en-us/library/ms187731.aspx

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using SELECT to retrieve rows and columns

USE AdventureWorks2012;
GO
SELECT *
FROM Production.Product
ORDER BY Name ASC;
-- Alternate way.
USE AdventureWorks2012;
GO
SELECT p.*
FROM Production.Product AS p
ORDER BY Name ASC;
GO

USE AdventureWorks2012;
GO
SELECT Name, ProductNumber, ListPrice AS Price
FROM Production.Product 
ORDER BY Name ASC;
GO

USE AdventureWorks2012;
GO
SELECT Name, ProductNumber, ListPrice AS Price
FROM Production.Product 
WHERE ProductLine = 'R' 
AND DaysToManufacture < 4
ORDER BY Name ASC;
GO

-- Select with Multiple Batch Separators
USE AdventureWorks2012;
GO
SELECT *
FROM Production.Product
ORDER BY Name ASC;
GO
GO

USE AdventureWorks2012;
GO
SELECT
    [Production].[Product].[ProductID],
    [Production].[Product].[Name]
FROM [Production].[Product]
GO

USE AdventureWorks2012;
GO
SELECT *, *, * -- yes, syntax is valid
FROM [Production].[Product]
GO

USE AdventureWorks2012;
GO
SELECT
    some_type::static_method (@arg1, @arg2, @arg3),
    another_type::method ('some value'),
    still_one_type.non_static_method (@but, @with, @params)
FROM [Production].[Product]
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using SELECT with column headings and calculations

USE AdventureWorks2012;
GO
SELECT p.Name AS ProductName, 
NonDiscountSales = (OrderQty * UnitPrice),
Discounts = ((OrderQty * UnitPrice) * UnitPriceDiscount)
FROM Production.Product AS p 
INNER JOIN Sales.SalesOrderDetail AS sod
ON p.ProductID = sod.ProductID 
ORDER BY ProductName DESC;
GO

USE AdventureWorks2012;
GO
SELECT 'Total income is', ((OrderQty * UnitPrice) * (1.0 - UnitPriceDiscount)), ' for ',
p.Name AS ProductName 
FROM Production.Product AS p 
INNER JOIN Sales.SalesOrderDetail AS sod
ON p.ProductID = sod.ProductID 
ORDER BY ProductName ASC;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using DISTINCT with SELECT

USE AdventureWorks2012;
GO
SELECT DISTINCT JobTitle
FROM HumanResources.Employee
ORDER BY JobTitle;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Creating tables with SELECT INTO

USE tempdb;
GO
IF OBJECT_ID (N'#Bicycles',N'U') IS NOT NULL
DROP TABLE #Bicycles;
GO
SELECT * 
INTO #Bicycles
FROM AdventureWorks2012.Production.Product
WHERE ProductNumber LIKE 'BK%';
GO

USE AdventureWorks2012;
GO
IF OBJECT_ID('dbo.NewProducts', 'U') IS NOT NULL
    DROP TABLE dbo.NewProducts;
GO
ALTER DATABASE AdventureWorks2012 SET RECOVERY BULK_LOGGED;
GO

SELECT * INTO dbo.NewProducts
FROM Production.Product
WHERE ListPrice > $25 
AND ListPrice < $100;
GO
ALTER DATABASE AdventureWorks2012 SET RECOVERY FULL;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using correlated subqueries

USE AdventureWorks2012;
GO
SELECT DISTINCT Name
FROM Production.Product AS p 
WHERE EXISTS
    (SELECT *
     FROM Production.ProductModel AS pm 
     WHERE p.ProductModelID = pm.ProductModelID
           AND pm.Name LIKE 'Long-Sleeve Logo Jersey%');
GO

-- OR

USE AdventureWorks2012;
GO
SELECT DISTINCT Name
FROM Production.Product
WHERE ProductModelID IN
    (SELECT ProductModelID 
     FROM Production.ProductModel
     WHERE Name LIKE 'Long-Sleeve Logo Jersey%');
GO

USE AdventureWorks2012;
GO
SELECT DISTINCT p.LastName, p.FirstName 
FROM Person.Person AS p 
JOIN HumanResources.Employee AS e
    ON e.BusinessEntityID = p.BusinessEntityID WHERE 5000.00 IN
    (SELECT Bonus
     FROM Sales.SalesPerson AS sp
     WHERE e.BusinessEntityID = sp.BusinessEntityID);
GO

USE AdventureWorks2012;
GO
SELECT p1.ProductModelID
FROM Production.Product AS p1
GROUP BY p1.ProductModelID
HAVING MAX(p1.ListPrice) >= ALL
    (SELECT AVG(p2.ListPrice)
     FROM Production.Product AS p2
     WHERE p1.ProductModelID = p2.ProductModelID);
GO

USE AdventureWorks2012;
GO
SELECT DISTINCT pp.LastName, pp.FirstName 
FROM Person.Person pp JOIN HumanResources.Employee e
ON e.BusinessEntityID = pp.BusinessEntityID WHERE pp.BusinessEntityID IN 
(SELECT SalesPersonID 
FROM Sales.SalesOrderHeader
WHERE SalesOrderID IN 
(SELECT SalesOrderID 
FROM Sales.SalesOrderDetail
WHERE ProductID IN 
(SELECT ProductID 
FROM Production.Product p 
WHERE ProductNumber = 'BK-M68B-42')));
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using GROUP BY

USE AdventureWorks2012;
GO
SELECT SalesOrderID, SUM(LineTotal) AS SubTotal
FROM Sales.SalesOrderDetail
GROUP BY SalesOrderID
ORDER BY SalesOrderID;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using GROUP BY with multiple groups

USE AdventureWorks2012;
GO
SELECT ProductID, SpecialOfferID, AVG(UnitPrice) AS [Average Price], 
    SUM(LineTotal) AS SubTotal
FROM Sales.SalesOrderDetail
GROUP BY ProductID, SpecialOfferID
ORDER BY ProductID;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using GROUP BY and WHERE

USE AdventureWorks2012;
GO
SELECT ProductModelID, AVG(ListPrice) AS [Average List Price]
FROM Production.Product
WHERE ListPrice > $1000
GROUP BY ProductModelID
ORDER BY ProductModelID;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using GROUP BY with an expression

USE AdventureWorks2012;
GO
SELECT AVG(OrderQty) AS [Average Quantity], 
NonDiscountSales = (OrderQty * UnitPrice)
FROM Sales.SalesOrderDetail
GROUP BY (OrderQty * UnitPrice)
ORDER BY (OrderQty * UnitPrice) DESC;
GO


--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using GROUP BY with ORDER BY

USE AdventureWorks2012;
GO
SELECT ProductID, AVG(UnitPrice) AS [Average Price]
FROM Sales.SalesOrderDetail
WHERE OrderQty > 10
GROUP BY ProductID
ORDER BY AVG(UnitPrice);
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the HAVING clause

USE AdventureWorks2012;
GO
SELECT ProductID 
FROM Sales.SalesOrderDetail
GROUP BY ProductID
HAVING AVG(OrderQty) > 5
ORDER BY ProductID;
GO

USE AdventureWorks2012 ;
GO
SELECT SalesOrderID, CarrierTrackingNumber 
FROM Sales.SalesOrderDetail
GROUP BY SalesOrderID, CarrierTrackingNumber
HAVING CarrierTrackingNumber LIKE '4BD%'
ORDER BY SalesOrderID ;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using HAVING and GROUP BY

USE AdventureWorks2012;
GO
SELECT ProductID 
FROM Sales.SalesOrderDetail
WHERE UnitPrice < 25.00
GROUP BY ProductID
HAVING AVG(OrderQty) > 5
ORDER BY ProductID;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using HAVING with SUM and AVG

USE AdventureWorks2012;
GO
SELECT ProductID, AVG(OrderQty) AS AverageQuantity, SUM(LineTotal) AS Total
FROM Sales.SalesOrderDetail
GROUP BY ProductID
HAVING SUM(LineTotal) > $1000000.00
AND AVG(OrderQty) < 3;
GO

USE AdventureWorks2012;
GO
SELECT ProductID, Total = SUM(LineTotal)
FROM Sales.SalesOrderDetail
GROUP BY ProductID
HAVING SUM(LineTotal) > $2000000.00;
GO

USE AdventureWorks2012;
GO
SELECT ProductID, SUM(LineTotal) AS Total
FROM Sales.SalesOrderDetail
GROUP BY ProductID
HAVING COUNT(*) > 1500;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the INDEX optimizer hint

USE AdventureWorks2012;
GO
SELECT pp.FirstName, pp.LastName, e.NationalIDNumber
FROM HumanResources.Employee AS e WITH (INDEX(AK_Employee_NationalIDNumber))
JOIN Person.Person AS pp on e.BusinessEntityID = pp.BusinessEntityID
WHERE LastName = 'Johnson';
GO

-- Force a table scan by using INDEX = 0.
USE AdventureWorks2012;
GO
SELECT pp.LastName, pp.FirstName, e.JobTitle
FROM HumanResources.Employee AS e WITH (INDEX = 0) JOIN Person.Person AS pp
ON e.BusinessEntityID = pp.BusinessEntityID
WHERE LastName = 'Johnson';
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using OPTION and the GROUP hints

USE AdventureWorks2012;
GO
SELECT ProductID, OrderQty, SUM(LineTotal) AS Total
FROM Sales.SalesOrderDetail
WHERE UnitPrice < $5.00
GROUP BY ProductID, OrderQty
ORDER BY ProductID, OrderQty
OPTION (HASH GROUP, FAST 10);
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the UNION query hint

USE AdventureWorks2012;
GO
SELECT BusinessEntityID, JobTitle, HireDate, VacationHours, SickLeaveHours
FROM HumanResources.Employee AS e1
UNION
SELECT BusinessEntityID, JobTitle, HireDate, VacationHours, SickLeaveHours
FROM HumanResources.Employee AS e2
OPTION (MERGE UNION);
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using a simple UNION

USE AdventureWorks2012;
GO
IF OBJECT_ID ('dbo.Gloves', 'U') IS NOT NULL
DROP TABLE dbo.Gloves;
GO
-- Create Gloves table.
SELECT ProductModelID, Name
INTO dbo.Gloves
FROM Production.ProductModel
WHERE ProductModelID IN (3, 4);
GO

-- Here is the simple union.
USE AdventureWorks2012;
GO
SELECT ProductModelID, Name
FROM Production.ProductModel
WHERE ProductModelID NOT IN (3, 4)
UNION
SELECT ProductModelID, Name
FROM dbo.Gloves
ORDER BY Name;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using SELECT INTO with UNION

USE AdventureWorks2012;
GO
IF OBJECT_ID ('dbo.ProductResults', 'U') IS NOT NULL
DROP TABLE dbo.ProductResults;
GO
IF OBJECT_ID ('dbo.Gloves', 'U') IS NOT NULL
DROP TABLE dbo.Gloves;
GO
-- Create Gloves table.
SELECT ProductModelID, Name
INTO dbo.Gloves
FROM Production.ProductModel
WHERE ProductModelID IN (3, 4);
GO

USE AdventureWorks2012;
GO
SELECT ProductModelID, Name
INTO dbo.ProductResults
FROM Production.ProductModel
WHERE ProductModelID NOT IN (3, 4)
UNION
SELECT ProductModelID, Name
FROM dbo.Gloves;
GO

SELECT ProductModelID, Name 
FROM dbo.ProductResults;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using UNION of two SELECT statements with ORDER BY

USE AdventureWorks2012;
GO
IF OBJECT_ID ('dbo.Gloves', 'U') IS NOT NULL
DROP TABLE dbo.Gloves;
GO
-- Create Gloves table.
SELECT ProductModelID, Name
INTO dbo.Gloves
FROM Production.ProductModel
WHERE ProductModelID IN (3, 4);
GO

/* CORRECT */
USE AdventureWorks2012;
GO
SELECT ProductModelID, Name
FROM Production.ProductModel
WHERE ProductModelID NOT IN (3, 4)
UNION
SELECT ProductModelID, Name
FROM dbo.Gloves
ORDER BY Name;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using UNION of three SELECT statements to show the effects of ALL and parentheses

USE AdventureWorks2012;
GO
IF OBJECT_ID ('dbo.EmployeeOne', 'U') IS NOT NULL
DROP TABLE dbo.EmployeeOne;
GO
IF OBJECT_ID ('dbo.EmployeeTwo', 'U') IS NOT NULL
DROP TABLE dbo.EmployeeTwo;
GO
IF OBJECT_ID ('dbo.EmployeeThree', 'U') IS NOT NULL
DROP TABLE dbo.EmployeeThree;
GO

SELECT pp.LastName, pp.FirstName, e.JobTitle 
INTO dbo.EmployeeOne
FROM Person.Person AS pp JOIN HumanResources.Employee AS e
ON e.BusinessEntityID = pp.BusinessEntityID
WHERE LastName = 'Johnson';
GO
SELECT pp.LastName, pp.FirstName, e.JobTitle 
INTO dbo.EmployeeTwo
FROM Person.Person AS pp JOIN HumanResources.Employee AS e
ON e.BusinessEntityID = pp.BusinessEntityID
WHERE LastName = 'Johnson';
GO
SELECT pp.LastName, pp.FirstName, e.JobTitle 
INTO dbo.EmployeeThree
FROM Person.Person AS pp JOIN HumanResources.Employee AS e
ON e.BusinessEntityID = pp.BusinessEntityID
WHERE LastName = 'Johnson';
GO
-- Union ALL
SELECT LastName, FirstName, JobTitle
FROM dbo.EmployeeOne
UNION ALL
SELECT LastName, FirstName ,JobTitle
FROM dbo.EmployeeTwo
UNION ALL
SELECT LastName, FirstName,JobTitle 
FROM dbo.EmployeeThree;
GO

SELECT LastName, FirstName,JobTitle
FROM dbo.EmployeeOne
UNION 
SELECT LastName, FirstName, JobTitle 
FROM dbo.EmployeeTwo
UNION 
SELECT LastName, FirstName, JobTitle 
FROM dbo.EmployeeThree;
GO

SELECT LastName, FirstName,JobTitle 
FROM dbo.EmployeeOne
UNION ALL
(
SELECT LastName, FirstName, JobTitle 
FROM dbo.EmployeeTwo
UNION
SELECT LastName, FirstName, JobTitle 
FROM dbo.EmployeeThree
);
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using SELECT with TOP clause

USE AdventureWorks2012;
GO
SELECT TOP 10 *
FROM Production.Product
ORDER BY Name ASC;
-- Alternate way.
USE AdventureWorks2012;
GO
SELECT TOP (10) *
FROM Production.Product
ORDER BY Name ASC;
-- Alternate way.
USE AdventureWorks2012;
GO
SELECT TOP (@localvar) *
FROM Production.Product
ORDER BY Name ASC;

-- TOP with percentage.
USE AdventureWorks2012;
GO
SELECT TOP 10.5 PERCENT *
FROM Production.Product
ORDER BY Name ASC;
-- Alternate way.
USE AdventureWorks2012;
GO
SELECT TOP (10.5) PERCENT *
FROM Production.Product
ORDER BY Name ASC;
-- Alternate way.
USE AdventureWorks2012;
GO
SELECT TOP (@localvar) PERCENT *
FROM Production.Product
ORDER BY Name ASC;

-- TOP with ties.
USE AdventureWorks2012;
GO
SELECT TOP 10 WITH TIES *
FROM Production.Product
ORDER BY Name ASC;
-- Alternate way.
USE AdventureWorks2012;
GO
SELECT TOP 10.5 PERCENT WITH TIES *
FROM Production.Product
ORDER BY Name ASC;
-- Alternate way.
USE AdventureWorks2012;
GO
SELECT TOP (@localvar) WITH TIES *
FROM Production.Product
ORDER BY Name ASC;
-- Alternate way.
USE AdventureWorks2012;
GO
SELECT TOP (@localvar) PERCENT WITH TIES *
FROM Production.Product
ORDER BY Name ASC;

-- Can apply any expression WITH parentheses.
--  If without parentheses, expression trails may be misunderstanding select_list.
--  ex: "SELECT TOP 1 + 2 * FROM HOGE" --> "+ 2" are matching in select_list.
USE AdventureWorks2012;
GO
SELECT TOP (NULL) *     -- (But cause runtime error by SQL Server)
FROM Production.Product
ORDER BY Name ASC;
-- Alternate way.
USE AdventureWorks2012;
GO
SELECT TOP (1+2) *
FROM Production.Product
ORDER BY Name ASC;
-- Alternate way.
USE AdventureWorks2012;
GO
SELECT TOP (1+@localvar) *
FROM Production.Product
ORDER BY Name ASC;
-- Alternate way.
USE AdventureWorks2012;
GO
SELECT TOP (NULL) PERCENT *     -- (But cause runtime error by SQL Server)
FROM Production.Product
ORDER BY Name ASC;
-- Alternate way.
USE AdventureWorks2012;
GO
SELECT TOP (1+2) PERCENT *
FROM Production.Product
ORDER BY Name ASC;
-- Alternate way.
USE AdventureWorks2012;
GO
SELECT TOP (1+@localvar) PERCENT *
FROM Production.Product
ORDER BY Name ASC;

-- TRY_CAST & TRY_CONVERT
use AdventureWorks2012;
GO
SELECT TRY_CAST(SalesOrderID AS INT),
    TRY_CONVERT(INT, SalesOrderID)
FROM Sales.SalesOrderDetail
WHERE SalesOrderDetailID = 1;
GO

-- SET statement
SET ANSI_WARNINGS OFF;
SELECT id FROM tbl;
GO

-- Select with full table name
SELECT * FROM ServerName.DBName.do.TestTable TestTable

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using SELECT from build in function
SELECT * FROM ::fn_helpcollations()


--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using PIVOT and UNPIVOT statements

-- Pivot table with one row and five columns
SELECT 'AverageCost' AS Cost_Sorted_By_Production_Days, 
[0], [1], [2], [3], [4]
FROM
(SELECT DaysToManufacture, StandardCost 
    FROM Production.Product) AS SourceTable
PIVOT
(
AVG(StandardCost)
FOR DaysToManufacture IN ([0], [1], [2], [3], [4])
) AS PivotTable;

--Create the table and insert values as portrayed in the previous example.
CREATE TABLE pvt (VendorID int, Emp1 int, Emp2 int,
    Emp3 int, Emp4 int, Emp5 int);
GO
INSERT INTO pvt VALUES (1,4,3,5,4,4);
INSERT INTO pvt VALUES (2,4,1,5,5,5);
INSERT INTO pvt VALUES (3,4,3,5,4,4);
INSERT INTO pvt VALUES (4,4,2,5,5,4);
INSERT INTO pvt VALUES (5,5,1,5,5,5);
GO
--Unpivot the table.
SELECT VendorID, Employee, Orders
FROM 
   (SELECT VendorID, Emp1, Emp2, Emp3, Emp4, Emp5
   FROM pvt) p
UNPIVOT
   (Orders FOR Employee IN 
      (Emp1, Emp2, Emp3, Emp4, Emp5)
)AS unpvt;
GO
--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using table value constructor

SELECT a, b FROM (VALUES (1, 2), (3, 4), (5, 6), (7, 8), (9, 10) ) AS MyTable(a, b);  
GO  
-- Used in an inner join to specify values to return.  
SELECT ProductID, a.Name, Color  
FROM Production.Product AS a  
INNER JOIN (VALUES ('Blade'), ('Crown Race'), ('AWC Logo Cap')) AS b(Name)   
ON a.Name = b.Name;  

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Open XML Statement


DECLARE @idoc int, @doc varchar(1000);  
SET @doc ='  
<ROOT>  
<Customer CustomerID="VINET" ContactName="Paul Henriot">  
   <Order CustomerID="VINET" EmployeeID="5" OrderDate="1996-07-04T00:00:00">  
      <OrderDetail OrderID="10248" ProductID="11" Quantity="12"/>  
      <OrderDetail OrderID="10248" ProductID="42" Quantity="10"/>  
   </Order>  
</Customer>  
<Customer CustomerID="LILAS" ContactName="Carlos Gonzlez">  
   <Order CustomerID="LILAS" EmployeeID="3" OrderDate="1996-08-16T00:00:00">  
      <OrderDetail OrderID="10283" ProductID="72" Quantity="3"/>  
   </Order>  
</Customer>  
</ROOT>';  
--Create an internal representation of the XML document.  
EXEC sp_xml_preparedocument @idoc OUTPUT, @doc;  
-- Execute a SELECT statement that uses the OPENXML rowset provider.  
SELECT    *  
FROM       OPENXML (@idoc, '/ROOT/Customer',1)  
            WITH (CustomerID  varchar(10),  
                  ContactName varchar(20));  
				  

				  DECLARE @idoc int, @doc varchar(1000);   
SET @doc ='  
<ROOT>  
<Customer CustomerID="VINET" ContactName="Paul Henriot">  
   <Order OrderID="10248" CustomerID="VINET" EmployeeID="5"   
           OrderDate="1996-07-04T00:00:00">  
      <OrderDetail ProductID="11" Quantity="12"/>  
      <OrderDetail ProductID="42" Quantity="10"/>  
   </Order>  
</Customer>  
<Customer CustomerID="LILAS" ContactName="Carlos Gonzlez">v  
   <Order OrderID="10283" CustomerID="LILAS" EmployeeID="3"   
           OrderDate="1996-08-16T00:00:00">  
      <OrderDetail ProductID="72" Quantity="3"/>  
   </Order>  
</Customer>  
</ROOT>';   
  
--Create an internal representation of the XML document.  
EXEC sp_xml_preparedocument @idoc OUTPUT, @doc;   
  
-- SELECT stmt using OPENXML rowset provider  
SELECT *  
FROM   OPENXML (@idoc, '/ROOT/Customer/Order/OrderDetail',2)   
         WITH (OrderID       int         '../@OrderID',   
               CustomerID  varchar(10) '../@CustomerID',   
               OrderDate   datetime    '../@OrderDate',   
               ProdID      int         '@ProductID',   
               Qty         int         '@Quantity');  