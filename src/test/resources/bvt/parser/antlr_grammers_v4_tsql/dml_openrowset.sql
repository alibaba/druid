SELECT * FROM OPENROWSET('SQLOLEDB', 'Server=.\SQLEXPRESS;Trusted_Connection=yes;', 'SET FMTONLY OFF;SET NOCOUNT ON;exec(''RESTORE headeronly FROM  DISK = N''''C:\Temp\Dev\SQL2012Backup.bak'''''')')
GO
