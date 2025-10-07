package Model;
import Enums.StatusMesa;
import jakarta.persistence.*;
import lombok.Data;
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
}
