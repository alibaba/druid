BACKUP CERTIFICATE sales05 TO FILE = 'c:\storedcerts\sales05cert';  
GO 
BACKUP CERTIFICATE sales05 TO FILE = 'c:\storedcerts\sales05cert'
    WITH PRIVATE KEY ( FILE = 'c:\storedkeys\sales05key' ,
    ENCRYPTION BY PASSWORD = '997jkhUbhk$w4ez0876hKHJH5gh' );
GO
BACKUP CERTIFICATE sales09 TO FILE = 'c:\storedcerts\sales09cert'
    WITH PRIVATE KEY ( DECRYPTION BY PASSWORD = '9875t6#6rfid7vble7r' ,
    FILE = 'c:\storedkeys\sales09key' ,
    ENCRYPTION BY PASSWORD = '9n34khUbhk$w4ecJH5gh' );
GO
BACKUP CERTIFICATE sales05 TO FILE = '\\ServerA7\storedcerts\sales05cert'
    WITH PRIVATE KEY ( FILE = '\\ServerA7\storedkeys\sales05key' ,
    ENCRYPTION BY PASSWORD = '997jkhUbhk$w4ez0876hKHJH5gh' );
GO  
