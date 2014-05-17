package it.massimobarbieri.mbloan.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import it.massimobarbieri.mbloan.exception.ApplicationLogicException;

public class CrudServiceImpl<T, K extends Serializable> implements CrudService<T, K> {

	@Autowired
	protected JpaRepository<T, K> repo;

	@Override
	public List<T> findAll() throws ApplicationLogicException {
		return repo.findAll();
	}

	@Override
	public T findOne(K id) throws ApplicationLogicException {
		return repo.findOne(id);
	}

	@Override
	public void save(T item) throws ApplicationLogicException {
		repo.save(item);
	}

	@Override
	public void delete(T item) throws ApplicationLogicException {
		repo.delete(item);
	}
}
