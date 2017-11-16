-----------------------------------------------------------------------
-- GRANT https://msdn.microsoft.com/en-us/library/ms187965.aspx

-- GRANT ALL
GRANT ALL TO pax_writer;

-- Execute the following as a database owner
GRANT EXECUTE ON TestProc TO TesterRole WITH GRANT OPTION;
EXEC sp_addrolemember TesterRole, User1;
-- Execute the following as User1
-- The following fails because User1 does not have the permission as the User1
GRANT EXECUTE ON TestMe TO User2;
-- The following succeeds because User1 invokes the TesterRole membership
GRANT EXECUTE ON TestMe TO User2 AS TesterRole;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT Asymmetric Key Permissions

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT Availability Group Permissions

-- Granting VIEW DEFINITION permission on an availability group
-- USE master;
-- GRANT VIEW DEFINITION ON AVAILABILITY GROUP::MyAg TO ZArifin;
-- GO

-- Granting TAKE OWNERSHIP permission with the GRANT OPTION
-- USE master;
-- GRANT TAKE OWNERSHIP ON AVAILABILITY GROUP::MyAg TO PKomosinski 
    -- WITH GRANT OPTION;
-- GO

-- Granting CONTROL permission on an availability group
-- USE master;
-- GRANT CONTROL ON AVAILABILITY GROUP::MyAg TO PKomosinski;
-- GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT Certificate Permissions https://msdn.microsoft.com/en-us/library/ms186278.aspx

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT Database Permissions https://msdn.microsoft.com/en-us/library/ms178569.aspx

-- Granting permission to create tables
USE AdventureWorks;
GRANT CREATE TABLE TO MelanieK;
GO

-- Granting SHOWPLAN permission to an application role
USE AdventureWorks2012;
GRANT SHOWPLAN TO AuditMonitor;
GO

-- Granting CREATE VIEW with GRANT OPTION
USE AdventureWorks2012;
GRANT CREATE VIEW TO CarmineEs WITH GRANT OPTION;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT Database Principal Permissions https://msdn.microsoft.com/en-us/library/ms173848.aspx

-- Granting CONTROL permission on a user to another user
-- USE AdventureWorks2012;
-- GRANT CONTROL ON USER::Wanida TO RolandX;
-- GO

-- Granting VIEW DEFINITION permission on a role to a user with GRANT OPTION
-- USE AdventureWorks2012;
-- GRANT VIEW DEFINITION ON ROLE::SammamishParking 
    -- TO JinghaoLiu WITH GRANT OPTION;
-- GO

-- Granting IMPERSONATE permission on a user to an application role
-- USE AdventureWorks2012;
-- GRANT IMPERSONATE ON USER::HamithaL TO AccountsPayable17;
-- GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT Endpoint Permissions https://msdn.microsoft.com/en-us/library/ms187811.aspx

-- Granting VIEW DEFINITION permission on an endpoint
-- USE master;
-- GRANT VIEW DEFINITION ON ENDPOINT::Mirror7 TO ZArifin;
-- GO

-- Granting TAKE OWNERSHIP permission with the GRANT OPTION
-- USE master;
-- GRANT TAKE OWNERSHIP ON ENDPOINT::Shipping83 TO PKomosinski 
    -- WITH GRANT OPTION;
-- GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT Full-Text Permissions https://msdn.microsoft.com/en-us/library/ms190502.aspx

-- Granting permissions to a full-text catalog
-- GRANT CONTROL
    -- ON FULLTEXT CATALOG :: ProductCatalog
    -- TO Ted ;

-- Granting permissions to a stoplist
-- GRANT VIEW DEFINITION
    -- ON FULLTEXT STOPLIST :: ProductStoplist
    -- TO Mary ;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT Object Permissions https://msdn.microsoft.com/en-us/library/ms188371.aspx

-- Granting SELECT permission on a table
-- USE AdventureWorks2012;
-- GRANT SELECT ON OBJECT::Person.Address TO RosaQdM;
-- GO

-- Granting EXECUTE permission on a stored procedure
-- USE AdventureWorks2012; 
-- GRANT EXECUTE ON OBJECT::HumanResources.uspUpdateEmployeeHireInfo
    -- TO Recruiting11;
-- GO 

