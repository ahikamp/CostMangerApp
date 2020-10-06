package costmanagerapp.lib.DAO;

import costmanagerapp.lib.Models.User;
import costmanagerapp.lib.UsersPlatformException;
import java.sql.SQLException;
import java.util.Collection;

public interface IUsersDAO {
    User getUser(int userId) throws UsersPlatformException;
    User getUser(String userName) throws UsersPlatformException;
    Collection <User> getAllUsers() throws UsersPlatformException;
    void insertUser(User user) throws UsersPlatformException, SQLException;
    void deleteUser(int id) throws UsersPlatformException, SQLException;
    void setPassword(int id , String password) throws UsersPlatformException, SQLException;;
}