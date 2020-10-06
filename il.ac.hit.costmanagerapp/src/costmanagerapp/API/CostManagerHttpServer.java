package costmanagerapp.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.istack.internal.NotNull;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import costmanagerapp.lib.DAO.IRetailDAO;
import costmanagerapp.lib.DAO.ITransactionDAO;
import costmanagerapp.lib.DAO.IUsersDAO;
import costmanagerapp.lib.Models.RetailType;
import costmanagerapp.lib.Models.Transaction;
import costmanagerapp.lib.Models.User;
import costmanagerapp.lib.UsersPlatformException;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

public class CostManagerHttpServer extends AbstractHttpServer<Transaction> {
    private static Collection<Transaction> transactions;
    private RestModelConnector restModelConnector;
    private static OutputStream outputStream;
    private Gson jsonCreator;

    //C'tors
    public CostManagerHttpServer(@NotNull int portNum, IUsersDAO usersDAO, IRetailDAO retailDAO, ITransactionDAO transactionDAO) throws UsersPlatformException {
        this(portNum ,new RestModelConnector(usersDAO, retailDAO, transactionDAO));
    }

    public CostManagerHttpServer(int portNum, RestModelConnector restModelConnector) {
        port = portNum;
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        jsonCreator = builder.create();
        transactions = new ArrayList<>();
        this.restModelConnector = restModelConnector;
    }

