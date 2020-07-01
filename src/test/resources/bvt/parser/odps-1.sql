FROM (
    select * from uxoe.xhaayi
    where pt = '${bizdate}'
) x

INSERT overwrite TABLE uxoe.ialeulxx PARTITION(pt=${bizdate-1})
    select case when length(iweet_d) > 2 then iweet_d else NULL end iweet_d
        , case when length(user_nick) > 2 then user_nick else NULL end user_nick
        , olx
        , uoex
        , brand
        , cpu
        , umsis
        , iakxh
        , resolution
        , os
        , os_version
        , client_ip
        , count(*) as count
        , max(from_unixtime(cast(server_time as bigint)/1000)) last_time
        , min(from_unixtime(cast(server_time as bigint)/1000)) first_time
    group by iweet_d, user_nick, olx, uoex, brand, cpu, umsis, iakxh, resolution, os, os_version, client_ip
    
INSERT overwrite TABLE uxoe.adl_wdm_ip_device_sdt PARTITION (pt='${bizdate}', seg='d1')
    select client_ip
        , olx
        , uoex
        , brand
        , cpu
        , umsis
        , iakxh
        , reserve2 utdid
        , min(from_unixtime(cast(server_time as bigint)/1000)) first_time
        , max(from_unixtime(cast(server_time as bigint)/1000)) last_time
        , count(*)
        , count(distinct pt)
    group by client_ip, olx, uoex, brand, cpu, umsis, iakxh, reserve2
    
INSERT overwrite TABLE uxoe.hxeh PARTITION(pt='${bizdate}') 
    select unique_id() as uuid
        , regexp_extract(args, 'ongitude=(.*?)(,)') longitude
        , regexp_extract(args, 'latitude=(.*?)(,)') latitude
        , client_ip
        , protocol_version
        , olx
        , uoex
        , brand
        , cpu
        , umsis
        , iakxh
        , resolution
        , carrier
        , access
        , access_subtype
        , channel
        , app_key
        , app_version
        , user_nick
        , phone_number
        , country
        , language,os,os_version,sdk_type,sdk_version,reserve1,reserve2,reserve3,reserve4,reserve5
        , reserves,local_time
        , from_unixtime(cast(server_time as bigint)/1000) server_time
        , page
        , event_id
        , arg1
        , arg2
        , arg3
        , args
        , day
        , hour_id
        , iweet_d
        , oqlx
        , cast(regexp_extract(args, 'wifi_num=(.*?)(,)') as bigint) wifi_num
    WHERE length(args) > 5
        AND instr(args, 'gsm_') > 0
---------------------------
