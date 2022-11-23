create or replace package green_tools
is

  procedure no_params_procedure;

  function add_brackets( p_string in varchar2 ) return varchar2;

  procedure print_green_info( p_with_brackets in boolean );

end green_tools;
/
