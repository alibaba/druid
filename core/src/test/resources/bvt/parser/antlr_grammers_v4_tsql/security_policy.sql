CREATE SECURITY POLICY [FederatedSecurityPolicy]   
ADD FILTER PREDICATE [rls].[fn_securitypredicate]([CustomerId])   
ON [dbo].[Customer];  
CREATE SECURITY POLICY [FederatedSecurityPolicy]
ADD FILTER PREDICATE [rls].[fn_securitypredicate1]([CustomerId])
    ON [dbo].[Customer],
ADD FILTER PREDICATE [rls].[fn_securitypredicate1]([VendorId])
    ON [dbo].[ Vendor],
ADD FILTER PREDICATE [rls].[fn_securitypredicate2]([WingId])
    ON [dbo].[Patient]
WITH (STATE = ON);
CREATE SECURITY POLICY rls.SecPol
    ADD FILTER PREDICATE rls.tenantAccessPredicate(TenantId) ON dbo.Sales,
    ADD BLOCK PREDICATE rls.tenantAccessPredicate(TenantId) ON dbo.Sales AFTER INSERT;

