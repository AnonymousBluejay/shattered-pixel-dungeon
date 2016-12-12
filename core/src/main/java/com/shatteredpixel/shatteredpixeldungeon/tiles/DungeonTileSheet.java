/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import android.util.SparseIntArray;

import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.watabou.utils.Random;

import java.util.Arrays;
import java.util.List;

public class DungeonTileSheet {

	private static final int WIDTH = 16;

	private static int xy(int x, int y){
		x -= 1; y -= 1;
		return x + WIDTH*y;
	}

	//used in cases like map-edge decision making.
	public static final int NULL_TILE       = -1;



	/**********************************************************************
	 * Floor Tiles
	 **********************************************************************/

	private static final int GROUND         =                               xy(1, 1);   //32 slots
	public static final int FLOOR           = GROUND +0;
	public static final int FLOOR_DECO      = GROUND +1;
	public static final int HIGH_GRASS      = GROUND +2;
	public static final int GRASS           = GROUND +3;
	public static final int EMBERS          = GROUND +4;
	public static final int FLOOR_SP        = GROUND +5;

	public static final int FLOOR_ALT_1     = GROUND +7;
	public static final int FLOOR_DECO_ALT  = GROUND +8;
	public static final int HIGH_GRASS_ALT  = GROUND +9;
	public static final int GRASS_ALT       = GROUND +10;
	public static final int EMBERS_ALT      = GROUND +11;
	public static final int FLOOR_SP_ALT    = GROUND +12;

	public static final int FLOOR_ALT_2     = GROUND +14;

	public static final int ENTRANCE        = GROUND +16;
	public static final int EXIT            = GROUND +17;
	public static final int SIGN            = GROUND +18;
	public static final int STATUE          = GROUND +19;
	public static final int STATUE_SP       = GROUND +20;
	public static final int WELL            = GROUND +21;
	public static final int EMPTY_WELL      = GROUND +22;
	public static final int ALCHEMY_POT     = GROUND +23;
	public static final int PEDESTAL        = GROUND +24;
	public static final int BARRICADE       = GROUND +25;
	public static final int BOOKSHELF       = GROUND +26;

	public static final int BOOKSHELF_ALT   = GROUND +28;



	/**********************************************************************
	 * Water Tiles
	 **********************************************************************/

	public static final int WATER =                                         xy(1, 3);   //16 slots
	//next 15 slots are all water stitching with ground.

	//These tiles can stitch with water
	public static List waterStitcheable = Arrays.asList(
			Terrain.EMPTY, Terrain.GRASS, Terrain.EMPTY_WELL,
			Terrain.ENTRANCE, Terrain.EXIT, Terrain.EMBERS,
			Terrain.BARRICADE, Terrain.HIGH_GRASS, Terrain.SECRET_TRAP,
			Terrain.TRAP, Terrain.INACTIVE_TRAP, Terrain.EMPTY_DECO,
			Terrain.SIGN, Terrain.WELL, Terrain.STATUE, Terrain.ALCHEMY,
			Terrain.DOOR, Terrain.OPEN_DOOR, Terrain.LOCKED_DOOR
	);

	//+1 for ground above, +2 for ground right, +4 for ground below, +8 for ground left.
	public static int stitchWaterTile(int top, int right, int bottom, int left){
		int result = WATER;
		if (waterStitcheable.contains(top))     result += 1;
		if (waterStitcheable.contains(right))   result += 2;
		if (waterStitcheable.contains(bottom))  result += 4;
		if (waterStitcheable.contains(left))    result += 8;
		return result;
	}



	/**********************************************************************
	 * Chasm Tiles
	 **********************************************************************/

	public static final int CHASM                   =                       xy(1, 4);   //16 tiles
	//chasm stitching visuals...
	public static final int CHASM_FLOOR             = CHASM+1;
	public static final int CHASM_FLOOR_SP          = CHASM+2;
	public static final int CHASM_WALL              = CHASM+3;
	public static final int CHASM_WATER             = CHASM+4;

