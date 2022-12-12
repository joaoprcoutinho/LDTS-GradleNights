package pt.up.fe.ldts.gd.viewer;

import pt.up.fe.ldts.gd.gui.GUI;
import pt.up.fe.ldts.gd.model.town.Town;

import java.util.List;

public class TownViewer extends Viewer<Town> {
    public TownViewer(Town town) {
        super(town);
    }

    @Override
    protected void drawElements(GUI gui) {
        String hp = "HP: " + Integer.toString(getModel().getPlayer().getHP());
        String gold = "GOLD: " + Integer.toString(getModel().getPlayer().getGold());

        gui.drawPlayerInfo(hp, gold);

        List<String> lines = getModel().getLines();
        for(int i = 0; i < lines.size(); i++) {
            gui.drawText(lines.get(i), 23, 8+i);
        }

        List<String> options = getModel().getOptions();
        int opt_col = 45;
        for(String str: options) {
            gui.drawText(str, opt_col, 35);
            opt_col += str.length() + 5;
        }
    }
}
