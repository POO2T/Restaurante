package Model;
import Enums.StatusMesa;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data

@Entity
@Table(name = "mesas")
public class Mesa {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nome; // Ex: "Mesa 01", "Balcão"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusMesa status; // Usando o Enum StatusMesa

    // Uma mesa pode ter várias comandas ativas ou no histórico
    @OneToMany(mappedBy = "mesa")
    private List<Comanda> comandas;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public StatusMesa getStatus() { return status; }
    public void setStatus(StatusMesa status) { this.status = status; }
}


