create or replace package in_out_parameters
is

  procedure some_procedure( p_string varchar2 );

  procedure some_in_procedure( p_string in varchar2 );

  procedure some_out_procedure( p_string out varchar2 );

  procedure some_in_out_procedure( p_string in out varchar2 );

  procedure some_in_out_nocopy_procedure( p_string in out nocopy varchar2 );

end in_out_parameters;
/
