package costmanagerapp.lib.DAO;

import com.sun.istack.internal.NotNull;
import costmanagerapp.Config.CostManagerDAOConfig;
import costmanagerapp.lib.Models.Transaction;
import costmanagerapp.lib.Models.User;
import costmanagerapp.lib.QueryUtils.AbstractDbConnector;
import costmanagerapp.lib.QueryUtils.HnetMySqlDbConnector;
import costmanagerapp.lib.QueryUtils.HnetMySqlQueryExecutor;
import costmanagerapp.lib.QueryUtils.IQueryExecuter;
import costmanagerapp.lib.UsersPlatformException;
import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

public class HnetMySqlUserDAO implements IUsersDAO {
    private String tableName = "user";
    private String guidColumn = "Guid";
    private String userNameColumn = "UserName";
    private IQueryExecuter<User> executor;
    private AbstractDbConnector dbConnector;
    private ITransactionDAO transactionDAO;
    private User userType;
    private final String filePath;//`"C:\\code\\Hit_ApplicationsCostManager\\il.ac.hit.costmanagerapp\\out\\production\\il.ac.hit.costmanagerapp\\costmanagerapp\\lib\\Models\\hibernate.cfg.xml";

    public HnetMySqlUserDAO(@NotNull CostManagerDAOConfig config){this(config,new HnetMySqlQueryExecutor<>(), new HnetMySqlTransactionDAO(config),  null);}
    public HnetMySqlUserDAO(@NotNull CostManagerDAOConfig config,@NotNull IQueryExecuter<User> queryExecutor,
                            @NotNull ITransactionDAO inputTransactionDAO, AbstractDbConnector connector){
       filePath = config.HibernateConfigPath;
        executor = queryExecutor;
        transactionDAO = inputTransactionDAO;
        userType =  new User();
        if(connector != null)
            dbConnector = connector;
        else
            dbConnector = new HnetMySqlDbConnector(new File(filePath));
    }

    @Override
    public User getUser(int userGuid) throws UsersPlatformException {
        executor.openConnection(dbConnector);
        Collection<User> rs = executor.tryExecuteGetQuery(dbConnector, "SELECT * FROM " + tableName +
                " WHERE "+guidColumn+" =" + userGuid, userType.getClass());
        executor.closeConnection();
        if (rs == null) throw new UsersPlatformException("Query result was null");
        if (rs.size() <= 0) throw new UsersPlatformException("User {" + userGuid + "}does not exist");
        return rs.stream().findFirst().get();
    }

    @Override
    public User getUser(String userName) throws UsersPlatformException {
        executor.openConnection(dbConnector);
        Collection<User> rs = executor.tryExecuteGetQuery(dbConnector, "SELECT * FROM " + tableName +
                " WHERE "+userNameColumn+" =\"" + userName+"\"", userType.getClass());
        executor.closeConnection();
        if (rs == null) return null;
        //if (rs == null) throw new UsersPlatformException("Query result was null");
        if (rs.size() <= 0) return null;
       // if (rs.size() <= 0) throw new UsersPlatformException("User {" + userName + "}does not exist");
        return rs.stream().findFirst().get();

    }

    @Override
    public Collection<User> getAllUsers() throws UsersPlatformException {
        Collection<User> users;
        executor.openConnection(dbConnector);
        users = executor.tryExecuteGetQuery(dbConnector, "SELECT * FROM " + tableName, userType.getClass());
        executor.closeConnection();
        if (users == null) throw new UsersPlatformException("Query result was null");
        return users;
    }
    @Override
    public void insertUser(User user) throws UsersPlatformException, SQLException {
        executor.openConnection(dbConnector);
        boolean resultsFlag = executor.TryExecuteInsertQuery(dbConnector, user);
        executor.closeConnection();
        if (!resultsFlag) throw new UsersPlatformException("Could not insert new user");
    }
    @Override
    public void deleteUser(int userGuid) throws UsersPlatformException, SQLException {
        User user = getUser(userGuid);
        if(user == null) throw new UsersPlatformException("User {" + userGuid + "} not found");
        if(user.getUserName() == "None" || userGuid == 1)throw new UsersPlatformException("Cant delete the none object");
        User noneUser = getUser(1);
        if(noneUser == null) noneUser = new User(1, "None", "","");
        Date noneDate = new Date(0,0,0);
        for (Transaction transaction : transactionDAO.getTransactionsByUser(user.getGuid())) {
            transactionDAO.updateTransactionDate(transaction.getGuid(),noneDate);
            transactionDAO.updateTransactionUser(transaction.getGuid(),noneUser);
        }
        executor.openConnection(dbConnector);
        boolean resultsFlag = executor.TryExecuteDeleteQuery(dbConnector,user);
        executor.closeConnection();
        if (!resultsFlag) throw new UsersPlatformException("Could not delete user {" + userGuid + "}");
    }
    @Override
    public void setPassword(int userGuid, String newPassword) throws UsersPlatformException, SQLException {
        if(userGuid == 1)throw new UsersPlatformException("Cant update None object");
        User user = getUser(userGuid);
        user.setPassword(newPassword);
        executor.openConnection(dbConnector);
        boolean resultsFlag = executor.TryExecuteUpdateQuery(dbConnector, user);
        executor.closeConnection();
        if(!resultsFlag) throw new UsersPlatformException("Could not update Retail");
    }
}
