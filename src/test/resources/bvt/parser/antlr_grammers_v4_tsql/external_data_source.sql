ALTER EXTERNAL DATA SOURCE hadoop_eds SET  
     LOCATION = 'hdfs://10.10.10.10:8020',  
     RESOURCE_MANAGER_LOCATION = '10.10.10.10:8032'  
    ;  
ALTER EXTERNAL DATA SOURCE hadoop_eds SET
   CREDENTIAL = new_hadoop_user
    ;
ALTER EXTERNAL DATA SOURCE MyAzureInvoices
    WITH  (
        TYPE = BLOB_STORAGE,
        LOCATION = 'https://newinvoices.blob.core.windows.net',
        CREDENTIAL = UploadInvoices
    );
