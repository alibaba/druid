create or replace type type_body_elements
as object
(
  some_string varchar2(64),
  member function function_one
  return varchar2,
  member function function_two
  return varchar2
);
/

create or replace type body type_body_elements
is

  member function function_one
  return varchar2
  is
  begin
    return 'the function_one result';
  end function_one;

  member function function_two
  return varchar2
  is
  begin
    return 'the function_two result';
  end function_two;

end;
/
