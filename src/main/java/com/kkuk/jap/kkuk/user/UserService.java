package com.kkuk.jap.kkuk.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kkuk.jap.kkuk.DataNotFoundException;



@Service
public class UserService {

	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	public User create(String username, String password, String email) {
		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		
		String cryptPassword = passwordEncoder.encode(password);
		user.setPassword(cryptPassword);
		
		userRepository.save(user);
		
		return user;
	}
	
	public User getUser(String username) {
		Optional<User> _user = userRepository.findByUsername(username);
		
		if(_user.isPresent()) {
			User user = _user.get();
			return user;
		}else {
			throw new DataNotFoundException("없는 유저입니다.");
		}
	}
}
