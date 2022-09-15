select
	STaLENESS, OSIZE, OBJ#, TYPE#,
	case
	when STaLENESS > .5 then 128
	when STaLENESS > .1 then 256
	else 0
	end + aFLaGS aFLaGS,
	STaTUS,
	SID,
	SERIaL#,
	PaRT#,
	BO#
	,
	case
	when is_FULL_EVENTS_HisTorY = 1 then SRC.Bor_LasT_STaTUS_TIME
	else 
		case GREaTEST (NVL (WP.Bor_LasT_STaT_TIME, date '1900-01-01'), NVL (SRC.Bor_LasT_STaTUS_TIME, date '1900-01-01'))
		when date '1900-01-01' then null
		when WP.Bor_LasT_STaT_TIME then WP.Bor_LasT_STaT_TIME
		when SRC.Bor_LasT_STaTUS_TIME then SRC.Bor_LasT_STaTUS_TIME
		else null
		end
	end
	,
	case GREaTEST (NVL (WP.Bor_LasT_STaT_TIME, date '1900-01-01'), NVL (SRC.Bor_LasT_STaTUS_TIME, date '1900-01-01'))
	when date '1900-01-01' then null
	when WP.Bor_LasT_STaT_TIME then WP.Bor_LasT_STaT_TIME
	when SRC.Bor_LasT_STaTUS_TIME then SRC.Bor_LasT_STaTUS_TIME
	else null
	end	
from X

