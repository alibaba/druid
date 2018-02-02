create or replace package deterministic_function
is

  function fixed_value return varchar2 deterministic;

end deterministic_function;
/
