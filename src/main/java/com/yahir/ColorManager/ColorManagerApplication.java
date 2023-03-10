package com.yahir.ColorManager;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yahir.ColorManager.Color.Color;
import com.yahir.ColorManager.Color.ColorRepository;
import com.yahir.ColorManager.Users.Users;
import com.yahir.ColorManager.Users.UsersRepository;

@SpringBootApplication
@RestController
@RequestMapping("api/v1/colors")
@CrossOrigin
public class ColorManagerApplication {

	private final ColorRepository colorRepository;
	private final UsersRepository userRepository;

	public ColorManagerApplication(ColorRepository colorRepository, UsersRepository userRepository) {
		this.colorRepository = colorRepository;
		this.userRepository = userRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(ColorManagerApplication.class, args);
	}

	//Returns all colors present in the database.
	@GetMapping
	public List<Color> getColors() {
		return colorRepository.findAll();
	}

	//Returns all users in the database.
	@GetMapping("/users")
	public List<Users> getUsers() {
		return userRepository.findAll();
	}

	record NewColorRequest(
		String color1,
		String color2,
		String color3,
		String creator
	) {}

	record NewUserRequest(
		String email,
		String password,
		String role,
		String username
	) {} 

	record NewLoginRequest(
		String email,
		String password
	){}

	//Receives a post method to add a color to the database.
	@PostMapping
	public void createColors(@RequestBody NewColorRequest request) {
		Color Color = new Color();
		Color.setColor1(request.color1());
		Color.setColor2(request.color2());
		Color.setColor3(request.color3());
		Color.setCreator(request.creator());
		colorRepository.save(Color);
	}

	
	// Creates a random number to retrieve a random row from psql.
	// The random number will work as the id of said row. The upperbound of the range is the amount of total entries.
	// In case the random number equals 0, it will instead return 1.
	public int RandomNumberGenerator() {
		int total = Math.toIntExact((colorRepository.count()));
		Random rand = new Random();
		int randomNumber = rand.nextInt(total + 1);
		if (randomNumber == 0) {
			return randomNumber + 1;
		}
		return randomNumber;
	}
	
	//Returns a random color from the datbase
	//Function currently not used at all.
	@GetMapping("/random")
	public Optional<Color> homeColors() {
		return colorRepository.findById(RandomNumberGenerator());
	}

	@GetMapping("/{id}")
	public Color GetColorById(@PathVariable String id) {
		return colorRepository.getColorById(Integer.parseInt(id));
	}

	@GetMapping("/creator/{creator}")
	public List<Color> GetColorsByName(@PathVariable String creator) {
		return colorRepository.getColorByCreator(creator);
	}

	// Signup function
	//Receives a post method to add a user to the database.
	//We verify if the user email already exists in our db via existsByEmail example function.
	@PostMapping("/users/signup")
	public Object createUser(@RequestBody NewUserRequest request) {
		boolean UserExists = userRepository.existsByEmail(request.email());
		if(!UserExists) {
			Users Users = new Users();
			Users.setEmail(request.email());
			Users.setPassword(request.password());
			Users.setRole(request.role());
			Users.setUsername(request.username());
			userRepository.save(Users);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	// Login function.
	//Receives record "NewLoginRequest", finds the user via email, and verifies if password is correct.
	@PostMapping("/users/login")
	public Object findUser(@RequestBody NewLoginRequest request) {
		Users Users = userRepository.findByEmail(request.email());
		String Password = Users.getPassword();
		if(Password.equals(request.password())) {
			//Basically we create a new object that only has the username and email.
			//Made this in order to avoid having passwords in localStorage.
			Users SecuredUser = new Users();
			SecuredUser.setUsername(Users.getUsername());
			SecuredUser.setEmail(Users.getEmail());
			return SecuredUser;
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
	}

	// Testing Hashcode functions.
	@PostMapping("/hashcode")
	public Integer HashCodeTest(@RequestBody NewLoginRequest request) {
		String Test = request.email() + request.password();
		return Test.hashCode();
	}
	
}