package com.kkuk.jap.kkuk;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	
	    @GetMapping("/")
	    public String root() {
	        return "index";
	    }
	    
	    @GetMapping("/index")
	    public String index() {
	        return "index";
	    }

}
