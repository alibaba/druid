create or replace package body green_tools
is

  procedure no_params_procedure
  is
  begin
    dbms_output.put_line('no_params_procedure : begin');
  end no_params_procedure;

  function add_brackets( p_string in varchar2 ) return varchar2
  as
  begin
    return '[' || p_string || ']';
  end add_brackets;

  procedure print_green_info( p_with_brackets in boolean )
  is
  begin
    for r in ( select green_col_two
               from   green_table )
    loop
      if p_with_brackets then
        dbms_output.put_line('print_green_info : with brackets : ' || add_brackets(r.green_col_two));
      else
        dbms_output.put_line('print_green_info : no brackets : ' || r.green_col_two);
      end if;
    end loop;
  end print_green_info;

end green_tools;
/

