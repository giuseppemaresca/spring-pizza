package jana60.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jana60.model.Ingrediente;
import jana60.repository.IngredienteRepository;

@Controller
@RequestMapping("/ingredienti")
public class IngredienteController {

	@Autowired
	private IngredienteRepository repo;

	@GetMapping("/list")
	public String pizza(Model model) {
		List<Ingrediente> IngredientiList = (List<Ingrediente>) repo.findAll();
		model.addAttribute("IngredientiList", IngredientiList);
		return "/listIngrediente";
	}

	@GetMapping("/edit")
	public String pizzaForm(Model model) {
		model.addAttribute("listaIngredienti", repo.findAllByOrderByNome());
		model.addAttribute("newIngrediente", new Ingrediente());
		return "/editIngrediente";
	}

	@PostMapping("/edit")
	public String saveIngrediente(@Valid @ModelAttribute("newIngrediente") Ingrediente formIngrediente,
			BindingResult br, Model model) {
		if (br.hasErrors()) {
			// ricarico la pagina
			model.addAttribute("ingredienti", repo.findAllByOrderByNome());
			return "/editIngrediente";

		} else {
			// salvo la category
			repo.save(formIngrediente);
			return "redirect:/ingredienti/list";
		}
	}

	@GetMapping("/update/{id}")
	public String update(@PathVariable("id") Integer ingredienteId, Model model) {
		Optional<Ingrediente> result = repo.findById(ingredienteId);
		if (result.isPresent()) {
			model.addAttribute("newIngrediente", result.get());
			repo.save(result.get());
			return "editIngrediente";
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Ingrediente con id " + ingredienteId + "Non esiste");
		}

	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") Integer ingredienteId, RedirectAttributes ra, Model model) {
		Optional<Ingrediente> result = repo.findById(ingredienteId);
		if (result.isPresent()) {
			repo.delete(result.get());
			ra.addFlashAttribute("successMessage",
					"L'ingrediente" + result.get().getNome() + "è stato cancellato con successo!");
			return "redirect:/ingredienti/list";
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Ingrediente con id " + ingredienteId + "Non esiste");
		}

	}
}