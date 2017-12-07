-----------------------------------------------------------------------
-- ranking_windowed_function

-- Simple CASE expression: 
-- CASE input_expression 
     -- WHEN when_expression THEN result_expression [ ...n ] 
     -- [ ELSE else_result_expression ] 
-- END 
-- Searched CASE expression:
-- CASE
     -- WHEN Boolean_expression THEN result_expression [ ...n ] 
     -- [ ELSE else_result_expression ] 
-- END

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using a SELECT statement with a simple CASE expression

USE AdventureWorks2012;
GO
SELECT   ProductNumber, Category =
      CASE ProductLine
         WHEN 'R' THEN 'Road'
         WHEN 'M' THEN 'Mountain'
         WHEN 'T' THEN 'Touring'
         WHEN 'S' THEN 'Other sale items'
         ELSE 'Not for sale'
      END,
   Name
FROM Production.Product
ORDER BY ProductNumber;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using a SELECT statement with a searched CASE expression

USE AdventureWorks2012;
GO
SELECT   ProductNumber, Name, "Price Range" = 
      CASE 
         WHEN ListPrice =  0 THEN 'Mfg item - not for resale'
         WHEN ListPrice < 50 THEN 'Under $50'
         WHEN ListPrice > = 50 and ListPrice < 250 THEN 'Under $250'
         WHEN ListPrice > = 250 and ListPrice < 1000 THEN 'Under $1000'
         ELSE 'Over $1000'
      END
FROM Production.Product
ORDER BY ProductNumber ;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using CASE in an ORDER BY clause

SELECT BusinessEntityID, SalariedFlag
FROM HumanResources.Employee
ORDER BY CASE SalariedFlag WHEN 1 THEN BusinessEntityID END DESC
        ,CASE WHEN SalariedFlag = 0 THEN BusinessEntityID END;
GO

SELECT BusinessEntityID, LastName, TerritoryName, CountryRegionName
FROM Sales.vSalesPerson
WHERE TerritoryName IS NOT NULL
ORDER BY CASE CountryRegionName WHEN 'United States' THEN TerritoryName
         ELSE CountryRegionName END;
         
--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using CASE in an UPDATE statement

USE AdventureWorks2012;
GO
UPDATE HumanResources.Employee
SET VacationHours = 
    ( CASE
         WHEN ((VacationHours - 10.00) < 0) THEN VacationHours + 40
         ELSE (VacationHours + 20.00)
       END
    )
OUTPUT Deleted.BusinessEntityID, Deleted.VacationHours AS BeforeValue, 
       Inserted.VacationHours AS AfterValue
WHERE SalariedFlag = 0; 

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using CASE in a SET statement
-- TODO: uncomment when create_function will be implemented.

-- USE AdventureWorks2012;
-- GO
-- CREATE FUNCTION dbo.GetContactInformation(@BusinessEntityID int)
    -- RETURNS @retContactInformation TABLE 
-- (
-- BusinessEntityID int NOT NULL,
-- FirstName nvarchar(50) NULL,
-- LastName nvarchar(50) NULL,
-- ContactType nvarchar(50) NULL,
    -- PRIMARY KEY CLUSTERED (BusinessEntityID ASC)
-- ) 
-- AS 
-- -- Returns the first name, last name and contact type for the specified contact.
-- BEGIN
    -- DECLARE 
        -- @FirstName nvarchar(50), 
        -- @LastName nvarchar(50), 
        -- @ContactType nvarchar(50);

    -- -- Get common contact information
    -- SELECT 
        -- @BusinessEntityID = BusinessEntityID, 
-- @FirstName = FirstName, 
        -- @LastName = LastName
    -- FROM Person.Person 
    -- WHERE BusinessEntityID = @BusinessEntityID;

    -- SET @ContactType = 
        -- CASE 
            -- -- Check for employee
            -- WHEN EXISTS(SELECT * FROM HumanResources.Employee AS e 
                -- WHERE e.BusinessEntityID = @BusinessEntityID) 
                -- THEN 'Employee'

            -- -- Check for vendor
            -- WHEN EXISTS(SELECT * FROM Person.BusinessEntityContact AS bec
                -- WHERE bec.BusinessEntityID = @BusinessEntityID) 
                -- THEN 'Vendor'

            -- -- Check for store
            -- WHEN EXISTS(SELECT * FROM Purchasing.Vendor AS v          
                -- WHERE v.BusinessEntityID = @BusinessEntityID) 
                -- THEN 'Store Contact'

            -- -- Check for individual consumer
            -- WHEN EXISTS(SELECT * FROM Sales.Customer AS c 
                -- WHERE c.PersonID = @BusinessEntityID) 
                -- THEN 'Consumer'
        -- END;

    -- -- Return the information to the caller
    -- IF @BusinessEntityID IS NOT NULL 
    -- BEGIN
        -- INSERT @retContactInformation
        -- SELECT @BusinessEntityID, @FirstName, @LastName, @ContactType;
    -- END;

    -- RETURN;
