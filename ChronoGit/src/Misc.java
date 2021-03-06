import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

public class Misc {
	// Returns a suitable TilePosition to build a given building type near 
	// specified TilePosition aroundTile, or null if not found. (builder parameter is our worker)
	
	static boolean lastBuildTileFailed = false;
	@SuppressWarnings("unused")
	public static TilePosition getBuildTile(Unit builder, UnitType buildingType, TilePosition aroundTile) {
		TilePosition ret = null;
		int maxDist = 3;
		int stopDist = 40;
		
		// Refinery, Assimilator, Extractor
		if (buildingType.isRefinery()) {
			for (Unit n : ChronoBot.game.neutral().getUnits()) {
				if ((n.getType() == UnitType.Resource_Vespene_Geyser) && 
						( Math.abs(n.getTilePosition().getX() - aroundTile.getX()) < stopDist ) &&
						( Math.abs(n.getTilePosition().getY() - aroundTile.getY()) < stopDist )
						) return n.getTilePosition();
			}
		}
		
		while ((maxDist < stopDist) && (ret == null)) {
			for (int i=aroundTile.getX()-maxDist; i<=aroundTile.getX()+maxDist; i++) {
				for (int j=aroundTile.getY()-maxDist; j<=aroundTile.getY()+maxDist; j++) {
					if (ChronoBot.game.canBuildHere(builder, new TilePosition(i,j), buildingType, false)) {
						// units that are blocking the tile
						boolean unitsInWay = false;
						for (Unit u : ChronoBot.game.getAllUnits()) {
							if (u.getID() == builder.getID()) continue;
							if ((Math.abs(u.getTilePosition().getX()-i) < 4) && (Math.abs(u.getTilePosition().getY()-j) < 4)) unitsInWay = true;
						}
						if (!unitsInWay) {
							return new TilePosition(i, j);
						}
						// creep for Zerg
						if (buildingType.requiresCreep()) {
							boolean creepMissing = false;
							for (int k=i; k<=i+buildingType.tileWidth(); k++) {
								for (int l=j; l<=j+buildingType.tileHeight(); l++) {
									if (!ChronoBot.game.hasCreep(k, l)) creepMissing = true;
									break;
								}
							}
							if (creepMissing) continue; 
						}
					}
				}
			}
			maxDist += 2;
		}
		
		if (ret == null) ChronoBot.game.printf("Unable to find suitable build position for "+buildingType.toString());
		lastBuildTileFailed = true;
		return ret;
	}
	
	public static boolean lastBuildAttemptFailed()
	{
		boolean failed = lastBuildTileFailed;
		lastBuildTileFailed = false;
		return failed;
	}
}
