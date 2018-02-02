
--create function return table , no BEGIN and END in this case
create function Test (@TZ int)
returns table
as
return
(
	select	1 AS s FROM dbo.Table1
)
GO

--create function return date_type
Create Function dbo.FooBar(
    @p1 nVarchar(4000)
)
Returns int
As
Begin
  return 123;
END
GO

--create function return @val table 
CREATE OR ALTER FUNCTION [dbo].[Foo](@String nvarchar(4000))
RETURNS @Bar TABLE (Col1 nvarchar(4000))
AS
   BEGIN

   RETURN
END

--Alter Function, should behave the same as create function, except the ALTER keyword
ALTER FUNCTION [dbo].[Foo](@String nvarchar(4000))
RETURNS @Bar TABLE (Col1 nvarchar(4000))
AS
   BEGIN

   RETURN
END

--drop Function, you can drop multiple at same time
Drop function Func1 
drop function Func1 , Func2
