ALTER CREDENTIAL Saddles WITH IDENTITY = 'RettigB',   
    SECRET = 'sdrlk8$40-dksli87nNN8';  
GO  

ALTER CREDENTIAL Frames WITH IDENTITY = 'Aboulrus8';
GO
CREATE CREDENTIAL AlterEgo WITH IDENTITY = 'Mary5',
    SECRET = '<EnterStrongPasswordHere>';
GO
CREATE CREDENTIAL CredentialForEKM
    WITH IDENTITY='User1OnEKM', SECRET='<EnterStrongPasswordHere>'
    FOR CRYPTOGRAPHIC PROVIDER MyEKMProvider;
GO