-- END;
-- GO

-- SELECT BusinessEntityID, FirstName, LastName, ContactType
-- FROM dbo.GetContactInformation(2200);
-- GO
-- SELECT BusinessEntityID, FirstName, LastName, ContactType
-- FROM dbo.GetContactInformation(5);

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using CASE in a HAVING clause

USE AdventureWorks2012;
GO
SELECT JobTitle, MAX(ph1.Rate)AS MaximumRate
FROM HumanResources.Employee AS e
JOIN HumanResources.EmployeePayHistory AS ph1 ON e.BusinessEntityID = ph1.BusinessEntityID
GROUP BY JobTitle
HAVING (MAX(CASE WHEN Gender = 'M' 
        THEN ph1.Rate 
        ELSE NULL END) > 40.00
     OR MAX(CASE WHEN Gender  = 'F' 
        THEN ph1.Rate  
        ELSE NULL END) > 42.00)
ORDER BY MaximumRate DESC;

-----------------------------------------------------------------------
-- RANK https://msdn.microsoft.com/en-us/library/ms176102.aspx
-- RANK ( ) OVER ( [ partition_by_clause ] order_by_clause )

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Ranking rows within a partition

USE AdventureWorks2012;
GO
SELECT i.ProductID, p.Name, i.LocationID, i.Quantity
    ,RANK() OVER 
    (PARTITION BY i.LocationID ORDER BY i.Quantity DESC) AS Rank
FROM Production.ProductInventory AS i 
INNER JOIN Production.Product AS p 
    ON i.ProductID = p.ProductID
WHERE i.LocationID BETWEEN 3 AND 4
ORDER BY i.LocationID;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Ranking all rows in a result set

USE AdventureWorks2012
SELECT TOP(10) BusinessEntityID, Rate, 
       RANK() OVER (ORDER BY Rate DESC) AS RankBySalary
FROM HumanResources.EmployeePayHistory AS eph1
WHERE RateChangeDate = (SELECT MAX(RateChangeDate) 
                        FROM HumanResources.EmployeePayHistory AS eph2
                        WHERE eph1.BusinessEntityID = eph2.BusinessEntityID)
ORDER BY BusinessEntityID;

-----------------------------------------------------------------------
-- DENSE_RANK https://msdn.microsoft.com/en-us/library/ms173825.aspx
-- DENSE_RANK ( ) OVER ( [ <partition_by_clause> ] < order_by_clause > )

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Ranking rows within a partition

USE AdventureWorks2012;
GO
SELECT i.ProductID, p.Name, i.LocationID, i.Quantity
    ,DENSE_RANK() OVER 
    (PARTITION BY i.LocationID ORDER BY i.Quantity DESC) AS Rank
FROM Production.ProductInventory AS i 
INNER JOIN Production.Product AS p 
    ON i.ProductID = p.ProductID
WHERE i.LocationID BETWEEN 3 AND 4
ORDER BY i.LocationID;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Ranking all rows in a result set

USE AdventureWorks2012;
GO
SELECT TOP(10) BusinessEntityID, Rate, 
       DENSE_RANK() OVER (ORDER BY Rate DESC) AS RankBySalary
FROM HumanResources.EmployeePayHistory;

-----------------------------------------------------------------------
-- NTILE https://msdn.microsoft.com/en-us/library/ms173825.aspx
-- NTILE (integer_expression) OVER ( [ <partition_by_clause> ] < order_by_clause > )

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Dividing rows into groups

USE AdventureWorks2012; 
GO
SELECT p.FirstName, p.LastName
    ,NTILE(4) OVER(ORDER BY SalesYTD DESC) AS Quartile
    ,CONVERT(nvarchar(20),s.SalesYTD,1) AS SalesYTD
    , a.PostalCode
FROM Sales.SalesPerson AS s 
INNER JOIN Person.Person AS p 
    ON s.BusinessEntityID = p.BusinessEntityID
INNER JOIN Person.Address AS a 
    ON a.AddressID = p.BusinessEntityID
WHERE TerritoryID IS NOT NULL 
    AND SalesYTD <> 0;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Dividing the result set by using PARTITION BY

