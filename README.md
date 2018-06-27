# session
session-manager
会话管理通过注册filterBean 设置sessionManager,以达到会话自定义管理

# 公共配置参数说明

- expireTime：1800(默认1800s); 会话过期时间
- cookiePath：/(默认)       ;  cookie保存路径
- cookieHttpOnly:true(默认) 
- cookieComment: 空字符以(默认)
- cookieDomain: "应用的域名"(默认); 会话域名
- cookieSecure: false(默认)
- cookieExpireType:session(默认) ; cookie过期类型: session：是会话级 ,time：时间级
- globalSessionCookieName : 全局cookie 名(用于多子域名共享主域名会话)
- sessionIdCookieName ：cookie的名称（默认为SHUSI-JSESSIONID）

# 会话存储目前实现了两个版本, RedisStore及WebStore
RedisStore用户简单的会话存储和读取（适用于会话管理简单的应用场景）
##配置项
- jedisPool : reids的连接池
- sample
```java
@Bean
    public FilterRegistrationBean filterRegistrationBean() {

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        FqjSessionFilter filter = new FqjSessionFilter();

        final AbstractStore store = new RedisStore(jedisPool);
        store.setCookieDomain("");
        store.setSessionIdCookieName("JSESSIONID");
        store.setCookieExpireType(baseExpireType);
        store.setExpireTime(baseExpireTime);

        FqjSessionManager manager = new FqjSessionManager();
        manager.setStore(store);

        filter.setSessionManager(manager);

        registrationBean.setFilter(filter);
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("/*");
        registrationBean.setUrlPatterns(urlPatterns);
        return registrationBean;
    }
```

WebStore用于调用外部接口进行会话的存储和读取（会话管理相对可以自定义复杂度，
会话存储和读取利用外部接口实现，
外部接口的实现方式可以有很大的想像空间）
##配置项
- appKey:应用key如 ： BOX 、NOTARY_BOX、SIMPLE_LOW
- urlRoot:url地址 ：  会话管理请求root地址 http://host:port
- saveRule:登录策略如 : UNLIMITED:无限制(默认) ;SINGLE_ONLINE:一个账号某时刻只能在全系统中使用 ; 
                      SUBSYSTEM_SINGLE_ONLINE:一个账号只能在全系统中的某个子系统中使用。此策略由会话管理的外部系统定义,
                      此处给出的是个例子.
- sample
```java
@Bean
    public FilterRegistrationBean filterRegistrationBean() {

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();

        FqjSessionFilter filter = new FqjSessionFilter();

        final AbstractStore store = new WebStore();
        store.setCookieDomain("");
        store.setSessionIdCookieName("JSESSIONID");
        store.setCookieExpireType(baseExpireType);
        store.setExpireTime(baseExpireTime);
        ((WebStore) store).setUrlRoot("web uri root");
        ((WebStore) store).setAppKey(baseUserKey);
        ((WebStore) store).setSaveRule(baseSaveRule);

        FqjSessionManager manager = new FqjSessionManager();
        manager.setStore(store);

        filter.setSessionManager(manager);

        registrationBean.setFilter(filter);
        List<String> urlPatterns = new ArrayList<>();
        urlPatterns.add("/*");
        registrationBean.setUrlPatterns(urlPatterns);
        return registrationBean;
    }
```