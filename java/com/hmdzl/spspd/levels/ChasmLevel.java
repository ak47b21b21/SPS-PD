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
package com.hmdzl.spspd.levels;

import com.hmdzl.spspd.Assets;
import com.hmdzl.spspd.Dungeon;
import com.hmdzl.spspd.DungeonTilemap;
import com.hmdzl.spspd.actors.mobs.Sentinel;
import com.hmdzl.spspd.items.TriforceOfWisdom;
import com.hmdzl.spspd.items.potions.PotionOfLevitation;
import com.hmdzl.spspd.levels.Room.Type;
import com.hmdzl.spspd.messages.Messages;
import com.hmdzl.spspd.scenes.GameScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.Scene;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class ChasmLevel extends RegularLevel {

	{
		color1 = 0x48763c;
		color2 = 0x59994a;
		cleared=true;
	}

	
	@Override
	public String tilesTex() {
		return Assets.TILES_PRISON;
	}

	@Override
	public String waterTex() {
		return Assets.WATER_PRISON;
	}

	@Override
	protected boolean[] water() {
		return Patch.generate(feeling == Feeling.WATER ? 0.60f : 0.45f, 5);
	}

	@Override
	protected boolean[] grass() {
		return Patch.generate(feeling == Feeling.GRASS ? 0.60f : 0.40f, 4);
	}
	
	@Override
	protected void createItems() {
		super.createItems();

		spawn(this, roomEntrance);
		
	}

	public static void spawn(ChasmLevel level, Room room) {
		int pos;
		do {pos = room.random();}
		while (level.heaps.get(pos) != null);
		level.drop(new PotionOfLevitation(), pos);
	}
	
	@Override
	protected boolean assignRoomType() {
		
		specialsc = new ArrayList<Room.Type>(Arrays.asList(Type.MAGIC_KEEPER));
		 
		  //if (Dungeon.isChallenged(Challenges.NO_HERBALISM)) {
			// sorry warden, no lucky sungrass or blandfruit seeds for you!
			//specialsc.remove(Room.Type.GARDEN);
		 // }
		  
		  int specialRooms = 0;

			for (Room r : rooms) {
				if (r.type == Type.NULL && r.connected.size() == 1) {

					if (specialsc.size() > 0 && r.width() > 3 && r.height() > 3
							//&& Random.Int(specialRooms * specialRooms + 2) == 0
							) {

						
						int n = specialsc.size();
						r.type = specialsc.get(Math.min(Random.Int(n),Random.Int(n)));
						
						Room.useType(r.type);
						//specialsc.remove(r.type);
						specialRooms++;

					} else if (Random.Int(2) == 0) {

						HashSet<Room> neigbours = new HashSet<Room>();
						for (Room n : r.neigbours) {
							if (!r.connected.containsKey(n)
									&& !Room.SPECIALS.contains(n.type)
									&& n.type != Type.PIT) {

								neigbours.add(n);
							}
						}
						if (neigbours.size() > 1) {
							r.connect(Random.element(neigbours));
						}
					}
				}
			}

		
		int count = 0;
		for (Room r : rooms) {
			if (r.type == Type.NULL) {
				int connections = r.connected.size();
				if (connections == 0) {

				} else if (Random.Int(connections * connections) == 0) {
					r.type = Type.STANDARD;
					count++;
				} else {
					r.type = Type.TUNNEL;
				}
			}
		}

		while (count < 4) {
			Room r = randomRoom(Type.TUNNEL, 1);
			if (r != null) {
				r.type = Type.STANDARD;
				count++;
			}
		}

		for (Room r : rooms) {
			if (r.type == Type.TUNNEL) {
				r.type = Type.PASSAGE;
			}
		}

		return true;
	}


	
	@Override
	protected void decorate() {

		for (int i = 0; i < getWidth(); i++) {
			if (map[i] == Terrain.WALL && map[i + getWidth()] == Terrain.WATER
					&& Random.Int(4) == 0) {

				map[i] = Terrain.WALL_DECO;
			}
		}

		for (int i = getWidth(); i < getLength() - getWidth(); i++) {
			if (map[i] == Terrain.WALL && map[i - getWidth()] == Terrain.WALL
					&& map[i + getWidth()] == Terrain.WATER && Random.Int(2) == 0) {

				map[i] = Terrain.WALL_DECO;
			}
		}

		for (int i = getWidth() + 1; i < getLength() - getWidth() - 1; i++) {
			if (map[i] == Terrain.EMPTY) {

				int count = (map[i + 1] == Terrain.WALL ? 1 : 0)
						+ (map[i - 1] == Terrain.WALL ? 1 : 0)
						+ (map[i + getWidth()] == Terrain.WALL ? 1 : 0)
						+ (map[i - getWidth()] == Terrain.WALL ? 1 : 0);

				if (Random.Int(16) < count * count) {
					map[i] = Terrain.EMPTY_DECO;
				}
			}
		}
		
			
		int length = Level.getLength();
		
		for (int i = 0; i < length; i++) {
			
					
			if (map[i]==Terrain.ENTRANCE){map[i] = Terrain.EMPTY;}
			if (map[i]==Terrain.EXIT){map[i] = Terrain.PEDESTAL;  if (!Dungeon.triforceofwisdom){drop(new TriforceOfWisdom(), i);}}
			if (map[i]==Terrain.EMPTY_SP && heaps.get(i) == null && Random.Float()<.25){map[i] = Terrain.TRAP_AIR;}
			if (map[i]==Terrain.EMPTY_SP && heaps.get(i) == null && Random.Float()<.05){
				Sentinel sentinel = new Sentinel();
				sentinel.pos = i;
				mobs.add(sentinel);	
			}
			//if (map[i]==Terrain.EMPTY && heaps.get(i) == null && Random.Float()<.02){
				//drop(new Phaseshift.Seed(), i);
			//}
			
			
		}
	}

	
	@Override
	public void addVisuals(Scene scene) {
		super.addVisuals(scene);
		addVisuals(this, scene);
	}

	public static void addVisuals(Level level, Scene scene) {
		for (int i = 0; i < getLength(); i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				scene.add(new Sink(i));
			}
		}
	}
	
	//@Override
	//public int randomRespawnCell() {
	//	return -1;
	//}

	@Override
	public String tileName(int tile) {
		switch (tile) {
		case Terrain.WATER:
			return Messages.get(PrisonLevel.class, "water_name");
		default:
			return super.tileName(tile);
		}
	}

	@Override
	public String tileDesc(int tile) {
		switch (tile) {
		case Terrain.EMPTY_DECO:
			return Messages.get(PrisonLevel.class, "empty_deco_desc");
		case Terrain.BOOKSHELF:
			return Messages.get(PrisonLevel.class, "bookshelf_desc");
		default:
			return super.tileDesc(tile);
		}
	}

	private static class Sink extends Emitter {

		private int pos;
		private float rippleDelay = 0;

		private static final Emitter.Factory factory = new Factory() {

			@Override
			public void emit(Emitter emitter, int index, float x, float y) {
				WaterParticle p = (WaterParticle) emitter
						.recycle(WaterParticle.class);
				p.reset(x, y);
			}
		};

		public Sink(int pos) {
			super();

			this.pos = pos;

			PointF p = DungeonTilemap.tileCenterToWorld(pos);
			pos(p.x - 2, p.y + 1, 4, 0);

			pour(factory, 0.05f);
		}

		@Override
		public void update() {
			if (visible = Dungeon.visible[pos]) {

				super.update();

				if ((rippleDelay -= Game.elapsed) <= 0) {
					GameScene.ripple(pos + getWidth()).y -= DungeonTilemap.SIZE / 2;
					rippleDelay = Random.Float(0.2f, 0.3f);
				}
			}
		}
	}

	public static final class WaterParticle extends PixelParticle {

		public WaterParticle() {
			super();

			acc.y = 50;
			am = 0.5f;

			color(ColorMath.random(0xb6ccc2, 0x3b6653));
			size(2);
		}

		public void reset(float x, float y) {
			revive();

			this.x = x;
			this.y = y;

			speed.set(Random.Float(-2, +2), 0);

			left = lifespan = 0.5f;
		}
	}

}
