-----------------------------------------------------------------------
-- BEGIN END https://msdn.microsoft.com/en-us/library/ms190487.aspx

USE AdventureWorks2012;
GO
BEGIN TRANSACTION;
GO
IF @@TRANCOUNT = 0
BEGIN
    SELECT FirstName, MiddleName 
    FROM Person.Person WHERE LastName = 'Adams';
    ROLLBACK TRANSACTION;
    PRINT N'Rolling back the transaction two times would cause an error.';
END;
ROLLBACK TRANSACTION;
PRINT N'Rolled back the transaction.';
GO

-----------------------------------------------------------------------
-- BREAK https://msdn.microsoft.com/en-us/library/ms181271.aspx

-----------------------------------------------------------------------
-- CONTINUE https://msdn.microsoft.com/en-us/library/ms174366.aspx

-----------------------------------------------------------------------
-- GOTO

DECLARE @Counter int;
SET @Counter = 1;
WHILE @Counter < 10
BEGIN 
    SELECT @Counter
    SET @Counter = @Counter + 1
    IF @Counter = 4 GOTO Branch_One --Jumps to the first branch.
    IF @Counter = 5 GOTO Branch_Two  --This will never execute.
END
Branch_One:
    SELECT 'Jumping To Branch One.'
    GOTO Branch_Three; --This will prevent Branch_Two from executing.
Branch_Two:
    SELECT 'Jumping To Branch Two.'
Branch_Three:
    SELECT 'Jumping To Branch Three.';

-----------------------------------------------------------------------
-- IF ELSE https://msdn.microsoft.com/en-us/library/ms182587.aspx

DECLARE @compareprice money, @cost money 
EXECUTE Production.uspGetList '%Bikes%', 700, 
    @compareprice OUT, 
    @cost OUTPUT
IF @cost <= @compareprice 
BEGIN
    PRINT 'These products can be purchased for less than 
    $'+RTRIM(CAST(@compareprice AS varchar(20)))+'.'
END
ELSE
    PRINT 'The prices for all products in this category exceed 
    $'+ RTRIM(CAST(@compareprice AS varchar(20)))+'.'

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using a query as part of a Boolean expression

IF 1 = 1 PRINT 'Boolean_expression is true.'
ELSE PRINT 'Boolean_expression is false.' ;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using a simple Boolean expression

USE AdventureWorks2012;
GO
IF 
(SELECT COUNT(*) FROM Production.Product WHERE Name LIKE 'Touring-3000%' ) > 5
PRINT 'There are more than 5 Touring-3000 bicycles.'
ELSE PRINT 'There are 5 or less Touring-3000 bicycles.' ;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using a statement block

USE AdventureWorks2012;
GO
DECLARE @AvgWeight decimal(8,2), @BikeCount int
IF 
(SELECT COUNT(*) FROM Production.Product WHERE Name LIKE 'Touring-3000%' ) > 5
BEGIN
   SET @BikeCount = 
        (SELECT COUNT(*) 
         FROM Production.Product 
         WHERE Name LIKE 'Touring-3000%');
   SET @AvgWeight = 
        (SELECT AVG(Weight) 
         FROM Production.Product 
         WHERE Name LIKE 'Touring-3000%');
   PRINT 'There are ' + CAST(@BikeCount AS varchar(3)) + ' Touring-3000 bikes.'
   PRINT 'The average weight of the top 5 Touring-3000 bikes is ' + CAST(@AvgWeight AS varchar(8)) + '.';
END
ELSE 
BEGIN
SET @AvgWeight = 
        (SELECT AVG(Weight)
         FROM Production.Product 
         WHERE Name LIKE 'Touring-3000%' );
   PRINT 'Average weight of the Touring-3000 bikes is ' + CAST(@AvgWeight AS varchar(8)) + '.' ;
END ;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using nested IF...ELSE statements

DECLARE @Number int;
SET @Number = 50;
IF @Number > 100
   PRINT 'The number is large.';
ELSE 
   BEGIN
      IF @Number < 10
      PRINT 'The number is small.';
   ELSE
      PRINT 'The number is medium.';
   END ;
GO

-----------------------------------------------------------------------
-- RETURN https://msdn.microsoft.com/en-us/library/ms174998.aspx

CREATE PROCEDURE findjobs @nm sysname = NULL
AS 
IF @nm IS NULL
    BEGIN
        PRINT 'You must give a user name'
        RETURN
    END
