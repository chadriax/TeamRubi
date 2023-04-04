package com.gfttraining.service;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.modelmapper.Condition;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gfttraining.exception.DuplicateEmailException;
import com.gfttraining.repository.UserRepository;
import com.gfttraining.user.User;

@Service
public class UserService {


	private UserRepository userRepository;

	private ModelMapper modelMapper;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
		this.modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
	}

	public List<User> findAll(){
		return userRepository.findAll();
	}

	public User findUserById(Integer id){
		Optional<User> user = userRepository.findById(id);
		if(user.isEmpty()) {
			throw new EntityNotFoundException("Usuario con el id: "+id+" no encontrado");
		}
		return user.get();
	}

	public User findUserByName(String name){
		Optional<User> user = Optional.ofNullable(userRepository.findByName(name));
		if(user.isEmpty()) {
			throw new EntityNotFoundException("Usuario con el nombre: "+name+" no encontrado");
		}
		return user.get();

	}

	public void saveUser(User user) {
		userRepository.save(user);
	}

	public void saveAllUsers(List<User> usersList) {
		userRepository.saveAll(usersList);
	}

	public void deleteAllUsers() {
		userRepository.deleteAll();
	}

	public void deleteUserById(Integer id) {
		try {
			userRepository.deleteById(id);
		} catch(Exception e) {
			throw new EntityNotFoundException("No se ha podido eliminar el usuario con el id: "+id+" de la base de datos");
		}
	}

	public User createUser(User user) {

		String email = user.getEmail();

		if(userRepository.existsByEmail(email)) {
			throw new DuplicateEmailException("The email " + email + " is already in use");
		}

		return userRepository.save(user);

	}

	public User updateUserById(int id, User user) {

		User existingUser = userRepository.findById(id)
				.orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		if(userRepository.existsByEmail(user.getEmail())) {
			throw new DuplicateEmailException("The email " + user.getEmail() + " is already in use");
		}

		user.setId(existingUser.getId());
		modelMapper.map(user, existingUser);

		return userRepository.save(existingUser);

	}


}