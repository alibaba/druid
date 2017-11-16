-- Create Table With Index Option
CREATE TABLE dbo.TestTable (
  TableID uniqueidentifier NOT NULL,
  Value nvarchar(64) NOT NULL
  CONSTRAINT PK_TestTable_ID PRIMARY KEY (TableID) WITH (DATA_COMPRESSION = PAGE))
GO

-- Create Table With Index Option and Table Option
CREATE TABLE dbo.TestTable (
  TableID uniqueidentifier NOT NULL,
  Value nvarchar(64) NOT NULL,
  Name nvarchar(64) NOT NULL,
  ModifiedDateUTC SMALLDATETIME,
  CONSTRAINT UQ_TestTable_ID  UNIQUE (Value) WITH (DATA_COMPRESSION = PAGE),
  CONSTRAINT PK_TestTable_ID PRIMARY KEY (TableID, Name))
  WITH (DATA_COMPRESSION = PAGE)
GO

-- Alter table drop constraint in transaction
IF NOT EXISTS (SELECT * FROM sys.columns cols
  JOIN sys.types AS types ON cols.user_type_id = types.user_type_id
WHERE object_id = OBJECT_ID('dbo.TestTable')
  AND cols.name = 'ModifiedDateUTC'
  AND types.name = 'datetime')
BEGIN
  BEGIN TRAN
    ALTER TABLE dbo.TestTable DROP CONSTRAINT DF_ModifiedDate;
  COMMIT TRAN
END
GO

-- Alter table drop multiple constraints in transaction
IF NOT EXISTS (SELECT * FROM sys.columns cols
  JOIN sys.types AS types ON cols.user_type_id = types.user_type_id
WHERE object_id = OBJECT_ID('dbo.TestTable')
  AND cols.name = 'ModifiedDateUTC'
  AND types.name = 'datetime')
BEGIN
  BEGIN TRAN
    ALTER TABLE dbo.TestTable DROP CONSTRAINT DF_ModifiedDate;
    ALTER TABLE dbo.TestTable DROP CONSTRAINT UQ_TestTable_ID;
  COMMIT TRAN
END
GO

-- Alter table Add Constraint with Default
ALTER TABLE dbo.TestTable ADD CONSTRAINT DF_ModifiedDateUTC DEFAULT(GETUTCDATE()) FOR ModifiedDateUTC;
GO

-- Alter table Alter Column
ALTER TABLE dbo.TestTable ALTER COLUMN ModifiedDateUTC DATETIME
GO

-- Alter table Rebuild with Table Options
ALTER TABLE TestTable REBUILD WITH (DATA_COMPRESSION = PAGE, ONLINE=ON);
GO

-- Create Table with Specified Order in Constraint
CREATE TABLE [dbo].[TestTable] (
  TableID UNIQUEIDENTIFIER NOT NULL,
  Value NVARCHAR(64) NOT NULL,
  Name NVARCHAR(64) NOT NULL,
  CONSTRAINT [PK_TestTable_Value] PRIMARY KEY CLUSTERED (
    [TableID] ASC,
    [Value] ASC))
GO

-- Create Table with NOT NULL and DEFAULT Constraint
CREATE TABLE [dbo].[TestTable] (
  TableID UNIQUEIDENTIFIER NOT NULL,
  Name NVARCHAR(64) NOT NULL,
  Value BIT CONSTRAINT DF_TestTable_Value NOT NULL DEFAULT (0))
  WITH (DATA_COMPRESSION = PAGE)
GO

-- Drop Column
IF EXISTS(SELECT * FROM sys.columns WHERE NAME = N'Name' AND Object_ID = Object_ID(N'dbo.TestTable'))
BEGIN
  ALTER TABLE dbo.TestTable
  DROP COLUMN Name
END
GO

-- Drop Index Using Fully Qualified Name
DROP INDEX dbo.TestTable.UIX_TestTable_Name_Value
GO

-- Alter Table Add Column With Default Constraint First
ALTER TABLE TestTable
  ADD Value BIT
  CONSTRAINT DF_TestTable_Value DEFAULT(0) NOT NULL
GO

-- Alter Table Add Column With Null Constraint First
ALTER TABLE TestTable
  ADD Value BIT
  CONSTRAINT DF_TestTable_Value NOT NULL DEFAULT(0)
GO

-- Alter Table Add Constraint To Column
ALTER TABLE dbo.TestTable 
  ADD CONSTRAINT DF_TestTable_Value DEFAULT(0) 
  FOR Value
GO

-- Alter Table Add Constraint With String Concatenation
ALTER TABLE dbo.TestTable
  ADD CONSTRAINT DF_Name
  DEFAULT('NONE_' + CONVERT(NVARCHAR(40),NEWID())) 
  FOR Name
GO
