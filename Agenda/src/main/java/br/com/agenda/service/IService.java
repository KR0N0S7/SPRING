package br.com.agenda.service;

import java.sql.SQLException;

public interface IService {

	void salvarObject(Object obj) throws SQLException;
	
	Object listarObjects(Class<?> clazz) throws SQLException;

	Object obterObject(Long id, Class<?> clazz) throws SQLException;
	
	void alterarObject(Long id, Object obj) throws SQLException;

	void excluirObject(Long id, Object objeto) throws SQLException;
}
