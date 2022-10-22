package br.com.agenda.dao;

import java.sql.SQLException;
import java.util.List;

public interface InterfaceDAO {

	public void salvar(Object objeto) throws SQLException;
	public Object consultarPorId(Long id, Class<?> clazz) throws SQLException;
	public List<Object> listar(Class<?> clazz) throws SQLException;
	public void alterar(Long id, Object objeto) throws SQLException;
	public void excluir(Long id, Object objeto) throws SQLException;
}
