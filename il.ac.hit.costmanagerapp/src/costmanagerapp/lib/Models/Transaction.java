package costmanagerapp.lib.Models;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.Entity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Table;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.Random;


@Entity
@Table(appliesTo = "transactions")
public class Transaction {
    @Id @GeneratedValue
    @Column(name = "Guid")
    private int Guid;
    @Column(name = "IsIncome")
    private boolean IsIncome;
    @Column(name = "Price")
    private double Price;
    @Column(name = "UserGuid")
    private RetailType Retail;
    @Column(name = "UserGuid")
    private User User;
    @Column(name = "DateOfTransaction", columnDefinition = "DATE")
    private Date DateOfTransaction;
    @Column(name = "Description")
    private String Description;

    public Transaction(@NotNull int guid, @NotNull boolean isIncome, @NotNull  double price, @NotNull  RetailType retailType, @NotNull  User user,
                       Date date,String description){
        //TODO::: Generate id automatically
        Guid = guid;
        IsIncome = isIncome;
        Price = price;
        Retail = retailType;
        User = user;
        DateOfTransaction = date;
        Description = description;
    }
    public Transaction(@NotNull boolean isIncome, @NotNull  double price, @NotNull  RetailType retailType, @NotNull  User user,
                       String description){
        //TODO::: Generate id automatically
        Guid = new Random().nextInt(100000);
        IsIncome = isIncome;
        Price = price;
        Retail = retailType;
        User = user;
        DateOfTransaction = Date.from(Instant.now());
        Description = description;
    }
    public Transaction() {

    }

    public int getGuid() {
        return Guid;
    }
    public boolean getIsIncome() {
        return IsIncome;
    }
    public double getPrice() {
        return Price;
    }
    public RetailType getRetail() {
        return Retail;
    }
    public User getUser() {
        return User;
    }
    public Date getDateOfTransaction() {
        return DateOfTransaction;
    }
    public String getDescription() {
        return Description;
    }
    public void setIsIncome(boolean isIncome){IsIncome = isIncome;}
    public void setGuid(int guid){Guid = guid;}
    public void setPrice(double price){Price = price;}
    public void setRetail(RetailType retail){Retail = retail;}
    public void setUser(User user){User = user;}
    public void setDateOfTransaction(Date dateOfTransaction){DateOfTransaction = dateOfTransaction;}
    public void setDescription(String description){Description = description;}
}