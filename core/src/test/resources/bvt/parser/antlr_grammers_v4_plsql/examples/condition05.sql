select *
from t
where
 ( t.type = '2' ) or ( t.type = '3' )
 and  t.cde < 20 
 and  t.se = 'xxx' 
 and  t.id = '000000000002' 
 and ( ( t.sku_attr_1 is null ) or ( t.sku_attr_1 = '*' ) )
 and ( ( t.sku_attr_2 is null ) or ( t.sku_attr_2 = '*' ) )
 and ( ( t.sku_attr_3 is null ) or ( t.sku_attr_3 = '*' ) )
 and ( ( t.sku_attr_4 is null ) or ( t.sku_attr_4 = '*' ) )
 and ( ( t.sku_attr_5 is null ) or ( t.sku_attr_5 = '*' ) )
 and ( ( t.itype is null ) or ( t.itype = '*' ) )
 and ( ( t.bnbr is null ) or ( t.bnbr = '*' ) )
 and ( ( t.stat = '01' ) or ( t.stat = '*' ) )
 and ( ( t.orgn is null ) or ( t.orgn = '*' ) )
 and ( t.mbr = '0000000000001' )
 and ( t.nbr is null )


