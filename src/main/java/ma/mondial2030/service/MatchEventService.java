package ma.mondial2030.service;

import ma.mondial2030.dao.MatchEventDAO;
import ma.mondial2030.model.MatchEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Service pour la gestion des matchs/événements
 */
public class MatchEventService {
    private static final Logger logger = LoggerFactory.getLogger(MatchEventService.class);
    private final MatchEventDAO matchEventDAO = new MatchEventDAO();

    /**
     * Récupère tous les matchs
     */
    public List<MatchEvent> getAllMatches() {
        return matchEventDAO.findAll();
    }

    /**
     * Récupère les matchs à venir
     */
    public List<MatchEvent> getUpcomingMatches() {
        return matchEventDAO.findUpcoming();
    }

    /**
     * Trouve un match par son ID
     */
    public MatchEvent findById(int id) {
        return matchEventDAO.findById(id);
    }

    /**
     * Crée un nouveau match
     */
    public boolean createMatch(MatchEvent matchEvent) {
        return matchEventDAO.create(matchEvent);
    }
}
