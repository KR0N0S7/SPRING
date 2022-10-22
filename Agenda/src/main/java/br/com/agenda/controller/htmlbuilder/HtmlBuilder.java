package br.com.agenda.controller.htmlbuilder;

public abstract class HtmlBuilder {

	protected static final String CRIAR_ALTERAR = "form";
	protected static final String LISTAR = "listar";
	protected static final int CONTROLLER_LENGTH = 10;
	
	public static String nomeEntidade(String nomeClasseController) throws Exception {
		String nomeEntidade = nomeClasseController
				.substring(0, nomeClasseController.length() - CONTROLLER_LENGTH);
		
		return nomeEntidade;
	}
	
	public static String construtorForm(String nomeClasseController) throws Exception {
		String nomeEntidadeMinusculo = nomeClasseController.toLowerCase();
		
		String html = nomeEntidadeMinusculo + "/" + CRIAR_ALTERAR + ".html";
		
		return html;
	}
	
	public static String construtorRedirect(String nomeClasseController) throws Exception {
		String nomeEntidade = nomeClasseController
				.substring(0, nomeClasseController.length() - CONTROLLER_LENGTH);
		String nomeEntidadeMinusculo = nomeEntidade.toLowerCase();
		
		String redirect = nomeEntidadeMinusculo + "/" + LISTAR;
		
		return redirect;
	}
	
	public static String construtorListar(String nomeClasseController) throws Exception {
		String nomeEntidade = nomeClasseController
				.substring(0, nomeClasseController.length() - CONTROLLER_LENGTH);
		String nomeEntidadeMinusculo = nomeEntidade.toLowerCase();
		
		String html = nomeEntidadeMinusculo + "/" + LISTAR + ".html";
		
		return html;
	}
}
