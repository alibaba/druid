with
	codes2codelocales as
	(
		select t6.cdl_name as cod_name, t7.cdl_name as cod_cod_name, t14.cod_oid
		from servicedesk.itsm_codes t14
		left outer join servicedesk.itsm_codes_locale t6 on (t6.cdl_cod_oid=t14.cod_oid)
		left outer join servicedesk.itsm_codes_locale t7 on (t7.cdl_cod_oid=t14.cod_cod_oid)
	)
	, incident as
	(
		select t1.*,
		       c2cl1.cod_name as "closure code", c2cl1.cod_cod_name as "closure code parent",
		       c2cl2.cod_name as "reason caused code", c2cl2.cod_cod_name as "reason caused code parent",
		       t11.cdl_name "severity", t13.cdl_name "business impact", t16.cdl_name "priority",
		       t2.rct_name "status", t12.rct_name "category", t99.rct_name "folder"
		from servicedesk.itsm_incidents t1
		join servicedesk.itsm_codes_locale t11   on (t1.inc_sev_oid=t11.cdl_cod_oid)
		join servicedesk.itsm_codes_locale t13   on (t1.inc_imp_oid=t13.cdl_cod_oid)
		join servicedesk.itsm_codes_locale t16   on (t1.inc_pri_oid=t16.cdl_cod_oid)
		join servicedeskrepo.rep_codes_text t2   on (t1.inc_sta_oid=t2.rct_rcd_oid)
		join servicedeskrepo.rep_codes_text t12  on (t1.inc_cat_oid=t12.rct_rcd_oid)
		join servicedeskrepo.rep_codes_text t99  on (t1.inc_poo_oid=t99.rct_rcd_oid)
		left outer join codes2codelocales c2cl1  on (t1.inc_clo_oid=c2cl1.cod_oid)
		left outer join codes2codelocales c2cl2  on (t1.inc_cla_oid=c2cl2.cod_oid) 
		where t1."reg_created" between sysdate-1 and sysdate
	)
	, workgrouphistory as
	(
		select i.inc_id
		, max(t101.hin_subject) keep (dense_rank first order by (t101.reg_created)) as "first"
		, max(t101.hin_subject) keep (dense_rank last order by (t101.reg_created)) as "last"
		from
		servicedesk.itsm_historylines_incident t101
		join incident i on (t101.hin_inc_oid = i.inc_oid)
--		from servicedesk.itsm_incidents i (t101.hin_inc_oid = i.inc_oid)
		where t101.hin_subject like 'to workgroup from%'
--		and i."reg_created" between sysdate-1 and sysdate		
		group by i.inc_id
	)
select
  incident.inc_id "id"
 ,incident."status"
 ,incident.inc_description "description"
 ,t4.wog_searchcode "workgroup"
 ,t5.per_searchcode "person"
 ,incident.inc_solution "solution"
 ,incident."closure code"
 ,incident."closure code parent"
 ,incident."reason caused code"
 ,incident."reason caused code parent"
 ,t10.cit_searchcode "ci"
 ,incident."severity"
 ,incident."category"
 ,incident."business impact"
 ,incident."priority"
 ,to_char(incident."reg_created", 'dd-mm-yy hh24:mi:ss') "registered"
 ,to_char(incident."inc_deadline", 'dd-mm-yy hh24:mi:ss') "deadline"
 ,to_char(incident."inc_actualfinish", 'dd-mm-yy hh24:mi:ss') "finish"
 ,t3.icf_incshorttext3 "message group"
 ,t3.icf_incshorttext4 "application"
 ,t3.icf_incshorttext2 "msg id"
 ,incident."folder"
 ,workgrouphistory."first" "first wg"
 ,workgrouphistory."last" "last wg"
 ,t102.hin_subject "frirst pri"
from incident
join servicedesk.itsm_inc_custom_fields t3  on (incident.inc_oid=t3.icf_inc_oid)
join servicedesk.itsm_workgroups t4         on (incident.inc_assign_workgroup=t4.wog_oid)
join workgrouphistory                       on (incident.inc_id = workgrouphistory.inc_id)
left outer join servicedesk.itsm_persons t5 on (incident.inc_assign_person_to=t5.per_oid)
left outer join servicedesk.itsm_configuration_items t10 on (incident.inc_cit_oid=t10.cit_oid)
left outer join servicedesk.itsm_historylines_incident t102 on (incident.inc_oid = t102.hin_inc_oid and t102.hin_subject like 'priority set to%')
