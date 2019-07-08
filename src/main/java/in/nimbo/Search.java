package in.nimbo;

public class Search {
    private String newsDaoTableName;
    private String sql = null;

    public Search(Configuration configuration) {
        this.newsDaoTableName = configuration.getDbConfig().getString("newsTable.tableName");
    }

    public void addFilter(String text, Filter filter) {
        if (sql == null) {
            sql += "select * from " + newsDaoTableName + " where " + filter.toString() + " like " + text;

        } else
            sql += "and " + filter.toString() + " like " + text;
    }

    public String getSql() {
        return sql;
    }
}