    //Public Methods
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }

    @Override
    public void stop() {

    }

    @Override
    public void start() throws UsersPlatformException {
        try{
            httpServer = HttpServer.create(new InetSocketAddress(port), port);
            httpServer.createContext("/", httpExchange -> {
                try {
                    defineRequestTypeAndDo(httpExchange);
                } catch (UsersPlatformException e) {
                    e.printStackTrace();
                }
            });
            httpServer.start();
        } catch (IOException e) {
            throw new UsersPlatformException("Could not start HttpServerApi - " + e.getMessage());
        }
    }

    //Http Generic
    private void responseMessage(HttpExchange httpExchange, int resCode, String data) throws UsersPlatformException {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        try {
            httpExchange.sendResponseHeaders(resCode, bytes.length);
            outputStream = httpExchange.getResponseBody();
            outputStream.write(bytes);
            outputStream.close();
        } catch (IOException e) {
            throw new UsersPlatformException("Server could not response correctly, " + e.getMessage());
        }

    }

    //Declaration and definition methods
    public static String parseBody(HttpExchange httpExchange) throws IOException {
        System.out.println("trying prase body");
        return parseBodyToString(httpExchange);
    }

    private void defineRequestTypeAndDo(HttpExchange httpExchange) throws UsersPlatformException {
        String methodType = httpExchange.getRequestMethod();
        switch (methodType.toLowerCase()){
            case "get":{
                defineGetUriAndDo(httpExchange);
                break;
            }
            case
                    "post":{
                definePostUriAndDo(httpExchange);
                break;
            }
            case "delete":{
                defineDeleteUriAndDo(httpExchange);
                break;
            }
            default:
                responseMessage(httpExchange, 405, jsonCreator.toJson("Not valid request method"));
        }
    }

    private void defineGetUriAndDo(HttpExchange httpExchange) {
        String uri = httpExchange.getRequestURI().toString();
        try {
            if (uri.toLowerCase().contains("/api/home/getusertransactions"))
                getUserTransactions(httpExchange);
            else if (uri.toLowerCase().contains("/api/home/getallretails"))
                getRetails(httpExchange);
            else if (uri.toLowerCase().contains("api/home/gettransactionsbyretail"))
                getTransactionsByRetail(httpExchange);
            else if (uri.toLowerCase().contains("api/home/gettransactionsbydates"))
                getTransactionsByDates(httpExchange);
            else
                responseMessage(httpExchange, 404, jsonCreator.toJson("Invalid URI"));
        } catch (UsersPlatformException ex) {
            ex.printStackTrace();
        }
    }


    private void definePostUriAndDo(HttpExchange httpExchange) {
        String uri = httpExchange.getRequestURI().toString();
        try {
            if (uri.toLowerCase().contains("api/login"))
                postLogin(httpExchange);
            else if (uri.toLowerCase().contains("api/home/addtransaction"))
                postTransaction(httpExchange);
            else if (uri.toLowerCase().contains("api/home/signup"))
                postSignUp(httpExchange);
            else
                responseMessage(httpExchange, 404, jsonCreator.toJson("Invalid URI"));

        } catch (UsersPlatformException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void defineDeleteUriAndDo(HttpExchange httpExchange) {
        String uri = httpExchange.getRequestURI().toString();
        try {
         if (uri.toLowerCase().contains("/api/home/deletetransaction"))
                deleteTransaction(httpExchange);
         else
             responseMessage(httpExchange, 501, jsonCreator.toJson("Invalid URI"));
         } catch (UsersPlatformException ex) {
            ex.printStackTrace();
        }
    }

    private static String parseBodyToString(HttpExchange httpExchange) throws IOException {
        try{
            System.out.println("parsing json body here");
            if(httpExchange.getRequestBody()== null)return "{}";
            InputStream requestBody = httpExchange.getRequestBody();

            StringBuilder sb = new StringBuilder();
            System.out.println("created sb" );
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(requestBody));
            System.out.println("created buffer");
            String i;int ii =0;
            while ((i = bufferedReader.readLine()) != null) {
                System.out.println("in"+ii+i);
                sb.append(i);
                ii++;
            }
            System.out.println(sb);
            if(ii==0){
                System.out.println("empty");
                return "{}";
            }
            return String.valueOf(sb);}
        catch (Exception e){
            System.out.println("error parsing body"+ e);
            return "";
        }
    }

    private static String parseEncodedTypeBody(HttpExchange httpExchange) throws IOException  {
        String bodyEncoded = parseBodyToString(httpExchange);
        String body = URLDecoder.decode(bodyEncoded, "UTF8");
        if(body.charAt(0) == '=')
            return body.substring(1);
        return body;
    }

    private User parseUserBody(HttpExchange httpExchange) throws IOException {
        String bodyStr = parseBody(httpExchange);
        Gson gson = new Gson();
        User userReceived = gson.fromJson(bodyStr, User.class);
        return userReceived;
    }

    //Get Api Methods
    private void getTransactionsByRetail(HttpExchange httpExchange) throws UsersPlatformException {

        String[] uriParams = httpExchange.getRequestURI().getQuery().split("&");
        String[] params = uriParams[0].split("=");
        String[] retailParams = uriParams[1].split("=");
        if(!(params[0].toLowerCase().equals("userid") && retailParams[0].toLowerCase().equals("retailid")))
        {
            responseMessage(httpExchange, 400, "WrongUriParams");
            return;
        }
        int userId = Integer.parseInt(params[params.length - 1]);
        int retailId = Integer.parseInt(retailParams[retailParams.length - 1]);
        try {
            Collection<Transaction> transactionsByUser = restModelConnector.getTransactionDAO().getTransactionsByUser(userId);

            transactionsByUser = transactionsByUser.stream().filter(t -> t.getRetail().getGuid() == retailId).collect(Collectors.toList());
            String j = jsonCreator.toJson(transactionsByUser);
            responseMessage(httpExchange, 200, j);
        } catch (UsersPlatformException e) {
            responseMessage(httpExchange, 404, jsonCreator.toJson(e.getMessage()));
        } catch (Exception e) {
            responseMessage(httpExchange, 404, jsonCreator.toJson(e.getMessage()));
        }
    }

    private void getRetails(HttpExchange httpExchange) throws UsersPlatformException {
        try {
            Collection<RetailType> retails = restModelConnector.getRetailDAO().getRetails();
            responseMessage(httpExchange, 200, jsonCreator.toJson(retails));
        } catch (UsersPlatformException e) {
            responseMessage(httpExchange, 404, jsonCreator.toJson(e.getMessage()));
        }
    }

    private void getUserTransactions(HttpExchange httpExchange) throws UsersPlatformException {
        String[] uri = httpExchange.getRequestURI().toString().split("/");
        int id = Integer.parseInt(uri[uri.length - 1]);
        try {
            Collection<Transaction> transactionsByUser = restModelConnector.getTransactionDAO().getTransactionsByUser(id);
            String j = jsonCreator.toJson(transactionsByUser);
            responseMessage(httpExchange, 200, j);
        } catch (UsersPlatformException e) {
            responseMessage(httpExchange, 404, jsonCreator.toJson(e.getMessage()));
        }
        catch (Exception e){
            responseMessage(httpExchange, 404, jsonCreator.toJson(e.getMessage()));
        }
    }

    private void getTransactionsByDates(HttpExchange httpExchange) throws UsersPlatformException {
        String[] uriParams = httpExchange.getRequestURI().getQuery().split("&");
        try {

            String[] userId = uriParams[0].split("=");
            String[] fromDateStrArray = uriParams[1].split("=");
            String[] toDateStrArray = uriParams[2].split("=");
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date fromDate = format.parse(fromDateStrArray[1]);
            Date toDate = format.parse(toDateStrArray[1]);

            Collection<Transaction> transactionsByDateRange = restModelConnector.getTransactionDAO().getTransactionsByDateRange(fromDate, toDate,Integer.parseInt(userId[1]));
            String j = jsonCreator.toJson(transactionsByDateRange);
            responseMessage(httpExchange, 200, j);
        } catch (UsersPlatformException e) {
            responseMessage(httpExchange, 404, jsonCreator.toJson(e.getMessage()));
        } catch (ParseException e) {
            responseMessage(httpExchange, 404, jsonCreator.toJson(e.getMessage()));
        }
    }

    //Post Api Methods
    private void postTransaction(HttpExchange httpExchange) throws IOException, UsersPlatformException {
        String bodyStr = parseBody(httpExchange);
        Transaction transactionToAdd = generateTransactionToInsert(bodyStr);
        try{
            restModelConnector.getTransactionDAO().insertTransaction(transactionToAdd);
            responseMessage(httpExchange, 200, jsonCreator.toJson(transactionToAdd));
        } catch (UsersPlatformException e) {
            responseMessage(httpExchange, 400, jsonCreator.toJson("Transaction's params incorrect"));
        } catch (SQLException e) {
            responseMessage(httpExchange, 400, jsonCreator.toJson("Transaction's params incorrect"));
        }
    }

    private Transaction generateTransactionToInsert(String transactionJsonString) {
        Transaction transactionToAdd = jsonCreator.fromJson(transactionJsonString, Transaction.class);
        if(transactionToAdd == null)
            return null;
        if(transactionToAdd.getDateOfTransaction() == null)
            transactionToAdd.setDateOfTransaction(new Date());
        try {
            transactionToAdd.setRetail(restModelConnector.getRetailDAO().getRetail(transactionToAdd.getRetail().getGuid()));
            transactionToAdd.setUser(restModelConnector.getUsersDAO().getUser(transactionToAdd.getUser().getGuid()));
            return transactionToAdd;
        } catch (UsersPlatformException e) {
            return null;
        }
    }

    private void postLogin(HttpExchange httpExchange) throws IOException, UsersPlatformException {
        User userReceived = parseUserBody(httpExchange);
        if(userReceived.getUserName() == null || userReceived.getPassword() == null){
            responseMessage(httpExchange, 401, jsonCreator.toJson("Incorrect Parameters"));
            return;
        }
        try{
            User user = restModelConnector.getUsersDAO().getUser(userReceived.getUserName());
            if(userReceived.getPassword().equals(user.getPassword())&& userReceived.getUserName().toLowerCase()
                    .equals(user.getUserName().toLowerCase()))
                responseMessage(httpExchange, 200, jsonCreator.toJson(user));
            else
                responseMessage(httpExchange, 404, jsonCreator.toJson("Incorrect Password"));
        } catch (UsersPlatformException e) {
            responseMessage(httpExchange, 401, jsonCreator.toJson("No Such user found"));
        }
    }

    private void postSignUp(HttpExchange httpExchange) throws IOException, UsersPlatformException {
        User userReceived = parseUserBody(httpExchange);
        if(userReceived.getUserName() == null || userReceived.getPassword() == null || userReceived.getEmail() == null){
            responseMessage(httpExchange, 400, jsonCreator.toJson("Incorrect Parameters"));
            return;
        }
        try {
            User user = restModelConnector.getUsersDAO().getUser(userReceived.getUserName());
            if (user == null) {
                user = new User(userReceived.getUserName(), userReceived.getEmail(), userReceived.getPassword());
                restModelConnector.getUsersDAO().insertUser(user);
                responseMessage(httpExchange, 200, jsonCreator.toJson(user));
            }
            else
                responseMessage(httpExchange, 404, jsonCreator.toJson("User exists"));
        }
       catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //Delete Api Methods
    private void deleteTransaction(HttpExchange httpExchange) throws UsersPlatformException {
        String[] uri = httpExchange.getRequestURI().toString().split("/");
        int id = Integer.parseInt(uri[uri.length - 1]);
        try {
            restModelConnector.getTransactionDAO().deleteTransaction(id);
            responseMessage(httpExchange, 200, "Res : transaction deleted successfully");
        } catch (UsersPlatformException e) {
            responseMessage(httpExchange, 404, jsonCreator.toJson(e.getMessage()));
        } catch (SQLException e) {
            responseMessage(httpExchange, 404, jsonCreator.toJson(e.getMessage()));
        }
    }




}
