-----------------------------------------------------------------------
-- INSERT https://msdn.microsoft.com/en-us/library/ms174335(v=sql.120).aspx
-- [ WITH <common_table_expression> [ ,...n ] ]
-- INSERT 
-- {
        -- [ TOP ( expression ) [ PERCENT ] ] 
        -- [ INTO ] 
        -- { <object> | rowset_function_limited 
          -- [ WITH ( <Table_Hint_Limited> [ ...n ] ) ]
        -- }
    -- {
        -- [ ( column_list ) ] 
        -- [ <OUTPUT Clause> ]
        -- { VALUES ( { DEFAULT | NULL | expression } [ ,...n ] ) [ ,...n     ] 
        -- | derived_table 
        -- | execute_statement
        -- | <dml_table_source>
        -- | DEFAULT VALUES 
        -- }
    -- }
-- }
-- [;]

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Inserting a single row of data

INSERT INTO Production.UnitMeasure
VALUES (N'FT', N'Feet', '20080414');

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Inserting multiple rows of data

INSERT INTO Production.UnitMeasure
VALUES (N'FT2', N'Square Feet ', '20080923'), (N'Y', N'Yards', '20080923'), (N'Y3', N'Cubic Yards', '20080923');

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Inserting data that is not in the same order as the table columns

INSERT INTO Production.UnitMeasure (Name, UnitMeasureCode, ModifiedDate)
VALUES (N'Square Yards', N'Y2', GETDATE());

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
--  Inserting data into a table with columns that have default values

IF OBJECT_ID ('dbo.T1', 'U') IS NOT NULL
    DROP TABLE dbo.T1;
GO
CREATE TABLE dbo.T1 
(
    column_1 AS 'Computed column ' + column_2, 
    column_2 varchar(30) 
        CONSTRAINT default_name DEFAULT ('my column default'),
    column_3 rowversion,
    column_4 varchar(40) NULL
);
GO
INSERT INTO dbo.T1 (column_4) 
    VALUES ('Explicit value');
INSERT INTO dbo.T1 (column_2, column_4) 
    VALUES ('Explicit value', 'Explicit value');
INSERT INTO dbo.T1 (column_2) 
    VALUES ('Explicit value');
INSERT INTO T1 DEFAULT VALUES; 
GO
SELECT column_1, column_2, column_3, column_4
FROM dbo.T1;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
--  Inserting data into a table with an identity column

IF OBJECT_ID ('dbo.T1', 'U') IS NOT NULL
    DROP TABLE dbo.T1;
GO
CREATE TABLE dbo.T1 ( column_1 int IDENTITY, column_2 VARCHAR(30));
GO
INSERT T1 VALUES ('Row #1');
INSERT T1 (column_2) VALUES ('Row #2');
GO
SET IDENTITY_INSERT T1 ON;
GO
INSERT INTO T1 (column_1,column_2) 
    VALUES (-99, 'Explicit identity value');
GO
SELECT column_1, column_2
FROM T1;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
--  Inserting data into a uniqueidentifier column by using NEWID()

IF OBJECT_ID ('dbo.T1', 'U') IS NOT NULL
    DROP TABLE dbo.T1;
GO
CREATE TABLE dbo.T1 
(
    column_1 int IDENTITY, 
    column_2 uniqueidentifier,
);
GO
INSERT INTO dbo.T1 (column_2) 
    VALUES (NEWID());
INSERT INTO T1 DEFAULT VALUES; 
GO
SELECT column_1, column_2
FROM dbo.T1;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
--  Inserting data into user-defined type columns

INSERT INTO dbo.Points (PointValue) VALUES (CONVERT(Point, '3,4'));
INSERT INTO dbo.Points (PointValue) VALUES (CONVERT(Point, '1,5'));
INSERT INTO dbo.Points (PointValue) VALUES (CAST ('1,99' AS Point));

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
--  Using the SELECT and EXECUTE options to insert data from other tables

IF OBJECT_ID ('dbo.EmployeeSales', 'U') IS NOT NULL
    DROP TABLE dbo.EmployeeSales;
GO
IF OBJECT_ID ('dbo.uspGetEmployeeSales', 'P') IS NOT NULL
    DROP PROCEDURE uspGetEmployeeSales;
