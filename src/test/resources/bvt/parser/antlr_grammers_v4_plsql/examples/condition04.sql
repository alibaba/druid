select *
from t
where
(
 (
  (
   (
    (
     (
      (
       (
        (
 	 (
 	  (
 	   (
 	    (
 	     (
 	      (
	       ( type = '2' ) or ( type = '3' )
  	      ) and ( t.cde < 20 )
  	     ) and ( t.se = 'xxx' )
  	    ) and ( t.id = '000000000002' )
  	   ) and ( ( t.attr_1 is null ) or ( t.attr_1 = '*' ) )
  	  ) and ( ( t.attr_2 is null ) or ( t.attr_2 = '*' ) )
  	 ) and ( ( t.attr_3 is null ) or ( t.attr_3 = '*' ) )
     	) and ( ( t.attr_4 is null ) or ( t.attr_4 = '*' ) )
       ) and ( ( t.attr_5 is null ) or ( t.attr_5 = '*' ) )
      ) and ( ( t.itype is null ) or ( t.itype = '*' ) )
     ) and ( ( t.inbr is null ) or ( t.inbr = '*' ) )
    ) and ( ( t.stat = '01' ) or ( t.stat = '*' ) )
   ) and ( ( t.orgn is null ) or ( t.orgn = '*' ) )
  ) and ( t.mbr = '0000000000001' )
 ) and ( t.nbr is null )
)


