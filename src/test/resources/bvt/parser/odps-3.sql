select a.user_id
    , a.user_nick
    , a.xrwq
    , a.exesag
    , a.brand
    , a.cpu
    , a.xagawef
	, a.qwgqwer
	, b.os
	, b.os_version
	, b.resolution
	, a.count
	, a.day_count
	, a.last_time
	, a.first_time
	, ip
from (
	select user_id
		, max(user_nick) user_nick
		, xrwq
		, exesag
		, brand
		, cpu
		, xagawef
		, qwgqwer
		, sum(count) count
		, count(distinct pt) day_count
		, max(coalesce(last_time, to_date(pt, 'yyyymmdd'))) last_time
		, min(coalesce(first_time, to_date(pt, 'yyyymmdd'))) first_time
	from x_dsls_yw3l 
	where xrwq <> 'xxx-xxxx--xxx'
	group by user_id, xrwq, exesag, brand, cpu, xagawef, qwgqwer
) a inner join (
	select * 
	from (
		select user_id
			, user_nick
			, xrwq
			, exesag
			, brand
			, cpu
			, xagawef
			, qwgqwer
			, os
			, os_version
			, resolution 
			, ip
			, row_number() over(partition by user_id, user_nick, xrwq, exesag, brand, cpu, xagawef, qwgqwer order by last_time desc) rn
		from x_dsls_yw3l
		where xrwq <> 'c1976429369bfe063ed8b3409db7c7e7d87196d9'
	) r1
	where rn = 1
) b on a.user_id = b.user_id 
	AND a.xrwq = b.xrwq
	AND a.exesag = b.exesag
	AND a.brand = b.brand
	AND a.cpu = b.cpu
	AND a.xagawef = b.xagawef
	AND a.qwgqwer = b.qwgqwer
;
---------------------------