-- Granting REFERENCES permission on a view with GRANT OPTION
-- USE AdventureWorks2012;
-- GRANT REFERENCES (BusinessEntityID) ON OBJECT::HumanResources.vEmployee 
    -- TO Wanida WITH GRANT OPTION;
-- GO

-- Granting SELECT permission on a table without using the OBJECT phrase
USE AdventureWorks2012;
GRANT SELECT ON Person.Address TO RosaQdM;
GO

-- Granting SELECT permission on a table to a domain account
USE AdventureWorks2012;
GRANT SELECT ON Person.Address TO [AdventureWorks2012\RosaQdM];
GO

-- Granting EXECUTE permission on a procedure to a role
USE AdventureWorks2012;
--CREATE ROLE newrole ;
GRANT EXECUTE ON dbo.uspGetBillOfMaterials TO newrole ;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT Schema Permissions https://msdn.microsoft.com/en-us/library/ms187940.aspx

-- Granting INSERT permission on schema HumanResources to guest
-- GRANT INSERT ON SCHEMA :: HumanResources TO guest;

-- Granting SELECT permission on schema Person to database user WilJo
-- GRANT SELECT ON SCHEMA :: Person TO WilJo WITH GRANT OPTION;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT Search Property List Permissions

-- GRANT VIEW DEFINITION
    -- ON SEARCH PROPERTY LIST :: DocumentTablePropertyList
    -- TO Mary ;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT Server Permissions https://msdn.microsoft.com/en-us/library/ms186717.aspx

-- Granting a permission to a login
USE master;
GRANT CONTROL SERVER TO TerryEminhizer;
GO

-- Granting a permission that has GRANT permission
-- USE master;
-- GRANT ALTER ANY EVENT NOTIFICATION TO JanethEsteves WITH GRANT OPTION;
-- GO

-- Granting a permission to a server role
USE master;
--CREATE SERVER ROLE ITDevAdmin ;
--CREATE SERVER ROLE ITDevelopers ;
GRANT ALTER ANY DATABASE TO ITDevAdmin WITH GRANT OPTION ;
GRANT ALTER ANY DATABASE TO ITDevelopers AS ITDevAdmin ;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT Server Principal Permissions https://msdn.microsoft.com/en-us/library/ms178640.aspx

-- Granting IMPERSONATE permission on a login
-- USE master;
-- GRANT IMPERSONATE ON LOGIN::WanidaBenshoof to [AdvWorks\YoonM];
-- GO

-- Granting IMPERSONATE permission on a login
-- Granting VIEW DEFINITION permission with GRANT OPTION
-- USE master;
-- GRANT VIEW DEFINITION ON LOGIN::EricKurjan TO RMeyyappan 
    -- WITH GRANT OPTION;
-- GO

-- Granting VIEW DEFINITION permission on a server role
-- USE master;
-- GRANT VIEW DEFINITION ON SERVER ROLE::Sales TO Auditors ;
-- GO 

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT Service Broker Permissions https://msdn.microsoft.com/en-us/library/ms188798.aspx

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT Symmetric Key Permissions https://msdn.microsoft.com/en-us/library/ms179887.aspx

-- USE AdventureWorks2012;
-- GRANT ALTER ON SYMMETRIC KEY::SamInventory42 TO HamidS;
-- GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT System Object Permissions  https://msdn.microsoft.com/en-us/library/ms187797.aspx
-- GRANT { SELECT | EXECUTE } ON [ sys.]system_object TO principal 

-- Granting SELECT permission on a view
USE AdventureWorks2012;
GRANT SELECT ON sys.sql_logins TO Sylvester1;
--GRANT VIEW SERVER STATE to Sylvester1;
GO

-- Granting EXECUTE permission on an extended stored procedure
GRANT EXECUTE ON xp_readmail TO Sylvester1;
GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT Type Permissions  https://msdn.microsoft.com/en-us/library/ms174346.aspx

-- GRANT VIEW DEFINITION ON TYPE::Telemarketing.PhoneNumber 
    -- TO KhalidR WITH GRANT OPTION;
-- GO

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- GRANT XML Schema Collection Permissions  https://msdn.microsoft.com/en-us/library/ms179867.aspx
-- USE AdventureWorks2012;
-- GRANT EXECUTE ON XML SCHEMA COLLECTION::Sales.Invoices4 TO Wanida;
-- GO

