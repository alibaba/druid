-- Use Explicit Mode with FOR XML
SELECT 1 as tag
  ,NULL as Parent
  ,3 as [Baz!1!Qux!CDATA]
FOR XML EXPLICIT,
BINARY BASE64, TYPE
GO;

-- Selecting into xml @local_variable with nested subquery
DECLARE @xml xml
SELECT @xml = (
  SELECT
    'foo' AS 'foo',
    (SELECT 1 as tag
       ,NULL as Parent
       ,3 as [Baz!1!Qux!CDATA]
     FOR XML EXPLICIT,
     BINARY BASE64, TYPE)
  FOR XML PATH ('Test'));
GO;

-- XML With Binary Base64
CREATE TABLE MyTable (Col1 int PRIMARY KEY, Col2 binary)
INSERT INTO MyTable VALUES (1, 0x7);

SELECT Col1,
  CAST(Col2 as image) as Col2
  FROM MyTable
FOR XML AUTO, BINARY BASE64;
GO;

-- Create a typed xml variable by specifying an XML schema collection
DECLARE @x xml (Sales.StoreSurveySchemaCollection)
GO;

-- Using the query() method against an xml type variable
declare @myDoc xml
set @myDoc = '<Root>
<ProductDescription ProductID="1" ProductName="Road Bike">
<Features>
  <Warranty>1 year parts and labor</Warranty>
  <Maintenance>3 year parts and labor extended maintenance is available</Maintenance>
</Features>
</ProductDescription>
</Root>'
SELECT @myDoc.query('/Root/ProductDescription/Features')
GO;

-- Using the query() method against an XML type column
SELECT CatalogDescription.query('
declare namespace PD="http://schemas.microsoft.com/sqlserver/2004/07/adventure-works/ProductModelDescription";
<Product ProductModelID="{ /PD:ProductDescription[1]/@ProductModelID }" />
') as Result
FROM Production.ProductModel
where CatalogDescription.exist('
declare namespace PD="http://schemas.microsoft.com/sqlserver/2004/07/adventure-works/ProductModelDescription";
declare namespace wm="http://schemas.microsoft.com/sqlserver/2004/07/adventure-works/ProductModelWarrAndMain";
     /PD:ProductDescription/PD:Features/wm:Warranty ') = 1
GO;

-- Using the value() method against an xml type variable
DECLARE @myDoc xml
DECLARE @ProdID int
SET @myDoc = '<Root>
<ProductDescription ProductID="1" ProductName="Road Bike">
<Features>
  <Warranty>1 year parts and labor</Warranty>
  <Maintenance>3 year parts and labor extended maintenance is available</Maintenance>
</Features>
</ProductDescription>
</Root>'

SET @ProdID =  @myDoc.value('(/Root/ProductDescription/@ProductID)[1]', 'int' )
SELECT @ProdID
GO;

-- Specifying the exist() method against an xml type variable
declare @x xml;
declare @f bit;
set @x = '<root Somedate = "2002-01-01Z"/>';
set @f = @x.exist('/root[(@Somedate cast as xs:date?) eq xs:date("2002-01-01Z")]');
select @f;
GO;

-- Specifying the exist() method against an xml type variable, with element
DECLARE @x xml;
DECLARE @f bit;
SET @x = '<Somedate>2002-01-01Z</Somedate>';
SET @f = @x.exist('/Somedate[(text()[1] cast as xs:date ?) = xs:date("2002-01-01Z") ]')
SELECT @f;
GO;

-- Specifying the exist() method against a typed xml variable
DECLARE @x xml (Production.ManuInstructionsSchemaCollection);
SELECT @x=Instructions
FROM Production.ProductModel
WHERE ProductModelID=67;
--SELECT @x
DECLARE @f int;
SET @f = @x.exist(' declare namespace AWMI="http://schemas.microsoft.com/sqlserver/2004/07/adventure-works/ProductModelManuInstructions";
    /AWMI:root/AWMI:Location[@LocationID=50]
');
SELECT @f;
GO;

-- Specifying the exist() method against an xml type column
SELECT ProductModelID, CatalogDescription.query('
declare namespace pd="http://schemas.microsoft.com/sqlserver/2004/07/adventure-works/ProductModelDescription";
    <Product
        ProductModelID= "{ sql:column("ProductModelID") }"
        />
') AS Result
FROM Production.ProductModel
WHERE CatalogDescription.exist('
    declare namespace  pd="http://schemas.microsoft.com/sqlserver/2004/07/adventure-works/ProductModelDescription";
     /pd:ProductDescription[not(pd:Specifications)]'
    ) = 1;
GO;

-- Specifying the exist() method against an xml type column with XMLNAMESPACES
WITH XMLNAMESPACES ('http://schemas.microsoft.com/sqlserver/2004/07/adventure-works/ProductModelDescription' AS pd)
SELECT ProductModelID, CatalogDescription.query('
    <Product
        ProductModelID= "{ sql:column("ProductModelID") }"
        />
') AS Result
FROM Production.ProductModel
WHERE CatalogDescription.exist('
     /pd:ProductDescription[not(pd:Specifications)]'
    ) = 1;
GO;

-- Replacing values in an XML instance using .modify
DECLARE @myDoc xml;
SET @myDoc = '<Root>
<Location LocationID="10"
            LaborHours="1.1"
            MachineHours=".2" >Manufacturing steps are described here.
<step>Manufacturing step 1 at this work center</step>
<step>Manufacturing step 2 at this work center</step>
</Location>
</Root>';

SET @myDoc.modify('
  replace value of (/Root/Location/step[1]/text())[1]
  with     "new text describing the manu step"
');
SELECT @myDoc;
GO;

-- Using nodes() to extract the subtree of the context item for each generated row:
SELECT T2.Loc.query('.')
FROM   T
CROSS APPLY Instructions.nodes('/root/Location') as T2(Loc)

-- Using nodes() method against a variable of xml type
DECLARE @x xml
SET @x='<Root>
    <row id="1"><name>Larry</name><oflw>some text</oflw></row>
    <row id="2"><name>moe</name></row>
    <row id="3" />
</Root>'
SELECT T.c.query('.') AS result
FROM @x.nodes('/Root/row') T(c) OPTION (OPTIMIZE FOR (@xml = NULL));
GO;

-- Specifying the nodes() method against a column of xml type
SELECT C.query('.') as result
FROM Production.ProductModel
CROSS APPLY Instructions.nodes('
declare namespace MI="http://schemas.microsoft.com/sqlserver/2004/07/adventure-works/ProductModelManuInstructions";
/MI:root/MI:Location') as T(C)
WHERE ProductModelID=7
GO;
