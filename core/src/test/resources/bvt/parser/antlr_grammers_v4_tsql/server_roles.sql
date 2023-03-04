ALTER SERVER ROLE Product WITH NAME = Production ;
ALTER SERVER ROLE Production ADD MEMBER [adventure-works\roberto0] ;
ALTER SERVER ROLE diskadmin ADD MEMBER Ted ;
ALTER SERVER ROLE Production DROP MEMBER [adventure-works\roberto0] ;
ALTER SERVER ROLE Production DROP MEMBER Ted ;
ALTER SERVER ROLE LargeRC ADD MEMBER Anna;
ALTER SERVER ROLE LargeRC DROP MEMBER Anna;
USE master;  
CREATE SERVER ROLE buyers AUTHORIZATION BenMiller;  
GO  
USE master;
CREATE SERVER ROLE auditors AUTHORIZATION securityadmin;
GO

