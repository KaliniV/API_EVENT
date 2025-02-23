package br.com.nlw.events.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.nlw.events.model.Event;
import br.com.nlw.events.repository.EventRepository;

@Service
public class EventService {
	// Autowired ->> como temos um respositorio e o o mesmo extende outro, ele busca a implementação e cria um objeto 
	// ** injeção de dependencia
	@Autowired
	private EventRepository repository;
	
	public Event addNewEvent(Event event) {
		// gerando pretty name
		event.setPrettyName(event.getTitle().toLowerCase().replaceAll(" ", "-"));
		return repository.save(event);
	}
	public List<Event> getAllEvents(){
		return (List<Event>) repository.findAll();
	}
	
	public Event getByPrettyName(String prettyName) {
		return repository.findByPrettyName(prettyName);
	}
}
