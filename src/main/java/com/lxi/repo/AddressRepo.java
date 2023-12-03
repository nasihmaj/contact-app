package com.lxi.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lxi.model.Address;

public interface AddressRepo extends JpaRepository<Address, Integer> {

	List<Address> findAddressByContactId(Integer id);

	Address findAddressById(Integer addressId);





}
