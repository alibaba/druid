select object_name, object_id,
 decode(status, 'INVALID', 'TRUE', 'FALSE') invalid, 
 'TRUE' runnable,
 plsql_debug
from sys.dba_objects o, dba_plsql_object_settings s
where o.owner = :schema
and s.owner = :schema
and s.name = o.object_name
and s.type = 'PACKAGE'
and object_type = 'PACKAGE'
and subobject_name is null
and object_id not in ( select purge_object from recyclebin )
and upper(object_name) in upper(:name)
 