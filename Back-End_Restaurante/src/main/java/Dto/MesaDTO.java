package Dto;


import Enums.StatusMesa;

public class MesaDTO
{
    private Long id;
    private String nome;
    private StatusMesa status;

    // Construtores
    public MesaDTO() {}

    public MesaDTO(Long id,String nome , StatusMesa status) {
        this.id = id;
        this.nome = nome;
        this.status = status;
}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public StatusMesa getStatus() { return status; }
    public void setStatus(StatusMesa status) { this.status = status; }
}