--Test if the security_statement can accept Public as target
--Also tests if it can accepts multiple targets
--ref: https://msdn.microsoft.com/en-us/library/ms187965.aspx
GRANT EXECUTE ON [dbo].[closeLead] to Public,XYZPublic

--Test empty statements
;;;

--+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
-- Declare XML Schema (https://docs.microsoft.com/en-us/sql/relational-databases/xml/create-xml-data-type-variables-and-columns)
DECLARE @x xml (Sales.StoreSurveySchemaCollection)

-- Decrypts a symmetric key and makes it available for use
OPEN SYMMETRIC KEY SomeKeyName DECRYPTION
BY CERTIFICATE SomeCertificateName;
GO;

-- Closing a symmetric key
CLOSE SYMMETRIC KEY SomeKeyName;
GO;

-- Closing all symmetric keys
CLOSE ALL SYMMETRIC KEYS;
GO

-- Conversation timer
BEGIN CONVERSATION TIMER (@dialog_handle)
TIMEOUT = 120;
GO;

-- Beginning a conversation dialog
DECLARE @dialog_handle UNIQUEIDENTIFIER;
BEGIN DIALOG CONVERSATION @dialog_handle
   FROM SERVICE [//Adventure-Works.com/ExpenseClient]
   TO SERVICE '//Adventure-Works.com/Expenses'
   ON CONTRACT [//Adventure-Works.com/Expenses/ExpenseSubmission];
GO;

-- Begin a dialog with an explicit lifetime
DECLARE @dialog_handle UNIQUEIDENTIFIER;
BEGIN DIALOG CONVERSATION @dialog_handle
   FROM SERVICE [//Adventure-Works.com/ExpenseClient]
   TO SERVICE '//Adventure-Works.com/Expenses'
   ON CONTRACT [//Adventure-Works.com/Expenses/ExpenseSubmission]
   WITH LIFETIME = 60;
GO;

-- Begin a dialog with a specific broker instance
DECLARE @dialog_handle UNIQUEIDENTIFIER;
BEGIN DIALOG CONVERSATION @dialog_handle
   FROM SERVICE [//Adventure-Works.com/ExpenseClient]
   TO SERVICE '//Adventure-Works.com/Expenses',
              'a326e034-d4cf-4e8b-8d98-4d7e1926c904'
   ON CONTRACT [//Adventure-Works.com/Expenses/ExpenseSubmission];
GO;

-- Begin a dialog, and relating it to an existing conversation group
DECLARE @dialog_handle UNIQUEIDENTIFIER;
DECLARE @conversation_group_id UNIQUEIDENTIFIER;
BEGIN DIALOG CONVERSATION @dialog_handle
   FROM SERVICE [//Adventure-Works.com/ExpenseClient]
   TO SERVICE '//Adventure-Works.com/Expenses'
   ON CONTRACT [//Adventure-Works.com/Expenses/ExpenseSubmission]
   WITH RELATED_CONVERSATION_GROUP = @conversation_group_id;
GO;

-- Begin a dialog with an explicit lifetime, and relating the dialog to an existing conversation
DECLARE @dialog_handle UNIQUEIDENTIFIER
DECLARE @existing_conversation_handle UNIQUEIDENTIFIER
BEGIN DIALOG CONVERSATION @dialog_handle
   FROM SERVICE [//Adventure-Works.com/ExpenseClient]
   TO SERVICE '//Adventure-Works.com/Expenses'
   ON CONTRACT [//Adventure-Works.com/Expenses/ExpenseSubmission]
   WITH RELATED_CONVERSATION = @existing_conversation_handle
   LIFETIME = 600;
GO;

-- Begin a dialog with optional encryption
DECLARE @dialog_handle UNIQUEIDENTIFIER
BEGIN DIALOG CONVERSATION @dialog_handle
   FROM SERVICE [//Adventure-Works.com/ExpenseClient]
   TO SERVICE '//Adventure-Works.com/Expenses'
   ON CONTRACT [//Adventure-Works.com/Expenses/ExpenseSubmission]
   WITH ENCRYPTION = OFF;
GO;

-- Ending a conversation
END CONVERSATION @dialog_handle;
GO;

-- Ending a conversation with an error
DECLARE @dialog_handle UNIQUEIDENTIFIER,
        @ErrorSave INT,
        @ErrorDesc NVARCHAR(100) ;
BEGIN TRANSACTION ;
SET @ErrorSave = @@ERROR ;

IF (@ErrorSave <> 0)
  BEGIN
      ROLLBACK TRANSACTION ;
      SET @ErrorDesc = N'An error has occurred.' ;
      END CONVERSATION @dialog_handle
      WITH ERROR = @ErrorSave DESCRIPTION = @ErrorDesc ;
  END
ELSE

COMMIT TRANSACTION ;
GO;

-- Cleaning up a conversation that cannot complete normally
END CONVERSATION @dialog_handle WITH CLEANUP;
GO;

-- Getting a conversation group, waiting indefinitely
DECLARE @conversation_group_id UNIQUEIDENTIFIER ;
WAITFOR (
 GET CONVERSATION GROUP @conversation_group_id
     FROM ExpenseQueue
);
GO;

-- Getting a conversation group, waiting one minute
DECLARE @conversation_group_id UNIQUEIDENTIFIER
WAITFOR (
    GET CONVERSATION GROUP @conversation_group_id
    FROM ExpenseQueue ),
TIMEOUT 60000 ;
GO;

-- Getting a conversation group, returning immediately
DECLARE @conversation_group_id UNIQUEIDENTIFIER;
GET CONVERSATION GROUP @conversation_group_id
FROM AdventureWorks.dbo.ExpenseQueue;
GO;

-- Starts a dialog and sends an XML message on the dialog
DECLARE @dialog_handle UNIQUEIDENTIFIER,
        @ExpenseReport XML;

SET @ExpenseReport = '<document/>';

BEGIN DIALOG @dialog_handle
FROM SERVICE [//Adventure-Works.com/Expenses/ExpenseClient]
TO SERVICE '//Adventure-Works.com/Expenses'
ON CONTRACT [//Adventure-Works.com/Expenses/ExpenseProcessing] ;

SEND ON CONVERSATION @dialog_handle
    MESSAGE TYPE [//Adventure-Works.com/Expenses/SubmitExpense]
    (@ExpenseReport) ;
GO;

-- Starts a dialog and sends an XML message on the dialog using local vars without message body
DECLARE @dialog_handle UNIQUEIDENTIFIER,
        @ExpenseReport XML;

SET @ExpenseReport = '<document/>';

BEGIN DIALOG @dialog_handle
FROM SERVICE @from_service
TO SERVICE @to_service
ON CONTRACT @contract;

SEND ON CONVERSATION @dialog_handle
    MESSAGE TYPE @MessageType
GO;

-- Create Master Key
CREATE MASTER KEY ENCRYPTION BY PASSWORD = 'password';
GO;

-- Open Master Key
OPEN MASTER KEY DECRYPTION BY PASSWORD = 'password';
GO;

-- Close Master Key
CLOSE MASTER KEY;
GO;

-- Creating a self-signed certificate
USE AdventureWorks2012;
CREATE CERTIFICATE Shipping04
   ENCRYPTION BY PASSWORD = 'pGFD4bb925DGvbd2439587y'
   WITH SUBJECT = 'Sammamish Shipping Records',
   EXPIRY_DATE = '20201031';
GO;

-- Creating a certificate from a file
USE AdventureWorks2012;
CREATE CERTIFICATE Shipping11
    FROM FILE = 'c:\Shipping\Certs\Shipping11.cer'
    WITH PRIVATE KEY (FILE = 'c:\Shipping\Certs\Shipping11.pvk',
    DECRYPTION BY PASSWORD = 'sldkflk34et6gs%53#v00');
GO;

-- Creating a certificate from a signed executable file
USE AdventureWorks2012;
CREATE CERTIFICATE Shipping19
    FROM EXECUTABLE FILE = 'c:\Shipping\Certs\Shipping19.dll';
GO;

-- Creating a symmetric key
CREATE SYMMETRIC KEY JanainaKey09
WITH ALGORITHM = AES_256
ENCRYPTION BY CERTIFICATE Shipping04;
GO;

-- Creating a temporary symmetric key
CREATE SYMMETRIC KEY #MarketingXXV
WITH ALGORITHM = AES_128,
KEY_SOURCE
     = 'The square of the hypotenuse is equal to the sum of the squares of the sides',
IDENTITY_VALUE = 'Pythagoras'
ENCRYPTION BY CERTIFICATE Marketing25;
GO;

-- Creating a symmetric key using an Extensible Key Management (EKM) device
CREATE SYMMETRIC KEY MySymKey
AUTHORIZATION User1
FROM PROVIDER EKMProvider
WITH
PROVIDER_KEY_NAME='KeyForSensitiveData',
CREATION_DISPOSITION=OPEN_EXISTING;
GO;

-- Creating a message type for an empty message
CREATE MESSAGE TYPE
    [//Adventure-Works.com/Expenses/SubmitExpense]
    VALIDATION = EMPTY;
GO;

-- Creating a message type containing binary data
CREATE MESSAGE TYPE
    [//Adventure-Works.com/Expenses/ReceiptImage]
    VALIDATION = NONE;
GO;

-- Create Contract
CREATE MESSAGE TYPE
    [//Adventure-Works.com/Expenses/SubmitExpense]
    VALIDATION = WELL_FORMED_XML ;

CREATE MESSAGE TYPE
    [//Adventure-Works.com/Expenses/ExpenseApprovedOrDenied]
    VALIDATION = WELL_FORMED_XML ;

CREATE MESSAGE TYPE
    [//Adventure-Works.com/Expenses/ExpenseReimbursed]
    VALIDATION= WELL_FORMED_XML ;

CREATE CONTRACT
    [//Adventure-Works.com/Expenses/ExpenseSubmission]
    ( [//Adventure-Works.com/Expenses/SubmitExpense]
          SENT BY INITIATOR,
      [//Adventure-Works.com/Expenses/ExpenseApprovedOrDenied]
          SENT BY TARGET,
      [//Adventure-Works.com/Expenses/ExpenseReimbursed]
          SENT BY TARGET
    ) ;
GO;

-- Creating a queue with no parameters
CREATE QUEUE ExpenseQueue ;
GO;

-- Creating an unavailable queue
CREATE QUEUE ExpenseQueue WITH STATUS=OFF ;
GO;

-- Creating a queue and specify internal activation information
CREATE QUEUE ExpenseQueue
    WITH STATUS=ON,
    ACTIVATION (
        PROCEDURE_NAME = expense_procedure,
        MAX_QUEUE_READERS = 5,
        EXECUTE AS 'ExpenseUser' ) ;
GO;

-- Creating a queue on a specific filegroup
CREATE QUEUE ExpenseQueue
    ON ExpenseWorkFileGroup ;
GO;

-- Creating a queue with multiple parameters
CREATE QUEUE ExpenseQueue
    WITH STATUS = OFF,
      RETENTION = ON,
      ACTIVATION (
          PROCEDURE_NAME = AdventureWorks2012.dbo.expense_procedure,
          MAX_QUEUE_READERS = 10,
          EXECUTE AS SELF )
    ON [DEFAULT] ;
GO;

-- Making a queue unavailable
ALTER QUEUE ExpenseQueue WITH STATUS = OFF ;
GO;

-- Changing the activation stored procedure
ALTER QUEUE ExpenseQueue
    WITH ACTIVATION (
        PROCEDURE_NAME = new_stored_proc,
        EXECUTE AS SELF) ;
GO;

-- Changing the number of queue readers
ALTER QUEUE ExpenseQueue WITH ACTIVATION (MAX_QUEUE_READERS = 7) ;
GO;

-- Changing the activation stored procedure and the EXECUTE AS account
ALTER QUEUE ExpenseQueue
    WITH ACTIVATION (
        PROCEDURE_NAME = AdventureWorks2012.dbo.new_stored_proc ,
        EXECUTE AS 'SecurityAccount') ;
GO;

-- Setting the queue to retain messages
ALTER QUEUE ExpenseQueue WITH RETENTION = ON ;
GO;

-- Removing activation from a queue
ALTER QUEUE ExpenseQueue WITH ACTIVATION (DROP) ;
GO;

-- Rebuilding queue indexes
ALTER QUEUE ExpenseQueue REBUILD WITH (MAXDOP = 2)
GO;

-- Reorganizing queue indexes
ALTER QUEUE ExpenseQueue REORGANIZE
GO;

-- Moving queue internal table to another filegroup
ALTER QUEUE ExpenseQueue MOVE TO [NewFilegroup]
GO;
