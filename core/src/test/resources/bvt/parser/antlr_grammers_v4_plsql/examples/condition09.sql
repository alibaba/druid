select 
  sum(nvl(pl.qty,0)) 
  from 
  oline ol, 
  pline pl,
  blocation bl
  where 
  ol.id = pl.id
  and pl.no = pl.no
  and bl.id = pl.id
  and
  (
  	(select count(*) from la.sa where pl.id like sa.bid) > 0
	or
	(select count(*) from la.sa where bl.id like sa.id) > 0
  )
