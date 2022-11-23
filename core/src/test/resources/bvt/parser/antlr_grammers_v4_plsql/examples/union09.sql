select * from (
        select row_.*
        from (
                select *
                        from
                        (
                                select results.*, 1 rn
                                from
                                (
                                        (
                                                select  dummy
                                                from    dual
                                                where 1=1
                                        )
                                        union
                                        (
                                                select dummy
                                                from   dual
                                                where 1=1
                                        )
                                ) results
                        )
                        where rn = 1 order by dummy desc
               )row_ 
               where rownum <= 1
        )
where rownum >= 1
