var ioc = {
    conf : {
        type : "org.nutz.ioc.impl.PropertiesProxy",
        fields : {
            paths : ["custom/"]
        }
    },
    dataSource : {
        type : "com.alibaba.druid.pool.DruidDataSource",
        events : {
            create : "init",
            depose : 'close'
        },
        fields : {
            url : "jdbc:mysql://192.168.1.9:3306/big_data_manager?useUnicode=true&characterEncoding=utf-8",
            username : "ces",
            password : "ces",
            testWhileIdle : true,
            validationQuery : "select 1" ,
            maxActive : 100,
            filters : "mergeStat",
            connectionProperties : "druid.stat.slowSqlMillis=2000"
        }
    },
    dao : {
        type : "org.nutz.dao.impl.NutDao",
        args : [{refer:"dataSource"}]
    }
};