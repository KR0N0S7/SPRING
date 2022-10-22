package br.com.agenda.dao.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class Conversor {

	public static List<Object> resultSetToList(Class<?> clazz, ResultSet resultado) throws SQLException {
		
		ResultSetMetaData metaDados = resultado.getMetaData();
		List<Object> lista = new ArrayList<>();
		
		while (resultado.next()) {

			Object obj = null;
			try {
				obj = clazz.getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			
			int n = 1;
			Field[] atributos = obj.getClass().getDeclaredFields();
			for (Field field : atributos) {
				
//				System.out.println(field.getName() + ": " +  resultado.getObject(n));
//				System.out.println(metaDados.getColumnLabel(n));
				
				field.setAccessible(true);
				try {
					Object valor = resultado.getObject(metaDados.getColumnLabel(n));
					field.set(obj, valor);
				} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
					e.printStackTrace();
				}
				n++;
			}
			lista.add(obj);
		}
		
		return lista;
	}
	
	public static Object resultSetToObject(Object objeto, ResultSet resultado) throws SQLException {
		
		ResultSetMetaData metaDados = resultado.getMetaData();
		
		while (resultado.next()) {
			int n = 1;
			for (Field field : objeto.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				try {
					field.set(objeto, resultado.getObject(metaDados.getColumnLabel(n)));
				} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
					e.printStackTrace();
				}
				n++;
			}
		}

		return objeto;		
	}
	
	public static Object resultSetToAtributeList(ResultSet resultado) throws SQLException {
	
		List<Object> lista = new ArrayList<>();
		
		int n = 1;
		if (resultado.next()) {
			lista.add(resultado.getObject(n++));
		}
		
		return lista;
	}
}
