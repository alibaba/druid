INSERT OVERWRITE TABLE wdm_device_gps_range_day PARTITION (pt)
SELECT a.imei, 
	a.imsi, 
	a.brand, 
	a.cpu, 
	a.device_id, 
	a.device_model, 
	a.longitude AS longitude_first, 
	a.latitude AS latitude_first, 
	b.longitude AS longitude_last, 
	b.latitude AS latitude_last, 
	first_time, 
	last_time, 
	rn_lead - rn_lag + 1 AS count, 
	province(b.longitude, b.latitude) AS province, 
	province_code(b.longitude, b.latitude) AS province_code, 
	city(b.longitude, b.latitude) AS city, 
	city_code(b.longitude, b.latitude) AS city_code, 
	a.pt
FROM (
	SELECT ROW_NUMBER() OVER (PARTITION BY pt, imei, imsi, brand, cpu, device_id, device_model ORDER BY rn_lead) AS rn, 
		imei, 
		imsi, 
		brand, 
		cpu, 
		device_id, 
		device_model, 
		longitude, 
		latitude, 
		server_time AS first_time, 
		pt, 
		rn_lead
	FROM (
		SELECT ROW_NUMBER() OVER (PARTITION BY pt, imei, imsi, brand, cpu, device_id, device_model ORDER BY server_time, longitude, latitude) AS rn_lead, 
			imei, 
			imsi, 
			brand, 
			cpu, 
			device_id, 
			device_model, 
			longitude, 
			LEAD(longitude, 1, 0) OVER (PARTITION BY pt, imei, imsi, brand, cpu, device_id, device_model ORDER BY server_time, longitude, latitude) AS longitude_lead, 
			latitude, 
			LEAD(latitude, 1, 0) OVER (PARTITION BY pt, imei, imsi, brand, cpu, device_id, device_model ORDER BY server_time, longitude, latitude) AS latitude_lead, 
			server_time, 
			pt
		FROM wdm_args
		WHERE length(imei) >= 15
			AND longitude IS NOT NULL
			AND latitude IS NOT NULL
			AND abs(longitude) <= 180
			AND abs(latitude) <= 90
			AND longitude <> 0
			AND latitude <> 0
			AND pt >= ${bizdate}
	) x1
	WHERE round(longitude, 2) <> round(longitude_lead, 2)
		OR round(latitude, 2) <> round(latitude_lead, 2)
) a
INNER JOIN (
	SELECT ROW_NUMBER() OVER (PARTITION BY pt, imei, imsi, brand, cpu, device_id, device_model ORDER BY rn_lag) AS rn, 
		imei, 
		imsi, 
		brand, 
		cpu, 
		device_id, 
		device_model, 
		longitude, 
		latitude, 
		server_time AS last_time, 
		pt, 
		rn_lag
	FROM (
		SELECT ROW_NUMBER() OVER (PARTITION BY pt, imei, imsi, brand, cpu, device_id, device_model ORDER BY server_time, longitude, latitude) AS rn_lag, 
			imei, 
			imsi, 
			brand, 
			cpu, 
			device_id, 
			device_model, 
			longitude, 
			LAG(longitude, 1, 0) OVER (PARTITION BY pt, imei, imsi, brand, cpu, device_id, device_model ORDER BY server_time, longitude, latitude) AS longitude_lag, 
			latitude, 
			LAG(latitude, 1, 0) OVER (PARTITION BY pt, imei, imsi, brand, cpu, device_id, device_model ORDER BY server_time, longitude, latitude) AS latitude_lag, 
			server_time, 
			pt
		FROM wdm_args
		WHERE length(imei) >= 15
			AND longitude IS NOT NULL
			AND latitude IS NOT NULL
			AND abs(longitude) <= 180
			AND abs(latitude) <= 90
			AND longitude <> 0
			AND latitude <> 0
			AND pt >= ${bizdate}
	) x2
	WHERE round(longitude, 2) <> round(longitude_lag, 2)
		OR round(latitude, 2) <> round(latitude_lag, 2)
) b ON a.pt = b.pt
	AND a.imei = b.imei
	AND a.imsi = b.imsi
	AND a.brand = b.brand
	AND a.cpu = b.cpu
	AND a.device_id = b.device_id
	AND a.device_model = b.device_model
	AND a.rn = b.rn
