package com.lxi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lxi.model.Address;
import com.lxi.model.Contact;
import com.lxi.model.UserInfo;
import com.lxi.repo.AddressRepo;
import com.lxi.repo.Apprepo;
import com.lxi.repo.UserInfoRepository;

	@Service
	public class AppService {
		
		@Autowired
		Apprepo apprepo;

		@Autowired
		AddressRepo addressRepo;
		
		@Autowired
		PasswordEncoder passwordEncoder;
		
		@Autowired
		UserInfoRepository repository;

		public Contact createContact(Contact contact) {
			return apprepo.save(contact);
		}

		public List<Contact> searchByName(String firstName) {
			
			return apprepo.searchByFirstName(firstName);
		}

		public Contact saveContact(Contact contact) {
		return apprepo.save(contact);
			
		}

		public List<Contact> getAllContacts() {
			
			return apprepo.findAll();
		}
		
		public Contact getContactById(Integer id) {
			return apprepo.getContactById(id);
		}

		public Contact updateContact(Contact contact) {
			return apprepo.save(contact);
		}
		public void deleteContactById(Integer id) {
			apprepo.deleteById(id);
		}

		public Address getAddressById(Integer addressId) {
			return addressRepo.findAddressById(addressId);
		}
		 public String addUser(UserInfo userInfo) {
		        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
		        repository.save(userInfo);
		        return "user added to system ";
		    }
	}