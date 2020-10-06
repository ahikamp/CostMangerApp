package costmanagerapp.lib.Models;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Entity;
import org.hibernate.annotations.Table;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Random;

@Entity
@Table(appliesTo = "retailtype")
public class RetailType {
    @Id @GeneratedValue
    @Column(name = "Guid")
    private int Guid;
    @Column(name = "Name")
    private String Name;

    public RetailType(int guid, @NotNull String name){
        Guid = guid;
        Name = name;
    }
    public RetailType(){ }
    public RetailType(String retailName) {
        Name = retailName;
    }

    public int getGuid() {
        return Guid;
    }
    public void setGuid(int guid){Guid = guid;}
    public String getType(){
        return Name;
    }
    public String getName() {
        return Name;
    }
    public void setName(String name){Name = name;}
}
