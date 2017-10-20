-- UPDATE https://msdn.microsoft.com/en-us/library/ms177523.aspx
-- https://msdn.microsoft.com/en-us/library/ms177523.aspx
-- [ WITH <common_table_expression> [...n] ]
-- UPDATE 
    -- [ TOP ( expression ) [ PERCENT ] ] 
    -- { { table_alias | <object> | rowset_function_limited 
         -- [ WITH ( <Table_Hint_Limited> [ ...n ] ) ]
      -- }
      -- | @table_variable    
    -- }
    -- SET
        -- { column_name = { expression | DEFAULT | NULL }
          -- | { udt_column_name.{ { property_name = expression
                                -- | field_name = expression }
                                -- | method_name ( argument [ ,...n ] )
                              -- }
          -- }
          -- | column_name { .WRITE ( expression , @Offset , @Length ) }
          -- | @variable = expression
          -- | @variable = column = expression
          -- | column_name { += | -= | *= | /= | %= | &= | ^= | |= } expression
          -- | @variable { += | -= | *= | /= | %= | &= | ^= | |= } expression
          -- | @variable = column { += | -= | *= | /= | %= | &= | ^= | |= } expression
        -- } [ ,...n ] 

    -- [ <OUTPUT Clause> ]
    -- [ FROM{ <table_source> } [ ,...n ] ] 
    -- [ WHERE { <search_condition> 
            -- | { [ CURRENT OF 
                  -- { { [ GLOBAL ] cursor_name } 
                      -- | cursor_variable_name 
                  -- } 
                -- ]
              -- }
            -- } 
    -- ] 
    -- [ OPTION ( <query_hint> [ ,...n ] ) ]
-- [ ; ]

-- <object> ::=
-- { 
    -- [ server_name . database_name . schema_name . 
    -- | database_name .[ schema_name ] . 
    -- | schema_name .
    -- ]
    -- table_or_view_name}
    
USE AdventureWorks2012;
GO
IF OBJECT_ID ('dbo.Table1', 'U') IS NOT NULL
    DROP TABLE dbo.Table1;
GO
IF OBJECT_ID ('dbo.Table2', 'U') IS NOT NULL
    DROP TABLE dbo.Table2;
GO
CREATE TABLE dbo.Table1 
    (ColA int NOT NULL, ColB decimal(10,3) NOT NULL);
GO
CREATE TABLE dbo.Table2 
    (ColA int NOT NULL PRIMARY KEY, ColB decimal(10,3) NOT NULL);
GO
INSERT INTO dbo.Table1 VALUES(1, 10.0), (1, 20.0);
INSERT INTO dbo.Table2 VALUES(1, 0.0);
GO
UPDATE dbo.Table2 
SET dbo.Table2.ColB = dbo.Table2.ColB + dbo.Table1.ColB
FROM dbo.Table2 
    INNER JOIN dbo.Table1 
    ON (dbo.Table2.ColA = dbo.Table1.ColA);
GO
SELECT ColA, ColB 
FROM dbo.Table2;

USE tempdb;
GO
-- UPDATE statement with CTE references that are correctly matched.
DECLARE @x TABLE (ID int, Value int);
DECLARE @y TABLE (ID int, Value int);
INSERT @x VALUES (1, 10), (2, 20);
INSERT @y VALUES (1, 100),(2, 200);

WITH cte AS (SELECT * FROM @x)
UPDATE x -- cte is referenced by the alias.
SET Value = y.Value
FROM cte AS x  -- cte is assigned an alias.
INNER JOIN @y AS y ON y.ID = x.ID;
SELECT * FROM @x;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using a simple UPDATE statement

USE AdventureWorks2012;
GO
UPDATE Person.Address
SET ModifiedDate = GETDATE();

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Updating multiple columns

USE AdventureWorks2012;
GO
UPDATE Sales.SalesPerson
SET Bonus = 6000, CommissionPct = .10, SalesQuota = NULL;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the WHERE clause

USE AdventureWorks2012;
GO
UPDATE Production.Product
SET Color = N'Metallic Red'
WHERE Name LIKE N'Road-250%' AND Color = N'Red';
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the TOP clause

USE AdventureWorks2012;
GO
UPDATE Production.Product
SET Color = N'Metallic Red'
WHERE Name LIKE N'Road-250%' AND Color = N'Red';
GO

UPDATE HumanResources.Employee
SET VacationHours = VacationHours + 8
FROM (SELECT TOP 10 BusinessEntityID FROM HumanResources.Employee
     ORDER BY HireDate ASC) AS th
