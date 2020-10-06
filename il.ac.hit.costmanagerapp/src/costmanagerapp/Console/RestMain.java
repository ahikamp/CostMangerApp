package costmanagerapp.Console;

import costmanagerapp.API.AbstractHttpServer;
import costmanagerapp.API.CostManagerHttpServer;
import costmanagerapp.API.RestModelConnector;
import costmanagerapp.Config.CostManagerDAOConfig;
import costmanagerapp.Config.CostManagerDAOConfigWrapper;
import costmanagerapp.lib.DAO.HnetMySqlRetailsDAO;
import costmanagerapp.lib.DAO.HnetMySqlTransactionDAO;
import costmanagerapp.lib.DAO.HnetMySqlUserDAO;
import costmanagerapp.lib.UsersPlatformException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RestMain {
    public static void main(String args[]) throws IOException, UsersPlatformException {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        System.out.println("Current relative path is: " + s);
        CostManagerDAOConfig config = CostManagerDAOConfigWrapper.Deserialize("./Config.json");
        RestModelConnector restModelConnector =
                new RestModelConnector(new HnetMySqlUserDAO(config), new HnetMySqlRetailsDAO(config),
                        new HnetMySqlTransactionDAO(config));
        AbstractHttpServer server = new CostManagerHttpServer(1234, restModelConnector);
        server.start();
        System.out.println("Started serving - port : " + 1234);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        br.readLine();
    }
}

