-----------------------------------------------------------------------
-- BEGIN DISTRIBUTED TRANSACTION https://msdn.microsoft.com/en-us/library/ms188386.aspx
-- BEGIN DISTRIBUTED { TRAN | TRANSACTION } 
     -- [ transaction_name | @tran_name_variable ] 
-- [ ; ]

USE AdventureWorks2012;
GO
BEGIN DISTRIBUTED TRANSACTION;
-- Delete candidate from local instance.
DELETE AdventureWorks2012.HumanResources.JobCandidate
    WHERE JobCandidateID = 13;
-- Delete candidate from remote instance.
DELETE RemoteServer.AdventureWorks2012.HumanResources.JobCandidate
    WHERE JobCandidateID = 13;
COMMIT TRANSACTION;
GO

-----------------------------------------------------------------------
-- BEGIN TRANSACTION https://msdn.microsoft.com/en-us/library/ms188929.aspx
-- BEGIN { TRAN | TRANSACTION } 
    -- [ { transaction_name | @tran_name_variable }
      -- [ WITH MARK [ 'description' ] ]
    -- ]
-- [ ; ]

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Naming a transaction

DECLARE @TranName VARCHAR(20);
SELECT @TranName = 'MyTransaction';

BEGIN TRANSACTION @TranName;
USE AdventureWorks2012;
DELETE FROM AdventureWorks2012.HumanResources.JobCandidate
    WHERE JobCandidateID = 13;

COMMIT TRANSACTION @TranName;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Marking a transaction

BEGIN TRANSACTION CandidateDelete
    WITH MARK N'Deleting a Job Candidate';
GO
USE AdventureWorks2012;
GO
DELETE FROM AdventureWorks2012.HumanResources.JobCandidate
    WHERE JobCandidateID = 13;
GO
COMMIT TRANSACTION CandidateDelete;
GO

-----------------------------------------------------------------------
-- COMMIT TRANSACTION https://msdn.microsoft.com/en-us/library/ms190295.aspx
-- COMMIT [ { TRAN | TRANSACTION } [ transaction_name | @tran_name_variable ] ]
--        [ WITH ( DELAYED_DURABILITY = { OFF | ON } ) ]

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Committing a transaction

USE AdventureWorks2012;
GO
BEGIN TRANSACTION;
GO
DELETE FROM HumanResources.JobCandidate
    WHERE JobCandidateID = 13;
GO
COMMIT TRANSACTION;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Committing a nested transaction

USE AdventureWorks2012;
GO
IF OBJECT_ID(N'TestTran',N'U') IS NOT NULL
    DROP TABLE TestTran;
GO
CREATE TABLE TestTran (Cola int PRIMARY KEY, Colb char(3));
GO
-- This statement sets @@TRANCOUNT to 1.
BEGIN TRANSACTION OuterTran;
GO
PRINT N'Transaction count after BEGIN OuterTran = '
    + CAST(@@TRANCOUNT AS nvarchar(10));
GO
INSERT INTO TestTran VALUES (1, 'aaa');
GO
-- This statement sets @@TRANCOUNT to 2.
BEGIN TRANSACTION Inner1;
GO
PRINT N'Transaction count after BEGIN Inner1 = '
    + CAST(@@TRANCOUNT AS nvarchar(10));
GO
INSERT INTO TestTran VALUES (2, 'bbb');
GO
-- This statement sets @@TRANCOUNT to 3.
BEGIN TRANSACTION Inner2;
GO
PRINT N'Transaction count after BEGIN Inner2 = '
    + CAST(@@TRANCOUNT AS nvarchar(10));
GO
INSERT INTO TestTran VALUES (3, 'ccc');
GO
-- This statement decrements @@TRANCOUNT to 2.
-- Nothing is committed.
COMMIT TRANSACTION Inner2;
GO
PRINT N'Transaction count after COMMIT Inner2 = '
    + CAST(@@TRANCOUNT AS nvarchar(10));
GO
-- This statement decrements @@TRANCOUNT to 1.
-- Nothing is committed.
COMMIT TRANSACTION Inner1;
GO
PRINT N'Transaction count after COMMIT Inner1 = '
    + CAST(@@TRANCOUNT AS nvarchar(10));
