USE AdventureWorks2012;  
GO  
ALTER SCHEMA HumanResources TRANSFER Person.Address;  
GO  
ALTER SCHEMA Person TRANSFER type::Production.TestType ;
GO
ALTER SCHEMA Sales TRANSFER OBJECT::dbo.Region;
GO
USE AdventureWorks2012;  
GO  
CREATE SCHEMA Sprockets AUTHORIZATION Annik  
    CREATE TABLE NineProngs (source int, cost int, partnumber int)  
    GRANT SELECT ON SCHEMA::Sprockets TO Mandar  
    DENY SELECT ON SCHEMA::Sprockets TO Prasanna;  
GO   
CREATE SCHEMA Sales;
GO;

CREATE TABLE Sales.Region
(Region_id int NOT NULL,
Region_Name char(5) NOT NULL)
WITH (DISTRIBUTION = REPLICATE);
GO  
CREATE SCHEMA Production AUTHORIZATION [Contoso\Mary];
GO
