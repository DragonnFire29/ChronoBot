package chrono.buildorder;

import bwapi.UnitType;

public class BQSeg
{
	public UnitType building;
	public int supplyAmount;
	public int supplyCap;
	
	public static final UnitType GENERATE_NEXT = UnitType.Hero_Alan_Schezar;
	
	public BQSeg(UnitType terran_Supply_Depot, int supplyAmount, int supplyCap)
	{
		this.building = terran_Supply_Depot;
		this.supplyAmount = supplyAmount;
		this.supplyCap = supplyCap;
	}
}
