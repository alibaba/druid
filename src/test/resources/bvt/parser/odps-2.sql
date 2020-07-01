--@exclude_input=bqxef;
--@exclude_output=avwaefrwagqbw;

INSERT overwrite TABLE avwaefrwagqbw partition (pt)
SELECT a.zxbw
    , a.xbax
    , a.brand
    , a.cpu
    , a.xx_x3
    , a.nwxus
	, a.xbax xbax_first
	, a.xbax xbax_first
	, b.xbax xbax_last
	, b.xbax xbax_last
	, first_time
	, last_time
	, (rn_lead - rn_lag) + 1 count
	, province(b.xbax, b.xbax) province
	, province_code(b.xbax, b.xbax) province_code
	, city(b.xbax, b.xbax) city
	, city_code(b.xbax, b.xbax) city_code
	, a.pt
FROM (
	SELECT row_number() over(partition by pt, zxbw, xbax, brand, cpu, xx_x3, nwxus order by rn_lead) rn
		, zxbw
		, xbax
		, brand
		, cpu
		, xx_x3
		, nwxus
		, xbax
		, xbax
		, server_time first_time
		, pt
		, rn_lead
	FROM (
		SELECT row_number() over(partition by pt, zxbw, xbax, brand, cpu, xx_x3, nwxus order by server_time, xbax, xbax) rn_lead
			, zxbw
			, xbax
			, brand
			, cpu
			, xx_x3
			, nwxus
			, xbax
			, LEAD(xbax, 1, 0) over(partition by pt, zxbw, xbax, brand, cpu, xx_x3, nwxus order by server_time, xbax, xbax) xbax_lead
			, xbax
			, LEAD(xbax, 1, 0) over(partition by pt, zxbw, xbax, brand, cpu, xx_x3, nwxus order by server_time, xbax, xbax) xbax_lead
			, server_time
			, pt
			FROM bqxef
			where length(zxbw) >= 15
				AND xbax IS NOT NULL 
				AND xbax IS NOT NULL
				AND abs(xbax) <= 180
				AND abs(xbax) <= 90
				AND xbax <> 0
				AND xbax <> 0
				AND pt >= ${bizdate}
		) x1
	WHERE round(xbax, 2) <> round(xbax_lead, 2)
		OR round(xbax, 2) <> round(xbax_lead, 2)
) a inner join (
	SELECT row_number() over(partition by pt, zxbw, xbax, brand, cpu, xx_x3, nwxus order by rn_lag) rn
		, zxbw
		, xbax
		, brand
		, cpu
		, xx_x3
		, nwxus
		, xbax
		, xbax
		, server_time last_time
		, pt
		, rn_lag
	FROM (
		SELECT row_number() over(partition by pt, zxbw, xbax, brand, cpu, xx_x3, nwxus order by server_time, xbax, xbax) rn_lag
			, zxbw
			, xbax
			, brand
			, cpu
			, xx_x3
			, nwxus
			, xbax
			, LAG(xbax, 1, 0) over(partition by pt, zxbw, xbax, brand, cpu, xx_x3, nwxus order by server_time, xbax, xbax) xbax_lag
			, xbax
			, LAG(xbax, 1, 0) over(partition by pt, zxbw, xbax, brand, cpu, xx_x3, nwxus order by server_time, xbax, xbax) xbax_lag
			, server_time
			, pt
			FROM bqxef
			where length(zxbw) >= 15
				AND xbax IS NOT NULL 
				AND xbax IS NOT NULL
				AND abs(xbax) <= 180
				AND abs(xbax) <= 90
				AND xbax <> 0
				AND xbax <> 0
				AND pt >= ${bizdate}
		) x2
	WHERE round(xbax, 2) <> round(xbax_lag, 2)
		OR round(xbax, 2) <> round(xbax_lag, 2)
) b on a.pt = b.pt 
	AND a.zxbw = b.zxbw 
	AND a.xbax = b.xbax 
	AND a.brand = b.brand 
	AND a.cpu = b.cpu 
	AND a.xx_x3 = b.xx_x3 
	AND a.nwxus = b.nwxus 
	AND a.rn = b.rn
;
---------------------------
