create or replace package body login
as

function fun return varchar2
as
	raw_key2 raw(128);
    raw_userpass raw(128);
    raw_encrypted raw(2048);
begin
    dbms_obfuscation_toolkit.desencrypt(input => raw_userpass, key => raw_key2, encrypted_data => raw_encrypted);
end fun;

end;