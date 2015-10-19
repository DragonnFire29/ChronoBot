import bwapi.Order;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;

public class ScoutingSystem {
	
	//Scout Tiers
	public static final int ST_SCV = 0;
	public static final int ST_MARINE = 1;
	public static final int ST_VULTURE = 2;
	public static final int ST_WRAITH = 3;
	
	//Scout Level
	public static final int SL_STARTONLY = 0;
	public static final int SL_ALLBASES = 1;
	
	//Other values
	public static int scoutID = -1;
	public static int scoutTier = 0;
	public static int scoutLevel = 0;
	public static int currentLocationID = 0;
	public static boolean enemyFound = false;
	
	public static void runScout()
	{
		//Check to see if our scout exists.
		if(scoutID == -1 || ChronoBot.game.getUnit(scoutID).getHitPoints() <= 0)
		{
			System.out.println("Getting new scout!");
			getNewScout();
		}
		else if(!(ChronoBot.game.getUnit(scoutID).getOrder() == Order.Move))
		{
			System.out.println("Going to the next location!");
			goToNextLocation();
		}
		
		if(!enemyFound)
		{
			for(Unit u: ChronoBot.game.getAllUnits())
			{
				if(u.getPlayer().isEnemy(ChronoBot.self))
				{
					System.out.println("Found the enemy base!");
					enemyFound = true;
					scoutLevel = SL_ALLBASES;
				}
			}
		}
	}
	
	public static void getNewScout()
	{
		UnitType scoutUnitType = UnitType.Terran_SCV;
		if(scoutTier == ST_SCV)
		{
			scoutUnitType = UnitType.Terran_SCV;
		}
		else if(scoutTier == ST_MARINE)
		{
			scoutUnitType = UnitType.Terran_Marine;
		}
		else if(scoutTier == ST_VULTURE)
		{
			scoutUnitType = UnitType.Terran_Vulture;
		}
		else if(scoutTier == ST_WRAITH)
		{
			scoutUnitType = UnitType.Terran_Wraith;
		}
		boolean scoutFound = false;
		for(Unit u: ChronoBot.self.getUnits())
		{
			if(!scoutFound)
			{
				if(u.getType() == scoutUnitType)
				{
					scoutFound = true;
					scoutID = u.getID();
				}
			}
		}
	}
	
	public static void goToNextLocation()
	{
		if(scoutLevel == SL_STARTONLY)
		{
			ChronoBot.game.getUnit(scoutID).move(BWTA.getStartLocations().get(currentLocationID).getPosition());
			currentLocationID++;
			if(currentLocationID >= BWTA.getStartLocations().size())
			{
				currentLocationID = 0;
			}
		}
		else if(scoutLevel == SL_ALLBASES)
		{
			ChronoBot.game.getUnit(scoutID).move(BWTA.getBaseLocations().get(currentLocationID).getPosition());
			currentLocationID++;
			if(currentLocationID >= BWTA.getBaseLocations().size())
			{
				currentLocationID = 0;
			}
		}
	}
}