WHERE HumanResources.Employee.BusinessEntityID = th.BusinessEntityID;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the WITH common_table_expression clause

USE AdventureWorks2012;
GO
WITH Parts(AssemblyID, ComponentID, PerAssemblyQty, EndDate, ComponentLevel) AS
(
    SELECT b.ProductAssemblyID, b.ComponentID, b.PerAssemblyQty,
        b.EndDate, 0 AS ComponentLevel
    FROM Production.BillOfMaterials AS b
    WHERE b.ProductAssemblyID = 800
          AND b.EndDate IS NULL
    UNION ALL
    SELECT bom.ProductAssemblyID, bom.ComponentID, p.PerAssemblyQty,
        bom.EndDate, ComponentLevel + 1
    FROM Production.BillOfMaterials AS bom 
        INNER JOIN Parts AS p
        ON bom.ProductAssemblyID = p.ComponentID
        AND bom.EndDate IS NULL
)
UPDATE Production.BillOfMaterials
SET PerAssemblyQty = c.PerAssemblyQty * 2
FROM Production.BillOfMaterials AS c
JOIN Parts AS d ON c.ProductAssemblyID = d.AssemblyID
WHERE d.ComponentLevel = 0; 

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the WHERE CURRENT OF clause

USE AdventureWorks2012;
GO
DECLARE complex_cursor CURSOR FOR
    SELECT a.BusinessEntityID
    FROM HumanResources.EmployeePayHistory AS a
    WHERE RateChangeDate <> 
         (SELECT MAX(RateChangeDate)
          FROM HumanResources.EmployeePayHistory AS b
          WHERE a.BusinessEntityID = b.BusinessEntityID) ;
OPEN complex_cursor;
FETCH FROM complex_cursor;
UPDATE HumanResources.EmployeePayHistory
SET PayFrequency = 2 
WHERE CURRENT OF complex_cursor;
CLOSE complex_cursor;
DEALLOCATE complex_cursor;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Specifying a computed value

USE AdventureWorks2012 ;
GO
UPDATE Production.Product
SET ListPrice = ListPrice * 2;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Specifying a compound operator

USE AdventureWorks2012;
GO
DECLARE @NewPrice int = 10;
UPDATE Production.Product
SET ListPrice += @NewPrice
WHERE Color = N'Red';
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Specifying a subquery in the SET clause

USE AdventureWorks2012;
GO
UPDATE Sales.SalesPerson
SET SalesYTD = SalesYTD + 
    (SELECT SUM(so.SubTotal) 
     FROM Sales.SalesOrderHeader AS so
     WHERE so.OrderDate = (SELECT MAX(OrderDate)
                           FROM Sales.SalesOrderHeader AS so2
                           WHERE so2.SalesPersonID = so.SalesPersonID)
     AND Sales.SalesPerson.BusinessEntityID = so.SalesPersonID
     GROUP BY so.SalesPersonID);
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Updating rows using DEFAULT values

USE AdventureWorks2012;
GO
UPDATE Production.Location
SET CostRate = DEFAULT
WHERE CostRate > 20.00;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Specifying a view as the target object

USE AdventureWorks2012;
GO
UPDATE Person.vStateProvinceCountryRegion
SET CountryRegionName = 'United States of America'
WHERE CountryRegionName = 'United States';

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Specifying a table alias as the target object

USE AdventureWorks2012;
GO
UPDATE sr
SET sr.Name += ' - tool malfunction'
FROM Production.ScrapReason AS sr
JOIN Production.WorkOrder AS wo 
     ON sr.ScrapReasonID = wo.ScrapReasonID
     AND wo.ScrappedQty > 300;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
--  Specifying a table variable as the target object

USE AdventureWorks2012;
GO
-- Create the table variable.
DECLARE @MyTableVar table(
    EmpID int NOT NULL,
    NewVacationHours int,
    ModifiedDate datetime);

-- Populate the table variable with employee ID values from HumanResources.Employee.
INSERT INTO @MyTableVar (EmpID)
    SELECT BusinessEntityID FROM HumanResources.Employee;

-- Update columns in the table variable.
UPDATE @MyTableVar
SET NewVacationHours = e.VacationHours + 20,
    ModifiedDate = GETDATE()
FROM HumanResources.Employee AS e 
WHERE e.BusinessEntityID = EmpID;

