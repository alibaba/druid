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
		from t "a4"
	)))
