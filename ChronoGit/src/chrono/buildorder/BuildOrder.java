package chrono.buildorder;

import java.util.LinkedList;
import java.util.Queue;

import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import chrono.ChronoBot;
import chrono.Misc;
import chrono.base.BaseManagement;
import chrono.base.ManagedBase;
import chrono.scout.ScoutingSystem;

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
	
	/**
	 * Constructor: This initializes the build order we'll be using: Either a static BO or a dynamic one.
	 * A static BO should only be used for early testing and effectiveness for early game against different strategies,
	 * while a dynamic BO should be used in the final version when dynamic BOs are functional.
	 * @param BOType Type of build order to use. Finals start with BOT_
	 * @param BOSub Subtype of selected build order. 
	 * For static, the final is prefixed BOSS_ and reflects the build; 
	 * For dynamic, the final is prefixed BOSD_ and reflects a mood; 
	 * For neural, the final is prefixed BOSN_ and reflects which brain will be used
	 */
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
	
	/**
	 * This runs the BuildOrder, according to the variables set by the constructor
	 */
	public static void run()
	{
		//TODO: There must be a way to improve this. As of right now, it's ass.
		//Check if buildOrder is empty, to prevent the bot from stopping for that frame.
		if(!buildOrder.isEmpty())
		{
			//Reserve gas and minerals for building construction
			reservedMineralCount = buildOrder.peek().building.mineralPrice();
			reservedGasCount = buildOrder.peek().building.gasPrice();
			/*
			 * Check if:
			 * 		1. We have enough supply used to trigger the order
			 * 		2. Our supply cap is high enough to trigger the order
			 * 		3. We have enough minerals to trigger the order
			 * 		4. We have enough gas to trigger the order
			 */
			if(buildOrder.peek().supplyAmount <= ChronoBot.self.supplyUsed() / 2 && buildOrder.peek().supplyCap <= ChronoBot.self.supplyTotal() / 2
					&& buildOrder.peek().building.mineralPrice() <= ChronoBot.self.minerals() && buildOrder.peek().building.gasPrice() <= ChronoBot.self.gas())
			{
				boolean validTargetFound = false;
				//Cycle through units
				for (Unit myUnit : ChronoBot.self.getUnits()) {
					if(!myUnit.isConstructing())
					{
						//Look for a non-scout SCV, if we haven't already found one.
						if (myUnit.getType() == UnitType.Terran_SCV && myUnit.getID() != ScoutingSystem.scoutID && validTargetFound == false
								&& !myUnit.isConstructing()) {
							//If the current order is for a Command Centre, find the nearest base to build from
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
							}
							else
							{
								System.out.println("Got an SCV, building my thing!");
								myUnit.build(Misc.getBuildTile(myUnit, buildOrder.peek().building, ChronoBot.self.getStartLocation()), buildOrder.peek().building);
								validTargetFound = true;
							}
						}
					}
					else
					{
						validTargetFound = false;
					}
				}
			}
			
			//Prevent supply blockage
			//Check if we are (or near) supply block
			if(ChronoBot.self.supplyTotal() - 2 < ChronoBot.self.supplyUsed())
			{
				//Check to make sure the next unit built is NOT a supply depot
				if(!(buildOrder.peek().building.equals(UnitType.Terran_Supply_Depot)))
				{
					boolean SDUnderConstruction = false;
					//Iterate over units to find any SCVs going to build or any supply depots under construction.
					for(Unit u: ChronoBot.self.getUnits())
					{
						//Check if we have already found one
						if(!SDUnderConstruction)
						{
							//TODO: Finish the anti-supplyblock code
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