	//tiles that can stitch with chasms (from above), and which visual represents the stitching
	public static SparseIntArray chasmStitcheable = new SparseIntArray(32);
	static {
		//floor
		chasmStitcheable.put( Terrain.EMPTY,        CHASM_FLOOR );
		chasmStitcheable.put( Terrain.GRASS,        CHASM_FLOOR );
		chasmStitcheable.put( Terrain.EMPTY_WELL,   CHASM_FLOOR );
		chasmStitcheable.put( Terrain.HIGH_GRASS,   CHASM_FLOOR );
		chasmStitcheable.put( Terrain.EMPTY_DECO,   CHASM_FLOOR );
		chasmStitcheable.put( Terrain.SIGN,         CHASM_FLOOR );
		chasmStitcheable.put( Terrain.EMPTY_WELL,   CHASM_FLOOR );
		chasmStitcheable.put( Terrain.STATUE,       CHASM_FLOOR );

		//special floor
		chasmStitcheable.put( Terrain.EMPTY_SP,     CHASM_FLOOR_SP );
		chasmStitcheable.put( Terrain.STATUE_SP,    CHASM_FLOOR_SP );

		//wall
		chasmStitcheable.put( Terrain.WALL,         CHASM_WALL );
		chasmStitcheable.put( Terrain.DOOR,         CHASM_WALL );
		chasmStitcheable.put( Terrain.OPEN_DOOR,    CHASM_WALL );
		chasmStitcheable.put( Terrain.LOCKED_DOOR,  CHASM_WALL );
		chasmStitcheable.put( Terrain.WALL_DECO,    CHASM_WALL );

		//water
		chasmStitcheable.put( Terrain.WATER,        CHASM_WATER );
	}

	public static int stitchChasmTile(int above){
		return chasmStitcheable.get(above, CHASM);
	}

	/**********************************************************************
	 Flat Wall Tiles
	 **********************************************************************/

	private static final int FLAT_WALLS         =                           xy(1, 5);   //16 slots
	public static final int FLAT_WALL           = FLAT_WALLS+0;
	public static final int FLAT_WALL_DECO      = FLAT_WALLS+1;

	public static final int FLAT_WALL_ALT       = FLAT_WALLS+3;
	public static final int FLAT_WALL_DECO_ALT  = FLAT_WALLS+4;

	private static final int FLAT_DOORS         =                           xy(1,6);    //16 slots
	public static final int FLAT_DOOR           = FLAT_DOORS+0;
	public static final int FLAT_DOOR_OPEN      = FLAT_DOORS+1;
	public static final int FLAT_DOOR_LOCKED    = FLAT_DOORS+2;
	public static final int UNLOCKED_EXIT       = FLAT_DOORS+3;
	public static final int LOCKED_EXIT         = FLAT_DOORS+4;



	/**********************************************************************
	 * Raised Wall Tiles, Lower Layer
	 **********************************************************************/

	private static final int RAISED_WALLS           =                       xy(1, 8);   //32 slots
	//+1 for open to the right, +2 for open to the left
	public static final int RAISED_WALL             = RAISED_WALLS+0;
	public static final int RAISED_WALL_DECO        = RAISED_WALLS+4;
	//wall that appears behind a top/bottom doorway
	public static final int RAISED_WALL_DOOR        = RAISED_WALLS+8;

	public static final int RAISED_WALL_ALT         = RAISED_WALLS+16;
	public static final int RAISED_WALL_DECO_ALT    = RAISED_WALLS+20;

	//These tiles count as wall for the purposes of wall stitching
	public static List wallStitcheable = Arrays.asList(
			Terrain.WALL, Terrain.WALL_DECO, Terrain.SECRET_DOOR,
			Terrain.LOCKED_EXIT, Terrain.UNLOCKED_EXIT, NULL_TILE
	);

	public static int getRaisedWallTile(int tile, int pos, int right, int below, int left){
		int result;
		if (doorTiles.contains(below))      result = RAISED_WALL_DOOR;
		else if (tile == Terrain.WALL)      result = RAISED_WALL;
		else if (tile == Terrain.WALL_DECO) result = RAISED_WALL_DECO;
		else    return -1;

		result = getVisualWithAlts(result, pos);

		if (!wallStitcheable.contains(right)) result += 1;
		if (!wallStitcheable.contains(left)) result += 2;
		return result;
	}

