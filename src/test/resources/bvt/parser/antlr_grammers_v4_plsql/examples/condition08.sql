select *
from "p"
where
-- note there are no parens around 231092	 
( ( "p"."id" in 231092 ) )    
