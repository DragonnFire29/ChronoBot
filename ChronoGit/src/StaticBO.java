import java.util.LinkedList;
import java.util.Queue;

import bwapi.UnitType;

public class StaticBO
{
	//One Factory Fast Expand
	//http://wiki.teamliquid.net/starcraft/1_Fact_FE_(vs._Terran)
	public static Queue<BQSeg> FactFE()
	{
		Queue<BQSeg> buildOrder = new LinkedList<BQSeg>();
		buildOrder.add(new BQSeg(UnitType.Terran_Supply_Depot, 9, 10));
		buildOrder.add(new BQSeg(UnitType.Terran_Barracks, 12, 18));
		buildOrder.add(new BQSeg(UnitType.Terran_Refinery, 12, 18));
		buildOrder.add(new BQSeg(UnitType.Terran_Supply_Depot, 15, 18));
		buildOrder.add(new BQSeg(UnitType.Terran_Factory, 16, 26));
		buildOrder.add(new BQSeg(UnitType.Terran_Machine_Shop, 20, 26));
		buildOrder.add(new BQSeg(UnitType.Terran_Siege_Tank_Tank_Mode, 22, 26));
		buildOrder.add(new BQSeg(UnitType.Terran_Supply_Depot, 23, 26));
		buildOrder.add(new BQSeg(UnitType.Terran_Command_Center, 28, 34));
		buildOrder.add(new BQSeg(UnitType.Terran_Supply_Depot, 28, 44));
		buildOrder.add(new BQSeg(UnitType.Terran_Factory, 32, 54));
		
		return buildOrder;
	}
	
	//1 Port Wraith
	//http://wiki.teamliquid.net/starcraft/1_Port_Wraith_(vs._Terran)
	
	//1 Rax FE
	//http://wiki.teamliquid.net/starcraft/1_Rax_FE_(vs._Terran)
	
	//14 CC
	//http://wiki.teamliquid.net/starcraft/14_CC_(vs._Terran)
	
	//2 Fact Vults
	//http://wiki.teamliquid.net/starcraft/2_Fact_Vults_(vs._Terran)
	
	//2 Port Wraith
	//http://wiki.teamliquid.net/starcraft/2_Port_Wraith
	
	//Barracks Barracks Supply
	//http://wiki.teamliquid.net/starcraft/Barracks_Barracks_Supply_(vs._Terran)
	
	//Proxy 5 Rax
	//http://wiki.teamliquid.net/starcraft/Proxy_5_Rax
	
	//Three Factory Vultures
	//http://wiki.teamliquid.net/starcraft/Three_Factory_Vultures
	
	//http://wiki.teamliquid.net/starcraft/Terran_vs._Terran_Guide
	//http://wiki.teamliquid.net/starcraft/Terran_vs._Terran_Timing
}
