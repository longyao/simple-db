# simple-db
非常简单，非常轻量级的数据库操作工具类

在这个拼凑框架的时代，哪怕写一个非常简单的数据库查询，都要动不动集成spring 框架啊，mybatis框架啊。

这个数据库操作工具，是平时写一些简单的代码用的，开箱可用，非常简单。


主要提供的接口，一般能满足平时工作90%的需求了
    
    //插入
    public int insert(String sql, Object... obs) throws SQLException;

    //更新
    public int update(String sql, Object... obs) throws SQLException;

    //删除
    public int delete(String sql, Object... obs) throws SQLException;

    //查询单个值，以字符串形式返回
    public String querySingleString(String sql, Object...obj) throws SQLException;

    //查询只有一个int值
    public int querySingleInt(String sql, Object...obj) throws SQLException;

    //查询只有一个long值
    public long querySingleLong(String sql, Object...obj) throws SQLException;

    //查询只有一个float值
    public float querySingleFloat(String sql, Object...obj) throws SQLException;

    //查询一个列的集合
    public <T> List<T> querySingleColumnList(String sql, Object...obj) throws SQLException;

    //查询一个对象
    public <T> T queryOne(String sql, Class<T> cls, Object...obj) throws SQLException;

    //查询，返回一个map列表（不想定义pojo类的情况下使用）
    public List<Map<String, Object>> query(String sql, Object... obj) throws SQLException;

    //查询一个集合对象（pojo对象，pojo可以用注解修饰，如果不用注解的话，字段名字和数据库同名就行了）
    public <T> List<T> query(String sql, Class<T> cls, Object... obj) throws SQLException;

    //查询一个对象集合，自己在回调里面组装成需要的pojo类
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... obj) throws SQLException;
    
    
    //简单的举例吧，
    DataSource source = xxx;//这个数据库连接池，自己定义，不管什么连接池都可以
    DBTemplate db = new DBTemplateImpl(source);
    
    //然后调用接口方法就可以了
    