GO
-- This statement decrements @@TRANCOUNT to 0 and
-- commits outer transaction OuterTran.
COMMIT TRANSACTION OuterTran;
GO
PRINT N'Transaction count after COMMIT OuterTran = '
    + CAST(@@TRANCOUNT AS nvarchar(10));
GO

-----------------------------------------------------------------------
-- ROLLBACK TRANSACTION https://msdn.microsoft.com/en-us/library/ms181299.aspx
-- ROLLBACK { TRAN | TRANSACTION } 
     -- [ transaction_name | @tran_name_variable
     -- | savepoint_name | @savepoint_variable ] 
-- [ ; ]

USE tempdb;
GO
CREATE TABLE ValueTable ([value] int)
GO

DECLARE @TransactionName varchar(20) = 'Transaction1';

--The following statements start a named transaction,
--insert two rows, and then roll back
--the transaction named in the variable @TransactionName.
--Another statement outside of the named transaction inserts two rows.
--The query returns the results of the previous statements.

BEGIN TRAN @TransactionName
       INSERT INTO ValueTable VALUES(1), (2);
ROLLBACK TRAN @TransactionName;

INSERT INTO ValueTable VALUES(3),(4);

SELECT [value] FROM ValueTable;

DROP TABLE ValueTable;

-----------------------------------------------------------------------
-- SAVE TRANSACTION https://msdn.microsoft.com/en-us/library/ms188378.aspx
-- SAVE { TRAN | TRANSACTION } { savepoint_name | @savepoint_variable } [ ; ]

USE AdventureWorks2012;
GO
IF EXISTS (SELECT name FROM sys.objects
           WHERE name = N'SaveTranExample')
    DROP PROCEDURE SaveTranExample;
GO
CREATE PROCEDURE SaveTranExample
    @InputCandidateID INT
AS
    -- Detect whether the procedure was called
    -- from an active transaction and save
    -- that for later use.
    -- In the procedure, @TranCounter = 0
    -- means there was no active transaction
    -- and the procedure started one.
    -- @TranCounter > 0 means an active
    -- transaction was started before the 
    -- procedure was called.
    DECLARE @TranCounter INT;
    SET @TranCounter = @@TRANCOUNT;
    IF @TranCounter > 0
        -- Procedure called when there is
        -- an active transaction.
        -- Create a savepoint to be able
        -- to roll back only the work done
        -- in the procedure if there is an
        -- error.
        SAVE TRANSACTION ProcedureSave;
    ELSE
        -- Procedure must start its own
        -- transaction.
        BEGIN TRANSACTION;
    -- Modify database.
    BEGIN TRY
        DELETE HumanResources.JobCandidate
            WHERE JobCandidateID = @InputCandidateID;
        -- Get here if no errors; must commit
        -- any transaction started in the
        -- procedure, but not commit a transaction
        -- started before the transaction was called.
        IF @TranCounter = 0
            -- @TranCounter = 0 means no transaction was
            -- started before the procedure was called.
            -- The procedure must commit the transaction
            -- it started.
            COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        -- An error occurred; must determine
        -- which type of rollback will roll
        -- back only the work done in the
        -- procedure.
        IF @TranCounter = 0
            -- Transaction started in procedure.
            -- Roll back complete transaction.
            ROLLBACK TRANSACTION;
        ELSE
            -- Transaction started before procedure
            -- called, do not roll back modifications
            -- made before the procedure was called.
            IF XACT_STATE() <> -1
                -- If the transaction is still valid, just
                -- roll back to the savepoint set at the
                -- start of the stored procedure.
                ROLLBACK TRANSACTION ProcedureSave;
                -- If the transaction is uncommitable, a
                -- rollback to the savepoint is not allowed
                -- because the savepoint rollback writes to
                -- the log. Just return to the caller, which
                -- should roll back the outer transaction.

        -- After the appropriate rollback, echo error
        -- information to the caller.
        DECLARE @ErrorMessage NVARCHAR(4000);
        DECLARE @ErrorSeverity INT;
        DECLARE @ErrorState INT;

        SELECT @ErrorMessage = ERROR_MESSAGE();
        SELECT @ErrorSeverity = ERROR_SEVERITY();
        SELECT @ErrorState = ERROR_STATE();

        RAISERROR (@ErrorMessage, -- Message text.
                   @ErrorSeverity, -- Severity.
                   @ErrorState -- State.
                   );
    END CATCH
GO