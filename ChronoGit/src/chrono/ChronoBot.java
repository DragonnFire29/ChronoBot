package chrono;

import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Unit;
import bwta.BWTA;
import chrono.base.BaseManagement;
import chrono.buildorder.BuildOrder;
import chrono.scout.ScoutingSystem;

public class ChronoBot extends DefaultBWListener {

    public static Mirror mirror = new Mirror();
    public static Game game;
    public static Player self;
    
    public void run() {
        mirror.getModule().setEventListener(this);
        mirror.startGame();
    }

    @Override
    public void onUnitCreate(Unit unit) {
    	/*
        System.out.println("New unit " + unit.getType());
        */
    }

    @Override
    public void onStart() {
        game = mirror.getGame();
        self = game.self();
        game.setLocalSpeed(0);

        //Use BWTA to analyze map
        //This may take a few minutes if the map is processed first time!
        System.out.println("Analyzing map...");
        BWTA.readMap();
        BWTA.analyze();
        System.out.println("Map data ready!!!");
        
        //f i'm in SP, enter some cheaty cheats
        if(!game.isMultiplayer())
        {
        	game.sendText("Single Player game detected: Entering debug settings.");
        	game.enableFlag(1);
            game.sendText("power overwhelming");
            //game.sendText("show me the money");
            //game.sendText("operation cwal");
        }
        else
        {
        	boolean specialFound = false;
        	for(Player p:game.enemies())
            {
            	if(p.getName().equals("Campeau"))
            	{
            		game.sendText("Ah, the enemy I was born to fight.");
            		game.sendText("This will be fun...");
            		specialFound = true;
            	}
            }
        	if(!specialFound)
        	{
        		game.sendText("Welcome to the Arena. ChronoBot online.");
        		game.sendText("Authored by Leo Henri");
        	}
        }
        
        BuildOrder.startBuildOrder(BuildOrder.BOT_Static, BuildOrder.BOSS_1FACTFE);
    }

    @Override
    public void onFrame() {
    	CustomUI.drawScreenUI(CustomUI.UI_DEBUG);
    	ScoutingSystem.runScout();
    	BaseManagement.runBaseManager();
    	BuildOrder.run();
    }
    
    @Override
    public void onEnd(boolean b)
    {
    	
    }

    public static void main(String[] args) {
        new ChronoBot().run();
    }
}