ELSE
    BEGIN
        SELECT o.name, o.id, o.uid
        FROM sysobjects o INNER JOIN master..syslogins l
            ON o.uid = l.sid
        WHERE l.name = @nm
    END;

-----------------------------------------------------------------------
-- THROW https://msdn.microsoft.com/en-us/library/ee677615.aspx

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using THROW to raise an exception

THROW 51000, 'The record does not exist.', 1;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using THROW to raise an exception again

USE tempdb;
GO
CREATE TABLE dbo.TestRethrow
(    ID INT PRIMARY KEY
);
BEGIN TRY
    INSERT dbo.TestRethrow(ID) VALUES(1);
--  Force error 2627, Violation of PRIMARY KEY constraint to be raised.
    INSERT dbo.TestRethrow(ID) VALUES(1);
END TRY
BEGIN CATCH

    PRINT 'In catch block.';
    THROW;
END CATCH;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using FORMATMESSAGE with THROW

EXEC sys.sp_addmessage
     @msgnum   = 60000
,@severity = 16
,@msgtext  = N'This is a test message with one numeric parameter (%d), one string parameter (%s), and another string parameter (%s).'
    ,@lang = 'us_english'; 
GO

DECLARE @msg NVARCHAR(2048) = FORMATMESSAGE(60000, 500, N'First string', N'second string'); 

THROW 60000, @msg, 1; 

-----------------------------------------------------------------------
-- TRY CATCH https://msdn.microsoft.com/en-us/library/ms175976.aspx

-- Verify that the stored procedure does not already exist.
IF OBJECT_ID ( 'usp_GetErrorInfo', 'P' ) IS NOT NULL 
    DROP PROCEDURE usp_GetErrorInfo;
GO

-- Create procedure to retrieve error information.
CREATE PROCEDURE usp_GetErrorInfo
AS
SELECT
    ERROR_NUMBER() AS ErrorNumber
    ,ERROR_SEVERITY() AS ErrorSeverity
    ,ERROR_STATE() AS ErrorState
    ,ERROR_PROCEDURE() AS ErrorProcedure
    ,ERROR_LINE() AS ErrorLine
    ,ERROR_MESSAGE() AS ErrorMessage;
GO

BEGIN TRY
    -- Generate divide-by-zero error.
    SELECT 1/0;
END TRY
BEGIN CATCH
    -- Execute error retrieval routine.
    EXECUTE usp_GetErrorInfo;
END CATCH; 

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using TRY…CATCH

BEGIN TRY
    -- Generate a divide-by-zero error.
    SELECT 1/0;
END TRY
BEGIN CATCH
    SELECT
        ERROR_NUMBER() AS ErrorNumber
        ,ERROR_SEVERITY() AS ErrorSeverity
        ,ERROR_STATE() AS ErrorState
        ,ERROR_PROCEDURE() AS ErrorProcedure
        ,ERROR_LINE() AS ErrorLine
        ,ERROR_MESSAGE() AS ErrorMessage;
END CATCH;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using TRY…CATCH in a transaction

BEGIN TRANSACTION;

BEGIN TRY
    -- Generate a constraint violation error.
    DELETE FROM Production.Product
    WHERE ProductID = 980;
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

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using TRY…CATCH with XACT_STATE

-- Check to see whether this stored procedure exists.
IF OBJECT_ID (N'usp_GetErrorInfo', N'P') IS NOT NULL
    DROP PROCEDURE usp_GetErrorInfo;
GO

-- Create procedure to retrieve error information.
CREATE PROCEDURE usp_GetErrorInfo
AS
    SELECT 
         ERROR_NUMBER() AS ErrorNumber
        ,ERROR_SEVERITY() AS ErrorSeverity
        ,ERROR_STATE() AS ErrorState
        ,ERROR_LINE () AS ErrorLine
        ,ERROR_PROCEDURE() AS ErrorProcedure
        ,ERROR_MESSAGE() AS ErrorMessage;
GO

