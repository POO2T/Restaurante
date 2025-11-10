package com.example.Back_End_Restaurante.Model;

import com.example.Back_End_Restaurante.Enums.TipoFuncionario;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "funcionarios")
public class Funcionario extends Usuario {



    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoFuncionario cargo; // Usando o Enum TipoFuncionario

    @Column(nullable = false)
    private double salario;

    @Column(nullable = false)
    private boolean ativo;

    public String getNome() {
        return super.getNome();
    }

    public void setNome(String nome) {
        super.setNome(nome);
    }

    public TipoFuncionario getCargo() {
        return cargo;
    }

    public void setCargo(TipoFuncionario cargo) {
        this.cargo = cargo;}

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public boolean isAtivo() {
        return ativo;}

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;}

    public void setEmail(String email) {
        super.setEmail(email);
    }

    public String getSenha() {
        return super.getSenha();
    }
    public void setSenha(String senha) {
        super.setSenha(senha);
    }

}
