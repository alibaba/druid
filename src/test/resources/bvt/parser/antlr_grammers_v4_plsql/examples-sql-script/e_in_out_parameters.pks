create or replace package e_in_out_parameters
is

  procedure some_in_nocopy_procedure( p_string in nocopy varchar2 );

end e_in_out_parameters;
/

show errors

create or replace package e_in_out_parameters
is

  procedure some_out_in_procedure( p_string out in varchar2 );

end e_in_out_parameters;
/

show errors

create or replace package e_in_out_parameters
is

  procedure some_inout_procedure( p_string inout varchar2 );

end e_in_out_parameters;
/

show errors