-- Display the results of the UPDATE statement.
SELECT EmpID, NewVacationHours, ModifiedDate FROM @MyTableVar
ORDER BY EmpID;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using the UPDATE statement with information from another table

USE AdventureWorks2012;
GO
UPDATE Sales.SalesPerson
SET SalesYTD = SalesYTD + SubTotal
FROM Sales.SalesPerson AS sp
JOIN Sales.SalesOrderHeader AS so
    ON sp.BusinessEntityID = so.SalesPersonID
    AND so.OrderDate = (SELECT MAX(OrderDate)
                        FROM Sales.SalesOrderHeader
                        WHERE SalesPersonID = sp.BusinessEntityID);
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Updating data in a remote table by using a linked server

USE master;
GO
-- Create a link to the remote data source. 
-- Specify a valid server name for @datasrc as 'server_name' or 'server_name\instance_name'.

EXEC sp_addlinkedserver @server = N'MyLinkServer',
    @srvproduct = N' ',
    @provider = N'SQLNCLI10', 
    @datasrc = N'<server name>',
    @catalog = N'AdventureWorks2012';
GO
USE AdventureWorks2012;
GO
-- Specify the remote data source using a four-part name 
-- in the form linked_server.catalog.schema.object.

UPDATE MyLinkServer.AdventureWorks2012.HumanResources.Department
SET GroupName = N'Public Relations'
WHERE DepartmentID = 4;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Updating data in a remote table by using the OPENQUERY function

UPDATE OPENQUERY (MyLinkServer, 'SELECT GroupName FROM HumanResources.Department WHERE DepartmentID = 4') 
SET GroupName = 'Sales and Marketing';

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Updating data in a remote table by using the OPENDATASOURCE function

UPDATE OPENQUERY (MyLinkServer, 'SELECT GroupName FROM HumanResources.Department WHERE DepartmentID = 4') 
SET GroupName = 'Sales and Marketing';

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using UPDATE with .WRITE to modify data in an nvarchar(max) column
-- TODO: .WRITE

-- USE AdventureWorks2012;
-- GO
-- DECLARE @MyTableVar table (
    -- SummaryBefore nvarchar(max),
    -- SummaryAfter nvarchar(max));
-- UPDATE Production.Document
-- SET DocumentSummary .WRITE (N'features',28,10)
-- OUTPUT deleted.DocumentSummary, 
       -- inserted.DocumentSummary 
    -- INTO @MyTableVar
-- WHERE Title = N'Front Reflector Bracket Installation';
-- SELECT SummaryBefore, SummaryAfter 
-- FROM @MyTableVar;
-- GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using UPDATE with .WRITE to modify data in an nvarchar(max) column
-- TODO: .WRITE

-- USE AdventureWorks2012;
-- GO
-- -- Replacing NULL value with temporary data.
-- UPDATE Production.Document
-- SET DocumentSummary = N'Replacing NULL value'
-- WHERE Title = N'Crank Arm and Tire Maintenance';
-- GO
-- SELECT DocumentSummary 
-- FROM Production.Document
-- WHERE Title = N'Crank Arm and Tire Maintenance';
-- GO
-- -- Replacing temporary data with the correct data. Setting @Length to NULL 
-- -- truncates all existing data from the @Offset position.
-- UPDATE Production.Document
-- SET DocumentSummary .WRITE(N'Carefully inspect and maintain the tires and crank arms.',0,NULL)
-- WHERE Title = N'Crank Arm and Tire Maintenance';
-- GO
-- SELECT DocumentSummary 
-- FROM Production.Document
-- WHERE Title = N'Crank Arm and Tire Maintenance';
-- GO
-- -- Appending additional data to the end of the column by setting 
-- -- @Offset to NULL.
-- UPDATE Production.Document
-- SET DocumentSummary .WRITE (N' Appending data to the end of the column.', NULL, 0)
-- WHERE Title = N'Crank Arm and Tire Maintenance';
-- GO
-- SELECT DocumentSummary 
-- FROM Production.Document
-- WHERE Title = N'Crank Arm and Tire Maintenance';
-- GO
-- -- Removing all data from @Offset to the end of the existing value by 
-- -- setting expression to NULL. 
-- UPDATE Production.Document
-- SET DocumentSummary .WRITE (NULL, 56, 0)
-- WHERE Title = N'Crank Arm and Tire Maintenance';
-- GO
-- SELECT DocumentSummary 
-- FROM Production.Document
-- WHERE Title = N'Crank Arm and Tire Maintenance';
-- GO
-- -- Removing partial data beginning at position 9 and ending at 
-- -- position 21.
-- UPDATE Production.Document
-- SET DocumentSummary .WRITE ('',9, 12)
-- WHERE Title = N'Crank Arm and Tire Maintenance';
-- GO
-- SELECT DocumentSummary 
-- FROM Production.Document
-- WHERE Title = N'Crank Arm and Tire Maintenance';
-- GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using UPDATE with OPENROWSET to modify a varbinary(max) column

