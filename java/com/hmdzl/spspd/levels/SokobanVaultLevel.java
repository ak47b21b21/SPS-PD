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
import com.hmdzl.spspd.actors.Actor;
import com.hmdzl.spspd.actors.Char;
import com.hmdzl.spspd.actors.blobs.WellWater;
import com.hmdzl.spspd.actors.mobs.npcs.Sheep;
import com.hmdzl.spspd.actors.mobs.npcs.SheepSokoban;
import com.hmdzl.spspd.actors.mobs.npcs.SheepSokobanBlack;
import com.hmdzl.spspd.actors.mobs.npcs.SheepSokobanCorner;
import com.hmdzl.spspd.actors.mobs.npcs.SheepSokobanSwitch;
import com.hmdzl.spspd.items.Dewdrop;
import com.hmdzl.spspd.items.Gold;
import com.hmdzl.spspd.items.Heap;
import com.hmdzl.spspd.items.Item;
import com.hmdzl.spspd.items.artifacts.TimekeepersHourglass;
import com.hmdzl.spspd.items.misc.LuckyBadge;
import com.hmdzl.spspd.items.wands.WandOfFlock;
import com.hmdzl.spspd.levels.features.Chasm;
import com.hmdzl.spspd.levels.features.Door;
import com.hmdzl.spspd.levels.features.HighGrass;
import com.hmdzl.spspd.levels.traps.ActivatePortalTrap;
import com.hmdzl.spspd.levels.traps.ChangeSheepTrap;
import com.hmdzl.spspd.levels.traps.FleecingTrap;
import com.hmdzl.spspd.levels.traps.SokobanPortalTrap;
import com.hmdzl.spspd.messages.Messages;
import com.hmdzl.spspd.plants.Plant;
import com.hmdzl.spspd.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.Collection;
import java.util.HashSet;

