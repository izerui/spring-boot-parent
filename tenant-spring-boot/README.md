# 租户

* 自动拦截web请求request的header头信息，获取`entCode`并设置到`TenantHolder`
* 支持通过注解`@Tenant`执行spel表达式，进行设置,变量列表为方法参数列表。