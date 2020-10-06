package costmanagerapp.lib.DAO;

import costmanagerapp.lib.Models.RetailType;
import costmanagerapp.lib.UsersPlatformException;
import java.sql.SQLException;
import java.util.Collection;

public interface IRetailDAO {
    RetailType getRetail(int guid) throws UsersPlatformException;
    Collection<RetailType> getRetails() throws UsersPlatformException;
    void updateRetailName(int guid, String newName) throws SQLException, UsersPlatformException;
    void insertRetail(RetailType retailType) throws UsersPlatformException, SQLException;
    void deleteRetail(int guid) throws UsersPlatformException, SQLException;
}