-- SET XACT_ABORT ON will cause the transaction to be uncommittable
-- when the constraint violation occurs.
SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;
        -- A FOREIGN KEY constraint exists on this table. This 
        -- statement will generate a constraint violation error.
        DELETE FROM Production.Product
            WHERE ProductID = 980;

    -- If the DELETE statement succeeds, commit the transaction.
    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    -- Execute error retrieval routine.
    EXECUTE usp_GetErrorInfo;

    -- Test XACT_STATE:
        -- If 1, the transaction is committable.
        -- If -1, the transaction is uncommittable and should 
        --     be rolled back.
        -- XACT_STATE = 0 means that there is no transaction and
        --     a commit or rollback operation would generate an error.

    -- Test whether the transaction is uncommittable.
    IF (XACT_STATE()) = -1
    BEGIN
        PRINT
            N'The transaction is in an uncommittable state.' +
            'Rolling back transaction.'
        ROLLBACK TRANSACTION;
    END;

    -- Test whether the transaction is committable.
    IF (XACT_STATE()) = 1
    BEGIN
        PRINT
            N'The transaction is committable.' +
            'Committing transaction.'
        COMMIT TRANSACTION;   
    END;
END CATCH;
GO

-----------------------------------------------------------------------
-- WAITFOR https://msdn.microsoft.com/en-us/library/ms187331.aspx

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using WAITFOR TIME

EXECUTE sp_add_job @job_name = 'TestJob';
BEGIN
    WAITFOR TIME '22:20';
    EXECUTE sp_update_job @job_name = 'TestJob',
        @new_name = 'UpdatedJob';
END;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using WAITFOR DELAY

BEGIN
    WAITFOR DELAY '02:00';
    EXECUTE sp_helpdb;
END;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using WAITFOR DELAY with a local variable

IF OBJECT_ID('dbo.TimeDelay_hh_mm_ss','P') IS NOT NULL
    DROP PROCEDURE dbo.TimeDelay_hh_mm_ss;
GO
CREATE PROCEDURE dbo.TimeDelay_hh_mm_ss 
    (
    @DelayLength char(8)= '00:00:00'
    )
AS
DECLARE @ReturnInfo varchar(255)
IF ISDATE('2000-01-01 ' + @DelayLength + '.000') = 0
    BEGIN
        SELECT @ReturnInfo = 'Invalid time ' + @DelayLength 
        + ',hh:mm:ss, submitted.';
        -- This PRINT statement is for testing, not use in production.
        PRINT @ReturnInfo 
        RETURN(1)
    END
BEGIN
    WAITFOR DELAY @DelayLength
    SELECT @ReturnInfo = 'A total time of ' + @DelayLength + ', 
        hh:mm:ss, has elapsed! Your time is up.'
    -- This PRINT statement is for testing, not use in production.
    PRINT @ReturnInfo;
END;
GO
/* This statement executes the dbo.TimeDelay_hh_mm_ss procedure. */
EXEC TimeDelay_hh_mm_ss '00:00:10';
GO

-- waitfor with receive statement
WAITFOR (
  RECEIVE *
  FROM ExpenseQueue);

-- waitfor with receive statement and timeout
WAITFOR (
  RECEIVE *
  FROM ExpenseQueue ), TIMEOUT 60000;

-- waitfor with receive statement containing column select
DECLARE @ConversationHandle uniqueidentifier;
WAITFOR (
  RECEIVE TOP (1)
  @ConversationHandle = conversation_handle
  FROM dbo.ReplicationInboundQueue), TIMEOUT 3000;

-----------------------------------------------------------------------
-- WHILE https://msdn.microsoft.com/en-us/library/ms178642.aspx

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using BREAK and CONTINUE with nested IF...ELSE and WHILE

USE AdventureWorks2012;
GO
WHILE (SELECT AVG(ListPrice) FROM Production.Product) < $300
BEGIN
   UPDATE Production.Product
      SET ListPrice = ListPrice * 2
   SELECT MAX(ListPrice) FROM Production.Product
   IF (SELECT MAX(ListPrice) FROM Production.Product) > $500
      BREAK
   ELSE
      CONTINUE
END
PRINT 'Too much for the market to bear';

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Using WHILE in a cursor

DECLARE Employee_Cursor CURSOR FOR
SELECT EmployeeID, Title 
FROM AdventureWorks2012.HumanResources.Employee
WHERE JobTitle = 'Marketing Specialist';
OPEN Employee_Cursor;
FETCH NEXT FROM Employee_Cursor;
WHILE @@FETCH_STATUS = 0
   BEGIN
      FETCH NEXT FROM Employee_Cursor;
   END;
CLOSE Employee_Cursor;
DEALLOCATE Employee_Cursor;
GO


