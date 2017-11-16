create index IDX on OBJ (CODE, DOCUMENT);

ALTER INDEX supplier_idx
  RENAME TO supplier_index_name;

DROP INDEX index_name;