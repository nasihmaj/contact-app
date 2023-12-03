package com.lxi.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lxi.model.Contact;

@Repository
public interface Apprepo extends JpaRepository<Contact, Integer> {

	List<Contact> searchByFirstName(String firstName);

	Contact getContactById(Integer id);




}
