/*
 * Pixel Dungeon
 * Copyright (C) 2012-2014  Oleg Dolya
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
package com.hmdzl.spspd.levels.painters;

import com.hmdzl.spspd.Dungeon;
import com.hmdzl.spspd.items.Generator;
import com.hmdzl.spspd.items.Item;
import com.hmdzl.spspd.items.keys.IronKey;
import com.hmdzl.spspd.items.weapon.missiles.buildblock.DoorBlock;
import com.hmdzl.spspd.levels.Level;
import com.hmdzl.spspd.levels.Room;
import com.hmdzl.spspd.levels.Terrain;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class MaterialPainter extends Painter {

	public static void paint(Level level, Room room) {

		fill(level, room, Terrain.WALL);
		fill(level, room, 1, Terrain.EMPTY);

		Room.Door entrance = room.entrance();
		Point statue = null;
		if (entrance.x == room.left) {
			statue = new Point(room.right - 1,
					Random.Int(2) == 0 ? room.top + 1 : room.bottom - 1);
		} else if (entrance.x == room.right) {
			statue = new Point(room.left + 1, Random.Int(2) == 0 ? room.top + 1
					: room.bottom - 1);
		} else if (entrance.y == room.top) {
			statue = new Point(Random.Int(2) == 0 ? room.left + 1
					: room.right - 1, room.bottom - 1);
		} else if (entrance.y == room.bottom) {
			statue = new Point(Random.Int(2) == 0 ? room.left + 1
					: room.right - 1, room.top + 1);
		}
		if (statue != null) {
			set(level, statue, Terrain.STATUE);
		}

		Point pot = room.center();

		set(level, pot, Terrain.IRON_MAKER);

		int n = Random.IntRange(2, 3);
		for (int i = 0; i < n; i++) {
			int pos;
			do {
				pos = room.random();
			} while (level.map[pos] != Terrain.EMPTY
					|| level.heaps.get(pos) != null);
			level.drop(prize(level), pos);
		}
		
		for (int i = 0; i < 3; i++) {
			int pos;
			do {
				pos = room.random();
			} while (level.map[pos] != Terrain.EMPTY
					|| level.heaps.get(pos) != null);
			level.drop(prize2(level), pos);
		}

		entrance.set(Room.Door.Type.LOCKED);
		level.addItemToSpawn(new IronKey(Dungeon.depth));
	}

	private static Item prize(Level level) {
		return  Generator.random(Generator.Category.BOMBS);
	}

	private static Item prize2(Level level) {
		return  new DoorBlock();
	}
}