USE AdventureWorks2012;
GO
DECLARE @NTILE_Var int = 4;

SELECT p.FirstName, p.LastName
    ,NTILE(@NTILE_Var) OVER(PARTITION BY PostalCode ORDER BY SalesYTD DESC) AS Quartile
    ,CONVERT(nvarchar(20),s.SalesYTD,1) AS SalesYTD
    ,a.PostalCode
FROM Sales.SalesPerson AS s 
INNER JOIN Person.Person AS p 
    ON s.BusinessEntityID = p.BusinessEntityID
INNER JOIN Person.Address AS a 
    ON a.AddressID = p.BusinessEntityID
WHERE TerritoryID IS NOT NULL 
    AND SalesYTD <> 0;
GO

-----------------------------------------------------------------------
-- ROW_NUMBER https://msdn.microsoft.com/en-us/library/ms186734.aspx
-- ROW_NUMBER ( ) OVER ( [ PARTITION BY value_expression , ... [ n ] ] order_by_clause )

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Returning the row number for salespeople

USE AdventureWorks2012; 
GO
SELECT ROW_NUMBER() OVER(ORDER BY SalesYTD DESC) AS Row, 
    FirstName, LastName, ROUND(SalesYTD,2,1) AS "Sales YTD" 
FROM Sales.vSalesPerson
WHERE TerritoryName IS NOT NULL AND SalesYTD <> 0;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Returning a subset of rows

USE AdventureWorks2012;
GO
WITH OrderedOrders AS
(
    SELECT SalesOrderID, OrderDate,
    ROW_NUMBER() OVER (ORDER BY OrderDate) AS RowNumber
    FROM Sales.SalesOrderHeader 
) 
SELECT SalesOrderID, OrderDate, RowNumber  
FROM OrderedOrders 
WHERE RowNumber BETWEEN 50 AND 60;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using ROW_NUMBER() with PARTITION

USE AdventureWorks2012;
GO
SELECT FirstName, LastName, TerritoryName, ROUND(SalesYTD,2,1),
ROW_NUMBER() OVER(PARTITION BY TerritoryName ORDER BY SalesYTD DESC) AS Row
FROM Sales.vSalesPerson
WHERE TerritoryName IS NOT NULL AND SalesYTD <> 0
ORDER BY TerritoryName;

-----------------------------------------------------------------------
-- OVER Clause https://msdn.microsoft.com/en-us/library/ms189461.aspx
-- OVER ( 
       -- [ <PARTITION BY clause> ]
       -- [ <ORDER BY clause> ] 
       -- [ <ROW or RANGE clause> ]
      -- )<PARTITION BY clause> ::=
-- PARTITION BY value_expression , ... [ n ]

-- <ORDER BY clause> ::=
-- ORDER BY order_by_expression
    -- [ COLLATE collation_name ] 
    -- [ ASC | DESC ] 
    -- [ ,...n ]

-- <ROW or RANGE clause> ::=
-- { ROWS | RANGE } <window frame extent>

-- <window frame extent> ::= 
-- {   <window frame preceding>
  -- | <window frame between>
-- }
-- <window frame between> ::= 
  -- BETWEEN <window frame bound> AND <window frame bound>

-- <window frame bound> ::= 
-- {   <window frame preceding>
  -- | <window frame following>
-- }

-- <window frame preceding> ::= 
-- {
    -- UNBOUNDED PRECEDING
  -- | <unsigned_value_specification> PRECEDING
  -- | CURRENT ROW
-- }

-- <window frame following> ::= 
-- {
    -- UNBOUNDED FOLLOWING
  -- | <unsigned_value_specification> FOLLOWING
  -- | CURRENT ROW
-- }

-- <unsigned value specification> ::= 
-- {  <unsigned integer literal> }

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the OVER clause with the ROW_NUMBER function

USE AdventureWorks2012;
GO
SELECT ROW_NUMBER() OVER(PARTITION BY PostalCode ORDER BY SalesYTD DESC) AS "Row Number", 
    p.LastName, s.SalesYTD, a.PostalCode
FROM Sales.SalesPerson AS s 
    INNER JOIN Person.Person AS p 
        ON s.BusinessEntityID = p.BusinessEntityID
    INNER JOIN Person.Address AS a 
        ON a.AddressID = p.BusinessEntityID
WHERE TerritoryID IS NOT NULL 
    AND SalesYTD <> 0
ORDER BY PostalCode;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the OVER clause with aggregate functions

