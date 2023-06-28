package com.app.projectory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.projectory.entity.Users;
import com.app.projectory.service.UserLoginService;

@Controller
@RequestMapping("")
public class HomeController {
	

	UserLoginService userLgServ = new UserLoginService();
	
	
	@GetMapping
	public String displayLandingPage(Model model, Users user) {
		model.addAttribute("user", user);
		model.addAttribute("userLevel", "guest");
		return "/home/landingPage";
	}
	@PostMapping("/login")
	public String validateUser(Model model, Users user, RedirectAttributes redAttr) {
		Users checkedUser = userLgServ.authenticateUser(user.getUsername(), user.getPassword());
		String indicator = "err";
		if(checkedUser != null) {
			//model.addAttribute("user", checkedUser);
			//redAttr.addAttribute("userd", checkedUser);
			/* redAttr.addFlashAttribute("username", checkedUser.getUsername()); */
//			redAttr.addAttribute("firstName", checkedUser.getFirstName());
//			redAttr.addAttribute("lastName", checkedUser.getFirstName());
			redAttr.addFlashAttribute("userA", checkedUser);
			indicator = "pass";
			return "redirect:/user/dashboard?indicator="+indicator;	
		}
		
		 return "redirect:/?indicator="+indicator; 
		/* return "redirect: /user/dashboard?indicator=\""+indicator+"\""; */
	}
	
}
