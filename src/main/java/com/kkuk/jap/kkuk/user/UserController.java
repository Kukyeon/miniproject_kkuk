package com.kkuk.jap.kkuk.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping(value = "/signup") // 회원가입 폼 띄우기
	public String signup(UserCreateForm userCreateForm) {
		return "signup_form";
	}
	
	
}
