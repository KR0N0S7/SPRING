package br.com.agenda.dao.sql;

import javax.persistence.Table;

import br.com.agenda.dao.annotations.Tabela;

public abstract class NameGeneratorSQL {

	public static String gerarNomeTabelaSQL(Object objeto) {
		Tabela tabela = objeto
				.getClass()
				.getDeclaredAnnotation(Tabela.class);
		
		String nomeTabela = objeto
				.getClass()
				.getSimpleName()
				.toLowerCase();
		
		Table tabelaRenomeada = objeto
				.getClass()
				.getDeclaredAnnotation(Table.class);
		
		if (tabelaRenomeada != null) {
			nomeTabela = tabelaRenomeada.name();
		}
		
		if (null != tabela && !tabela.nome().equals("_SEM_NOME_")) {
			nomeTabela = tabela.nome();
		}
		
		return nomeTabela;
	}
	
	public static String gerarNomeTabelaSuperClasseSQL(Object objeto) {
		Tabela tabela = objeto
				.getClass()
				.getSuperclass()
				.getDeclaredAnnotation(Tabela.class);
		
		String nomeTabela = objeto
				.getClass()
				.getSuperclass()
				.getSimpleName()
				.toLowerCase();
		
		Table tabelaRenomeada = objeto
				.getClass()
				.getSuperclass()
				.getDeclaredAnnotation(Table.class);
		
		if (tabelaRenomeada != null) {
			nomeTabela = tabelaRenomeada.name();
		}
		
		if (null != tabela && !tabela.nome().equals("_SEM_NOME_")) {
			nomeTabela = tabela.nome();
		}
		
		return nomeTabela;
	}
}
