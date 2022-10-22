package br.com.agenda.controller.classes;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.agenda.controller.GenericController;
import br.com.agenda.entities.Cadastro;

@Controller
@RequestMapping("cadastro")
public class CadastroController extends GenericController {

	@RequestMapping(method = RequestMethod.POST, path = CRIAR_ALTERAR)
	public ModelAndView salvar(Cadastro objeto, BindingResult bindingResult, RedirectAttributes redirectAttributes)
			throws Exception {
		return super.salvar(objeto, bindingResult, redirectAttributes);
	}
}
