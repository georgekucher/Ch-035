package com.crsms.controller;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.crsms.domain.Role;
import com.crsms.domain.User;
import com.crsms.service.RoleService;
import com.crsms.service.UserService;
import com.crsms.validator.AdminValidator;
/**
 * 
 * @author Roman Romaniuk
 *
 */
@Controller
@RequestMapping(value = { "/admin" })
public class AdminController {
	public static final int ITEMSPERPAGE = 5;
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private AdminValidator validator;
	
	@RequestMapping(method = RequestMethod.GET)
	public String getAllUsers(@RequestParam (value = "pagenumber", required = false, defaultValue = "1") int pageNumber, ModelMap model, HttpSession session) {

		long rowsCount = userService.getRowsCount();
		int lastPageNumber = (int)((rowsCount/ITEMSPERPAGE)+1);
		int startPosition = (pageNumber - 1) * ITEMSPERPAGE;
		System.out.println(lastPageNumber);
//		System.out.println(lastPageNumber);
		System.out.println(startPosition);
		List<User> users = userService.getPagingUsers(startPosition, ITEMSPERPAGE);
		model.addAttribute("lastpagenumber", lastPageNumber);
		model.addAttribute("pagenumber", pageNumber);
		model.addAttribute("users", users);
		return "admin";
	}
//	
	
	
	@RequestMapping(value = { "/delete/{userId}" }, method = RequestMethod.GET)
	public String deleteUser(@PathVariable long userId) {
		userService.delete(userId);
		return "redirect:/admin";
	}
	
	@RequestMapping(value = { "/adduser" }, method = RequestMethod.GET)
	public String addUser(ModelMap model) {
		User user = new User();
		model.addAttribute("user", user);
		return "adduser";
	}
	
	@RequestMapping(value = { "/adduser" }, method = RequestMethod.POST)
	public String saveUser(@Validated User user, BindingResult result,
			ModelMap model) {
		validator.validate(user, result);
		if (result.hasErrors()) {
			return "adduser";
		}
		userService.saveUser(user);
		return "redirect:/admin";
	}

	@RequestMapping(value = { "/edit/{userId}" }, method = RequestMethod.GET)
	public String editUser(@PathVariable Long userId, ModelMap model) {
		User user = userService.getUserById(userId);
		model.addAttribute("user", user);
		return "adduser";
	}

	@RequestMapping(value = "/edit/{userId}", method = RequestMethod.POST)
	public String updateUser(@PathVariable long userId, 
								@Validated User user, BindingResult result) {
		validator.validate(user, result);
		if (result.hasErrors()) {
			return "adduser";
		}
		userService.saveUser(user);
		return "redirect:/admin";
	}
	
	@ModelAttribute("roles")
	public List<Role> initializeRoles() {
		List<Role> roles = new ArrayList<>();
		roles = roleService.getAllRoles();
		return roles;
	}

	class RoleEditor extends PropertyEditorSupport {
		@Override
        public void setAsText(String text) {
            Long id = Long.parseLong(text);
            Role role = roleService.getRoleById(id);
            setValue(role);
        }
    }
 
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Role.class, new RoleEditor());
    }
}
