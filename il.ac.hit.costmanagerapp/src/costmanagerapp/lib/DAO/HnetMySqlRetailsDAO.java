package costmanagerapp.lib.DAO;

import com.sun.istack.internal.NotNull;
import costmanagerapp.Config.CostManagerDAOConfig;
import costmanagerapp.lib.Models.RetailType;
import costmanagerapp.lib.Models.Transaction;
import costmanagerapp.lib.QueryUtils.AbstractDbConnector;
import costmanagerapp.lib.QueryUtils.HnetMySqlDbConnector;
import costmanagerapp.lib.QueryUtils.HnetMySqlQueryExecutor;
import costmanagerapp.lib.QueryUtils.IQueryExecuter;
import costmanagerapp.lib.UsersPlatformException;
import java.io.File;
import java.sql.SQLException;
import java.util.Collection;

public class HnetMySqlRetailsDAO implements IRetailDAO {
    private String guidColumn = "Guid";
    private String tableName = "retailtype";
    private IQueryExecuter<RetailType> executor;
    private AbstractDbConnector dbConnector;
    private ITransactionDAO transactionDAO;
    private RetailType RetailType;
    private final String filePath;
//    =    "C:\\code\\Hit_ApplicationsCostManager\\il.ac.hit.costmanagerapp\\out\\production\\il.ac.hit.costmanagerapp\\costmanagerapp\\lib\\Models\\hibernate.cfg.xml";

    public HnetMySqlRetailsDAO(@NotNull CostManagerDAOConfig config){this(config,new HnetMySqlQueryExecutor<>(),
            new HnetMySqlTransactionDAO(config), null);}
    public HnetMySqlRetailsDAO(@NotNull CostManagerDAOConfig config,@NotNull IQueryExecuter<RetailType> queryExecutor,
                               @NotNull ITransactionDAO inputTransactionDAO, AbstractDbConnector connector){
        filePath = config.HibernateConfigPath;
        executor = queryExecutor;
        RetailType =  new RetailType();
        transactionDAO = inputTransactionDAO;
        if(connector != null)
            dbConnector = connector;
        else
            dbConnector = new HnetMySqlDbConnector(new File(filePath));
    }

    @Override
    public RetailType getRetail(@NotNull int retailGuid) throws UsersPlatformException {
        executor.openConnection(dbConnector);
        Collection<RetailType> queryResults = executor.tryExecuteGetQuery(dbConnector,
                "SELECT * FROM " + tableName + " WHERE "+guidColumn+"=" + retailGuid, RetailType.getClass());
        executor.closeConnection();

        if (queryResults == null)throw new UsersPlatformException("Query result was null");
        if (queryResults.size() <= 0)throw new UsersPlatformException("Retail does not exist");

        return queryResults.stream().findFirst().get();
    }
    @Override
    public Collection<RetailType> getRetails() throws UsersPlatformException {
        executor.openConnection(dbConnector);
        Collection<RetailType> retails = executor.tryExecuteGetQuery(dbConnector, "SELECT * FROM " + tableName, RetailType.getClass());
        executor.closeConnection();
        if (retails == null)throw new UsersPlatformException("Query result was null");

        return retails;
    }
    @Override
    public void updateRetailName(@NotNull int retailGuid, @NotNull String retailNewName) throws UsersPlatformException, SQLException {
        if(retailGuid == 1)throw new UsersPlatformException("Cant update None object");
        RetailType retailTypeToSet = getRetail(retailGuid);
        if(retailTypeToSet == null)throw new UsersPlatformException("Retail {"+retailGuid + "} not found");
        retailTypeToSet.setName(retailNewName);
        executor.openConnection(dbConnector);
        boolean resultsFlag = executor.TryExecuteUpdateQuery(dbConnector, retailTypeToSet);
        executor.closeConnection();
        if(!resultsFlag)throw new UsersPlatformException("Could not update Retail {" + retailGuid + "} name");
    }
    @Override
    public void insertRetail(@NotNull RetailType retailType) throws UsersPlatformException, SQLException {
        executor.openConnection(dbConnector);
        boolean resultsFlag = executor.TryExecuteInsertQuery(dbConnector, retailType);
        executor.closeConnection();
        if (!resultsFlag) throw new UsersPlatformException("Could not Insert new Retail");
    }
    @Override
    public void deleteRetail(@NotNull int retailGuid) throws UsersPlatformException, SQLException {
        RetailType rt = getRetail(retailGuid);
        if(rt.getName() == "None" || retailGuid == 1)throw new UsersPlatformException("Cant delete the none object");
        RetailType noneRetail = getRetail(1);
        if(noneRetail == null)noneRetail = new RetailType(1,"None");
        for (Transaction transaction : transactionDAO.getTransactionsByRetail(rt.getGuid())) {
            transactionDAO.updateTransactionRetail(transaction.getGuid(), noneRetail);
        }
        executor.openConnection(dbConnector);
        boolean resultsFlag = executor.TryExecuteDeleteQuery(dbConnector,rt);
        executor.closeConnection();
        if (!resultsFlag) throw new UsersPlatformException("Could not delete Retail {"+retailGuid+"}");
    }
}
