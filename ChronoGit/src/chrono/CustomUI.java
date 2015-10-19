package chrono;

import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import chrono.base.BaseManagement;
import chrono.base.ManagedBase;
import chrono.buildorder.BuildOrder;
import chrono.scout.ScoutingSystem;

public class CustomUI {
	public static final int UI_MINIMAL = 1;
	public static final int UI_EXTENDED = 2;
	public static final int UI_DEBUG = 3;
	public static void drawScreenUI(int UILevel)
	{
		if(UILevel >= UI_MINIMAL)
		{
			ChronoBot.game.setTextSize(1);
			//Draw map info
			ChronoBot.game.drawTextScreen(5, 20, "MAP INFORMATION");
			ChronoBot.game.drawTextScreen(5, 30, "NAME: " + ChronoBot.game.mapName());
			ChronoBot.game.drawTextScreen(5, 40, "BASE LOCATIONS: " + BWTA.getBaseLocations().size());
			ChronoBot.game.drawTextScreen(5, 50, "START LOCATIONS: " + BWTA.getStartLocations().size());
			//Draw players. Max theoretical 16, max realistic 12.
			ChronoBot.game.drawTextScreen(5, 60, "Players:");
			int playerCount = 0;
			for(Player p: ChronoBot.game.getPlayers())
			{
				ChronoBot.game.drawBoxScreen(5, 70 + (10 * playerCount), 20, 70 + (10 * playerCount) + 10, p.getColor());
				for(int i = 0; i < 10; i++)
				{
					ChronoBot.game.drawLineScreen(5, 70 + (10 * playerCount)+ i, 20, 70 + (10 * playerCount) + i, p.getColor());
				}
				ChronoBot.game.drawTextScreen(25, 70 + (10 * playerCount), p.getID() + " - " + p.getName() + " - " + p.getRace());
				
				playerCount++;
			}
			
			//Draw base info
			ChronoBot.game.drawTextScreen(200, 20, "BASE INFORMATION");
			int myBaseCount = 0;
			if(BaseManagement.baseArray != null)
			{
				for(ManagedBase b: BaseManagement.baseArray)
				{
					ChronoBot.game.drawTextScreen(200, 30 + (b.baseID * 10) + (myBaseCount * 40), b.toString());
					
					if(b.OwnedPlayerID == ChronoBot.self.getID())
					{
						ChronoBot.game.drawTextScreen(210, 30 + (b.baseID * 10) + (myBaseCount * 40) + 10, "GH:" + b.GasHarvesters);
						ChronoBot.game.drawTextScreen(210, 30 + (b.baseID * 10) + (myBaseCount * 40) + 20, "MH:" + b.MineralHarvesters);
						ChronoBot.game.drawTextScreen(210, 30 + (b.baseID * 10) + (myBaseCount * 40) + 30, "WN:" + b.WorkersNeeded);
						ChronoBot.game.drawTextScreen(210, 30 + (b.baseID * 10) + (myBaseCount * 40) + 40, "WP:" + b.WorkersPresent);
						ChronoBot.game.drawTextScreen(250, 30 + (b.baseID * 10) + (myBaseCount * 40) + 10, "MGH:" + b.MaxGasHarvesters);
						ChronoBot.game.drawTextScreen(250, 30 + (b.baseID * 10) + (myBaseCount * 40) + 20, "MMH:" + b.MaxMineralHarvesters);
						ChronoBot.game.drawTextScreen(250, 30 + (b.baseID * 10) + (myBaseCount * 40) + 30, "MC:" + b.MineralCount);
						ChronoBot.game.drawTextScreen(250, 30 + (b.baseID * 10) + (myBaseCount * 40) + 40, "GMC:" + b.GasMineCount);
						myBaseCount++;
					}
				}
			}
			ChronoBot.game.drawTextScreen(350, 20, "BUILD INFO");
			if(!BuildOrder.buildOrder.isEmpty())
			{
				ChronoBot.game.drawTextScreen(350, 30, "Next unit: " + BuildOrder.buildOrder.peek().building.c_str());
				ChronoBot.game.drawTextScreen(350, 40, "Supply Threshold: " + BuildOrder.buildOrder.peek().supplyAmount + "/" + BuildOrder.buildOrder.peek().supplyCap);
				ChronoBot.game.drawTextScreen(350, 50, "M:" + BuildOrder.buildOrder.peek().building.mineralPrice() + " G:" + BuildOrder.buildOrder.peek().building.gasPrice());
				ChronoBot.game.drawTextScreen(350, 60, "Reserved:");
				ChronoBot.game.drawTextScreen(350, 70, "M:" + BuildOrder.reservedMineralCount + " G:" + BuildOrder.reservedGasCount);
			}
			else
			{
				ChronoBot.game.drawTextScreen(350, 30, "No more Build Order!");
			}
		}
		
		if(UILevel >= UI_DEBUG)
		{
			//Base/Start info
			for(int i = 0; i < BWTA.getBaseLocations().size(); i++)
			{
				ChronoBot.game.drawTextMap(BWTA.getBaseLocations().get(i).getX(), BWTA.getBaseLocations().get(i).getY(), "Base " + i);
			}
			for(int i = 0; i < BWTA.getStartLocations().size(); i++)
			{
				ChronoBot.game.drawTextMap(BWTA.getStartLocations().get(i).getX(), BWTA.getBaseLocations().get(i).getY() + 1, "Start " + i);
			}
			
			//Scout info
			if(ScoutingSystem.scoutID != -1)
			{
				ChronoBot.game.drawTextMap(ChronoBot.game.getUnit(ScoutingSystem.scoutID).getPosition().getX(),
						ChronoBot.game.getUnit(ScoutingSystem.scoutID).getPosition().getY(), "CURRENT SCOUT");
				ChronoBot.game.drawTextMap(ChronoBot.game.getUnit(ScoutingSystem.scoutID).getPosition().getX(),
						ChronoBot.game.getUnit(ScoutingSystem.scoutID).getPosition().getY() + 10, "MISSION TYPE: " + ScoutingSystem.scoutLevel);
				ChronoBot.game.drawTextMap(ChronoBot.game.getUnit(ScoutingSystem.scoutID).getPosition().getX(),
						ChronoBot.game.getUnit(ScoutingSystem.scoutID).getPosition().getY() + 20, "CURRENT MISSION: " + ScoutingSystem.currentLocationID);
				ChronoBot.game.drawTextMap(ChronoBot.game.getUnit(ScoutingSystem.scoutID).getPosition().getX(),
						ChronoBot.game.getUnit(ScoutingSystem.scoutID).getPosition().getY() + 30, "CURRENT MOVEMENT: " + ChronoBot.game.getUnit(ScoutingSystem.scoutID).getOrder().c_str());
			}
			
			//Unit IDs
			for(Unit u: ChronoBot.game.getAllUnits())
			{
				ChronoBot.game.drawTextMap(u.getPosition().getX() - 30, u.getPosition().getY(), "ID: " + u.getID());
				ChronoBot.game.drawTextMap(u.getPosition().getX() - 30, u.getPosition().getY() + 10, u.getOrder().c_str());
				
				if(u.getPlayer().getID() == ChronoBot.self.getID())
				{
					if(u.getType() == UnitType.Terran_Command_Center)
					{
						ChronoBot.game.drawCircleMap(u.getX(), u.getY(), 300, u.getPlayer().getColor());
					}
				}
			}
		}
	}
}
