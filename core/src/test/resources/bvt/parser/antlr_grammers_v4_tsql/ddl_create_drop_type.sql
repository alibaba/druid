IF  EXISTS (SELECT * FROM sys.types st JOIN sys.schemas ss ON st.schema_id = ss.schema_id WHERE st.name = N'udt_pk' AND ss.name = N'dbo')
DROP TYPE [dbo].[udt_pk]
go

CREATE TYPE [dbo].[udt_pk] FROM [uniqueidentifier] NULL
go

-- Creating a user-defined table type
CREATE TYPE LocationTableType AS TABLE
    ( LocationName VARCHAR(50)
    , CostRate INT );
GO
