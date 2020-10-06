var retails = [];
var loggedUser = [];
var totalExpensesSum = 0;
transactions = [];
var serverIp = 0;
var port = 0;

function getRandomColor() {
  var letters = '0123456789ABCDEF';
  var color = '#';
  for (var i = 0; i < 6; i++) {
    color += letters[Math.floor(Math.random() * 16)];
  }
  return color;
}

function setRandomColor(id){

  $(id).css("background", getRandomColor());
}

function initGenerics(){
    totalExpensesSum = 0;
    document.getElementById("total-expenses-num").innerHTML = totalExpensesSum;
    document.getElementById("total-expenses-num").innerHTML += " $";
    transactions = []
    loggedUser = [];
}

function addNewExpensePrice(priceToAdd){
    totalExpensesSum += priceToAdd;
    document.getElementById("total-expenses-num").innerHTML = totalExpensesSum;
    document.getElementById("total-expenses-num").innerHTML += " $";
}

function load(serverIpAddr, portNum){
    serverIp = serverIpAddr;
    port = portNum;
}

function getRetails(){return retails;}
function getRetailsNames(){
    var results = [];
    retails.forEach(function(retail){
        results.push(retail.Name);
    })
    return results;
}

function getLoggedUser(){return loggedUser;}

function setLoggedUser(user){

    loggedUser = user;
}

function initRetails(){
    $.ajax({
         url: 'http://'+serverIp+':'+port+'/api/home/getallretails',
         type: 'GET',
         dataType: 'json',
         beforeSend: function() {
                     $.mobile.showPageLoadingMsg(true);
         },
         success: function(data, textStatus, jqXHR){
            $.mobile.hidePageLoadingMsg();
            retails = data;
            addToDL('retails-choose-list', getRetails());
         },
         error: function(a,b,c) {
                      retails = [];
                      console.log('something went wrong1:',a);
                      console.log('something went wrong2:',b);
                      console.log('something went wrong:3',c);
                 }
    });
};

function initUserTransactions(){
   $.when(getAllUserTransactions()).done(function(results){
        results.forEach(function(res){
            var viewedT = setTransactionForView(res);
            addToTransactionsListView("transactions-list",viewedT);
            if(res.IsIncome == true)
                addNewExpensePrice(res.Price);
            else
                addNewExpensePrice(-1* res.Price);
        });
        return results;
});
};

function setTransactionForView(res){
    var toView = {};
    toView["IsIncome"] = res.IsIncome;
    toView["Guid"] = res.Guid;
    toView["Price"] = res.Price;
    toView["Description"] = res.Description;
    toView["Category"] = res.Retail.Name;
    toView["Date"] = res.DateOfTransaction;
    return toView;
}

function addToTransactionsListView(id, item){
  var color = "#F44336";
  var isIncomeTxt = "-";
  if(item["IsIncome"] == true){
    color = "#03DAC5";
    isIncomeTxt = "+";
  }
  var html = ' <div data-role="collapsible" id="'+item.Guid
  +'" data-collapsed="true" style="background:'+color+' !important;">'
  +'<h3><label style="text-align:left">'
  +item.Category+' : </label><label>'
  +isIncomeTxt + item.Price+'$</label></h3>'
  +'<p style="color:'+color+';">Date :       '+ item.Date+'</p>'
  +'<p style="color:'+color+';">Description : '+ item.Description+'</p>'
//  +'<input onclick="removeTransaction('+item.Guid+')" type="button" value="Remove expense" data-icon="delete">'
  $("#"+id).append(html).collapsibleset('refresh');
}

function addToTransactionsTable(id, item){
    var html = '<tr><td>'+item["Category"]+
    '</td><td>'+item["Description"]+'</td><td>'+item["IsIncome"]+
    '</td><td>'+item["Price"]+'</td><td>'+item["Date"]+'</td></tr>'

   $("#"+id).append(html);
}

function getAllUserTransactions(){
    return $.when($.ajax({
                 url: 'http://'+serverIp+':'+port+'/api/home/getusertransactions/' + loggedUser.Guid,
                 type: 'GET',
                 dataType: 'json',
                  beforeSend: function() {
                             $.mobile.showPageLoadingMsg(true);
                         },
                         complete: function() {
                             $.mobile.hidePageLoadingMsg();
                         },
                 }).then(function(res){
                 return res;}));
};

function addToDL(id,array){
    var options = '';
    for(var i = 0; i < array.length; i++)
        options += '<option id="'+array[i].Guid+'Exp" label="'+array[i].Name +'" value="'+array[i].Guid+'">'+array[i].Name+'</option>';
    document.getElementById(id).innerHTML = options;
};

function addToHomeTransactionsList(id,array){
    var options = '';

    for(var i = 0; i < array.length; i++)
        options += '<li id=\"'+array[i].Guid+'-tran\">'+array[i].IsIncome+' <span class="ui-listview-item-count-bubble" style="">'+array[i].Price+'</span></li>';

    document.getElementById(id).innerHTML = options;
};

function signOut(){
    initGenerics();
    $("#transactions-list").empty();
    goToLogin();
}

function goToHome(){
    if(loggedUser == [])
        goToLogin();
    else
        window.location.href= "#home-page";
}

function goToAddExpense(){

    window.location.href= "#new-transaction-page";
}

function goToLogin(){

    window.location.href= "#login-page";
}

function goToSignUp(){

    window.location.href= "#sign-up-page";
}

function goToDetailedCategory(){

    window.location.href= "#category-detailed-page";
}
