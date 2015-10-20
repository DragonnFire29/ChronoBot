package chrono.base;

import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import chrono.ChronoBot;
import chrono.buildorder.BuildOrder;

public class ManagedBase {

	public int baseID;
	//TownHallID is -1 when there is none.
	public int TownHallID;
	//OwnedPlayerID is -1 when not owned
	public int OwnedPlayerID;
	public int MineralCount;
	public int GeyserCount;
	public int GasMineCount;
	public int MineralHarvesters;
	public int GasHarvesters;
	public int MaxMineralHarvesters;
	public int MaxGasHarvesters;
	public int WorkersNeeded;
	public int WorkersPresent;
	public boolean isVisible = false;
	
	public ManagedBase(int baseID)
	{
		this.baseID = baseID;
		TownHallID = -1;
		OwnedPlayerID = -1;
	}
	
	public void Update()
	{
		//First, check to see if this location is visible. If it is, we can continue the update.
		if(ChronoBot.game.isVisible(BWTA.getBaseLocations().get(baseID).getTilePosition().getX(),
				BWTA.getBaseLocations().get(baseID).getTilePosition().getY()))
		{
			isVisible = true;
			//If it's visible, see if there's a town hall at that position.
			boolean tileStillOwned = false;
			for(Unit u:ChronoBot.game.getUnitsOnTile(BWTA.getBaseLocations().get(baseID).getTilePosition().getX(),
				BWTA.getBaseLocations().get(baseID).getTilePosition().getY()))
			{
				if(u.getType() == UnitType.Terran_Command_Center || u.getType() == UnitType.Protoss_Nexus
						|| u.getType() == UnitType.Zerg_Hatchery || u.getType() == UnitType.Zerg_Lair
						|| u.getType() == UnitType.Zerg_Hive)
				{
					//If it is, then mark is as occupied, with playerID and TownHallID.
					OwnedPlayerID = u.getPlayer().getID();
					TownHallID = u.getID();
					tileStillOwned = true;
				}
			}
			if(tileStillOwned == false)
			{
				OwnedPlayerID = -1;
				TownHallID = -1;
			}
			//Since it's visible, we should check to see if it's our base.
			if (OwnedPlayerID == ChronoBot.self.getID())
			{
				//Since it's ours, we should write up a worker concentration summary. That way, we will know if we need more workers.
				int workerCount = 0;
				int geyserCount = 0;
				int gasMineCount = 0;
				int mineralCount = 0;
				int mineralMinerCount = 0;
				int gasMinerCount = 0;
				for(Unit u: ChronoBot.game.getUnitsInRadius(ChronoBot.game.getUnit(TownHallID).getPosition(), 200))
				{
					if(u.getPlayer().equals(ChronoBot.self))
					{
						if(u.getType() == UnitType.Terran_SCV || u.getType() == UnitType.Protoss_Probe
								|| u.getType() == UnitType.Zerg_Drone)
						{
							workerCount++;
							if(u.isGatheringMinerals())
							{
								mineralMinerCount++;
							}
							if(u.isGatheringGas())
							{
								gasMinerCount++;
							}
						}
					}
					if(u.getType() == UnitType.Resource_Vespene_Geyser)
					{
						geyserCount++;
					}
					if(u.getType() == UnitType.Terran_Refinery || u.getType() == UnitType.Protoss_Assimilator
							|| u.getType() == UnitType.Zerg_Extractor)
					{
						gasMineCount++;
					}
					if(u.getType() == UnitType.Resource_Mineral_Field || u.getType() == UnitType.Resource_Mineral_Field_Type_2
							|| u.getType() == UnitType.Resource_Mineral_Field_Type_3)
					{
						mineralCount++;
					}
					//Update our public values.
					MineralCount = mineralCount;
					GeyserCount = geyserCount;
					GasMineCount = gasMineCount;
					MaxMineralHarvesters = mineralCount * 3;
					MaxGasHarvesters = gasMineCount * 3;
					MineralHarvesters = mineralMinerCount;
					GasHarvesters = gasMinerCount;
					WorkersNeeded = (MaxMineralHarvesters + MaxGasHarvesters) - (MineralHarvesters + GasHarvesters);
					WorkersPresent = workerCount;
				}
			}

			for(Unit u: ChronoBot.game.getUnitsInRadius(ChronoBot.game.getUnit(TownHallID).getPosition(), 500))
			{
				//Set any idle workers to work, if we're not past the threshold.
				if(u.getType() == UnitType.Terran_SCV)
				{
					if(u.isIdle())
					{
						System.out.println("Idle found in base " + baseID);
						for(Unit un:ChronoBot.game.getUnitsInRadius(u.getPosition(), 500))
						{
							//TODO: Piroritize gas production in some way. As of now, we tend to have too few gas workers.
							if(MaxGasHarvesters > GasHarvesters)
							{
								if(un.getType() == UnitType.Terran_Refinery)
								{
									u.gather(un);
								}
							}
							if(MaxMineralHarvesters > MineralHarvesters)
							{
								if(un.getType() == UnitType.Resource_Mineral_Field || un.getType() == UnitType.Resource_Mineral_Field_Type_2
										|| un.getType() == UnitType.Resource_Mineral_Field_Type_3)
								{
									u.gather(un);
								}
							}
						}
					}
				}
				//If we have enough minerals and we need more workers, build some.
				/*TODO: Rewrite this. Ideally, we would have a flag indicating when worker construction is appropriate, and when it's
				 * not. Just building workers all willy-nilly is a horrid idea.
				 */
				if (u.getType() == UnitType.Terran_Command_Center) {
					if(WorkersNeeded > 0 && ChronoBot.self.minerals() >= 50 + BuildOrder.reservedMineralCount && !u.isTraining())
					{
						u.train(UnitType.Terran_SCV);
					}
				}
			}
		}
		else
		{
			isVisible = false;
		}
	}
	
	public boolean isOccupied()
	{
		if(OwnedPlayerID == -1 && TownHallID == -1)
		{
			return true;
		}
		return false;
	}
	
	public boolean isMine()
	{
		if(OwnedPlayerID == ChronoBot.self.getID())
		{
			return true;
		}
		return false;
	}
	
	public boolean isEnemy()
	{
		for(Player p: ChronoBot.game.enemies())
		{
			if(OwnedPlayerID == p.getID())
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		if(OwnedPlayerID == -1)
		{
			return "Base: " + baseID;
		}
		else
		{
			return "Base: " + baseID + " - Owner: " + ChronoBot.game.getPlayer(OwnedPlayerID).getName();
		}
	}
}
