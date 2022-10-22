package br.com.agenda.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {

	public static Connection getConexao() throws SQLException {
		return DriverManager.getConnection("jdbc:h2:mem:_agenda",
				"sa",
				"");
	}
}
