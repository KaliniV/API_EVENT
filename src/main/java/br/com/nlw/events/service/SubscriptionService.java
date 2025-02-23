package br.com.nlw.events.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.exception.UserIndicadorNotFoundException;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.repository.EventRepository;
import br.com.nlw.events.repository.SubscriptionRepository;
import br.com.nlw.events.repository.UserRepository;

@Service
public class SubscriptionService {
	@Autowired
	private EventRepository evtRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SubscriptionRepository SubRepository;

	public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId) {

		// recuperar evento pelo nome
		Event evt = evtRepository.findByPrettyName(eventName);
		if (evt == null) { // verificação se o evento existe
			throw new EventNotFoundException("evento " + eventName + " não existe.");
		}

		User userRecuperado = userRepository.findByEmail(user.getEmail());
		if (userRecuperado == null) { // verificar se o usuario existe
			userRecuperado = userRepository.save(user);
		}
		
		User indicador = null;
		if (userId != null) {
			indicador = userRepository.findById(userId).orElse(null);
			if (indicador == null) {
				throw new UserIndicadorNotFoundException("Usuário " + userId + " indicador não existe");
			}
		}
		Subscription subs = new Subscription();
		subs.setEvent(evt);
		subs.setSubscriber(userRecuperado);
		subs.setIndication(indicador);
		Subscription tmpSub = SubRepository.findByEventAndSubscriber(evt, userRecuperado);
		if (tmpSub != null) { // verifica se usuario esta inscrito no evento
			throw new SubscriptionConflictException(
					"Já existe inscrição para o usuário  " + userRecuperado.getName() + " no evento: " + eventName);
		}

		Subscription resultado = SubRepository.save(subs);
		return new SubscriptionResponse(resultado.getSubscriptionNumber(), "http://codecraft.com/subscription/"
				+ resultado.getEvent().getPrettyName() + "/" + resultado.getSubscriber().getId());
	}

	
	
	public List<SubscriptionRankingItem> getCompleteRanking(String prettyName){
		Event evt = evtRepository.findByPrettyName(prettyName);
		if (evt == null) {
			throw new EventNotFoundException("Ranking do evento  " + prettyName + " não existe.");
		}
		
		return SubRepository.generateRanking(evt.getId());
	}
	
	public SubscriptionRankingByUser getRankingBuUser(String prettyName, Integer userId) {
		List<SubscriptionRankingItem> ranking = getCompleteRanking(prettyName);
		
		SubscriptionRankingItem item = ranking.stream().filter(i-> i.userId().equals(userId)).findFirst().orElse(null);
		
		if(item == null) {
			throw new UserIndicadorNotFoundException("Não a inscrições com indicação do usuário  " + userId );

		}
		Integer posicao = IntStream.range(0, ranking.size()).filter(pos -> ranking.get(pos).userId().equals(userId)).findFirst().getAsInt();
		return new SubscriptionRankingByUser(item, posicao+1);
	}
	
}
