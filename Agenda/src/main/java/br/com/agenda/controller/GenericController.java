package br.com.agenda.controller;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.agenda.controller.htmlbuilder.HtmlBuilder;
import br.com.agenda.dao.annotations.ChaveEstrangeira;
import br.com.agenda.service.ObjectService;
import br.com.agenda.service.ObjectSuperService;

public abstract class GenericController extends HtmlBuilder {
	
	public String nomeCompletoPacote = "br.com.agenda.entities.";
	
	public ObjectService service = new ObjectService();
	
	public ObjectSuperService superService = new ObjectSuperService();

	@RequestMapping(CRIAR_ALTERAR)
	public ModelAndView criar(@RequestParam(required = false) Long id) throws Exception {
		String nomeClasse = this.getClass().getSimpleName();
		
		String nomeEntidade = HtmlBuilder.nomeEntidade(nomeClasse);
				
		String pacote = nomeCompletoPacote + nomeEntidade;
		Class<?> classeEntity = Class.forName(pacote);
		
		String html = HtmlBuilder.construtorForm(nomeEntidade);
		
		ModelAndView mv = new ModelAndView(html);		
		Object objeto;		
		
		if(id == null) {
			objeto = classeEntity.getDeclaredConstructor().newInstance();
		} else {
			try {
				if (!classeEntity.getSuperclass().getSimpleName().equals("Object")) {
					objeto = superService.obterObject(id, classeEntity);
				} else {
					objeto = service.obterObject(id, classeEntity);
				}
			} catch (Exception e) {
				objeto = classeEntity.getDeclaredConstructor().newInstance();
				mv.addObject("mensagem", e.getMessage());
			}
		}
		Field[] fields = classeEntity.getDeclaredFields();
		
		for (Field field : fields) {
			if (field.getDeclaredAnnotation(ChaveEstrangeira.class) != null) {
				String nome = field.getName();
				nome = nome.substring(0, 1).toUpperCase() + nome.substring(1);
				String pacoteLista = nomeCompletoPacote + nome;
				Class<?> classeQueSeraListada = Class.forName(pacoteLista);
				mv.addObject(nome, service.listarObjects(classeQueSeraListada));
			}
		}
		
		mv.addObject(nomeEntidade.toLowerCase(), objeto);
		return mv;
	}
	
	public ModelAndView salvar(Object objeto, BindingResult bindingResult, RedirectAttributes redirectAttributes) throws Exception {
		String nomeClasse = this.getClass().getSimpleName();
		
		String nomeEntidade = HtmlBuilder.nomeEntidade(nomeClasse);
		
		String htmlForm = HtmlBuilder.construtorForm(nomeEntidade);
//		String redirect = HtmlBuilder.construtorRedirect(nomeClasse);
		
		String pacote = nomeCompletoPacote + nomeEntidade;
		Class<?> classeEntity = Class.forName(pacote);
				
		String nomeEntidadeLetraMinuscula = nomeEntidade.toLowerCase();
		
		if(bindingResult.hasErrors()) {
			ModelAndView mv = new ModelAndView(htmlForm);
			mv.addObject(nomeEntidadeLetraMinuscula, objeto);
			mv.addObject("mensagem", "Erro ao salvar objeto.");
			return mv;
		}
		ModelAndView mv = new ModelAndView(htmlForm);
		boolean novo = true;
		
//		objeto = objeto.getClass().cast(classeEntity.getDeclaredConstructor().newInstance());
		Method getId = objeto.getClass().getMethod("getId");
		
		if (getId.invoke(objeto) != null) {
			novo = false;
		} 
		if (novo) {
			if (!classeEntity.getSuperclass().getSimpleName().equals("Object")) {
				superService.salvarObject(objeto);
			} else {
				service.salvarObject(objeto);
			}
			mv.addObject(nomeEntidadeLetraMinuscula, classeEntity.getDeclaredConstructor().newInstance());
		} else {
			Long id = (Long) getId.invoke(objeto);
			if (!classeEntity.getSuperclass().getSimpleName().equals("Object")) {
				superService.alterarObject(id, objeto);
			} else {
				service.alterarObject(id, objeto);
			}
			mv.addObject(nomeEntidadeLetraMinuscula, objeto);
		}
		mv.addObject("mensagem", "Ação realizada com sucesso!");
		return mv;
	}
	
	@RequestMapping("/listar")
	public ModelAndView listar() throws Exception {
		String nomeClasse = this.getClass().getSimpleName();
		
		String nomeEntidade = HtmlBuilder.nomeEntidade(nomeClasse);
		
		String htmlListar = HtmlBuilder.construtorListar(nomeClasse);
		
		String pacote = nomeCompletoPacote + nomeEntidade;
		Class<?> classeEntity = Class.forName(pacote);
		
		ModelAndView mv = new ModelAndView(htmlListar);
		Object listaObjects;
		if (!classeEntity.getSuperclass().getSimpleName().equals("Object")) {
			listaObjects = superService.listarObjects(classeEntity);
		} else {
			listaObjects = service.listarObjects(classeEntity);
		}
		mv.addObject("lista", listaObjects);
		return  mv;	
	}
	
	@RequestMapping("/excluir")
	public ModelAndView excluir(@RequestParam Long id, RedirectAttributes redirectAttributes) throws Exception {
		String nomeClasse = this.getClass().getSimpleName();
		
		String nomeEntidade = HtmlBuilder.nomeEntidade(nomeClasse);
		
		String pacote = nomeCompletoPacote + nomeEntidade;
		Class<?> classeEntity = Class.forName(pacote);
		@SuppressWarnings("deprecation")
		Object objeto = classeEntity.newInstance();
		
		ModelAndView mv = new ModelAndView("redirect:/" + nomeEntidade.toLowerCase() + "/listar");
		try {
			service.excluirObject(id, objeto);
			redirectAttributes.addFlashAttribute("mensagem", "Ação realizada com sucesso.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("mensagem", "Erro ao tentar excluir.");
		}
		return mv;
	}
}
