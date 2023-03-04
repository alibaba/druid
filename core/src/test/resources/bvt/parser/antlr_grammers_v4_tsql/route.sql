CREATE ROUTE ExpenseRoute  
    WITH  
    SERVICE_NAME = '//Adventure-Works.com/Expenses',  
    BROKER_INSTANCE = 'D8D4D268-00A3-4C62-8F91-634B89C1E315',  
    ADDRESS = 'TCP://www.Adventure-Works.com:1234' ;  
CREATE ROUTE ExpenseRoute
    WITH
    SERVICE_NAME = '//Adventure-Works.com/Expenses',
    BROKER_INSTANCE = 'D8D4D268-00A3-4C62-8F91-634B89C1E315',
    ADDRESS = 'TCP://SERVER02:1234' ;
CREATE ROUTE ExpenseRoute
    WITH
    SERVICE_NAME = '//Adventure-Works.com/Expenses',
    BROKER_INSTANCE = 'D8D4D268-00A3-4C62-8F91-634B89C1E315',
    ADDRESS = 'TCP://192.168.10.2:1234' ;
CREATE ROUTE ExpenseRoute
    WITH
    ADDRESS = 'TCP://dispatch.Adventure-Works.com' ;
CREATE ROUTE LogRequests
    WITH
    SERVICE_NAME = '//Adventure-Works.com/LogRequests',
    ADDRESS = 'LOCAL' ;
CREATE ROUTE ExpenseRoute
    WITH
    SERVICE_NAME = '//Adventure-Works.com/Expenses',
    LIFETIME = 259200,
    ADDRESS = 'TCP://services.Adventure-Works.com:1234' ;
CREATE ROUTE ExpenseRoute  
    WITH  
    SERVICE_NAME = '//Adventure-Works.com/Expenses',  
    BROKER_INSTANCE = '69fcc80c-2239-4700-8437-1001ecddf933',  
    ADDRESS = 'TCP://services.Adventure-Works.com:1234',   
    MIRROR_ADDRESS = 'TCP://services-mirror.Adventure-Works.com:1234' ;  
CREATE ROUTE TransportRoute  
    WITH ADDRESS = 'TRANSPORT' ;  
