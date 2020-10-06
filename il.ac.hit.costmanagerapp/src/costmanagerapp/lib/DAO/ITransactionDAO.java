package costmanagerapp.lib.DAO;

import com.sun.istack.internal.NotNull;
import costmanagerapp.lib.Models.RetailType;
import costmanagerapp.lib.Models.Transaction;
import costmanagerapp.lib.Models.User;
import costmanagerapp.lib.QueryUtils.AbstractDbConnector;
import costmanagerapp.lib.QueryUtils.IQueryExecuter;
import costmanagerapp.lib.UsersPlatformException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;

public interface ITransactionDAO {
    Transaction getTransaction(int transaction_id) throws UsersPlatformException, SQLException;
    Collection<Transaction> getTransactionsByUser(int userId) throws UsersPlatformException;
    Collection<Transaction> getTransactionsByRetail(int retailId) throws UsersPlatformException;
    Collection<Transaction> getTransactionsByDateRange(Date from, Date to, int userGuid) throws UsersPlatformException;

    Collection<Transaction> getTransactionsByPriceRange(double fromPrice, double toPrice) throws UsersPlatformException;
    void updateTransactionPrice(@NotNull int transactionGuid, @NotNull double newPrice) throws UsersPlatformException, SQLException;
    void updateTransactionIncomeStatus(@NotNull int transactionGuid, @NotNull boolean newIsIncome) throws UsersPlatformException, SQLException;
    void updateTransactionDate(@NotNull int transactionGuid, @NotNull Date newDate) throws UsersPlatformException, SQLException;
    void updateTransactionRetail(@NotNull int transactionGuid, @NotNull RetailType newRetailType) throws UsersPlatformException, SQLException;
    void updateTransactionUser(int transactionGuid, User newUser) throws UsersPlatformException, SQLException;
    void insertTransaction(Transaction transaction) throws SQLException, UsersPlatformException;
    void deleteTransaction(int guid) throws UsersPlatformException, SQLException;
}