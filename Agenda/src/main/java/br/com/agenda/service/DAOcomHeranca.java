package br.com.agenda.service;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import br.com.agenda.connection.ConexaoDB;
import br.com.agenda.dao.AbstractDAO;
import br.com.agenda.dao.annotations.ChaveEstrangeira;
import br.com.agenda.dao.annotations.Coluna;
import br.com.agenda.dao.annotations.Transiente;
import br.com.agenda.dao.sql.NameGeneratorSQL;

public class DAOcomHeranca extends AbstractDAO {

	@Override
	public void salvar(Object objeto) throws SQLException {
		
		String nomeTabela = NameGeneratorSQL.gerarNomeTabelaSuperClasseSQL(objeto);
		
		String sql = "INSERT INTO " + nomeTabela;
		sql += "(";
		
		String parteFinalSQL = " VALUES(";
		List<Field> atributosSuperClasse = Arrays.asList(objeto.getClass().getSuperclass().getDeclaredFields());
		List<Field> atributosClasse = Arrays.asList(objeto.getClass().getDeclaredFields());
		
		List<Field> atributos = Stream
				.concat(atributosSuperClasse.stream(), atributosClasse.stream())
                .collect(Collectors.toList());
		
		for (Field atributo : atributos) {
			Transiente transiente = atributo.getDeclaredAnnotation(Transiente.class);
			if (null != transiente) {
				continue;
			}
			Id id = atributo.getDeclaredAnnotation(Id.class);
			if (null == id) {
				ManyToMany mTm = atributo.getDeclaredAnnotation(ManyToMany.class);
				OneToMany oTm = atributo.getDeclaredAnnotation(OneToMany.class);
				
				if (mTm == null && oTm == null) {
					String nomeColuna = atributo
							.getName()
							.toLowerCase();
					
					Coluna coluna = atributo.getDeclaredAnnotation(Coluna.class);
					if (null != coluna && !coluna.nome().equals("_SEM_NOME_")) {
						nomeColuna = coluna.nome();
					}
					
					Column column = atributo.getDeclaredAnnotation(Column.class);
					if (column != null) {
						nomeColuna = column.name();
					}
					
					ChaveEstrangeira chaveEstrangeira = atributo.getDeclaredAnnotation(ChaveEstrangeira.class);
					if (chaveEstrangeira != null) {
						nomeColuna += "_id";
					}
					
					sql +=  nomeColuna + ", ";
				parteFinalSQL += "?, ";
				}
			}
		}
		sql = sql.substring(0, sql.length() - 2) + ")";
		sql += parteFinalSQL.substring(0, parteFinalSQL.length() - 2) + ")";
		
		Connection conexao = ConexaoDB.getConexao();
		PreparedStatement psql = conexao.prepareStatement(sql);
		
		int i = 1;
		for (Field atributo : atributos) {
			Transiente transiente = atributo.getDeclaredAnnotation(Transiente.class);
			if (null != transiente) {
				continue;
			}
			ManyToMany mTm = atributo.getDeclaredAnnotation(ManyToMany.class);
			OneToMany oTm = atributo.getDeclaredAnnotation(OneToMany.class);
			
			if (mTm == null && oTm == null) {
				Id id = atributo.getDeclaredAnnotation(Id.class);
				if (null == id) {
					atributo.setAccessible(true);
					try {
						psql.setObject(i++, atributo.get(objeto));
					} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
						throw new RuntimeException();
					}
				}
			}
		}
		psql.execute();
		conexao.close();
		System.out.println(sql);
	}

	@SuppressWarnings("deprecation")
	public static Object consultarUltimaEntrada(String nome) throws SQLException {
		nome = nome.substring(0,1).toUpperCase().concat(nome.substring(1));
		String pacote = "com.empresa.gestao.entities." + nome;
		Object objeto = new Object();
		try {
			objeto = Class.forName(pacote).newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		String nomeTabela = NameGeneratorSQL.gerarNomeTabelaSQL(objeto);
		
		String sql = "SELECT MAX(id) as numero FROM " + nomeTabela;
		
		Connection conexao = ConexaoDB.getConexao();
		PreparedStatement psql = conexao.prepareStatement(sql);
				
		ResultSet resultado = psql.executeQuery();
		
		Object numeroId = null;
		
		if (resultado.next()) {
			numeroId = resultado.getLong("numero");
		}
		
		conexao.close();
		
		return numeroId;
	}
	
	@Override
	public void salvarObjectHandler(Object objeto) throws SQLException {
		
		String nomeTabela = NameGeneratorSQL.gerarNomeTabelaSuperClasseSQL(objeto);
		
		String sql = "INSERT INTO " + nomeTabela;
		sql += "(";
		
		String parteFinalSQL = " VALUES(";
		List<Field> atributosSuperClasse = Arrays.asList(objeto.getClass().getSuperclass().getDeclaredFields());
		List<Field> atributosClasse = Arrays.asList(objeto.getClass().getDeclaredFields());
		
		List<Field> atributos = Stream
				.concat(atributosSuperClasse.stream(), atributosClasse.stream())
                .collect(Collectors.toList());
		
		for (Field atributo : atributos) {
			Transiente transiente = atributo.getDeclaredAnnotation(Transiente.class);
			if (null != transiente) {
				continue;
			}
			Id id = atributo.getDeclaredAnnotation(Id.class);
			if (null == id) {
				ManyToMany mTm = atributo.getDeclaredAnnotation(ManyToMany.class);
				OneToMany oTm = atributo.getDeclaredAnnotation(OneToMany.class);
				
				if (mTm == null && oTm == null) {
					String nomeColuna = atributo
							.getName()
							.toLowerCase();
					
					Coluna coluna = atributo.getDeclaredAnnotation(Coluna.class);
					if (null != coluna && !coluna.nome().equals("_SEM_NOME_")) {
						nomeColuna = coluna.nome();
					}
					
					Column column = atributo.getDeclaredAnnotation(Column.class);
					if (column != null) {
						nomeColuna = column.name();
					}
					
					ChaveEstrangeira chaveEstrangeira = atributo.getDeclaredAnnotation(ChaveEstrangeira.class);
					if (chaveEstrangeira != null) {
						nomeColuna += "_id";
					}
					
					sql +=  nomeColuna + ", ";
				parteFinalSQL += "?, ";
				}
			}
		}
		sql = sql.substring(0, sql.length() - 2) + ")";
		sql += parteFinalSQL.substring(0, parteFinalSQL.length() - 2) + ")";
		
		Connection conexao = ConexaoDB.getConexao();
		PreparedStatement psql = conexao.prepareStatement(sql);
		
		int i = 1;
		for (Field atributo : atributos) {
			Transiente transiente = atributo.getDeclaredAnnotation(Transiente.class);
			if (null != transiente) {
				continue;
			}
			ManyToMany mTm = atributo.getDeclaredAnnotation(ManyToMany.class);
			OneToMany oTm = atributo.getDeclaredAnnotation(OneToMany.class);
			
			if (mTm == null && oTm == null) {
				Id id = atributo.getDeclaredAnnotation(Id.class);
				if (null == id) {
					atributo.setAccessible(true);
					
					ChaveEstrangeira chaveEstrangeira = atributo.getDeclaredAnnotation(ChaveEstrangeira.class);
					if (chaveEstrangeira != null) {
						String nomeAtributo = atributo
								.getName();
						Object ultimaEntrada = DAOcomHeranca.consultarUltimaEntrada(nomeAtributo);
						
						try {
							psql.setObject(i++, ultimaEntrada);
						} catch (SecurityException | SQLException e) {
							e.printStackTrace();
						}
					} else {
						
						try {
							psql.setObject(i++, atributo.get(objeto));
						} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
							throw new RuntimeException();
						}
					}
					
				}
			}
		}
		psql.execute();
		conexao.close();
		System.out.println(sql);
	}

}
