package com.gfttraining.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import org.hibernate.exception.ConstraintViolationException;


import java.util.Arrays;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import com.gfttraining.controller.UserController;
import com.gfttraining.exception.DuplicateEmailException;
import com.gfttraining.repository.UserRepository;
import com.gfttraining.user.User;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository repository;


	@Test
	void getUserById_test(){
		int id=1;
		User userTest1= new User();
		userTest1.setId(1);
		userTest1.setName("Erna");

		when(repository.findById(1)).thenReturn(Optional.of(userTest1));

		User result = userService.findUserById(id);

		assertNotNull(result);
		assertEquals(userTest1.getName(), result.getName());

		verify(repository, times(1)).findById(1);

	}


	@Test
	void getAllUsersByName_test(){
		String name="Erna";
		List <User> userListTest1 = new ArrayList<>();
		User userTest1 = new User();
		userTest1.setId(1);
		userTest1.setName("Erna");
		userListTest1.add(userTest1);

		when(repository.findAllByName("Erna")).thenReturn((userListTest1));

		List<User> result = userService.findAllByName("Erna");

		assertNotNull(result);
		assertEquals(userListTest1.get(0).getName(), result.get(0).getName());

		verify(repository, times(1)).findAllByName("Erna");

	}


	@Test
	void deleteUserById_test(){
		int id=1;
		userService.deleteUserById(id);

		verify(repository, times(1)).deleteById(1);
	}

	@Test
	void createUser_test() {
		User user = new User("Pepito", "Perez", "calle falsa", "TRANSFERENCIA");
		when(repository.save(user)).thenReturn(user);
		User createduser = userService.createUser(user);
		assertThat(user).isEqualTo(createduser);
	}

	@Test
	void updateUserById_test() {

		User existingUser = new User("Pepito", "Perez", "calle falsa", "TRANSFERENCIA");
		existingUser.setId(1);

		User updatedUser = new User();
		updatedUser.setName("Jose");

		when(repository.findById(1)).thenReturn(Optional.of(existingUser));
		when(repository.save(existingUser)).thenReturn(existingUser);

		User result = userService.updateUserById(1, updatedUser);

		verify(repository, times(1)).findById(1);
		verify(repository, times(1)).save(existingUser);
		assertThat(updatedUser.getName()).isEqualTo(result.getName());

	}

	@Test
	void updateUserByIdWithNullValues_test() {

		User existingUser = new User("Pepito", "Perez", "calle falsa", "TRANSFERENCIA");
		existingUser.setId(1);

		User updatedUser = new User();
		updatedUser.setName("Jose");

		when(repository.findById(1)).thenReturn(Optional.of(existingUser));
		when(repository.save(existingUser)).thenReturn(existingUser);

		User result = userService.updateUserById(1, updatedUser);

		assertThat(result.getLastname()).isNotEqualTo(null);

	}



	@Test
	void createUserWithEmailThatAlreadyExists_test() {

		User newUser = new User("repeatedemail@gmail.com","Pepito", "Perez", "calle falsa", "TRANSFERENCIA");

		when(repository.existsByEmail("repeatedemail@gmail.com")).thenReturn(true);

		assertThatThrownBy(()-> userService.createUser(newUser))
		.isInstanceOf(DuplicateEmailException.class)
		.hasMessageContaining("email " + newUser.getEmail() + " is already in use");

	}

	@Test
	void updateUserByIdWithEmailThatAlreadyExists_test() {

		Optional<User> newUser = Optional.of(new User("repeatedemail@gmail.com","Pepito", "Perez", "calle falsa", "TRANSFERENCIA"));

		when(repository.existsByEmail("repeatedemail@gmail.com")).thenReturn(true);
		when(repository.findById(1)).thenReturn(newUser);

		assertThatThrownBy(()-> userService.updateUserById(1, newUser.get()))
		.isInstanceOf(DuplicateEmailException.class)
		.hasMessageContaining("email " + newUser.get().getEmail() + " is already in use");

	}

	@Test
	void getUserByEmailWithEmailNotFound_test() {

		String email = "pedro@chapo.com"; 

		when(repository.findByEmail(email)).thenReturn(null);

		assertThatThrownBy(()-> userService.findUserByEmail(email))
		.isInstanceOf(ResponseStatusException.class)
		.hasMessageContaining("User with email " + email + " not found");

	}

}
