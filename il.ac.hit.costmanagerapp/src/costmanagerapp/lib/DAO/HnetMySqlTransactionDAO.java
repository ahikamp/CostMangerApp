package costmanagerapp.lib.DAO;

import com.sun.istack.internal.NotNull;
import costmanagerapp.Config.CostManagerDAOConfig;
import costmanagerapp.lib.Models.RetailType;
import costmanagerapp.lib.Models.Transaction;
import costmanagerapp.lib.Models.User;
import costmanagerapp.lib.QueryUtils.AbstractDbConnector;
import costmanagerapp.lib.QueryUtils.HnetMySqlDbConnector;
import costmanagerapp.lib.QueryUtils.HnetMySqlQueryExecutor;
import costmanagerapp.lib.QueryUtils.IQueryExecuter;
import costmanagerapp.lib.UsersPlatformException;
import org.hibernate.Hibernate;

import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class HnetMySqlTransactionDAO implements ITransactionDAO {
    private String transactionsTable = "transactions";
    private String dateOfTransactionColumn = "DateOfTransaction";
    private String guidColumn = "Guid";
    private String priceColumn = "Price";
    private IQueryExecuter<Transaction> executor;
    private AbstractDbConnector dbConnector;
    private Transaction TransactionClass;
    private final String configFilePath;// = "C:\\code\\Hit_ApplicationsCostManager\\il.ac.hit.costmanagerapp\\out\\production\\il.ac.hit.costmanagerapp\\costmanagerapp\\lib\\Models\\hibernate.cfg.xml";
    private String userGuidColumn = "UserGuid";
    private String retailGuidColumn = "RetailGuid";

    public HnetMySqlTransactionDAO(@NotNull CostManagerDAOConfig config) {this(config, new HnetMySqlQueryExecutor() ,null);}
    public HnetMySqlTransactionDAO(@NotNull CostManagerDAOConfig config,@NotNull IQueryExecuter iQueryExecuter,
                                   AbstractDbConnector abstractDbConnector){
        configFilePath = config.HibernateConfigPath;
        executor = iQueryExecuter;
        TransactionClass = new Transaction();
        if (abstractDbConnector != null)
            dbConnector = abstractDbConnector;
        else
            dbConnector = new HnetMySqlDbConnector(new File(configFilePath));
    }

    private Collection<Transaction> getTransactions(String query) {
        executor.openConnection(dbConnector);
        Collection<Transaction> results =executor.tryExecuteGetQuery(dbConnector, query, TransactionClass.getClass());
        results.forEach(r ->{
            Hibernate.initialize(r.getUser());
            Hibernate.initialize(r.getRetail());
        });
        executor.closeConnection();
        return results;
    }
    @Override
    public Transaction getTransaction(int transactionGuid) throws UsersPlatformException {
        String stringQuery = ("SELECT * FROM " + transactionsTable + " WHERE "+ guidColumn +"=" + transactionGuid);

        Collection<Transaction> results = getTransactions(stringQuery);
        if(results == null)throw new UsersPlatformException("Query result was null");
        if(results.size() <= 0) throw new UsersPlatformException("Transaction {"+transactionGuid + "} was not found");
        return results.stream().findFirst().get();
    }
    @Override
    public Collection<Transaction> getTransactionsByUser(int userGuid) throws UsersPlatformException {
        String stringQuery = ("SELECT * FROM " + transactionsTable + " WHERE " + userGuidColumn + " = " + userGuid);
        Collection transactions = getTransactions(stringQuery);
        if (transactions == null) throw new UsersPlatformException("Query result was null");
        return transactions;
    }
    @Override
    public Collection<Transaction> getTransactionsByRetail(int retailGuid) throws UsersPlatformException{
        String stringQuery = ("SELECT * FROM " + transactionsTable + " WHERE "+ retailGuidColumn+"=" + retailGuid);
        Collection transactions = getTransactions(stringQuery);
        if (transactions == null) throw new UsersPlatformException("Query result was null");
        return transactions;
    }
    @Override
    public Collection<Transaction> getTransactionsByDateRange(Date from, Date to, int userGuid) throws UsersPlatformException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String stringQuery = ("SELECT * FROM " + transactionsTable + " WHERE "+ dateOfTransactionColumn +" BETWEEN '" +
                formatter.format(from)+ "' and '" + formatter.format(to)+ "' and "+userGuidColumn+" = "+ userGuid + "  ORDER BY " + dateOfTransactionColumn + " desc");
        Collection transactions = getTransactions(stringQuery);
        if (transactions == null) throw new UsersPlatformException("Query result was null");
        return transactions;
    }
    @Override
    public Collection<Transaction> getTransactionsByPriceRange(double fromPrice, double toPrice) throws UsersPlatformException {
        String stringQuery = ("SELECT * FROM " + transactionsTable + " WHERE "+ priceColumn +" BETWEEN '" +
                fromPrice + "' and '" + toPrice + "' ORDER BY " + dateOfTransactionColumn + " desc");
        Collection transactions = getTransactions(stringQuery);
        if (transactions == null) throw new UsersPlatformException("Query result was null");
        return transactions;
    }
    @Override
    public void updateTransactionPrice(int transactionGuid, double newPrice) throws UsersPlatformException, SQLException {
        Transaction transaction = getTransaction(transactionGuid);
        if(transaction == null)throw new UsersPlatformException("Transaction {"+transactionGuid+"} was not found");
        transaction.setPrice(newPrice);
        executor.openConnection(dbConnector);
        executor.TryExecuteUpdateQuery(dbConnector, transaction);
        executor.closeConnection();
    }
    @Override
    public void updateTransactionIncomeStatus(int transactionGuid, boolean newIsIncome) throws UsersPlatformException, SQLException {
        Transaction transaction = getTransaction(transactionGuid);
        if(transaction == null)throw new UsersPlatformException("Transaction {"+transactionGuid+"} was not found");
        transaction.setIsIncome(newIsIncome);
        executor.TryExecuteUpdateQuery(dbConnector, transaction);
    }
    @Override
    public void updateTransactionDate(int transactionGuid, Date newDate) throws UsersPlatformException, SQLException {
        Transaction transaction = getTransaction(transactionGuid);
        if(transaction == null)throw new UsersPlatformException("Transaction {"+transactionGuid+"} was not found");
        transaction.setDateOfTransaction(newDate);
        executor.openConnection(dbConnector);
        executor.TryExecuteUpdateQuery(dbConnector, transaction);
        executor.closeConnection();
    }
    @Override
    public void updateTransactionRetail(int transactionGuid, RetailType newRetailType) throws UsersPlatformException, SQLException {
        Transaction transaction = getTransaction(transactionGuid);
        if(transaction == null)throw new UsersPlatformException("Transaction {"+transactionGuid+"} was not found");
        transaction.setRetail(newRetailType);
        executor.openConnection(dbConnector);
        executor.TryExecuteUpdateQuery(dbConnector, transaction);
        executor.closeConnection();

    }

    @Override
    public void updateTransactionUser(int transactionGuid, User newUser) throws UsersPlatformException, SQLException {
        Transaction transaction = getTransaction(transactionGuid);
        if(transaction == null)throw new UsersPlatformException("Transaction {"+transactionGuid+"} was not found");
        transaction.setUser(newUser);
        executor.openConnection(dbConnector);
        executor.TryExecuteUpdateQuery(dbConnector, transaction);
        executor.closeConnection();
    }

    @Override
    public void insertTransaction(Transaction transaction) throws SQLException, UsersPlatformException {
        executor.openConnection(dbConnector);
        boolean resultsFlag = executor.TryExecuteInsertQuery(dbConnector, transaction);
        executor.closeConnection();
        if (!resultsFlag) throw new UsersPlatformException("Could not update Retail");
    }

    @Override
    public void deleteTransaction(int transactionGuid) throws UsersPlatformException{
        Transaction transaction = getTransaction(transactionGuid);
        if(transaction == null)throw new UsersPlatformException("Transaction {"+transactionGuid + "} not found");
        executor.openConnection(dbConnector);
        executor.tryExecuteWildCardQuery("DELETE FROM Transaction WHERE "+guidColumn+" = " + transaction.getGuid());
        executor.closeConnection();
    }
}
