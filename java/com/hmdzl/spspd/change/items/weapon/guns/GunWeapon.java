package com.hmdzl.spspd.change.items.weapon.guns;


import com.hmdzl.spspd.change.Assets;
import com.hmdzl.spspd.change.Dungeon;
import com.hmdzl.spspd.change.actors.Actor;
import com.hmdzl.spspd.change.actors.Char;
import com.hmdzl.spspd.change.actors.buffs.Buff;
import com.hmdzl.spspd.change.actors.buffs.Vertigo;
import com.hmdzl.spspd.change.actors.hero.Hero;
import com.hmdzl.spspd.change.actors.hero.HeroSubClass;
import com.hmdzl.spspd.change.effects.Splash;
import com.hmdzl.spspd.change.effects.particles.ElmoParticle;
import com.hmdzl.spspd.change.items.Item;
import com.hmdzl.spspd.change.items.StoneOre;
import com.hmdzl.spspd.change.items.artifacts.EyeOfSkadi;
import com.hmdzl.spspd.change.items.wands.WandOfFlow;
import com.hmdzl.spspd.change.items.weapon.Weapon;
import com.hmdzl.spspd.change.items.weapon.missiles.ShatteredAmmo;
import com.hmdzl.spspd.change.items.weapon.missiles.MissileWeapon;
import com.hmdzl.spspd.change.items.weapon.spammo.SpAmmo;
import com.hmdzl.spspd.change.mechanics.Ballistica;
import com.hmdzl.spspd.change.messages.Messages;
import com.hmdzl.spspd.change.plants.Plant;
import com.hmdzl.spspd.change.scenes.CellSelector;
import com.hmdzl.spspd.change.scenes.GameScene;
import com.hmdzl.spspd.change.scenes.PixelScene;
import com.hmdzl.spspd.change.sprites.CharSprite;
import com.hmdzl.spspd.change.sprites.ItemSprite;
import com.hmdzl.spspd.change.sprites.ItemSpriteSheet;
import com.hmdzl.spspd.change.ui.RenderedTextMultiline;
import com.hmdzl.spspd.change.ui.Window;
import com.hmdzl.spspd.change.utils.GLog;
import com.hmdzl.spspd.change.windows.IconTitle;
import com.hmdzl.spspd.change.windows.WndBag;
import com.hmdzl.spspd.change.windows.WndBlacksmith;
import com.hmdzl.spspd.change.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class GunWeapon extends Weapon {
	
	public static final String AC_SHOOT		= "SHOOT";
	public static final String AC_RELOAD		= "RELOAD";
	public static final String AC_AMMO		= "AMMO";

	private int tier;
	private int fullcharge;
   // private Ammo ammo = null;
	
    private SpAmmo spammo;
	
    public int charge = 0;
	public int maxcharge = fullcharge;
	public int maxammo = 100;
	
	

	public GunWeapon(int tier /*int ammo*/ ,int fullcharge) {
		super();

		this.tier = tier;

		//AMMO = ammo
		spammo = null;
		this.fullcharge = fullcharge;

		STR = typicalSTR();

		MIN = min();
		MAX = max();		
	}
	
    private static final String SPAMMO =  "spammo";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		if (spammo != null) bundle.put( SPAMMO, spammo );
	}
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains(SPAMMO)) spammo = (SpAmmo) bundle.get( SPAMMO );
	}

	public GunWeapon(SpAmmo spammo) {
		this.spammo = spammo;
	}


	private int min() {
		return tier + 4;
	}

	private int max() {
		return (int) (tier * tier - tier + 8);
	}

	public int typicalSTR() {
		return 8 + tier * 2;
	}

	public Item upgrade() {
		MIN += 2;
		MAX += 3 + tier;
		maxammo += 10;

		return super.upgrade();
	}
	
	@Override
	public int price() {
		int price = 100 ;
		if (enchantment != null) {
			price *= 1.5;
		}
		if (cursed && cursedKnown) {
			price /= 2;
		}
		if (levelKnown) {
			if (level > 0) {
				price *= (level + 1);
			} else if (level < 0) {
				price /= (1 - level);
			}
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}	
	
	{
		image = ItemSpriteSheet.AMMO;
		
		defaultAction = AC_SHOOT;
		usesTargeting = true;
		
		bones = false;
	}
	
	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		//actions.remove(AC_EQUIP);	
	if (isEquipped(hero)){
		actions.add(AC_SHOOT);
		actions.add(AC_RELOAD);
	}
	actions.add(AC_AMMO);
		return actions;
	}
	
	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_SHOOT)) {
			if (!isEquipped( hero ) && Dungeon.hero.subClass != HeroSubClass.AGENT)
				GLog.i( Messages.get(ToyGun.class, "need_to_equip") );
			else if (charge<1){
				if ( maxammo < 1 ){
					GLog.n(Messages.get(ToyGun.class, "empty"));
				} else {
					if (durable() && maxammo > 0){
						maxammo =  Math.max( maxammo - ( fullcharge - charge),0);
					}
					charge = Math.min(fullcharge,maxammo);
					hero.sprite.showStatus(CharSprite.DEFAULT,  Messages.get(this, "reloading"));
					if (Dungeon.hero.subClass == HeroSubClass.AGENT){ hero.spendAndNext(1/2 * 1f);}
					 else hero.spendAndNext(fullcharge/2 * 1f);
				}
			} else GameScene.selectCell( shooter );
		} else if(action.equals(AC_RELOAD)){
			if (!isEquipped( hero ) && Dungeon.hero.subClass != HeroSubClass.AGENT)
				GLog.i( Messages.get(ToyGun.class, "need_to_equip") );
			else if (charge == fullcharge){
				GLog.n(Messages.get(ToyGun.class,"full"));
			} else if ( maxammo < 1 ){
				GLog.n(Messages.get(ToyGun.class, "empty"));
			} else {
				if (durable() && maxammo > 0) {
					maxammo = Math.max(maxammo - (fullcharge - charge), 0);
				}
				float reloadtime = (Dungeon.hero.subClass == HeroSubClass.AGENT ) ? 1/2 : (fullcharge - charge) / 2;
				hero.spendAndNext(reloadtime * 1f);
				hero.sprite.showStatus(CharSprite.DEFAULT, Messages.get(this, "reloading"));
				charge = Math.min(fullcharge, maxammo);
			}
		}  else if (action.equals(AC_AMMO)) {
			curUser = hero;
			GameScene.selectItem(itemSelector, WndBag.Mode.AMMO ,Messages.get(this, "prompt2"));
		}
	}

	@Override
	public int damageRoll(Hero owner) {
		return 0;
	}

	public int damageRoll2(Hero owner) {
		return Random.Int(MIN, MAX);
	}	
	
   @Override
    public void proc(Char attacker, Char defender, int damage) {

		int oppositeDefender = defender.pos + (defender.pos - attacker.pos);
		Ballistica trajectory = new Ballistica(defender.pos, oppositeDefender, Ballistica.MAGIC_BOLT);
		WandOfFlow.throwChar(defender, trajectory, 1);
		Buff.prolong(defender, Vertigo.class,3f);
    }	
	
	@Override
	public String info() {
		String info = desc();


		info += "\n\n" + Messages.get(ToyGun.class, "stats_known", tier, MIN, MAX, STR);

        String stats_desc = Messages.get(this, "stats_desc");
        if (!stats_desc.equals("")) info+= "\n\n" + stats_desc;
        //Messages.get(MeleeWeapon.class, "stats_known", tier, MIN, MAX,STR,ACU,DLY,RCH )

		if (spammo != null){
			info += "\n" + Messages.get(GunWeapon.class, "ammo_add") + Messages.get(spammo,"name") ;
		}

        if (reinforced) {
            info += "\n" + Messages.get(Item.class, "reinforced");
        }

        info += "\n " + Messages.get(ToyGun.class, "charge", charge, fullcharge);

		return info;
	}
	
	
	@Override
	public String status() {
		if (levelKnown) {
			return charge + "/" + fullcharge;
		} else {
			return null;
		}
	}

	public Item addSpAmmo(SpAmmo spammo, Char owner){

		this.spammo = null;

		//GLog.p( Messages.get(this, "imbue", spammo.name()));
		
		this.spammo= spammo;
		spammo.identify();
		spammo.cursed = false;
		//name = Messages.get(spammo, "spammo_name");

		updateQuickslot();

		return this;
	}
	
	private final WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect( final Item item ) {
			if (item != null) {

				GameScene.show(
						new WndOptions("",
								Messages.get(GunWeapon.class, "warning"),
								Messages.get(GunWeapon.class, "yes"),
								Messages.get(GunWeapon.class, "no")) {
							@Override
							protected void onSelect(int index) {
								if (index == 0) {
									Sample.INSTANCE.play(Assets.SND_EVOKE);
									item.detach(curUser.belongings.backpack);

									addSpAmmo((SpAmmo) item, curUser);

									curUser.spendAndNext(2f);

									updateQuickslot();
								}
							}
						}
				);
			}
		}
	};	
	
	private int targetPos;


		
	@Override
	public boolean isUpgradable() {
		return true;
	}
	
	public NormalAmmo Ammo(){
		return new NormalAmmo();
	}
	
	public class NormalAmmo extends MissileWeapon {
		
		{
			image = ItemSpriteSheet.AMMO;
			STR = Math.max(typicalSTR(),Dungeon.hero.STR);
		}

		public int damageRoll(Hero owner) {
			return GunWeapon.this.damageRoll2(owner);
		}

		@Override
		protected void onThrow( int cell ) {
			Char enemy = Actor.findChar( cell );
			if (enemy == null || enemy == curUser) {
				parent = null;
				Splash.at( cell, 0xCC99FFFF, 1 );
			} else {
				if (!curUser.shoot( enemy, this )) {
					Splash.at(cell, 0xCC99FFFF, 1);
				}
			}
		}

		@Override
		public void proc(Char attacker, Char defender, int damage) {
			if (spammo != null) {
				spammo.onHit(GunWeapon.this, attacker, defender, damage);
			}
			super.proc(attacker, defender, damage);
		}
		
		int flurryCount = -1;
		
		@Override
		public void cast(final Hero user, final int dst) {
			final int cell = throwPos( user, dst );
			GunWeapon.this.targetPos = cell;
			charge--;
				super.cast(user, dst);
		}

	}
	
	private CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer target ) {
			if (target != null) {
				Ammo().cast(curUser, target);
			}
		}
		@Override
		public String prompt() {
			return Messages.get(GunWeapon.class, "prompt");
		}
	};
}