public class SokobanVaultLevel extends Level {


	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
		WIDTH = 48;
		HEIGHT = 48;
		LENGTH = HEIGHT*WIDTH;
		cleared=true;
	}
	
	
	public HashSet<Item> heapstogen;
	public int[] heapgenspots;
	public int[] teleportspots;
	public int[] portswitchspots;
	public int[] teleportassign;
	public int[] destinationspots;
	public int[] destinationassign;
	public int prizeNo;
	
	private static final String HEAPSTOGEN = "heapstogen";
	private static final String HEAPGENSPOTS = "heapgenspots";
	private static final String TELEPORTSPOTS = "teleportspots";
	private static final String PORTSWITCHSPOTS = "portswitchspots";
	private static final String DESTINATIONSPOTS = "destinationspots";
	private static final String TELEPORTASSIGN = "teleportassign";
	private static final String DESTINATIONASSIGN= "destinationassign";
	private static final String PRIZENO = "prizeNo";
	
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(HEAPSTOGEN, heapstogen);
		bundle.put(HEAPGENSPOTS, heapgenspots);
		bundle.put(TELEPORTSPOTS, teleportspots);
		bundle.put(PORTSWITCHSPOTS, portswitchspots);
		bundle.put(DESTINATIONSPOTS, destinationspots);
		bundle.put(DESTINATIONASSIGN, destinationassign);
		bundle.put(TELEPORTASSIGN, teleportassign);
		bundle.put(PRIZENO, prizeNo);
	}
	
	
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		
		      super.restoreFromBundle(bundle);
		      
		      heapgenspots = bundle.getIntArray(HEAPGENSPOTS);
		      teleportspots = bundle.getIntArray(TELEPORTSPOTS);
		      portswitchspots = bundle.getIntArray(PORTSWITCHSPOTS);
		      destinationspots = bundle.getIntArray(DESTINATIONSPOTS);
		      destinationassign = bundle.getIntArray(DESTINATIONASSIGN);
		      teleportassign = bundle.getIntArray(TELEPORTASSIGN);
		      prizeNo = bundle.getInt(PRIZENO);
		      
		      heapstogen = new HashSet<Item>();
		      
		      Collection <Bundlable> collectionheap = bundle.getCollection(HEAPSTOGEN);
				for (Bundlable i : collectionheap) {
					Item item = (Item) i;
					if (item != null) {
						heapstogen.add(item);
					}
				}
	}

  @Override
  public void create() {
	   heapstogen = new HashSet<Item>();
	   heapgenspots = new int[10];
	   teleportspots = new int[10];
	   portswitchspots = new int[10];
	   destinationspots = new int[10];
	   destinationassign = new int[10];
	   teleportassign = new int[10];
	   super.create();	
   }	
  
  public void addItemToGen(Item item, int arraypos, int pos) {
		if (item != null) {
			heapstogen.add(item);
			heapgenspots[arraypos]=pos;
		}
	}
  
  
	public Item genPrizeItem() {
		return genPrizeItem(null);
	}
	
	
	public Item genPrizeItem(Class<? extends Item> match) {
		
		boolean keysLeft = false;
		
		if (heapstogen.size() == 0)
			return null;

		for (Item item : heapstogen) {
			if (match.isInstance(item)) {
				heapstogen.remove(item);
				keysLeft=true;
				return item;
			}
		}
		
		if (match == null || !keysLeft) {
			Item item = Random.element(heapstogen);
			heapstogen.remove(item);
			return item;
		}

		return null;
	}
	
	@Override
	public void press(int cell, Char ch) {

		if (pit[cell] && ch == Dungeon.hero) {
			Chasm.heroFall(cell);
			return;
		}

		TimekeepersHourglass.timeFreeze timeFreeze = null;

		if (ch != null)
			timeFreeze = ch.buff(TimekeepersHourglass.timeFreeze.class);

		boolean trap = false;
		boolean interrupt = false;
		
		switch (map[cell]) {

			case Terrain.FLEECING_TRAP:			
			
			if (ch==null){	
			  interrupt=true;	
			}
			
			if (ch != null && ch==Dungeon.hero){
				trap = true;
				FleecingTrap.trigger(cell, ch);
			}
			break;
			
		case Terrain.CHANGE_SHEEP_TRAP:
			
			if (ch instanceof SheepSokoban || ch instanceof SheepSokobanSwitch || ch instanceof SheepSokobanCorner || ch instanceof Sheep || ch instanceof WandOfFlock.MagicSheep){
				trap = true;
				ChangeSheepTrap.trigger(cell, ch);
			}						
			break;
			
		case Terrain.WOOL_RUG:
			
		break;
		
		case Terrain.SOKOBAN_PORT_SWITCH:
			trap=false;
			if (ch!=null){
			   ActivatePortalTrap.trigger(cell, ch);
				
					
				int arraypos = -1; //position in array of teleport switch
				int portpos = -1; //position on map of teleporter
				int portarraypos = -1; //position in array of teleporter
				int destpos = -1; //destination position assigned to switch
				
				for(int i = 0; i < portswitchspots.length; i++) {
				  if(portswitchspots[i] == cell) {
				     arraypos = i;
				     break;
				  }
				}
				
				portpos = teleportassign[arraypos];
				destpos = destinationassign[arraypos];
				
				/* Stepping on switch deactivates the portal */
				destpos = -1;
				
				for(int i = 0; i < teleportspots.length; i++) {
					  if(teleportspots[i] == portpos) {
						     portarraypos = i;
						     break;
						  }
				}
				
				if (map[portpos] == Terrain.PORT_WELL){
					destinationspots[portarraypos]=destpos;	
				}
				
				
			}				
			break;

			
        case Terrain.PORT_WELL:
			
			if (ch != null && ch==Dungeon.hero){

				int portarray=-1;
				int destinationspot=cell;
				
				for(int i = 0; i < teleportspots.length; i++) {
					  if(teleportspots[i] == cell) {
						     portarray = i;
						     break;
						  }
				}
				
				if(portarray != -1) {
					destinationspot=destinationspots[portarray];
					if (destinationspot>0){
					SokobanPortalTrap.trigger(cell, ch, destinationspot);
					}
				}				
			}						
			break;

		case Terrain.HIGH_GRASS:
			HighGrass.trample(this, cell, ch);
			break;

		case Terrain.WELL:
			WellWater.affectCell(cell);
			break;

		/*case Terrain.ALCHEMY:
			if (ch == null) {
				Alchemy.transmute(cell);
			}
			break;*/

		case Terrain.DOOR:
			Door.enter(cell);
			break;
		}

		if (trap){

			if (Dungeon.visible[cell])
				Sample.INSTANCE.play(Assets.SND_TRAP);

			if (ch == Dungeon.hero)
				Dungeon.hero.interrupt();

			set(cell, Terrain.INACTIVE_TRAP);
			GameScene.updateMap(cell);					
		} 
		
		if (interrupt){

			Dungeon.hero.interrupt();
			GameScene.updateMap(cell);					
		} 

		Plant plant = plants.get(cell);
		if (plant != null) {
			plant.activate(ch);
		}
	}

	@Override
	public String tilesTex() {
		return Assets.TILES_PUZZLE;
	}

	@Override
	public String waterTex() {
		return Assets.WATER_PRISON;
	}

	@Override
	protected boolean build() {
		
		map = SokobanLayouts2.SOKOBAN_VAULT_LEVEL.clone();
		decorate();

		buildFlagMaps();
		cleanWalls();
		createSwitches();
		createSheep();
	
		entrance = 8 + WIDTH * 5;
		exit = 0 + WIDTH * 47;


		return true;
	}
	@Override
	protected void decorate() {
		for (int i = 0; i < getLength(); i++) {
			
			if (map[i]==Terrain.EMPTY && heaps.get(i) == null && Random.Float()<.20){map[i] = Terrain.HIGH_GRASS;}
			if (map[i]==Terrain.EMPTY && heaps.get(i) == null && Random.Float()<.25){map[i] = Terrain.GRASS;}
			if (map[i]==Terrain.EMPTY && heaps.get(i) == null && Random.Float()<.30){map[i] = Terrain.SHRUB;}
		}
	}

	@Override
	protected void createMobs() {
		/*
		    SokobanSentinel mob = new SokobanSentinel();
			mob.pos = 38 + WIDTH * 21;
			mobs.add(mob);
			Actor.occupyCell(mob);
			
			SokobanSentinel mob2 = new SokobanSentinel();
			mob2.pos = 25 + WIDTH * 36;
			mobs.add(mob2);
			Actor.occupyCell(mob2);		
			*/	
	}
	
	

	protected void createSheep() {
		 for (int i = 0; i < LENGTH; i++) {				
				if (map[i]==Terrain.SOKOBAN_SHEEP){SheepSokoban npc = new SheepSokoban(); mobs.add(npc); npc.pos = i; Actor.occupyCell(npc);}
				else if (map[i]==Terrain.CORNER_SOKOBAN_SHEEP){SheepSokobanCorner npc = new SheepSokobanCorner(); mobs.add(npc); npc.pos = i; Actor.occupyCell(npc);}
				else if (map[i]==Terrain.SWITCH_SOKOBAN_SHEEP){SheepSokobanSwitch npc = new SheepSokobanSwitch(); mobs.add(npc); npc.pos = i; Actor.occupyCell(npc);}
				else if (map[i]==Terrain.BLACK_SOKOBAN_SHEEP){SheepSokobanBlack npc = new SheepSokobanBlack(); mobs.add(npc); npc.pos = i; Actor.occupyCell(npc);}
				else if (map[i]==Terrain.PORT_WELL){
				
					/*
					Portal portal = new Portal();
				portal.seed(i, 1);
				blobs.put(Portal.class, portal);
				*/
				}
				
			}
	}
	
	
	protected void createSwitches(){
		
		//spots where your portals are	
		
		
		//spots where your portal switches are	
		
			
		//assign each switch to a portal	
		
			
		//assign each switch to a destination spot	
		
			
		//set the original destination of portals	
				
	}
	

	@Override
	protected void createItems() {
		int goldmin=1; int goldmax=10;
		boolean ringDrop = false;
		if (first){
			goldmin=30; goldmax=50;
		}
		 for (int i = 0; i < LENGTH; i++) {				
				if (map[i]==Terrain.EMPTY && heaps.get(i) == null && Random.Int(100)>70){
					if (first && !ringDrop){drop(new LuckyBadge(), i).type = Heap.Type.CHEST; ringDrop=true;}
				    else if (Random.Int(5)==0){drop(new Gold(Random.Int(goldmin, goldmax)), i).type = Heap.Type.CHEST;}
					else {drop(new Dewdrop(), i).type = Heap.Type.E_DUST;}
				}
			}	 
		
		 
	}

	@Override
	public int randomRespawnCell() {
		return -1;
	}

	@Override
	public String tileName(int tile) {
		switch (tile) {
	case Terrain.SOKOBAN_SHEEP:
	case Terrain.SWITCH_SOKOBAN_SHEEP:
	case Terrain.CORNER_SOKOBAN_SHEEP:
	case Terrain.BLACK_SOKOBAN_SHEEP:
		return Messages.get(Level.class, "floor_name");
		case Terrain.WATER:
				return Messages.get(Level.class, "water_name");
			case Terrain.WOOL_RUG:
				return Messages.get(Level.class, "wool_rug_name");
			case Terrain.FLEECING_TRAP:
				return Messages.get(Level.class, "fleecing_trap_name");
			case Terrain.CHANGE_SHEEP_TRAP:
				return Messages.get(Level.class, "change_sheep_trap_name");
			case Terrain.SOKOBAN_ITEM_REVEAL:
				return Messages.get(Level.class, "sokoban_item_reveal_name");
			case Terrain.SOKOBAN_PORT_SWITCH:
				return Messages.get(Level.class, "sokoban_port_switch_name");
			case Terrain.PORT_WELL:
				return Messages.get(Level.class, "port_well_name");
		   default:
		   	return super.tileName(tile);
		}
	}


	@Override
	public String tileDesc(int tile) {
		switch (tile) {
		case Terrain.SOKOBAN_SHEEP:
		case Terrain.SWITCH_SOKOBAN_SHEEP:
		case Terrain.CORNER_SOKOBAN_SHEEP:
		case Terrain.BLACK_SOKOBAN_SHEEP:
			return Messages.get(Level.class, "default_desc");
			case Terrain.EMPTY_DECO:
				return Messages.get(PrisonLevel.class, "empty_deco_desc");
			case Terrain.BOOKSHELF:
				return Messages.get(PrisonLevel.class, "bookshelf_desc");
			case Terrain.WOOL_RUG:
				return Messages.get(Level.class, "wool_rug_desc");
			case Terrain.FLEECING_TRAP:
				return Messages.get(Level.class, "fleecing_trap_desc");
			case Terrain.CHANGE_SHEEP_TRAP:
				return Messages.get(Level.class, "change_sheep_trap_desc");
			case Terrain.SOKOBAN_ITEM_REVEAL:
				return Messages.get(Level.class, "sokoban_item_reveal_desc");
			case Terrain.SOKOBAN_PORT_SWITCH:
				return Messages.get(Level.class, "sokoban_port_switch_desc");
			case Terrain.PORT_WELL:
				return Messages.get(Level.class, "port_well_desc");
			default:
				return super.tileDesc(tile);
		}
	}

}
