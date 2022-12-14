package pt.up.fe.ldts.gd.model.wild;

import pt.up.fe.ldts.gd.model.player.Player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Wild {
    private Player player;
    private List<Enemy> enemies;
    private List<String> lines;
    private List<String> options;

    public Wild(Player player) throws IOException {
        this.player = player;
        this.enemies = new ArrayList<>();
        this.lines = readAscii();
        this.options = Arrays.asList("1: Continue", "0: Menu");
    }

    public void createEnemies() {
        Random rand = new Random();
        int num_enemies = rand.nextInt(2) + 1;
        for(int i = 0; i < num_enemies; i++) {
            enemies.add(new Enemy(10,10,10,20));
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
    
    public boolean fight(Player player) {
        Random rand = new Random();
        int num_enemy;
        if(enemies.size() == 1)
            num_enemy = 0;
        else
            num_enemy = rand.nextInt(enemies.size() - 1);
        int player_total_atk = enemies.get(num_enemy).getHp() / player.getDamage();
        int enemy_total_atk = player.getHP() / enemies.get(num_enemy).getDamage();
        if(player_total_atk > enemy_total_atk)
            return false;
        player.setHP(player.getHP() - player_total_atk * enemies.get(num_enemy).getDamage());
        enemies.get(num_enemy).loot(player);
        enemies.remove(num_enemy);
        return true;
    }

    public List<String> getLines() {
        return this.lines;
    }

    public List<String> getOptions() {
        return options;
    }

    private List<String> readAscii() throws IOException {
        List<String> lines = new ArrayList<>();
        URL resource = Wild.class.getResource("/ascii/wild/wild.txt");
        assert resource != null;
        BufferedReader br = new BufferedReader(new FileReader(resource.getFile()));

        for(String line; (line = br.readLine()) != null;)
            lines.add(line);

        return lines;
    }
}
