package cz.vutbr.fit.maros.dip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "publisher")
public class Publisher implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "address")
    @NotNull
    private String address;

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL)
    //@JsonManagedReference("publisher")
    @JsonIgnore
    private List<Book> bookList = new ArrayList<>();

    public void addBook(Book book) {
        this.bookList.add(book);
    }
}
