package costmanagerapp.API;

import costmanagerapp.lib.DAO.IRetailDAO;
import costmanagerapp.lib.DAO.ITransactionDAO;
import costmanagerapp.lib.DAO.IUsersDAO;
import costmanagerapp.lib.UsersPlatformException;

public class RestModelConnector {
    private IUsersDAO usersDAO;
    private IRetailDAO retailDAO;
    private ITransactionDAO transactionDAO;

    public RestModelConnector(IUsersDAO usersDAO, IRetailDAO retailDAO, ITransactionDAO transactionDAO) throws UsersPlatformException {
       if(usersDAO == null || retailDAO == null || transactionDAO == null)throw new UsersPlatformException("Invalid DAO");
        this.usersDAO = usersDAO;
        this.retailDAO = retailDAO;
        this.transactionDAO = transactionDAO;
    }

    public ITransactionDAO getTransactionDAO() {
        return transactionDAO;
    }

    public IRetailDAO getRetailDAO() {
        return retailDAO;
    }
    public IUsersDAO getUsersDAO() {
        return usersDAO;
    }
}
