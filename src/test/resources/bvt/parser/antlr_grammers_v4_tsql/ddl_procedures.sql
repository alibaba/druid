create procedure a as begin
	select 1
end
go
create procedure b as begin
	select 2
end
go

-- call stored procedure, name in local var
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'TestSproc')
  DROP PROCEDURE dbo.TestSproc
GO

CREATE PROCEDURE dbo.TestSproc @Name nvarchar(30) AS SELECT @Name RETURN
GO

DECLARE @SprocName nvarchar(64) = 'TestSproc';
DECLARE @TestName nvarchar(64) = 'Foo'
DECLARE @Result nvarchar(64)
EXEC @Result = @SprocName @Name = @TestName;
SELECT @Result
GO


CREATE OR ALTER PROC What_DB_is_this
AS
SELECT DB_NAME() AS ThisDB;