package costmanagerapp.Tests;

import costmanagerapp.Config.CostManagerDAOConfigWrapper;
import costmanagerapp.lib.*;
import costmanagerapp.lib.DAO.*;
import costmanagerapp.lib.Models.RetailType;
import costmanagerapp.lib.Models.Transaction;
import costmanagerapp.lib.Models.User;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;

public class TransactionDAOUnitTest {
    static ITransactionDAO tester;
    @BeforeClass
    public static void testSetup() throws IOException {
            tester = new HnetMySqlTransactionDAO(CostManagerDAOConfigWrapper.Deserialize("./Config.json"));
    }
    @AfterClass
    public static void testCleanup() {
        // Do your cleanup here like close URL connection , releasing resources etc
    }

    //Get Tests
    @Test
    public void testGetTransaction() throws UsersPlatformException, SQLException {
        int transactionGuid = 1;
        Transaction cl = tester.getTransaction(transactionGuid);
        if(cl == null) throw new AssertionError("No transaction found");
        System.out.println(cl.getGuid() + " :  " + cl.getIsIncome() +  ", " + cl.getPrice() +", "
                + cl.getDateOfTransaction()  +  ", " + cl.getDescription() +  ", " + cl.getUser().getUserName() +  ", " +
                cl.getRetail().getName());
    }
    @Test
    public void testGetTransactionByUser() throws UsersPlatformException {
        int userId = 1;
        Collection<Transaction> res = tester.getTransactionsByUser(userId);
        if(res.size() == 0) throw new AssertionError("No transaction found");
        res.forEach(cl -> System.out.println(cl.getGuid() + " :  " + cl.getIsIncome() +  ", " + cl.getPrice() +", "
                + cl.getDateOfTransaction()  +  ", " + cl.getDescription() +  ", " + cl.getUser().getUserName() +  ", " +
                cl.getRetail().getName()));
    }
    @Test
    public void testGetTransactionByRetail() throws Exception {
        int retailGuid = 1;
        Collection<Transaction> res = tester.getTransactionsByRetail(retailGuid);
        if(res.size() == 0) throw new AssertionError("No transaction found");
        res.forEach(cl -> System.out.println(cl.getGuid() + " :  " + cl.getIsIncome() +  ", " + cl.getPrice() +", "
                + cl.getDateOfTransaction()  +  ", " + cl.getDescription() +  ", " + cl.getUser().getUserName() +  ", " +
                cl.getRetail().getName()));
    }
    @Test
    public void testGetTransactionByDateRange() throws UsersPlatformException {
//        LocalDate d1 = LocalDate.of(2020, 05, 10);
//        LocalDate d2 = LocalDate.of(2020, 05, 15);
        Date d1 = new Date(2020 -1900,00,01);
        Date d2 = new Date(2020 - 1900,05,31);
        Collection<Transaction> res = tester.getTransactionsByDateRange(d1, d2,1);
        if(res.size() == 0) throw new AssertionError("No transaction found");
        res.forEach(cl -> System.out.println(cl.getGuid() + " :  " + cl.getIsIncome() +  ", " + cl.getPrice() +", "
                + cl.getDateOfTransaction()  +  ", " + cl.getDescription() +  ", " + cl.getUser().getUserName() +  ", " +
                cl.getRetail().getName()));
    }
    @Test
    public void testGetTransactionByPriceRange() throws UsersPlatformException {
        double from  = 10.5;
        double to  = 100.5;
        Collection<Transaction> res = tester.getTransactionsByPriceRange(from, to);
        if(res.size() == 0) throw new AssertionError("No transaction found");
        res.forEach(cl -> System.out.println(cl.getGuid() + " :  " + cl.getIsIncome() +  ", " + cl.getPrice() +", "
                + cl.getDateOfTransaction()  +  ", " + cl.getDescription() +  ", " + cl.getUser().getUserName() +  ", " +
                cl.getRetail().getName()));
    }

    //Update Tests
    @Test
    public void testUpdateTransaction() throws IOException {
        LocalDate d = LocalDate.of(2020, 05, 10);
        String desc = "Lord Of The Rings";
        IRetailDAO retailDAO = new HnetMySqlRetailsDAO(CostManagerDAOConfigWrapper.Deserialize("./Config.json"));
        IUsersDAO usersDAO = new HnetMySqlUserDAO(CostManagerDAOConfigWrapper.Deserialize("./Config.json"));
    }
    @Test
    public void testUpdateTransactionUser() throws UsersPlatformException, SQLException, IOException {
        IUsersDAO usersDAO = new HnetMySqlUserDAO(CostManagerDAOConfigWrapper.Deserialize("./Config.json"));
        int userGuid = 8;
        User user = usersDAO.getUser(userGuid);
        int newUserGuid = 4;
        User newUser = usersDAO.getUser(newUserGuid);
        Collection<Transaction> transactionsByUser = tester.getTransactionsByUser(userGuid);
        for (Transaction t :
                transactionsByUser) {
            tester.updateTransactionUser(t.getGuid(), newUser);
        }
    }
    //Insert Tests
    @Test
    public void testInsertTransaction() throws UsersPlatformException, IOException {
        IRetailDAO retailDAO = new HnetMySqlRetailsDAO(CostManagerDAOConfigWrapper.Deserialize("./Config.json"));
        IUsersDAO usersDAO = new HnetMySqlUserDAO(CostManagerDAOConfigWrapper.Deserialize("./Config.json"));
        Collection<User> users = usersDAO.getAllUsers();
        Collection<RetailType> retails = retailDAO.getRetails();
        boolean isIncome = false;
        double price = 1.5;
        for (User user : users) {
            for (RetailType retailType: retails) {
                isIncome = !isIncome;
                price *= 1.5;
                try {
                    tester.insertTransaction(new Transaction( isIncome, price, retailType,user,
                            "Test etset"));
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new AssertionError();
                } catch (UsersPlatformException e) {
                    e.printStackTrace();
                    throw new AssertionError();
                }

            }
        }


        }
    //Delete Tests
    @Test
    public void testDeleteTransaction(){
       int transactionGuid = 1;
        try{
            tester.deleteTransaction(transactionGuid);
        } catch (UsersPlatformException e) {
            e.printStackTrace();
            throw new AssertionError();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new AssertionError();
        }
    }
}
