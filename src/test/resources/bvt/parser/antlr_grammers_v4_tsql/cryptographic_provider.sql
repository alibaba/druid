ALTER CRYPTOGRAPHIC PROVIDER SecurityProvider   
DISABLE;  
GO  
ALTER CRYPTOGRAPHIC PROVIDER SecurityProvider
FROM FILE = 'c:\SecurityProvider\SecurityProvider_v2.dll';
GO
ALTER CRYPTOGRAPHIC PROVIDER SecurityProvider
ENABLE;
GO  

CREATE CRYPTOGRAPHIC PROVIDER SecurityProvider
    FROM FILE = 'C:\SecurityProvider\SecurityProvider_v1.dll';
