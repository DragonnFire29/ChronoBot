package chrono.base;

import bwta.BWTA;

public class BaseManagement
{
	public static ManagedBase[] baseArray;
	
	public static void runBaseManager()
	{
		if(baseArray == null)
		{
			baseArray = new ManagedBase[BWTA.getBaseLocations().size()];
			for(int i = 0; i < BWTA.getBaseLocations().size(); i++)
			{
				System.out.println("Creating base for ID " + i);
				baseArray[i] = new ManagedBase(i);
			}
		}
		else
		{
			for(ManagedBase b: baseArray)
			{
				b.Update();
			}
		}
	}
}
