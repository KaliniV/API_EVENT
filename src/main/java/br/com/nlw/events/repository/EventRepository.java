package br.com.nlw.events.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.nlw.events.model.Event;

public interface EventRepository extends CrudRepository<Event, Integer>{

	//CRUDRepository ->> Fornece  funcionalidades básicas. //
	
	
	// baseado no findyBy ele gera de acordo com o campo
	public Event findByPrettyName(String prettyName);
	
}
