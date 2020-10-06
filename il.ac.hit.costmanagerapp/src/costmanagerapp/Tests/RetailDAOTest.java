package costmanagerapp.Tests;

import costmanagerapp.Config.CostManagerDAOConfigWrapper;
import costmanagerapp.lib.DAO.HnetMySqlRetailsDAO;
import costmanagerapp.lib.DAO.IRetailDAO;
import costmanagerapp.lib.Models.RetailType;
import costmanagerapp.lib.UsersPlatformException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

public class RetailDAOTest {
    static IRetailDAO tester;
    @BeforeClass
    public static void testSetup() throws IOException {
        tester = new HnetMySqlRetailsDAO(CostManagerDAOConfigWrapper.Deserialize("./Config.json"));
    }
    @AfterClass
    public static void testCleanup() {
        // Do your cleanup here like close URL connection , releasing resources etc
    }

    //Get tests
    @Test
    public void testGetRetailByGuid() throws Exception {
        int retailGuid = 1;
        RetailType result = tester.getRetail(retailGuid);
        if (result == null) throw new AssertionError();
        System.out.println(result.getGuid() +" : "+result.getName());
    }
    @Test
    public void testGetAllRetails() throws UsersPlatformException {
        Collection<RetailType> cl = tester.getRetails();
        if(cl.size() == 0) throw new AssertionError("empty list");
        cl.forEach(c -> System.out.println(c.getGuid() +" : "+ c.getType()));
    }
    //Insert Tests
    @Test
    public void insertRetail(){
        String retailName = "Sports";
        try{
            tester.insertRetail(new RetailType(retailName));
        } catch (UsersPlatformException e) {
            throw new AssertionError("Retail insertion failure");
        } catch (SQLException e) {
            throw new AssertionError("Retail insertion failure");
        }

    }
    @Test
    public void testInsertRetails(){
        String[] retailNames = {"Food", "HouseHold", "Clothes", "Sports"};

        for (int i = 0; i< retailNames.length;i++){
            try{
                tester.insertRetail(new RetailType(retailNames[i]));
            } catch (UsersPlatformException e) {
                throw new AssertionError("Retail insertion failure");
            } catch (SQLException e) {
                throw new AssertionError("Retail insertion failure");
            }
        }
    }
    //Delete Tests
    @Test
    public void testDeleteRetail(){
        int retailGuid = 1;
        try {
            tester.deleteRetail(retailGuid);
        } catch (UsersPlatformException e) {
            throw new AssertionError("Could not delete retail, " + e.getMessage());
        } catch (SQLException e) {
            throw new AssertionError("Could not delete retail, " + e.getMessage());
        }
    }
    //Update tests
    @Test
    public void testUpdateRetail(){
        int retailGuid = 1;
        String retailsNewName = "None";
        try {
            tester.updateRetailName(retailGuid,retailsNewName);
        } catch (SQLException e) {
            throw new AssertionError("Could not update retails name" + e.getMessage());
        } catch (UsersPlatformException e) {
            throw new AssertionError("Could not update retails name" + e.getMessage());
        }
    }
}
