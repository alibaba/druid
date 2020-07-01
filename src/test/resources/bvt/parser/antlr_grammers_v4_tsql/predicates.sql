-----------------------------------------------------------------------
-- ALL https://msdn.microsoft.com/en-us/library/ms178543.aspx
-- scalar_expression { = | <> | != | > | >= | !> | < | <= | !< } ALL ( subquery )

USE AdventureWorks2012 ;
GO

CREATE PROCEDURE DaysToBuild @OrderID int, @NumberOfDays int
AS
IF 
@NumberOfDays >= ALL
   (
    SELECT DaysToManufacture
    FROM Sales.SalesOrderDetail
    JOIN Production.Product 
    ON Sales.SalesOrderDetail.ProductID = Production.Product.ProductID 
    WHERE SalesOrderID = @OrderID
   )
PRINT 'All items for this order can be manufactured in specified number of days or less.'
ELSE 
PRINT 'Some items for this order cannot be manufactured in specified number of days or less.' ;

-----------------------------------------------------------------------
-- SOME | ANY https://msdn.microsoft.com/en-us/library/ms175064.aspx
-- scalar_expression { = | < > | ! = | > | > = | ! > | < | < = | ! < } { SOME | ANY } ( subquery ) 

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Running a simple example

IF 3 < SOME (SELECT ID FROM T1)
PRINT 'TRUE' 
ELSE
PRINT 'FALSE' ;

IF 3 < ALL (SELECT ID FROM T1)
PRINT 'TRUE' 
ELSE
PRINT 'FALSE' ;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Running a practical example

USE AdventureWorks2012 ;
GO

CREATE PROCEDURE ManyDaysToComplete @OrderID int, @NumberOfDays int
AS
IF 
@NumberOfDays < SOME
   (
    SELECT DaysToManufacture
    FROM Sales.SalesOrderDetail
    JOIN Production.Product 
    ON Sales.SalesOrderDetail.ProductID = Production.Product.ProductID 
    WHERE SalesOrderID = @OrderID
   )
PRINT 'At least one item for this order cannot be manufactured in specified number of days.'
ELSE 
PRINT 'All items for this order can be manufactured in the specified number of days or less.' ;

-----------------------------------------------------------------------
-- BETWEEN https://msdn.microsoft.com/en-us/library/ms187922.aspx
-- test_expression [ NOT ] BETWEEN begin_expression AND end_expression

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using BETWEEN

USE AdventureWorks2012;
GO
SELECT e.FirstName, e.LastName, ep.Rate
FROM HumanResources.vEmployee e 
JOIN HumanResources.EmployeePayHistory ep 
    ON e.BusinessEntityID = ep.BusinessEntityID
WHERE ep.Rate BETWEEN 27 AND 30
ORDER BY ep.Rate;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using NOT BETWEEN

USE AdventureWorks2012;
GO
SELECT e.FirstName, e.LastName, ep.Rate
FROM HumanResources.vEmployee e 
JOIN HumanResources.EmployeePayHistory ep 
    ON e.BusinessEntityID = ep.BusinessEntityID
WHERE ep.Rate NOT BETWEEN 27 AND 30
ORDER BY ep.Rate;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using BETWEEN with datetime values

USE AdventureWorks2012;
GO
SELECT BusinessEntityID, RateChangeDate
FROM HumanResources.EmployeePayHistory
WHERE RateChangeDate BETWEEN '20011212' AND '20020105';

-----------------------------------------------------------------------
-- IN https://msdn.microsoft.com/en-us/library/ms177682.aspx
-- test_expression [ NOT ] IN ( subquery | expression [ ,...n ]) 

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
--  Using IN with a subquery

USE AdventureWorks2012;
GO
SELECT p.FirstName, p.LastName
FROM Person.Person AS p
    JOIN Sales.SalesPerson AS sp
    ON p.BusinessEntityID = sp.BusinessEntityID
WHERE p.BusinessEntityID IN
   (SELECT BusinessEntityID
   FROM Sales.SalesPerson
   WHERE SalesQuota > 250000);
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
--  Using IN with a subquery

USE AdventureWorks2012;
GO
SELECT p.FirstName, p.LastName
FROM Person.Person AS p
    JOIN Sales.SalesPerson AS sp
    ON p.BusinessEntityID = sp.BusinessEntityID