	private static final int RAISED_DOORS           =                       xy(1, 10);  //16 slots
	public static final int RAISED_DOOR             = RAISED_DOORS+0;
	public static final int RAISED_DOOR_OPEN        = RAISED_DOORS+1;
	public static final int RAISED_DOOR_LOCKED      = RAISED_DOORS+2;
	//floor tile that appears on a top/bottom doorway
	public static final int RAISED_DOOR_SIDEWAYS    = RAISED_DOORS+3;


	public static int getRaisedDoorTile(int tile, int below){
		if (wallStitcheable.contains(below))    return RAISED_DOOR_SIDEWAYS;
		else if (tile == Terrain.DOOR)          return DungeonTileSheet.RAISED_DOOR;
		else if (tile == Terrain.OPEN_DOOR)     return DungeonTileSheet.RAISED_DOOR_OPEN;
		else if (tile == Terrain.LOCKED_DOOR)   return DungeonTileSheet.RAISED_DOOR_LOCKED;
		else return -1;
	}

	public static List doorTiles = Arrays.asList(
			Terrain.DOOR, Terrain.LOCKED_DOOR, Terrain.OPEN_DOOR
	);



	/**********************************************************************
	 * Raised Wall Tiles, Upper Layer
	 **********************************************************************/

	//+1 for open right, +2 for open right-below, +4 for open left-below, +8 for open left.
	public static final int WALLS_INTERNAL              =                   xy(1, 12);  //16 slots

	public static int stitchInternalWallTile(int right, int rightBelow, int leftBelow, int left){
		int result = WALLS_INTERNAL;
		if (!wallStitcheable.contains(right))        result += 1;
		if (!wallStitcheable.contains(rightBelow))   result += 2;
		if (!wallStitcheable.contains(leftBelow))    result += 4;
		if (!wallStitcheable.contains(left))         result += 8;
		return result;
	}

	//+1 for open to the down-right, +2 for open to the down-left
	private static final int WALLS_OVERHANG             =                   xy(1, 13);  //16 slots
	public static final int WALL_OVERHANG               = WALLS_OVERHANG+0;
	public static final int DOOR_SIDEWAYS_OVERHANG      = WALL_OVERHANG+4;
	public static final int DOOR_SIDEWAYS_OVERHANG_OPEN = WALL_OVERHANG+8;

	public static int stitchWallOverhangTile(int tile, int rightBelow, int leftBelow){
		int visual;
		if (tile == Terrain.DOOR || tile == Terrain.LOCKED_DOOR)    visual = DOOR_SIDEWAYS_OVERHANG;
		else if (tile == Terrain.OPEN_DOOR)                         visual = DOOR_SIDEWAYS_OVERHANG_OPEN;
		else                                                        visual = WALL_OVERHANG;

		if (!wallStitcheable.contains(rightBelow))  visual += 1;
		if (!wallStitcheable.contains(leftBelow))   visual += 2;

		return visual;
	}

	//no attachment to adjacent walls
	public static final int DOOR_OVERHANG               = WALL_OVERHANG+12;
	public static final int DOOR_OVERHANG_OPEN          = WALL_OVERHANG+13;
	public static final int DOOR_SIDEWAYS               = WALL_OVERHANG+14;
	public static final int DOOR_SIDEWAYS_LOCKED        = WALL_OVERHANG+15;

	/**********************************************************************
	 * Logic for the selection of tile visuals
	 **********************************************************************/

