package com.lxi.controller;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lxi.*;
import com.lxi.model.Address;
import com.lxi.model.AuthRequest;
import com.lxi.model.Contact;
import com.lxi.model.UserInfo;
import com.lxi.repo.AddressRepo;
import com.lxi.repo.Apprepo;
import com.lxi.repo.UserInfoRepository;
import com.lxi.service.AppService;
import com.lxi.service.JwtService;
import com.lxi.*;

@RestController
public class AppController implements ErrorController {
	
	@Autowired
	AppService appService;
	
	@Autowired
	Apprepo apprepo;
	
	@Autowired
	AddressRepo addressRepo;
	
	@Autowired
	UserInfoRepository userInfoRepository;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	AuthenticationManager authenticationManager;
	

	@GetMapping("contacts")
	public String getContact(Model model) {
		 model.addAttribute("contacts", appService.getAllContacts());
		 return "contacts";
	}
	@PreAuthorize("hasAuthority('ROLE_USER')")
	@GetMapping("contacts/new")
	public String createContact(Model model) {
		Contact contact = new Contact();
		model.addAttribute("contact", contact);
		return "create_contact";
	}
	
	@PostMapping("newcontacts")
	public String saveContact(@ModelAttribute("contact") Contact contact) {
		appService.saveContact(contact);
		return "redirect:/contacts";
	}
	
	@GetMapping("contacts/edit/{id}")
	public String editContact(@PathVariable Integer id, Model model) {
		Contact contact =  appService.getContactById(id);
		model.addAttribute("contact",contact);
		return "edit_contact";
	}
	
	@PostMapping("contacts/{id}")
	public String updateContact(@PathVariable Integer id,
		@ModelAttribute("contact") Contact contact) {
		
		Contact existingContact = appService.getContactById(id);
		existingContact.setId(id);
		existingContact.setFirstName(contact.getFirstName());
		existingContact.setLastName(contact.getLastName());
		existingContact.setPhonenum(contact.getPhonenum());
		existingContact.setEmail(contact.getEmail());
		appService.updateContact(existingContact);
		return "redirect:/contacts";
	}
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@GetMapping("contacts/{id}")
	public String deleteContact(@PathVariable Integer id){
		appService.deleteContactById(id);
		return "redirect:/contacts";
	}
	
	@GetMapping("/search")
	public String searchContacts(@RequestParam("firstName")
	String firstName, Model model) {
	    List<Contact> searchResults = appService.searchByName(firstName);
	    model.addAttribute("searchResults", searchResults);
	    return "search-results";
	}


	@PostMapping("new")
	public Contact addContact(@RequestBody Contact contact) {
		
		if(contact.getAddresses()!= null) {
			contact.getAddresses().forEach(address -> address.setContact(contact));
		}
		return apprepo.save(contact);
	}
	
	@GetMapping("all")
	public List<Contact> getAll() {
		return apprepo.findAll();	
	}
	
	@GetMapping("alll/{id}")
	public List<Address> getAddressByContactId(@PathVariable Integer id){
		return addressRepo.findAddressByContactId(id);
	}
	@GetMapping("/{id}/address")
	public String viewAddress(@PathVariable Integer id, Model model) {
		List<Address> addresslist = addressRepo.findAddressByContactId(id);
		 model.addAttribute("addresses", addresslist);
		 Contact contact = apprepo.findById(id).orElseThrow();
		 model.addAttribute("contact", contact);
		return "address";
	}
	@GetMapping("details/{id}")
	public String viewContact(@PathVariable Integer id, Model model) {
	    Optional<Contact> contactOpt = apprepo.findById(id);

	    if (contactOpt.isPresent()) {
	        model.addAttribute("contact", contactOpt.get());
	    } else {
	        // Handle the case where the contact is not found
	        model.addAttribute("errorMessage", "Contact not found");
	        // Optionally redirect to an error page or display a message in the view
	    }
	    return "contact_details";
	}
	
	@GetMapping("create/{id}")
	public String createAddress(@PathVariable Integer id, Model model) {
		Address address = new Address();
		Contact contact = apprepo.findById(id).orElseThrow();
		address.setContact(contact);
		model.addAttribute("address", address);
		return "create_address";
	}
	
	
	@PostMapping("newadd")
	public String saveAddress(@ModelAttribute("address") Address address) {

		addressRepo.save(address);
		
		return "redirect:/address";
	}
	
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@PostMapping("/newad")
	public String saveAddress(@ModelAttribute("address") Address address, RedirectAttributes redirectAttributes) {
	    addressRepo.save(address);
	    Integer id = address.getContact().getId();
	    redirectAttributes.addFlashAttribute("successMessage", "Address saved successfully!");
	    return "redirect:/{id}/address".replace("{id}", String.valueOf(id));
	}
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	@GetMapping("/delete/{addressId}")
	public String removeAddress(@PathVariable Integer addressId) {
		addressRepo.deleteById(addressId);
	    return "redirect:/contacts";
	}
	
	@GetMapping("{id}/edit/{addressId}")
	public String editAddress(@PathVariable Integer addressId, Model model) {
		Address address = appService.getAddressById(addressId);
		model.addAttribute("address", address);
		return "edit_address";
	}
	
	
	@PostMapping("update/{addressId}")
	public String updateAddress(@PathVariable Integer addressId, @ModelAttribute("address") Address address) {
		
		Address existingAddress = appService.getAddressById(addressId);
		existingAddress.setId(addressId);
		existingAddress.setCity(address.getCity());
		existingAddress.setState(address.getState());
		existingAddress.setStreetAddress(null);
		addressRepo.save(existingAddress);
		return "redirect:/contacts";
		
	}
	
	@RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // You can add additional logic here based on the error
        return "errorpage"; // Make sure you have a template named 'errorPage'
    }
	  
	@GetMapping("alluser")
	public List<UserInfo> getAllUser(){
		return userInfoRepository.findAll();
		
	}
	 @PostMapping("/newuser")
	    public String addNewUser(@RequestBody UserInfo userInfo) {
	        return appService.addUser(userInfo);
	    }
	
	@PostMapping("authenticate")
	public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) throws UserPrincipalNotFoundException {
	 org.springframework.security.core.Authentication authentication = 
			 authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword()));
		
	 if(authentication.isAuthenticated()) {
		return jwtService.generateToken(authRequest.getUserName());
	}
	else {
		throw new UserPrincipalNotFoundException("invalid user");
	}
	 switch (key) {
	case value: {
		
		yield type;
	}
	default:
		throw new IllegalArgumentException("Unexpected value: " + key);
	}
}
}
