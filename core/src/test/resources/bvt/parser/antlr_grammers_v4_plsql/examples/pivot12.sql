 select value from
 (
    (
        select
            'a' v1,
            'e' v2,
            'i' v3,
            'o' v4,
            'u' v5
        from dual
    )
    unpivot include nulls
    (
        value
        for value_type in
            (v1, v2,v3,v4,v5) -- Also can give ANY here.
    )
 )