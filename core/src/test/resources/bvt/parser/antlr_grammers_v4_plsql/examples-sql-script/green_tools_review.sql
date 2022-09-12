set serveroutput on

select green_tools.add_brackets('to-be-bracketed') as with_brackets
from dual;

begin
  green_tools.print_green_info( p_with_brackets => false );
end;
/

begin
  green_tools.print_green_info( p_with_brackets => true );
end;
/
