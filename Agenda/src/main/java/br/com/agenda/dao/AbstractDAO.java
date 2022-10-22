package br.com.agenda.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import br.com.agenda.connection.ConexaoDB;
import br.com.agenda.dao.annotations.ChaveEstrangeira;
import br.com.agenda.dao.annotations.Coluna;
import br.com.agenda.dao.annotations.Transiente;
import br.com.agenda.dao.mapper.Conversor;
import br.com.agenda.dao.sql.NameGeneratorSQL;

public class AbstractDAO implements InterfaceDAO {

	@Override
	public void salvar(Object objeto) throws SQLException {
		
		String nomeTabela = NameGeneratorSQL.gerarNomeTabelaSQL(objeto);
		
		String sql = "INSERT INTO " + nomeTabela;
		sql += "(";
		
		String parteFinalSQL = " VALUES(";
		Field[] atributos = objeto.getClass().getDeclaredFields();
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

	@Override
	public void alterar(Long id, Object objeto) throws SQLException {
		
		String nomeTabela = NameGeneratorSQL.gerarNomeTabelaSQL(objeto);
		
		String sql = "UPDATE " + nomeTabela;
		String parteFinal = " SET ";
		
		Field[] atributos = objeto.getClass().getDeclaredFields();
		for (Field atributo : atributos) {
			Transiente transiente = atributo.getDeclaredAnnotation(Transiente.class);
			if (null != transiente) {
				continue;
			}
			
			Id identificacao = atributo.getDeclaredAnnotation(Id.class);
			if (null != identificacao) {
				continue;
			}
			
			ManyToMany mTm = atributo.getDeclaredAnnotation(ManyToMany.class);
			OneToMany oTm = atributo.getDeclaredAnnotation(OneToMany.class);
			
			if (mTm == null && oTm == null) {
				String nomeColuna = atributo
						.getName()
						.toLowerCase();
				Coluna coluna = atributo.getDeclaredAnnotation(Coluna.class);
				ChaveEstrangeira chaveEstrangeira = atributo.getDeclaredAnnotation(ChaveEstrangeira.class);
				if (chaveEstrangeira != null) {
					nomeColuna += "_id";
				}
				
				Column column = atributo.getDeclaredAnnotation(Column.class);
				if (column != null) {
					nomeColuna = column.name();
				}
				if (null != coluna && !coluna.nome().equals("_SEM_NOME_")) {
					nomeColuna = coluna.nome();
				}
				nomeColuna += " = ";
				parteFinal += nomeColuna + "?, ";
			}
		}
		sql = sql + parteFinal;
		sql = sql.substring(0, sql.length() - 2) + " WHERE id=" + id;
				
		Connection conexao = ConexaoDB.getConexao();
		PreparedStatement psql = conexao.prepareStatement(sql);
		
		int i = 1;
		for (Field atributo : atributos) {
			Transiente transiente = atributo.getDeclaredAnnotation(Transiente.class);
			if (null != transiente) {
				continue;
			}
			Id identificacao = atributo.getDeclaredAnnotation(Id.class);
			if (null != identificacao) {
				continue;
			}
			ManyToMany mTm = atributo.getDeclaredAnnotation(ManyToMany.class);
			OneToMany oTm = atributo.getDeclaredAnnotation(OneToMany.class);
			
			if (mTm == null && oTm == null) {
				atributo.setAccessible(true);
				try {
					psql.setObject(i++, atributo.get(objeto));
				} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
					throw new RuntimeException();
				}
			}
		}
		psql.execute();
		conexao.close();
		System.out.println(sql);
	}

	@Override
	public void excluir(Long id, Object objeto) throws SQLException {
		
		String nomeTabela = NameGeneratorSQL.gerarNomeTabelaSQL(objeto);
		
		String sql = "DELETE FROM " + nomeTabela + " WHERE id=" + id;
		
		Connection conexao = ConexaoDB.getConexao();
		PreparedStatement psql = conexao.prepareStatement(sql);
		
//		Field id = objeto
//				.getClass()
//				.getDeclaredField("id");
//		id.setAccessible(true);
//		psql.setObject(1, id.get(objeto));
		
		psql.execute();
		conexao.close();
		System.out.println(sql);
	}

	@Override
	public List<Object> listar(Class<?> clazz) throws SQLException {
				
		Object objeto;
		try {
			objeto = clazz.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | 
				IllegalArgumentException | InvocationTargetException | 
				NoSuchMethodException | SecurityException e1) {
			throw new RuntimeException();
		}
		
		String nomeTabela = NameGeneratorSQL.gerarNomeTabelaSQL(objeto);
		
		String sql = "SELECT * FROM " + nomeTabela;
		
		Connection conexao = ConexaoDB.getConexao();
		PreparedStatement psql = conexao.prepareStatement(sql);
		
		ResultSet resultado = psql.executeQuery();
			
		List<Object> lista = null;
		try {
			lista = Conversor.resultSetToList(clazz, resultado);
		} catch (SecurityException |
				IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		conexao.close();
		System.out.println(sql);
		
		return lista;
	}

	@Override
	public Object consultarPorId(Long id, Class<?> clazz) throws SQLException {
		
		Object objeto;
		try {
			objeto = clazz.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | 
				IllegalArgumentException | InvocationTargetException | 
				NoSuchMethodException | SecurityException e1) {
			throw new RuntimeException();
		}
		
		String nomeTabela = NameGeneratorSQL.gerarNomeTabelaSQL(objeto);
		
		String sql = "SELECT * FROM " + nomeTabela + " WHERE id=" + id;
 
		Connection conexao = ConexaoDB.getConexao();
		PreparedStatement psql = conexao.prepareStatement(sql);
				
		ResultSet resultado = psql.executeQuery();
		
		try {
			
			objeto = Conversor.resultSetToObject(objeto, resultado);
		} catch (SecurityException |
				IllegalArgumentException e) {
			e.printStackTrace();
		}	
		
		conexao.close();
		System.out.println(sql);
		
		return objeto;
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
	
	public void salvarObjectHandler(Object objeto) throws SQLException {
		
		String nomeTabela = NameGeneratorSQL.gerarNomeTabelaSQL(objeto);
		
		String sql = "INSERT INTO " + nomeTabela;
		sql += "(";
		
		String parteFinalSQL = " VALUES(";
		Field[] atributos = objeto.getClass().getDeclaredFields();
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
						String nomeAtributo = atributo.getName();
						Long ultimaEntrada = (Long) AbstractDAO.consultarUltimaEntrada(nomeAtributo);
						
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
	
	public static Object listarObjetoEspecifico(String coluna, String tabela) throws SQLException {
		
		
		String sql = "SELECT " + coluna + " FROM " + tabela;
		
		Connection conexao = ConexaoDB.getConexao();
		PreparedStatement psql = conexao.prepareStatement(sql);
		
		ResultSet resultado = psql.executeQuery();
			
		Object listaAtributos = Conversor.resultSetToAtributeList(resultado);
		
		conexao.close();
		System.out.println(sql);
		
		return listaAtributos;
	}
}
