BACKUP DATABASE AdventureWorks2012  
TO DISK='X:\SQLServerBackups\AdventureWorks1.bak',   
DISK='Y:\SQLServerBackups\AdventureWorks2.bak',   
DISK='Z:\SQLServerBackups\AdventureWorks3.bak'  
WITH FORMAT,  
   MEDIANAME = 'AdventureWorksStripedSet0',  
   MEDIADESCRIPTION = 'Striped media set for AdventureWorks2012 database';  
GO 
BACKUP DATABASE AdventureWorks2012
TO DISK='X:\SQLServerBackups\AdventureWorks1a.bak',
DISK='Y:\SQLServerBackups\AdventureWorks2a.bak',
DISK='Z:\SQLServerBackups\AdventureWorks3a.bak'
MIRROR TO DISK='X:\SQLServerBackups\AdventureWorks1b.bak',
DISK='Y:\SQLServerBackups\AdventureWorks2b.bak',
DISK='Z:\SQLServerBackups\AdventureWorks3b.bak';
GO
BACKUP DATABASE AdventureWorks2012
 TO DISK = 'Z:\SQLServerBackups\AdvWorksData.bak'
   WITH FORMAT;
GO
BACKUP DATABASE AdventureWorks2012 TO AdvWorksData;
GO
BACKUP DATABASE Sales
   FILEGROUP = 'SalesGroup1',
   FILEGROUP = 'SalesGroup2'
   TO DISK = 'Z:\SQLServerBackups\SalesFiles.bck';
GO
BACKUP DATABASE Sales
   FILEGROUP = 'SalesGroup1',
   FILEGROUP = 'SalesGroup2'
   TO DISK = 'Z:\SQLServerBackups\SalesFiles.bck'
   WITH
      DIFFERENTIAL;
GO
BACKUP DATABASE AdventureWorks2012
TO TAPE = '\\.\tape0'
MIRROR TO TAPE = '\\.\tape1'
MIRROR TO TAPE = '\\.\tape2'
MIRROR TO TAPE = '\\.\tape3'
WITH
   FORMAT,
   MEDIANAME = 'AdventureWorksSet0'
BACKUP DATABASE AdventureWorks2012
TO TAPE = '\\.\tape0', TAPE = '\\.\tape1'
MIRROR TO TAPE = '\\.\tape2', TAPE = '\\.\tape3'
WITH
   FORMAT,
   MEDIANAME = 'AdventureWorksSet1';
BACKUP DATABASE AdventureWorks2012 TO DISK='Z:\SQLServerBackups\AdvWorksData.bak'
WITH
   FORMAT,
   COMPRESSION;
BACKUP DATABASE Sales
TO URL = 'https://mystorageaccount.blob.core.windows.net/myfirstcontainer/Sales_20160726.bak'
WITH STATS = 5;
BACKUP LOG AdventureWorks2012
   TO AdvWorksLog;

BACKUP LOG AdventureWorks2012
TO TAPE = '\\.\tape0', TAPE = '\\.\tape1'
MIRROR TO TAPE = '\\.\tape2', TAPE = '\\.\tape3'
WITH
   NOINIT,
   MEDIANAME = 'AdventureWorksSet1';

