package br.com.agenda.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cadastros")
public class Cadastro {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String acoes;
	private String email;
	private String nome;
	private Integer telefone;
	
	public Long getId() {	return id;	}
	public void setId(Long id) {	this.id = id;	}
	public String getNome() {	return nome;	}
	public void setNome(String nome) {	this.nome = nome;	}
	public Integer getTelefone() {	return telefone;	}
	public void setTelefone(Integer telefone) {	this.telefone = telefone;	}
	public String getEmail() {	return email;	}
	public void setEmail(String email) {	this.email = email;	}
	public String getAcoes() {	return acoes;	}
	public void setAcoes(String acoes) {	this.acoes = acoes;	}
	
}