USE AdventureWorks2012;
GO
SELECT SalesOrderID, ProductID, OrderQty
    ,SUM(OrderQty) OVER(PARTITION BY SalesOrderID) AS Total
    ,AVG(OrderQty) OVER(PARTITION BY SalesOrderID) AS "Avg"
    ,COUNT(OrderQty) OVER(PARTITION BY SalesOrderID) AS "Count"
    ,MIN(OrderQty) OVER(PARTITION BY SalesOrderID) AS "Min"
    ,MAX(OrderQty) OVER(PARTITION BY SalesOrderID) AS "Max"
FROM Sales.SalesOrderDetail 
WHERE SalesOrderID IN(43659,43664);
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the OVER clause with aggregate functions

USE AdventureWorks2012;
GO
SELECT SalesOrderID, ProductID, OrderQty
    ,SUM(OrderQty) OVER(PARTITION BY SalesOrderID) AS Total
    ,CAST(1. * OrderQty / SUM(OrderQty) OVER(PARTITION BY SalesOrderID) 
        *100 AS DECIMAL(5,2))AS "Percent by ProductID"
FROM Sales.SalesOrderDetail 
WHERE SalesOrderID IN(43659,43664);
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Producing a moving average and cumulative total

USE AdventureWorks2012;
GO
SELECT BusinessEntityID, TerritoryID 
   ,DATEPART(yy,ModifiedDate) AS SalesYear
   ,CONVERT(varchar(20),SalesYTD,1) AS  SalesYTD
   ,CONVERT(varchar(20),AVG(SalesYTD) OVER (PARTITION BY TerritoryID 
                                            ORDER BY DATEPART(yy,ModifiedDate) 
                                           ),1) AS MovingAvg
   ,CONVERT(varchar(20),SUM(SalesYTD) OVER (PARTITION BY TerritoryID 
                                            ORDER BY DATEPART(yy,ModifiedDate) 
                                            ),1) AS CumulativeTotal
FROM Sales.SalesPerson
WHERE TerritoryID IS NULL OR TerritoryID < 5
ORDER BY TerritoryID,SalesYear;

SELECT BusinessEntityID, TerritoryID 
   ,DATEPART(yy,ModifiedDate) AS SalesYear
   ,CONVERT(varchar(20),SalesYTD,1) AS  SalesYTD
   ,CONVERT(varchar(20),AVG(SalesYTD) OVER (ORDER BY DATEPART(yy,ModifiedDate) 
                                            ),1) AS MovingAvg
   ,CONVERT(varchar(20),SUM(SalesYTD) OVER (ORDER BY DATEPART(yy,ModifiedDate) 
                                            ),1) AS CumulativeTotal
FROM Sales.SalesPerson
WHERE TerritoryID IS NULL OR TerritoryID < 5
ORDER BY SalesYear;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Specifying the ROWS clause

SELECT BusinessEntityID, TerritoryID 
    ,CONVERT(varchar(20),SalesYTD,1) AS  SalesYTD
    ,DATEPART(yy,ModifiedDate) AS SalesYear
    ,CONVERT(varchar(20),SUM(SalesYTD) OVER (PARTITION BY TerritoryID 
                                             ORDER BY DATEPART(yy,ModifiedDate) 
                                             ROWS BETWEEN CURRENT ROW AND 1 FOLLOWING ),1) AS CumulativeTotal
FROM Sales.SalesPerson
WHERE TerritoryID IS NULL OR TerritoryID < 5;

SELECT BusinessEntityID, TerritoryID 
    ,CONVERT(varchar(20),SalesYTD,1) AS  SalesYTD
    ,DATEPART(yy,ModifiedDate) AS SalesYear
    ,CONVERT(varchar(20),SUM(SalesYTD) OVER (PARTITION BY TerritoryID 
                                             ORDER BY DATEPART(yy,ModifiedDate) 
                                             ROWS UNBOUNDED PRECEDING),1) AS CumulativeTotal
FROM Sales.SalesPerson
WHERE TerritoryID IS NULL OR TerritoryID < 5;

-- addition assignment operator
DECLARE @count int = 0;
SELECT @count += 1;
SELECT @count;
GO;

-- subtraction assignment operator
DECLARE @count int = 0;
SELECT @count -= 1;
SELECT @count;
GO;

-- division assignment operator
DECLARE @count int = 1;
SELECT @count /= 1;
SELECT @count;
GO;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Specifying the ISNULL function

USE AdventureWorks2012;
GO
SELECT AVG(ISNULL(Weight, 50))
FROM Production.Product;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Specifying the STUFF function
SELECT STUFF('abcdef', 2, 3, 'ijklmn');
GO