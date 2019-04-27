package com.hmdzl.spspd.change.levels;

import com.hmdzl.spspd.change.Assets;
import com.hmdzl.spspd.change.Dungeon;
import com.hmdzl.spspd.change.actors.Char;
import com.hmdzl.spspd.change.actors.blobs.Alchemy;
import com.hmdzl.spspd.change.actors.blobs.Alter;
import com.hmdzl.spspd.change.actors.blobs.WellWater;
import com.hmdzl.spspd.change.actors.mobs.Mob;
import com.hmdzl.spspd.change.actors.mobs.TestMob;
import com.hmdzl.spspd.change.actors.mobs.npcs.Coconut;
import com.hmdzl.spspd.change.actors.mobs.npcs.Coconut2;
import com.hmdzl.spspd.change.actors.mobs.npcs.Evan;
import com.hmdzl.spspd.change.actors.mobs.npcs.FruitCat;
import com.hmdzl.spspd.change.actors.mobs.npcs.G2159687;
import com.hmdzl.spspd.change.actors.mobs.npcs.HBB;
import com.hmdzl.spspd.change.actors.mobs.npcs.HateSokoban;
import com.hmdzl.spspd.change.actors.mobs.npcs.Hmdzl001;
import com.hmdzl.spspd.change.actors.mobs.npcs.Jinkeloid;
import com.hmdzl.spspd.change.actors.mobs.npcs.Kostis12345;
import com.hmdzl.spspd.change.actors.mobs.npcs.LaJi;
import com.hmdzl.spspd.change.actors.mobs.npcs.Lery;
import com.hmdzl.spspd.change.actors.mobs.npcs.Lyn;
import com.hmdzl.spspd.change.actors.mobs.npcs.Lynn;
import com.hmdzl.spspd.change.actors.mobs.npcs.MemoryOfSand;
import com.hmdzl.spspd.change.actors.mobs.npcs.NewPlayer;
import com.hmdzl.spspd.change.actors.mobs.npcs.RENnpc;
import com.hmdzl.spspd.change.actors.mobs.npcs.Rustyblade;
import com.hmdzl.spspd.change.actors.mobs.npcs.SFB;
import com.hmdzl.spspd.change.actors.mobs.npcs.StormAndRain;
import com.hmdzl.spspd.change.actors.mobs.npcs.Tempest102;
import com.hmdzl.spspd.change.actors.mobs.npcs.ThankList;
import com.hmdzl.spspd.change.actors.mobs.npcs.Tinkerer5;
import com.hmdzl.spspd.change.items.Generator;
import com.hmdzl.spspd.change.items.Heap;
import com.hmdzl.spspd.change.items.Item;
import com.hmdzl.spspd.change.items.artifacts.TimekeepersHourglass;
import com.hmdzl.spspd.change.items.food.completefood.PetFood;
import com.hmdzl.spspd.change.items.scrolls.ScrollOfUpgrade;
import com.hmdzl.spspd.change.items.weapon.missiles.PocketBall;
import com.hmdzl.spspd.change.levels.Level;
import com.hmdzl.spspd.change.levels.features.Chasm;
import com.hmdzl.spspd.change.levels.features.Door;
import com.hmdzl.spspd.change.levels.features.HighGrass;
import com.hmdzl.spspd.change.messages.Messages;
import com.hmdzl.spspd.change.plants.Plant;
import com.hmdzl.spspd.change.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class SokobanSPLevel extends Level {


	{
		color1 = 0x534f3e;
		color2 = 0xb9d661;
		WIDTH = 48;
		HEIGHT = 48;
		LENGTH = HEIGHT*WIDTH;
		cleared=false;
	}
	
	public int mineDepth=0;
	
    public int[] storespots;
    public int[] bombpots;
	
	
	
	private static final String MINEDEPTH = "mineDepth";
	private static final String STORESPOTS = "storespots";
	private static final String BOMBPOTS = "bombpots";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(MINEDEPTH, mineDepth);
		bundle.put(STORESPOTS, storespots);
		bundle.put(BOMBPOTS, bombpots);
	}
		
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		mineDepth = bundle.getInt(MINEDEPTH);
		storespots = bundle.getIntArray(STORESPOTS);
		bombpots = bundle.getIntArray(BOMBPOTS);
	}
	
	public void storeStock (){


			for (int i : bombpots) {
				Heap heap = heaps.get(i);
				if (heap == null){
					Item storeitem2 = storeItem2();
					drop(storeitem2, i).type = Heap.Type.FOR_SALE;
				}					
			}
			for (int i : storespots) {
				Heap heap = heaps.get(i);
				if (heap == null){
					Item storeitem = storeItem();
					drop(storeitem, i).type = Heap.Type.FOR_SALE;
				}					
			}
	}	
	

	public Item storeItem (){
		Item prize;
		switch (Random.Int(2)) {
		case 0:
			prize = Generator.random(Generator.Category.ARTIFACT);;
			break;
		case 1:
			prize = Generator.random(Generator.Category.RING);
			break;
		default:
			prize = new PetFood();
			break;
		}

		return prize;
 
	}
	
	public Item storeItem2 (){
		Item prize;
		switch (Random.Int(6)) {
		case 0:
			prize = Generator.random(Generator.Category.WEAPON);
			break;
		case 1:
			prize = Generator.random(Generator.Category.ARMOR);
			break;
		case 2:
			prize = Generator.random(Generator.Category.POTION);
			break;
		case 3:
			prize = Generator.random(Generator.Category.WAND);
			break;
		case 4:
			prize = Generator.random(Generator.Category.SCROLL);
			break;	
		case 5:
			prize = Generator.random(Generator.Category.HIGHFOOD);
			break;			
		default:
			prize = new ScrollOfUpgrade();
			break;
		}

		return prize;
 
	}	
	
  @Override
  public void create() {
	   super.create();	
   }	
  
   @Override
	protected void createItems() {

      Mob g2159687 = new G2159687();
	  g2159687.pos = 36 + WIDTH * 6;
	  mobs.add(g2159687);

      Mob evan = new Evan();
	  evan.pos = 40 + WIDTH * 42;
	  mobs.add(evan);  	  
	  
      Mob hbb = new HBB();
	  hbb.pos = 38 + WIDTH * 13;
	  mobs.add(hbb);

      Mob sfb = new SFB();
	  sfb.pos = 34 + WIDTH * 11;
	  mobs.add(sfb);

      Mob jinkeloid = new Jinkeloid();
	  jinkeloid.pos = 38 + WIDTH * 41;
	  mobs.add(jinkeloid);

      Mob rustyblade = new Rustyblade();
	  rustyblade.pos = 36 + WIDTH * 15;
	  mobs.add(rustyblade);
     
	  Mob lyn = new Lyn();
	  lyn.pos = 37 + WIDTH * 9;
	  mobs.add(lyn);
	  
	  Mob lery = new Lery();
	  lery.pos = 33 + WIDTH * 9;
	  mobs.add(lery);

	  Mob fruitcat = new FruitCat();
	  fruitcat.pos = 37 + WIDTH * 4;
	  mobs.add(fruitcat);
	  
	  Mob coconut2 = new Coconut2();
	  coconut2.pos = 20 + WIDTH * 35;
	  mobs.add(coconut2);

	   Mob hmdzl = new Hmdzl001();
	   hmdzl.pos = 20 + WIDTH * 42;
	   mobs.add(hmdzl);
	  
	  Mob tempest102 = new Tempest102();
	  tempest102.pos = 33 + WIDTH * 42;
	  mobs.add(tempest102);
	  
      Mob MOS = new MemoryOfSand();
	  MOS.pos = 26 + WIDTH * 5;
	  mobs.add(MOS);
	  
      Mob HS = new HateSokoban();
	  HS.pos = 28 + WIDTH * 8;
	  mobs.add(HS);
	  
	  Mob LJ = new LaJi();
	  LJ.pos = 23 + WIDTH * 10;
	  mobs.add(LJ);

	   Mob NP = new NewPlayer();
	   NP.pos = 23 + WIDTH * 11;
	   mobs.add(NP);

	   Mob TL = new ThankList();
	   TL.pos = 39 + WIDTH * 37;
	   mobs.add(TL);

	   Mob SAR = new StormAndRain();
	  SAR.pos = 24 + WIDTH * 6;
	  mobs.add(SAR);	
	  
	  Mob LYNN = new Lynn();
	  LYNN.pos = 40 + WIDTH * 28;
	  mobs.add(LYNN);  

	  Mob REN = new RENnpc();
	  REN.pos = 40 + WIDTH * 26;
	  mobs.add(REN);

	  Mob KOSTIS = new Kostis12345();
	  KOSTIS.pos = 7 + WIDTH * 9;;
	  mobs.add(KOSTIS);

	  TestMob test4 = new TestMob();
	  test4.pos = 15 + WIDTH * 3;
	  mobs.add(test4);

	   Mob tinkerer5 =  new Tinkerer5();
	   tinkerer5.pos = 10 + WIDTH * 34;
	   mobs.add(tinkerer5);
      
      storespots = new int[1];
      storespots[0] =  7 + WIDTH * 10;
	  
	  bombpots = new int[1];
      bombpots[0] =  21 + WIDTH * 35;

	}	 
  
	@Override
	public void press(int cell, Char ch) {
		
		if(!special){
			storeStock();
			special=true;
		}
				
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
					
        
		case Terrain.HIGH_GRASS:
			HighGrass.trample(this, cell, ch);
			break;

		case Terrain.WELL:
			WellWater.affectCell(cell);
			break;

		//case Terrain.ALCHEMY:
		       //  Alchemy alchemy = new Alchemy();
                //level.blobs.put( Alchemy.class, alchemy );
		//	break;
			
		case Terrain.PEDESTAL:
			if (ch == null ) {
				Alter.transmute(cell);
			}
			break;

		/*case Terrain.BED:
			if(ch == null){
				BedLight.transmute(cell);
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
		return Assets.TILES_SP;
	}

	@Override
	public String waterTex() {
		return Assets.WATER_SEWERS;
	}

	@Override
	protected boolean build() {
		
		map = SokobanLayouts2.SOKOBAN_VAULT_LEVEL.clone();
		decorate();

		buildFlagMaps();
		cleanWalls();
	
		entrance = 8 + WIDTH * 5;
		exit = 0;


		return true;
	}
	@Override
	protected void decorate() {
	}

	@Override
	protected void createMobs() {

	}

	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.WATER:
				return Messages.get(PrisonLevel.class, "water_name");
			default:
				return super.tileName( tile );
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
				return super.tileDesc( tile );
		}
	}
	

	@Override
	public int randomRespawnCell() {
		return -1;
	}
	
	

}
