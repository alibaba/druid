select * from (
 select times_purchased as "purchase frequency", state_code
 from customers t
 )
 pivot xml
 (
 count(state_code)
 for state_code in (any)
 )
 order by 1

	    