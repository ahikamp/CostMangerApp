package costmanagerapp.lib.QueryUtils;

import org.hibernate.Session;

public abstract class AbstractDbConnector {
    public String connectionString;
    public String userName;
    public String password;

    abstract Session openConn();
}
