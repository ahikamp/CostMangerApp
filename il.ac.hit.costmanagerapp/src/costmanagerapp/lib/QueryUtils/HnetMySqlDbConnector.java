package costmanagerapp.lib.QueryUtils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.io.File;

public class HnetMySqlDbConnector extends AbstractDbConnector {
    static SessionFactory sessionFactory;

    public HnetMySqlDbConnector(File hibernateConfigFlePath){
        sessionFactory = new Configuration().configure(hibernateConfigFlePath).buildSessionFactory();
    }

    @Override
    Session openConn() {
        if(sessionFactory == null)return null;
        Session session = sessionFactory.openSession();
        return session;
    }
}
