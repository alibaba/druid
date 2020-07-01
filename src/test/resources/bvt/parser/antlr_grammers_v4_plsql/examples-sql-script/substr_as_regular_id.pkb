create or replace package body testPack as

function blob_to_clob (blob_in in blob) return clob
as  
begin     
    utl_raw.cast_to_varchar2(dbms_lob.substr(a, b, c));
    return null;
end blob_to_clob;

end testPack;