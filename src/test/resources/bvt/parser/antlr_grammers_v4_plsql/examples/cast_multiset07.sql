select
	"a3"."r_id" "r_id"
from
	"pe" "a3",
	"me" "a2"
where
	 "a3"."m_id"="a2"."m_id"
	 and "a2"."mi_t" =
	 any
	 (((
		select "a4"."sys$"."id"
		from the
		(
 		 	select "qa"."u_pkg"."getchartable"
 		 	(
 		 	 	"qa"."u_pkg"."glist"
 		 	 	(
 		 	 		cursor
 		 	 		(
 		 	 			select "qa"."u_pkg"."glist"
 		 	 			(
 		 	 				cursor
 		 	 				(
		 	 					select "a6"."mi_t" "mi_t"
		 	 					from "me" "a6"
		 	 					connect by "a6"."mi_uid"=prior "a6"."mi_id"
		 	 					start with "a6"."mi_t"=:b1
	 	 	 				)
	 	 	 			)
 	 	 	 			"lst"
	 	 	 			from "sys"."dual" "a5"
	 	 	 		)
	 	 	 	)
	 	 	)
	 	 	from dual
	 	)
	 	"a4"
	)))

