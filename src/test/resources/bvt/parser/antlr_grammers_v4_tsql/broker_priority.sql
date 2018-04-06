ALTER BROKER PRIORITY SimpleContractDefaultPriority  
    FOR CONVERSATION  
    SET (PRIORITY_LEVEL = 3);  
ALTER BROKER PRIORITY SimpleContractPriority
    FOR CONVERSATION
    SET (CONTRACT_NAME = SimpleContractB,
         LOCAL_SERVICE_NAME = TargetServiceB,
         REMOTE_SERVICE_NAME = N'InitiatorServiceB',
         PRIORITY_LEVEL = 8);
CREATE BROKER PRIORITY InitiatorAToTargetPriority
    FOR CONVERSATION
    SET (CONTRACT_NAME = SimpleContract,
         LOCAL_SERVICE_NAME = InitiatorServiceA,
         REMOTE_SERVICE_NAME = N'TargetService',
         PRIORITY_LEVEL = 3);
CREATE BROKER PRIORITY TargetToInitiatorAPriority
    FOR CONVERSATION
    SET (CONTRACT_NAME = SimpleContract,
         LOCAL_SERVICE_NAME = TargetService,
         REMOTE_SERVICE_NAME = N'InitiatorServiceA',
         PRIORITY_LEVEL = 3);
CREATE BROKER PRIORITY SimpleContractDefaultPriority
    FOR CONVERSATION
    SET (CONTRACT_NAME = SimpleContract,
         LOCAL_SERVICE_NAME = ANY,
         REMOTE_SERVICE_NAME = ANY,
         PRIORITY_LEVEL = 7);
CREATE BROKER PRIORITY [//Adventure-Works.com/Expenses/BasePriority]
    FOR CONVERSATION
    SET (CONTRACT_NAME = ANY,
         LOCAL_SERVICE_NAME = ANY,
         REMOTE_SERVICE_NAME = ANY,
         PRIORITY_LEVEL = 3);
CREATE BROKER PRIORITY GoldInitToTargetPriority
    FOR CONVERSATION
    SET (CONTRACT_NAME = SimpleContract,
         LOCAL_SERVICE_NAME = GoldInitiatorService,
         REMOTE_SERVICE_NAME = N'TargetService',
         PRIORITY_LEVEL = 6);
CREATE BROKER PRIORITY GoldTargetToInitPriority
    FOR CONVERSATION
    SET (CONTRACT_NAME = SimpleContract,
         LOCAL_SERVICE_NAME = TargetService,
         REMOTE_SERVICE_NAME = N'GoldInitiatorService',
         PRIORITY_LEVEL = 6);
CREATE BROKER PRIORITY SilverInitToTargetPriority
    FOR CONVERSATION
    SET (CONTRACT_NAME = SimpleContract,
         LOCAL_SERVICE_NAME = SilverInitiatorService,
         REMOTE_SERVICE_NAME = N'TargetService',
         PRIORITY_LEVEL = 4);
CREATE BROKER PRIORITY SilverTargetToInitPriority
    FOR CONVERSATION
    SET (CONTRACT_NAME = SimpleContract,
         LOCAL_SERVICE_NAME = TargetService,
         REMOTE_SERVICE_NAME = N'SilverInitiatorService',
         PRIORITY_LEVEL = 4);
CREATE BROKER PRIORITY BronzeInitToTargetPriority
    FOR CONVERSATION
    SET (CONTRACT_NAME = SimpleContract,
         LOCAL_SERVICE_NAME = BronzeInitiatorService,
         REMOTE_SERVICE_NAME = N'TargetService',
         PRIORITY_LEVEL = 2);
CREATE BROKER PRIORITY BronzeTargetToInitPriority
    FOR CONVERSATION
    SET (CONTRACT_NAME = SimpleContract,
         LOCAL_SERVICE_NAME = TargetService,
         REMOTE_SERVICE_NAME = N'BronzeInitiatorService',
         PRIORITY_LEVEL = 2);
CREATE BROKER PRIORITY GoldPriority
    FOR CONVERSATION
    SET (CONTRACT_NAME = GoldContract,
         LOCAL_SERVICE_NAME = ANY,
         REMOTE_SERVICE_NAME = ANY,
         PRIORITY_LEVEL = 6);
CREATE BROKER PRIORITY SilverPriority
    FOR CONVERSATION
    SET (CONTRACT_NAME = SilverContract,
         LOCAL_SERVICE_NAME = ANY,
         REMOTE_SERVICE_NAME = ANY,
         PRIORITY_LEVEL = 4);
CREATE BROKER PRIORITY BronzePriority
    FOR CONVERSATION
    SET (CONTRACT_NAME = BronzeContract,
         LOCAL_SERVICE_NAME = ANY,
         REMOTE_SERVICE_NAME = ANY,
         PRIORITY_LEVEL = 2);  

