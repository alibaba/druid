ALTER REMOTE SERVICE BINDING APBinding  
    WITH USER = SecurityAccount 
CREATE REMOTE SERVICE BINDING APBinding
    TO SERVICE '//Adventure-Works.com/services/AccountsPayable'
    WITH USER = APUser ;
CREATE REMOTE SERVICE BINDING APBinding
    TO SERVICE '//Adventure-Works.com/services/AccountsPayable'
    WITH USER = APUser, ANONYMOUS=ON ;