USE AdventureWorks2012;
GO
UPDATE Production.ProductPhoto
SET ThumbNailPhoto = (
    SELECT *
    FROM OPENROWSET(BULK 'c:\Tires.jpg', SINGLE_BLOB) AS x )
WHERE ProductPhotoID = 1;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using UPDATE to modify FILESTREAM data

UPDATE Archive.dbo.Records
SET [Chart] = CAST('Xray 1' as varbinary(max))
WHERE [SerialNumber] = 2;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using a system data type

UPDATE dbo.Cities
SET Location = CONVERT(Point, '12.3:46.2')
WHERE Name = 'Anchorage';

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Invoking a method

UPDATE dbo.Cities
SET Location.SetXY(23.5, 23.5)
WHERE Name = 'Anchorage';

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Modifying the value of a property or data member

UPDATE dbo.Cities
SET Location.X = 23.5
WHERE Name = 'Anchorage';

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Specifying a table hint

USE AdventureWorks2012;
GO
UPDATE Production.Product
WITH (TABLOCK)
SET ListPrice = ListPrice * 1.10
WHERE ProductNumber LIKE 'BK-%';
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Specifying a query hint

USE AdventureWorks2012;
GO
CREATE PROCEDURE Production.uspProductUpdate
@Product nvarchar(25)
AS
SET NOCOUNT ON;
UPDATE Production.Product
SET ListPrice = ListPrice * 1.10
WHERE ProductNumber LIKE @Product
OPTION (OPTIMIZE FOR (@Product = 'BK-%') );
GO
-- Execute the stored procedure 
EXEC Production.uspProductUpdate 'BK-%';

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using UPDATE with the OUTPUT clause

USE AdventureWorks2012;
GO
DECLARE @MyTableVar table(
    EmpID int NOT NULL,
    OldVacationHours int,
    NewVacationHours int,
    ModifiedDate datetime);
UPDATE TOP (10) HumanResources.Employee
SET VacationHours = VacationHours * 1.25,
    ModifiedDate = GETDATE() 
OUTPUT inserted.BusinessEntityID,
       deleted.VacationHours,
       inserted.VacationHours,
       inserted.ModifiedDate
INTO @MyTableVar;
--Display the result set of the table variable.
SELECT EmpID, OldVacationHours, NewVacationHours, ModifiedDate
FROM @MyTableVar;
GO
--Display the result set of the table.
SELECT TOP (10) BusinessEntityID, VacationHours, ModifiedDate
FROM HumanResources.Employee;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using UPDATE in a stored procedure

USE AdventureWorks2012;
GO
CREATE PROCEDURE HumanResources.Update_VacationHours
@NewHours smallint
AS 
SET NOCOUNT ON;
UPDATE HumanResources.Employee
SET VacationHours = 
    ( CASE
         WHEN SalariedFlag = 0 THEN VacationHours + @NewHours
         ELSE @NewHours
       END
    )
WHERE CurrentFlag = 1;
GO

EXEC HumanResources.Update_VacationHours 40;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using UPDATE in a TRY…CATCH Block

USE AdventureWorks2012;
GO
BEGIN TRANSACTION;

BEGIN TRY
    -- Intentionally generate a constraint violation error.
    UPDATE HumanResources.Department
    SET Name = N'MyNewName'
    WHERE DepartmentID BETWEEN 1 AND 2;
END TRY
BEGIN CATCH
    SELECT 
         ERROR_NUMBER() AS ErrorNumber
        ,ERROR_SEVERITY() AS ErrorSeverity
        ,ERROR_STATE() AS ErrorState
        ,ERROR_PROCEDURE() AS ErrorProcedure
        ,ERROR_LINE() AS ErrorLine
        ,ERROR_MESSAGE() AS ErrorMessage;

    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;
END CATCH;

IF @@TRANCOUNT > 0
    COMMIT TRANSACTION;
GO

-- ROWCOUNT
SET ROWCOUNT 4;
SELECT *
FROM Production.ProductInventory
WHERE Quantity < 300;
GO
