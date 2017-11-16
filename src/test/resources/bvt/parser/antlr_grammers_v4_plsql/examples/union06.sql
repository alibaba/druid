(  select "x"."r_no",   
          "x"."i_id",   
	  "x"."ind",	
	  "x"."item",   
	  '0' "o"
   from "x"        
   where ("x"."r_no" = :a)
   minus
   select "y"."r_no",   
          "y"."i_id",
 	  "y"."ind",	   
 	  "y"."item",   
 	  '0' "o"  
   from "y"
   where ("y"."r_no" = :a)
)
union
(  select "y"."r_no",   
          "y"."i_id",   
 	  "y"."ind",	
 	  "y"."item",   
 	  '1' "o"  
   from "y"
   where ("y"."r_no" = :a)
   minus
   select "x"."r_no",   
          "x"."i_id",
          "x"."ind",   
          "x"."item",   
	  '1' "o"  
    from "x"
    where ("x"."r_no" = :a)
)
order by 4,3,1

