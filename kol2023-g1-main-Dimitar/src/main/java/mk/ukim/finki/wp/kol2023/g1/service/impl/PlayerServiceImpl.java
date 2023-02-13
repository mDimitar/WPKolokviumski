package mk.ukim.finki.wp.kol2023.g1.service.impl;

import mk.ukim.finki.wp.kol2023.g1.model.Player;
import mk.ukim.finki.wp.kol2023.g1.model.PlayerPosition;
import mk.ukim.finki.wp.kol2023.g1.model.Team;
import mk.ukim.finki.wp.kol2023.g1.repository.PlayerRepository;
import mk.ukim.finki.wp.kol2023.g1.repository.TeamRepository;
import mk.ukim.finki.wp.kol2023.g1.service.PlayerService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository, TeamRepository teamRepository) {
        this.playerRepository = playerRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    public List<Player> listAllPlayers() {
        return playerRepository.findAll();
    }

    @Override
    public Player findById(Long id) {
        return playerRepository.findById(id).get();
    }

    @Override
    public Player create(String name, String bio, Double pointsPerGame, PlayerPosition position, Long team) {
        Team team1 = teamRepository.findById(team).get();
        return playerRepository.save(new Player(name,bio,pointsPerGame,position,team1));
    }

    @Override
    public Player update(Long id, String name, String bio, Double pointsPerGame, PlayerPosition position, Long team) {
        Player player = playerRepository.findById(id).get();
        player.setName(name);
        player.setBio(bio);
        player.setPointsPerGame(pointsPerGame);
        player.setPosition(position);
        Team t = teamRepository.findById(team).get();
        player.setTeam(t);
        return playerRepository.save(player);
    }

    @Override
    public Player delete(Long id) {
        Player p = playerRepository.findById(id).get();
        playerRepository.delete(p);
        return p;
    }

    @Override
    public Player vote(Long id) {
        return playerRepository.findById(id).get();
    }

    @Override
    public List<Player> listPlayersWithPointsLessThanAndPosition(Double pointsPerGame, PlayerPosition position) {

        List<Player> filtered = new ArrayList<>();

            if(pointsPerGame == null && position == null){
                filtered = playerRepository.findAll();
            }
            if(pointsPerGame == null && position != null){
                filtered = playerRepository.findByPosition(position);
            }
            if(pointsPerGame != null && position == null){
                filtered = playerRepository.findByPointsPerGameLessThan(pointsPerGame);
            }
            if(pointsPerGame != null && position != null){
                filtered = playerRepository.findByPointsPerGameLessThanAndPosition(pointsPerGame,position);
            }
        return filtered;
    }
}
