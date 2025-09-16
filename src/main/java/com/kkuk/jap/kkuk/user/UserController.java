package com.kkuk.jap.kkuk.user;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;



import jakarta.validation.Valid;


@RequestMapping(value = "/user")
@Controller
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping(value = "/signup") // 회원가입 폼 띄우기
	public String signup(UserCreateForm userCreateForm) {
		return "signup_form";
	}
	
	@PostMapping(value = "/signup") // 회원가입정보 DB 입력
	public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
		
		if(bindingResult.hasErrors()) {//참이면 회원정보입력 에러발생 
			return "signup_form";
		}
		if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
			bindingResult.rejectValue("password2", "passwordInCorrect","비밀번호가 일치하지않습니다.");
			return "signup_form";
		}
		try {
			userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
		} catch (DataIntegrityViolationException e) { //중복된 데이터에 대한 예외처리
			e.printStackTrace();
			//이미 등록된 사용자 아이디일경유 발생하는 에러 추가
			bindingResult.reject("signupFailed","이미 등록된 ID입니다.");
			return "signup_form";
		} catch (Exception e) { // 기타 예외처리
			e.printStackTrace();
			bindingResult.reject("signupFailed","회원가입실패");
			return "signup_form";
		}
			
		return "redirect:/index"; //첫화면으로 이동
	}
	
	@GetMapping(value = "/login")
	public String login() {
		return "login_form";
	}
	
	@GetMapping(value = "/mypage")
	public String mypage(Model model, Principal principal) {
		 User user = userService.getUser(principal.getName());
		    model.addAttribute("user", user);  // 반드시 넣어줘야 함
		return "mypage";
	}
}
