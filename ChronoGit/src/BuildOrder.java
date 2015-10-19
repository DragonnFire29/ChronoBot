import java.util.LinkedList;
import java.util.Queue;

import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;

public class BuildOrder
{
	//BOType
	public static final int BOT_Static = 0;
	public static final int BOT_DYNAMIC = 1;
	public static final int BOT_Neural = 2;
	
	//BOSub - Static
	public static final int BOSS_DEAD = 0;
	public static final int BOSS_1FACTFE = 1;
	
	//BOSub - Dynamic
	//This is mood-based
	public static final int BOSD_PASSIVE = 0;
	public static final int BOSD_AGGRESSIVE = 1;
	public static final int BOSD_CHEEKY = 2;
	public static final int BOSD_JOKER = 3;
	public static final int BOSD_ULTRACHEEKY = 4;
	public static final int BOSD_RANDO = 5;
	
	//BOSub - Neural
	public static final int BOSN_BRAIN1 = 0;
	
	public static Queue<BQSeg> buildOrder;
	public static int reservedMineralCount = 0;
	public static int reservedGasCount = 0;
	
	public static void startBuildOrder (int BOType, int BOSub)
	{
		if(BOType == BOT_Static)
		{
			if(BOSub == BOSS_1FACTFE)
			{
				buildOrder = StaticBO.FactFE();
			}
		}
	}
	
	public static void run()
	{
		//TODO: There must be a way to improve this. As of right now, it's ass.
		//Building construction
		reservedMineralCount = buildOrder.peek().building.mineralPrice();
		reservedGasCount = buildOrder.peek().building.gasPrice();
		if(!buildOrder.isEmpty())
		{
			if(buildOrder.peek().supplyAmount <= ChronoBot.self.supplyUsed() / 2 && buildOrder.peek().supplyCap <= ChronoBot.self.supplyTotal() / 2
					&& buildOrder.peek().building.mineralPrice() <= ChronoBot.self.minerals() && buildOrder.peek().building.gasPrice() <= ChronoBot.self.gas())
			{
				boolean validTargetFound = false;
				for (Unit myUnit : ChronoBot.self.getUnits()) {
					if (myUnit.getType() == UnitType.Terran_SCV && myUnit.getID() != ScoutingSystem.scoutID && validTargetFound == false
							&& !myUnit.isConstructing()) {
						if(buildOrder.peek().building == UnitType.Terran_Command_Center)
						{
							System.out.println("Building a Command Centre!");
							int currentClosestBase = 0;
							double distanceByAir = 99999999;
							for(int b = 0; b < BWTA.getBaseLocations().size(); b++)
							{
								if(!BaseManagement.baseArray[b].isOccupied())
								{
									if(distanceByAir < BWTA.getBaseLocations().get(b).getAirDistance(BWTA.getStartLocation(ChronoBot.self)))
									{
										distanceByAir = BWTA.getBaseLocations().get(b).getAirDistance(BWTA.getStartLocation(ChronoBot.self));
										currentClosestBase = b;
									}
								}
							}
							myUnit.build(BWTA.getBaseLocations().get(currentClosestBase).getTilePosition(), UnitType.Terran_Command_Center);
							buildOrder.poll();
						}
						else
						{
							System.out.println("Got an SCV, building my thing!");
							myUnit.build(Misc.getBuildTile(myUnit, buildOrder.peek().building, ChronoBot.self.getStartLocation()), buildOrder.peek().building);
							buildOrder.poll();
							validTargetFound = true;
						}
					}
				}
			}
			
			//Prevent supply blockage
			//TODO: Super buggy and needs fixing
			if(ChronoBot.self.supplyTotal() - 2 < ChronoBot.self.supplyUsed())
			{
				if(!(buildOrder.peek().building.equals(UnitType.Terran_Supply_Depot)))
				{
					boolean SDUnderConstruction = false;
					for(Unit u: ChronoBot.self.getUnits())
					{
						if (u.getType() == UnitType.Terran_SCV && !SDUnderConstruction)
						{
							if (u.getType() == UnitType.Terran_Supply_Depot && !SDUnderConstruction)
							{
								if(u.isConstructing())
								{
									SDUnderConstruction = true;
								}
							}
							if(u.getBuildType() == UnitType.Terran_Supply_Depot)
							{
								SDUnderConstruction = true;
							}
							else
							{
								System.out.println("Adding Supply Depot to the build list!");
								Queue<BQSeg> newBO = new LinkedList<BQSeg>();
								newBO.add(new BQSeg(UnitType.Terran_Supply_Depot, 0, 0));
								for(BQSeg b: buildOrder)
								{
									newBO.add(b);
								}
								buildOrder = newBO;
								SDUnderConstruction = true;
							}
						}
					}
				}
			}
		}
		
		//For units
		for (Unit myUnit : ChronoBot.self.getUnits()) {
			if (myUnit.getType() == UnitType.Terran_Barracks && !myUnit.isTraining())
			{
				if(ChronoBot.self.minerals() >= 50 + reservedMineralCount &&
						(ChronoBot.self.supplyTotal() - ChronoBot.self.supplyUsed()) / 2 >= 1)
				{
					myUnit.train(UnitType.Terran_Marine);
				}
			}
			if (myUnit.getType() == UnitType.Terran_Factory && !myUnit.isTraining())
			{
				if(myUnit.getAddon().getType() == UnitType.Terran_Machine_Shop)
				{
					if(ChronoBot.self.minerals() >= UnitType.Terran_Siege_Tank_Tank_Mode.mineralPrice() + reservedMineralCount &&
							ChronoBot.self.gas() >= UnitType.Terran_Siege_Tank_Tank_Mode.gasPrice() + reservedGasCount &&
							(ChronoBot.self.supplyTotal() - ChronoBot.self.supplyUsed()) / 2 >= 2)
					{
						myUnit.train(UnitType.Terran_Siege_Tank_Tank_Mode);
					}
				}
				else
				{
					if(ChronoBot.self.minerals() >= UnitType.Terran_Vulture.mineralPrice() + reservedMineralCount &&
							ChronoBot.self.gas() >= UnitType.Terran_Vulture.gasPrice() + reservedGasCount &&
							(ChronoBot.self.supplyTotal() - ChronoBot.self.supplyUsed()) / 2 >= 2)
					{
						myUnit.train(UnitType.Terran_Vulture);
					}
				}
			}
		}
		
		//Attack the bitch when we have enough units.
		int attackThreshold = 6;
		int attackerCount = 0;
		for(Unit u: ChronoBot.self.getUnits())
		{
			if(u.getType() == UnitType.Men)
			{
				attackerCount++;
			}
		}
		if(attackerCount >= attackThreshold)
		{
			for(Unit u: ChronoBot.self.getUnits())
			{
				for(ManagedBase b: BaseManagement.baseArray)
				{
					if(b.isEnemy())
					{
						u.attack(BWTA.getBaseLocations().get(b.baseID).getPosition());
					}
				}
			}
		}
	}
	
	public static void getNextSegment()
	{
		//TODO: Implement the dynamic decision engine.
	}
}
