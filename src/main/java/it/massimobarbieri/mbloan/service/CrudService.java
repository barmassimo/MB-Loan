package it.massimobarbieri.mbloan.service;

import java.util.List;

import it.massimobarbieri.mbloan.exception.ApplicationLogicException;

public interface CrudService<T, K> {
	List<T> findAll() throws ApplicationLogicException;

	T findOne(K id) throws ApplicationLogicException;

	void save(T u) throws ApplicationLogicException;

	void delete(T u) throws ApplicationLogicException;
}
