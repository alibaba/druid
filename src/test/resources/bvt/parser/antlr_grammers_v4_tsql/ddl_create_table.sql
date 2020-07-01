-- create table with foreign key
CREATE TABLE dbo.Employee (
  EmployeeID int PRIMARY KEY CLUSTERED,
  SalesPersonID int NULL REFERENCES SalesPerson(SalesPersonID)
);
GO;

-- create table and with table constraint
CREATE TABLE dbo.Employee (
  EmployeeID int PRIMARY KEY CLUSTERED,
  ProductID int,
  SpecialOfferID int,
  CONSTRAINT FK_SpecialOfferProduct_SalesOrderDetail FOREIGN KEY (
    ProductID, SpecialOfferID)
  REFERENCES SpecialOfferProduct (
    ProductID, SpecialOfferID)
);
GO;

-- create table with multiple column constratints
CREATE TABLE [#TestTable] (
  [ID] INT PRIMARY KEY NOT NULL
) WITH (DATA_COMPRESSION = PAGE);
GO;

-- create table with multiple columns and constraints
CREATE TABLE [#TestTable] (
  [ID] INT PRIMARY KEY NOT NULL,
  [Name] NVARCHAR(64) NOT NULL
) WITH (DATA_COMPRESSION = PAGE);
GO;
