package costmanagerapp.Console;

import costmanagerapp.Config.CostManagerDAOConfig;
import costmanagerapp.Config.CostManagerDAOConfigWrapper;
import costmanagerapp.lib.DAO.HnetMySqlUserDAO;
import costmanagerapp.lib.DAO.IUsersDAO;
import costmanagerapp.lib.Models.User;
import costmanagerapp.lib.UsersPlatformException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.io.IOException;

public class ConsoleMain {


    public static void main(String args[]){
        IUsersDAO dao = null;
        try {
            CostManagerDAOConfig daoConfig = new CostManagerDAOConfig();
            daoConfig.HibernateConfigPath = "C:\\code\\Hit_ApplicationsCostManager\\il.ac.hit.costmanagerapp\\out\\production\\il.ac.hit.costmanagerapp\\costmanagerapp\\lib\\Models\\hibernate.cfg.xml";
            CostManagerDAOConfigWrapper.Serialize(daoConfig, "./Config.json");
            String javaHome = System.getProperty("java.home");
            System.out.println(javaHome);
            Configuration cfg = new Configuration();
            //FileReader f = new FileReader("C:\\code\\Hit_ApplicationsCostManager\\il.ac.hit.costmanagerapp\\src\\costmanagerapp\\Config\\HnetConfig\\hibernate.cfg.xml");
            cfg.configure(new File("C:\\code\\Hit_ApplicationsCostManager\\il.ac.hit.costmanagerapp\\out\\production\\il.ac.hit.costmanagerapp\\costmanagerapp\\lib\\Models\\hibernate.cfg.xml"));

            SessionFactory sessionFactory = cfg.buildSessionFactory();
            //SessionFactory sessionFactory = new Configuration().configure("C:\\code\\Hit_ApplicationsCostManager\\il.ac.hit.costmanagerapp\\src\\costmanagerapp\\Config\\HnetConfig\\hibernate.cfg.xml");.buildSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction t = session.beginTransaction();
        }
        catch (Exception e){
            System.out.println(e);
        }
        try {
            dao = new HnetMySqlUserDAO(CostManagerDAOConfigWrapper.Deserialize("./Config.json"));
            User u = dao.getUser(1);
            System.out.println(u);
        } catch (UsersPlatformException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
