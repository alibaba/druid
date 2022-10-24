create or replace package result_cache_function
is

  function some_function( p_string in varchar2 ) return varchar2 result_cache;

end result_cache_function;
/
