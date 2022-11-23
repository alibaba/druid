ALTER ASYMMETRIC KEY PacificSales09
WITH PRIVATE KEY (
DECRYPTION BY PASSWORD = '<oldPassword>',
ENCRYPTION BY PASSWORD = '<enterStrongPasswordHere>');
GO
ALTER ASYMMETRIC KEY PacificSales19 REMOVE PRIVATE KEY;
GO
--Left out open master key  because it doesn't match 2008 and up syntax
ALTER ASYMMETRIC KEY PacificSales09 WITH PRIVATE KEY (
DECRYPTION BY PASSWORD = '<enterStrongPasswordHere>' );
go
CREATE ASYMMETRIC KEY PacificSales09
    WITH ALGORITHM = RSA_2048
    ENCRYPTION BY PASSWORD = '<enterStrongPasswordHere>';
GO
CREATE ASYMMETRIC KEY PacificSales19 AUTHORIZATION Christina
    FROM FILE = 'c:\PacSales\Managers\ChristinaCerts.tmp'
    ENCRYPTION BY PASSWORD = '<enterStrongPasswordHere>';
GO
CREATE ASYMMETRIC KEY PacificSales19 AUTHORIZATION Christina
    FROM FILE = 'c:\PacSales\Managers\ChristinaCerts.tmp'
    ENCRYPTION BY PASSWORD = '<enterStrongPasswordHere>';
GO