	//These visuals always directly represent a game tile with no stitching required
	public static SparseIntArray directVisuals = new SparseIntArray(32);
	static {
		directVisuals.put(Terrain.EMPTY,            FLOOR);
		directVisuals.put(Terrain.GRASS,            GRASS);
		directVisuals.put(Terrain.EMPTY_WELL,       EMPTY_WELL);
		directVisuals.put(Terrain.ENTRANCE,         ENTRANCE);
		directVisuals.put(Terrain.EXIT,             EXIT);
		directVisuals.put(Terrain.EMBERS,           EMBERS);
		directVisuals.put(Terrain.PEDESTAL,         PEDESTAL);
		directVisuals.put(Terrain.BARRICADE,        BARRICADE);
		directVisuals.put(Terrain.EMPTY_SP,         FLOOR_SP);
		directVisuals.put(Terrain.HIGH_GRASS,       HIGH_GRASS);

		directVisuals.put(Terrain.SECRET_TRAP,      directVisuals.get(Terrain.EMPTY));
		directVisuals.put(Terrain.TRAP,             directVisuals.get(Terrain.EMPTY));
		directVisuals.put(Terrain.INACTIVE_TRAP,    directVisuals.get(Terrain.EMPTY));

		directVisuals.put(Terrain.EMPTY_DECO,       FLOOR_DECO);
		directVisuals.put(Terrain.LOCKED_EXIT,      LOCKED_EXIT);
		directVisuals.put(Terrain.UNLOCKED_EXIT,    UNLOCKED_EXIT);
		directVisuals.put(Terrain.SIGN,             SIGN);
		directVisuals.put(Terrain.WELL,             WELL);
		directVisuals.put(Terrain.STATUE,           STATUE);
		directVisuals.put(Terrain.STATUE_SP,        STATUE_SP);
		directVisuals.put(Terrain.BOOKSHELF,        BOOKSHELF);
		directVisuals.put(Terrain.ALCHEMY,          ALCHEMY_POT);

	}

	//These visuals directly represent game tiles (no stitching) when terrain is being shown as flat
	public static SparseIntArray directFlatVisuals = new SparseIntArray(32);
	static {
		directFlatVisuals.put(Terrain.WALL,             FLAT_WALL);
		directFlatVisuals.put(Terrain.DOOR,             FLAT_DOOR);
		directFlatVisuals.put(Terrain.OPEN_DOOR,        FLAT_DOOR_OPEN);
		directFlatVisuals.put(Terrain.LOCKED_DOOR,      FLAT_DOOR_LOCKED);
		directFlatVisuals.put(Terrain.WALL_DECO,        FLAT_WALL_DECO);

		directFlatVisuals.put(Terrain.SECRET_DOOR,      directFlatVisuals.get(Terrain.WALL));
	}


	/**********************************************************************
	 * Logic for the selection of alternate tile visuals
	 **********************************************************************/

	public static byte[] tileVariance;

	public static void setupVariance(int size, long seed){
		Random.seed( seed );
		tileVariance = new byte[size];
		for (int i = 0; i < tileVariance.length; i++)
			tileVariance[i] = (byte)Random.Int(100);
		Random.seed();
	}

	//These alt visuals will trigger 50% of the time (45% of the time if a rare alt is also present)
	public static SparseIntArray commonAltVisuals = new SparseIntArray(32);
	static {
		commonAltVisuals.put(FLOOR,             FLOOR_ALT_1);
		commonAltVisuals.put(GRASS,             GRASS_ALT);
		commonAltVisuals.put(FLAT_WALL,         FLAT_WALL_ALT);
		commonAltVisuals.put(EMBERS,            EMBERS_ALT);
		commonAltVisuals.put(FLAT_WALL_DECO,    FLAT_WALL_DECO_ALT);
		commonAltVisuals.put(FLOOR_SP,          FLOOR_SP_ALT);
		commonAltVisuals.put(HIGH_GRASS,        HIGH_GRASS_ALT);
		commonAltVisuals.put(FLOOR_DECO,        FLOOR_DECO_ALT);

		commonAltVisuals.put(BOOKSHELF,         BOOKSHELF_ALT);

		commonAltVisuals.put(RAISED_WALL,       RAISED_WALL_ALT);
		commonAltVisuals.put(RAISED_WALL_DECO,  RAISED_WALL_DECO_ALT);
	}

	//These alt visuals trigger 5% of the time (and also override common alts when they show up)
	public static SparseIntArray rareAltVisuals = new SparseIntArray(32);
	static {
		rareAltVisuals.put(FLOOR,               FLOOR_ALT_2);
	}

	public static int getVisualWithAlts(int visual, int pos){
		if (tileVariance[pos] >= 95 && rareAltVisuals.indexOfKey(visual) >= 0)
			return rareAltVisuals.get(visual);
		else if (tileVariance[pos] >= 50 && commonAltVisuals.indexOfKey(visual) >= 0)
			return commonAltVisuals.get(visual);
		else
			return visual;
	}

}