GO
CREATE TABLE dbo.EmployeeSales
( DataSource   varchar(20) NOT NULL,
  BusinessEntityID   varchar(11) NOT NULL,
  LastName     varchar(40) NOT NULL,
  SalesDollars money NOT NULL
);
GO
CREATE PROCEDURE dbo.uspGetEmployeeSales 
AS 
    SET NOCOUNT ON;
    SELECT 'PROCEDURE', sp.BusinessEntityID, c.LastName, 
        sp.SalesYTD 
    FROM Sales.SalesPerson AS sp  
    INNER JOIN Person.Person AS c
        ON sp.BusinessEntityID = c.BusinessEntityID
    WHERE sp.BusinessEntityID LIKE '2%'
    ORDER BY sp.BusinessEntityID, c.LastName;
GO
--INSERT...SELECT example
INSERT INTO dbo.EmployeeSales
    SELECT 'SELECT', sp.BusinessEntityID, c.LastName, sp.SalesYTD 
    FROM Sales.SalesPerson AS sp
    INNER JOIN Person.Person AS c
        ON sp.BusinessEntityID = c.BusinessEntityID
    WHERE sp.BusinessEntityID LIKE '2%'
    ORDER BY sp.BusinessEntityID, c.LastName;
GO
--INSERT...EXECUTE procedure example
INSERT INTO dbo.EmployeeSales 
EXECUTE dbo.uspGetEmployeeSales;
GO
--INSERT...EXECUTE('string') example
INSERT INTO dbo.EmployeeSales 
EXECUTE 
('
SELECT ''EXEC STRING'', sp.BusinessEntityID, c.LastName, 
    sp.SalesYTD 
    FROM Sales.SalesPerson AS sp 
    INNER JOIN Person.Person AS c
        ON sp.BusinessEntityID = c.BusinessEntityID
    WHERE sp.BusinessEntityID LIKE ''2%''
    ORDER BY sp.BusinessEntityID, c.LastName
');
GO
--Show results.
SELECT DataSource,BusinessEntityID,LastName,SalesDollars
FROM dbo.EmployeeSales;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
--  Using WITH common table expression to define the data inserted

IF OBJECT_ID (N'HumanResources.NewEmployee', N'U') IS NOT NULL
    DROP TABLE HumanResources.NewEmployee;
GO
CREATE TABLE HumanResources.NewEmployee
(
    EmployeeID int NOT NULL,
    LastName nvarchar(50) NOT NULL,
    FirstName nvarchar(50) NOT NULL,
    PhoneNumber Phone NULL,
    AddressLine1 nvarchar(60) NOT NULL,
    City nvarchar(30) NOT NULL,
    State nchar(3) NOT NULL, 
    PostalCode nvarchar(15) NOT NULL,
    CurrentFlag Flag
);
GO
WITH EmployeeTemp (EmpID, LastName, FirstName, Phone, 
                   Address, City, StateProvince, 
                   PostalCode, CurrentFlag)
AS (SELECT 
       e.BusinessEntityID, c.LastName, c.FirstName, pp.PhoneNumber,
       a.AddressLine1, a.City, sp.StateProvinceCode, 
       a.PostalCode, e.CurrentFlag
    FROM HumanResources.Employee e
        INNER JOIN Person.BusinessEntityAddress AS bea
        ON e.BusinessEntityID = bea.BusinessEntityID
        INNER JOIN Person.Address AS a
        ON bea.AddressID = a.AddressID
        INNER JOIN Person.PersonPhone AS pp
        ON e.BusinessEntityID = pp.BusinessEntityID
        INNER JOIN Person.StateProvince AS sp
        ON a.StateProvinceID = sp.StateProvinceID
        INNER JOIN Person.Person as c
        ON e.BusinessEntityID = c.BusinessEntityID
    )
INSERT INTO HumanResources.NewEmployee 
    SELECT EmpID, LastName, FirstName, Phone, 
           Address, City, StateProvince, PostalCode, CurrentFlag
    FROM EmployeeTemp;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using TOP to limit the data inserted from the source table

IF OBJECT_ID ('dbo.EmployeeSales', 'U') IS NOT NULL
    DROP TABLE dbo.EmployeeSales;
GO
CREATE TABLE dbo.EmployeeSales
( EmployeeID   nvarchar(11) NOT NULL,
  LastName     nvarchar(20) NOT NULL,
  FirstName    nvarchar(20) NOT NULL,
  YearlySales  money NOT NULL
 );
GO
INSERT TOP(5)INTO dbo.EmployeeSales
    OUTPUT inserted.EmployeeID, inserted.FirstName, inserted.LastName, inserted.YearlySales
    SELECT sp.BusinessEntityID, c.LastName, c.FirstName, sp.SalesYTD 
    FROM Sales.SalesPerson AS sp
    INNER JOIN Person.Person AS c
        ON sp.BusinessEntityID = c.BusinessEntityID
    WHERE sp.SalesYTD > 250000--.00
    ORDER BY sp.SalesYTD DESC;
    
INSERT INTO dbo.EmployeeSales
    OUTPUT inserted.EmployeeID, inserted.FirstName, inserted.LastName, inserted.YearlySales
    SELECT TOP (5) sp.BusinessEntityID, c.LastName, c.FirstName, sp.SalesYTD 
    FROM Sales.SalesPerson AS sp
    INNER JOIN Person.Person AS c
        ON sp.BusinessEntityID = c.BusinessEntityID
    WHERE sp.SalesYTD > 250000.00
    ORDER BY sp.SalesYTD DESC;
    
--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Inserting data by specifying a view

IF OBJECT_ID ('dbo.T1', 'U') IS NOT NULL
    DROP TABLE dbo.T1;
GO
IF OBJECT_ID ('dbo.V1', 'V') IS NOT NULL
    DROP VIEW dbo.V1;
GO
CREATE TABLE T1 ( column_1 int, column_2 varchar(30));
GO
CREATE VIEW V1 AS 
SELECT column_2, column_1 
FROM T1;
GO
INSERT INTO V1 
    VALUES ('Row 1',1);
GO
SELECT column_1, column_2 
FROM T1;
GO
SELECT column_1, column_2
FROM V1;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Inserting data into a table variable

-- Create the table variable.
DECLARE @MyTableVar table(
    LocationID int NOT NULL,
    CostRate smallmoney NOT NULL,
    NewCostRate AS CostRate * 1.5,
    ModifiedDate datetime);

-- Insert values into the table variable.
INSERT INTO @MyTableVar (LocationID, CostRate, ModifiedDate)
    SELECT LocationID, CostRate, GETDATE() FROM Production.Location
    WHERE CostRate > 0;

-- View the table variable result set.
SELECT * FROM @MyTableVar;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Inserting data into a remote table by using a linked server

USE master;
GO
-- Create a link to the remote data source. 
-- Specify a valid server name for @datasrc as 'server_name' or 'server_name\instance_name'.

EXEC sp_addlinkedserver @server = N'MyLinkServer',
    @srvproduct = N' ',
    @provider = N'SQLNCLI', 
    @datasrc = N'server_name',
    @catalog = N'AdventureWorks2012';
GO

-- Specify the remote data source in the FROM clause using a four-part name 
-- in the form linked_server.catalog.schema.object.

INSERT INTO MyLinkServer.AdventureWorks2012.HumanResources.Department (Name, GroupName)
VALUES (N'Public Relations', N'Executive General and Administration');
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Inserting data into a remote table by using the OPENQUERY function

INSERT OPENQUERY (MyLinkServer, 'SELECT Name, GroupName FROM AdventureWorks2012.HumanResources.Department')
VALUES ('Environmental Impact', 'Engineering');
GO

INSERT INTO OPENDATASOURCE('SQLNCLI',
    'Data Source= <server_name>; Integrated Security=SSPI')
    .AdventureWorks2012.HumanResources.Department (Name, GroupName)
    VALUES (N'Standards and Methods', 'Quality Assurance');
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Inserting data into a heap with minimal logging

-- Create the target heap.
CREATE TABLE Sales.SalesHistory(
    SalesOrderID int NOT NULL,
    SalesOrderDetailID int NOT NULL,
    CarrierTrackingNumber nvarchar(25) NULL,
    OrderQty smallint NOT NULL,
    ProductID int NOT NULL,
    SpecialOfferID int NOT NULL,
    UnitPrice money NOT NULL,
    UnitPriceDiscount money NOT NULL,
    LineTotal money NOT NULL,
    rowguid uniqueidentifier /*ROWGUIDCOL*/  NOT NULL,
    ModifiedDate datetime NOT NULL );
GO
-- Temporarily set the recovery model to BULK_LOGGED.
ALTER DATABASE AdventureWorks2012
SET RECOVERY BULK_LOGGED;
GO
-- Transfer data from Sales.SalesOrderDetail to Sales.SalesHistory
INSERT INTO Sales.SalesHistory WITH (TABLOCK)
    (SalesOrderID, 
     SalesOrderDetailID,
     CarrierTrackingNumber, 
     OrderQty, 
     ProductID, 
     SpecialOfferID, 
     UnitPrice, 
     UnitPriceDiscount,
     LineTotal, 
     rowguid, 
     ModifiedDate)
SELECT * FROM Sales.SalesOrderDetail;
GO
-- Reset the recovery model.
ALTER DATABASE AdventureWorks2012
SET RECOVERY FULL;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the OPENROWSET function with BULK to bulk load data into a table

-- Use the OPENROWSET function to specify the data source and specifies the IGNORE_TRIGGERS table hint.
INSERT INTO HumanResources.Department WITH (IGNORE_TRIGGERS) (Name, GroupName)
SELECT b.Name, b.GroupName 
FROM OPENROWSET (
    BULK 'C:\SQLFiles\DepartmentData.txt',
    FORMATFILE = 'C:\SQLFiles\BulkloadFormatFile.xml',
    ROWS_PER_BATCH = 15000)AS b ;
    
--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the TABLOCK hint to specify a locking method

INSERT INTO Production.Location WITH (XLOCK)
(Name, CostRate, Availability)
VALUES ( N'Final Inventory', 15.00, 80.00);

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- A Using OUTPUT with an INSERT statement

DECLARE @MyTableVar table( NewScrapReasonID smallint,
                           Name varchar(50),
                           ModifiedDate datetime);
INSERT Production.ScrapReason
    OUTPUT INSERTED.ScrapReasonID, INSERTED.Name, INSERTED.ModifiedDate
        INTO @MyTableVar
VALUES (N'Operator error', GETDATE());

--Display the result set of the table variable.
SELECT NewScrapReasonID, Name, ModifiedDate FROM @MyTableVar;
--Display the result set of the table.
SELECT ScrapReasonID, Name, ModifiedDate 
FROM Production.ScrapReason;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using OUTPUT with identity and computed columns

IF OBJECT_ID ('dbo.EmployeeSales', 'U') IS NOT NULL
    DROP TABLE dbo.EmployeeSales;
GO
CREATE TABLE dbo.EmployeeSales
( EmployeeID   int IDENTITY (1,5)NOT NULL,
  LastName     nvarchar(20) NOT NULL,
  FirstName    nvarchar(20) NOT NULL,
  CurrentSales money NOT NULL,
  ProjectedSales AS CurrentSales * 1.10 
);
GO
DECLARE @MyTableVar table(
  LastName     nvarchar(20) NOT NULL,
  FirstName    nvarchar(20) NOT NULL,
  CurrentSales money NOT NULL
  );

INSERT INTO dbo.EmployeeSales (LastName, FirstName, CurrentSales)
  OUTPUT INSERTED.LastName, 
         INSERTED.FirstName, 
         INSERTED.CurrentSales
  INTO @MyTableVar
    SELECT c.LastName, c.FirstName, sp.SalesYTD
    FROM Sales.SalesPerson AS sp
    INNER JOIN Person.Person AS c
        ON sp.BusinessEntityID = c.BusinessEntityID
    WHERE sp.BusinessEntityID LIKE '2%'
    ORDER BY c.LastName, c.FirstName;

SELECT LastName, FirstName, CurrentSales
FROM @MyTableVar;
GO
SELECT EmployeeID, LastName, FirstName, CurrentSales, ProjectedSales
FROM dbo.EmployeeSales;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Inserting data returned from an OUTPUT clause
-- TODO: uncomment when merge_statement will be implemented.

-- IF OBJECT_ID(N'Production.ZeroInventory', N'U') IS NOT NULL
    -- DROP TABLE Production.ZeroInventory;
-- GO
-- --Create ZeroInventory table.
-- CREATE TABLE Production.ZeroInventory (DeletedProductID int, RemovedOnDate DateTime);
-- GO

-- INSERT INTO Production.ZeroInventory (DeletedProductID, RemovedOnDate)
-- SELECT ProductID, GETDATE()
-- FROM
-- (   MERGE Production.ProductInventory AS pi
    -- USING (SELECT ProductID, SUM(OrderQty) FROM Sales.SalesOrderDetail AS sod
           -- JOIN Sales.SalesOrderHeader AS soh
           -- ON sod.SalesOrderID = soh.SalesOrderID
           -- AND soh.OrderDate = '20070401'
           -- GROUP BY ProductID) AS src (ProductID, OrderQty)
    -- ON (pi.ProductID = src.ProductID)
    -- WHEN MATCHED AND pi.Quantity - src.OrderQty <= 0
        -- THEN DELETE
    -- WHEN MATCHED
        -- THEN UPDATE SET pi.Quantity = pi.Quantity - src.OrderQty
    -- OUTPUT $action, deleted.ProductID) AS Changes (Action, ProductID)
-- WHERE Action = 'DELETE';
-- IF @@ROWCOUNT = 0
-- PRINT 'Warning: No rows were inserted';
-- GO
-- SELECT DeletedProductID, RemovedOnDate FROM Production.ZeroInventory; 