WHERE p.BusinessEntityID NOT IN
   (SELECT BusinessEntityID
   FROM Sales.SalesPerson
   WHERE SalesQuota > 250000);
GO

-----------------------------------------------------------------------
-- LIKE https://msdn.microsoft.com/en-us/library/ms179859.aspx
-- match_expression [ NOT ] LIKE pattern [ ESCAPE escape_character ]

USE AdventureWorks2012;
GO
CREATE PROCEDURE FindEmployee @EmpLName char(20)
AS
SELECT @EmpLName = RTRIM(@EmpLName) + '%';
SELECT p.FirstName, p.LastName, a.City
FROM Person.Person p JOIN Person.Address a ON p.BusinessEntityID = a.AddressID
WHERE p.LastName LIKE @EmpLName;
GO
EXEC FindEmployee @EmpLName = 'Barb';
GO

USE AdventureWorks2012;
GO
CREATE PROCEDURE FindEmployee @EmpLName varchar(20)
AS
SELECT @EmpLName = RTRIM(@EmpLName) + '%';
SELECT p.FirstName, p.LastName, a.City
FROM Person.Person p JOIN Person.Address a ON p.BusinessEntityID = a.AddressID
WHERE p.LastName LIKE @EmpLName;
GO
EXEC FindEmployee @EmpLName = 'Barb';

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Pattern Matching by Using LIKE

-- ASCII pattern matching with char column
CREATE TABLE t (col1 char(30));
INSERT INTO t VALUES ('Robert King');
SELECT * 
FROM t 
WHERE col1 LIKE '% King';   -- returns 1 row

-- Unicode pattern matching with nchar column
CREATE TABLE t (col1 nchar(30));
INSERT INTO t VALUES ('Robert King');
SELECT * 
FROM t 
WHERE col1 LIKE '% King';   -- no rows returned

-- Unicode pattern matching with nchar column and RTRIM
CREATE TABLE t (col1 nchar (30));
INSERT INTO t VALUES ('Robert King');
SELECT * 
FROM t 
WHERE RTRIM(col1) LIKE '% King';   -- returns 1 row

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the % Wildcard Character

USE AdventureWorks2012;
GO
SELECT Name
FROM sys.system_views
WHERE Name LIKE 'dm%';
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using LIKE with the % wildcard character

USE AdventureWorks2012;
GO
SELECT p.FirstName, p.LastName, ph.PhoneNumber
FROM Person.PersonPhone AS ph
INNER JOIN Person.Person AS p
ON ph.BusinessEntityID = p.BusinessEntityID
WHERE ph.PhoneNumber LIKE '415%'
ORDER by p.LastName;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using NOT LIKE with the % wildcard character

USE AdventureWorks2012;
GO
SELECT p.FirstName, p.LastName, ph.PhoneNumber
FROM Person.PersonPhone AS ph
INNER JOIN Person.Person AS p
ON ph.BusinessEntityID = p.BusinessEntityID
WHERE ph.PhoneNumber NOT LIKE '415%' AND p.FirstName = 'Gail'
ORDER BY p.LastName;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the ESCAPE clause

USE tempdb;
GO
IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
      WHERE TABLE_NAME = 'mytbl2')
   DROP TABLE mytbl2;
GO
USE tempdb;
GO
CREATE TABLE mytbl2
(
 c1 sysname
);
GO
INSERT mytbl2 VALUES ('Discount is 10-15% off'), ('Discount is .10-.15 off');
GO
SELECT c1 
FROM mytbl2
WHERE c1 LIKE '%10-15!% off%' ESCAPE '!';
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the [ ] wildcard characters

USE AdventureWorks2012;
GO
SELECT BusinessEntityID, FirstName, LastName 
FROM Person.Person 
WHERE FirstName LIKE '[CS]heryl';
GO

USE AdventureWorks2012;
GO
SELECT LastName, FirstName
FROM Person.Person
WHERE LastName LIKE 'Zh[ae]ng'
ORDER BY LastName ASC, FirstName ASC;
GO

-----------------------------------------------------------------------
-- OR https://msdn.microsoft.com/en-us/library/ms188361.aspx
-- boolean_expression OR boolean_expression

USE AdventureWorks2012;
GO
SELECT FirstName, LastName, Shift 
FROM HumanResources.vEmployeeDepartmentHistory
WHERE Department = 'Quality Assurance'
   AND (Shift = 'Evening' OR Shift = 'Night');
