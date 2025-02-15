/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2015 Evan Debenham
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
package com.hmdzl.spspd.items.wands;

import com.hmdzl.spspd.Assets;
import com.hmdzl.spspd.Challenges;
import com.hmdzl.spspd.Dungeon;
import com.hmdzl.spspd.actors.Actor;
import com.hmdzl.spspd.actors.Char;
import com.hmdzl.spspd.actors.buffs.Buff;
import com.hmdzl.spspd.actors.buffs.Invisibility;
import com.hmdzl.spspd.actors.buffs.Recharging;
import com.hmdzl.spspd.actors.buffs.Silent;
import com.hmdzl.spspd.actors.buffs.SoulMark;
import com.hmdzl.spspd.actors.hero.Hero;
import com.hmdzl.spspd.actors.hero.HeroClass;
import com.hmdzl.spspd.actors.hero.HeroSubClass;
import com.hmdzl.spspd.effects.MagicMissile;
import com.hmdzl.spspd.items.Item;
import com.hmdzl.spspd.items.bags.Bag;
import com.hmdzl.spspd.items.misc.GnollMark;
import com.hmdzl.spspd.items.rings.Ring;
import com.hmdzl.spspd.items.rings.RingOfEnergy;
import com.hmdzl.spspd.mechanics.Ballistica;
import com.hmdzl.spspd.messages.Messages;import com.hmdzl.spspd.ResultDescriptions;
import com.hmdzl.spspd.scenes.CellSelector;
import com.hmdzl.spspd.scenes.GameScene;
import com.hmdzl.spspd.ui.QuickSlotButton;
import com.hmdzl.spspd.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class Wand extends Item {

	private static final int USAGES_TO_KNOW    = 20;

	public static final String AC_ZAP	= "ZAP";

	private static final float TIME_TO_ZAP	= 1f;
	
	public int maxCharges = initialCharges();
	public int curCharges = maxCharges;
	public float partialCharge = 0f;
	
	protected Charger charger;
	
	private boolean curChargeKnown = false;

	protected boolean hitChars = true;

	protected int usagesToKnow = USAGES_TO_KNOW;
	
	protected int collisionProperties = Ballistica.MAGIC_BOLT;	
	
	{
		defaultAction = AC_ZAP;
		usesTargeting = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (curCharges > 0 || !curChargeKnown) {
			actions.add( AC_ZAP );
		}

		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {
		if (action.equals( AC_ZAP )) {
			if (hero.buff(Silent.class) != null) {
				GLog.w(Messages.get(Wand.class, "silent"));
			} else 	if (Dungeon.hero.heroClass == HeroClass.MAGE && Dungeon.skins == 4 && Dungeon.hero.spp < 1) {
				GLog.w(Messages.get(Wand.class, "needmana"));
			} else{
			curUser = hero;
			curItem = this;
			GameScene.selectCell( zapper );
			}
			
		} else {
			
			super.execute( hero, action );
			
		}
	}

	protected abstract void onZap( Ballistica attack );

	@Override
	public boolean collect( Bag container ) {
		if (super.collect( container )) {
			if (container.owner != null) {
				charge( container.owner );
			}
			return true;
		} else {
			return false;
		}
	}
	
	public void charge( Char owner ) {
		if (charger == null) charger = new Charger();
		charger.attachTo( owner );
	}
	
	public void charge( Char owner, float chargeScaleFactor ){
		charge( owner );
		charger.setScaleFactor( chargeScaleFactor );
	}	

	protected void processSoulMark(Char target, int chargesUsed){
		if (target != Dungeon.hero &&
				Dungeon.hero.subClass == HeroSubClass.WARLOCK &&
				Random.Float() < .15f + (level*chargesUsed*0.03f)){
			SoulMark.prolong(target, SoulMark.class, SoulMark.DURATION + level);
		}
	}

	@Override
	public void onDetach( ) {
		stopCharging();
	}

	public void stopCharging() {
		if (charger != null) {
			charger.detach();
			charger = null;
		}
	}
	
	public int level() {
		
		return level;
	}
	
	@Override
	public Item identify() {

		curChargeKnown = true;
		super.identify();
		
		updateQuickslot();
		
		return this;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder( super.toString() );
		
		String status = status();
		if (status != null) {
			sb.append( " (" + status +  ")" );
		}
		
		return sb.toString();
	}
	
	@Override
	public String info() {
		String desc = desc();

		desc += "\n\n" + statsDesc();

		if (cursed && cursedKnown)
			desc += "\n\n" + Messages.get(Wand.class, "cursed");

		if(reinforced){
			desc += "\n\n" + Messages.get(Item.class, "reinforced");
		}
		
		return desc;
	}
	
	public String statsDesc(){
		return Messages.get(this, "stats_desc");
	}

    @Override
	public boolean isIdentified() {
		return super.isIdentified() && curChargeKnown;
	}
	
	@Override
	public String status() {
		if (levelKnown) {
			return (curChargeKnown ? curCharges : "?") + "/" + maxCharges;
		} else {
			return null;
		}
	}
	
	@Override
	public Item upgrade() {

		super.upgrade();
		
		updateLevel();
		curCharges = Math.min( curCharges + 1, maxCharges );
		updateQuickslot();
		
		return this;
	}
	
	@Override
	public Item degrade() {
		super.degrade();
		
		updateLevel();
		updateQuickslot();
		
		return this;
	}
	
	public void updateLevel() {
		maxCharges =
				Dungeon.isChallenged(Challenges.ENERGY_LOST)? Math.min( initialCharges() + level, 4 ) : Math.min( initialCharges() + level, 10 );
		curCharges = Math.min( curCharges, maxCharges );
	}
	
	protected int initialCharges() {
		return 2;
	}
	
	protected int chargesPerCast() {
		return 1;
	}	
	
	protected void fx( Ballistica bolt, Callback callback ) {
		MagicMissile.whiteLight( curUser.sprite.parent, bolt.sourcePos, bolt.collisionPos, callback );
		Sample.INSTANCE.play( Assets.SND_ZAP );
	}

	protected void wandUsed() {
        curCharges -= chargesPerCast();
		//if (!isIdentified() && usagesToKnow <= 0) {
		//	identify();
		//	GLog.w( Messages.get(Wand.class, "identify", name()) );
		//} else {
			if (curUser.heroClass == HeroClass.MAGE) levelKnown = true;
			updateQuickslot();
	//	}

		if (Dungeon.hero.heroClass == HeroClass.MAGE && Dungeon.hero.subClass == HeroSubClass.BATTLEMAGE){
			curUser.spendAndNext(TIME_TO_ZAP/3);
		} else if (Dungeon.hero.heroClass == HeroClass.MAGE ){
			curUser.spendAndNext(TIME_TO_ZAP*2/3);
		} else curUser.spendAndNext(TIME_TO_ZAP);
		
		GnollMark gnollmark = curUser.belongings.getItem(GnollMark.class);
		if (gnollmark!=null && gnollmark.charge<gnollmark.fullCharge) {gnollmark.charge++;}

		if (Dungeon.hero.heroClass == HeroClass.MAGE && Dungeon.skins == 4 ) {
			Dungeon.hero.spp -- ;
		}
		
	}
	
	@Override
	public Item random() {
		int n = 0;

		if (Random.Int(2) == 0) {
			n++;
			if (Random.Int(5) == 0) {
				n++;
			}
		}

		upgrade(n);
		if (Random.Float() < 0.3f) {
			cursed = true;
			cursedKnown = false;
		}

		return this;
	}
	
	@Override
	public int price() {
		int price = 75;
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

	private static final String UNFAMILIRIARITY     = "unfamiliarity";
	private static final String CUR_CHARGES			= "curCharges";
	private static final String CUR_CHARGE_KNOWN	= "curChargeKnown";
	private static final String PARTIALCHARGE 		= "partialCharge";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( UNFAMILIRIARITY, usagesToKnow );
		bundle.put( CUR_CHARGES, curCharges );
		bundle.put( CUR_CHARGE_KNOWN, curChargeKnown );
		bundle.put( PARTIALCHARGE , partialCharge );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		if ((usagesToKnow = bundle.getInt( UNFAMILIRIARITY )) == 0) {
			usagesToKnow = USAGES_TO_KNOW;
		}
		curCharges = bundle.getInt( CUR_CHARGES );
		curChargeKnown = bundle.getBoolean( CUR_CHARGE_KNOWN );
		partialCharge = bundle.getFloat( PARTIALCHARGE );
	}

	protected static CellSelector.Listener zapper = new  CellSelector.Listener() {

		@Override
		public void onSelect( Integer target ) {

			if (target != null) {

				final Wand curWand = (Wand)Wand.curItem;

				final Ballistica shot = new Ballistica( curUser.pos, target, curWand.collisionProperties);
				int cell = shot.collisionPos;

				if (target == curUser.pos || cell == curUser.pos) {
					GLog.i( Messages.get(Wand.class, "self_target") );
					return;
				}

				curUser.sprite.zap(cell);

				//attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
				if (Actor.findChar(target) != null)
					QuickSlotButton.target(Actor.findChar(target));
				else
					QuickSlotButton.target(Actor.findChar(cell));

				if (curWand.curCharges >= 1) {

					curUser.busy();

					curWand.fx(shot, new Callback() {
						@Override
						public void call() {
							curWand.onZap(shot);
							curWand.wandUsed();
						}
					});

					Invisibility.dispel();

				} else {

					GLog.w( Messages.get(Wand.class, "fizzles") );

				}

			}
		}

		@Override
		public String prompt() {
			return Messages.get(Wand.class, "prompt");
		}
	};

	protected class Charger extends Buff {
		
		private static final float BASE_CHARGE_DELAY = 10f;
		private static final float SCALING_CHARGE_ADDITION = 40f;
		private static final float NORMAL_SCALE_FACTOR = 0.95f;

		private static final float CHARGE_BUFF_BONUS = 0.25f;

		float scalingFactor = NORMAL_SCALE_FACTOR;
		
		@Override
		public boolean attachTo( Char target ) {
			super.attachTo( target );
			
			return true;
		}
		
		@Override
		public boolean act() {
			if (curCharges < maxCharges)
				gainCharge();
			
			if (partialCharge >= 1 && curCharges < maxCharges) {
				partialCharge--;
				curCharges++;
				updateQuickslot();
			}
			
			spend( TICK );
			
			return true;
		}

		private void gainCharge(){
			int missingCharges = 0;
			missingCharges += Math.min(0.75f, 0.25f * Ring.getBonus(target, RingOfEnergy.Energy.class)/10);
			//missingCharges = Math.max(0, missingCharges);
            if (Dungeon.hero.heroClass==HeroClass.MAGE && Dungeon.skins==2){
			missingCharges+=0.05f;
			}
			if (partialCharge < missingCharges)
				partialCharge = missingCharges;
			float turnsToCharge = (float) (BASE_CHARGE_DELAY
					+ (SCALING_CHARGE_ADDITION - Math.max(1, missingCharges)));
					
			partialCharge += 0.02f;
			//partialCharge += 0.01f*Math.min(1,missingCharges/10);
		
			Recharging bonus = target.buff(Recharging.class);
			if (bonus != null && bonus.remainder() > 0f){
				partialCharge += CHARGE_BUFF_BONUS * bonus.remainder();
			}
		}

		private void setScaleFactor(float value){
			this.scalingFactor = value;
		}
	}
}