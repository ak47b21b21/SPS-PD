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
package com.hmdzl.spspd.items.misc;

import com.hmdzl.spspd.Assets;
import com.hmdzl.spspd.Dungeon;
import com.hmdzl.spspd.actors.buffs.Buff;
import com.hmdzl.spspd.actors.buffs.Shieldblock;
import com.hmdzl.spspd.actors.hero.Hero;
import com.hmdzl.spspd.actors.mobs.Mob;
import com.hmdzl.spspd.items.Item;
import com.hmdzl.spspd.levels.Level;
import com.hmdzl.spspd.messages.Messages;
import com.hmdzl.spspd.scenes.GameScene;
import com.hmdzl.spspd.sprites.ItemSpriteSheet;
import com.hmdzl.spspd.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;


public class Ankhshield extends Item {
	{
		//name = "Ankh Shield";
		image = ItemSpriteSheet.SHIELD;
		defaultAction = AC_DEFENCE;
        unique = true;
		 
	}
	
	private static final String AC_DEFENCE = "DEFENCE";
	public final int fullCharge = 250;
	public int charge = 0;
	private static final String CHARGE = "charge";
	
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
	}	
	
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		charge = bundle.getInt(CHARGE);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (charge >= 120){
			actions.add(AC_DEFENCE);
		}
		actions.remove(AC_DROP);
		actions.remove(AC_THROW);
		return actions;
	}


	@Override
	public void execute(final Hero hero, String action) {
		if (action.equals(AC_DEFENCE)) {
		    if (charge < 120)
				GLog.i(Messages.get(this, "rest"));
            else {
				
			    curUser = hero;
				GameScene.flash(0x009900);
			    Sample.INSTANCE.play(Assets.SND_TELEPORT);
				
			    for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			    if (Level.fieldOfView[mob.pos]) {
				mob.damage(5, this);
				Buff.prolong(mob, Shieldblock.class, 3);
			        }
		        }
				
				charge -= 120;
				updateQuickslot();
		    }
	    } else {

			super.execute(hero, action);
		}
    }	
	
	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public boolean isIdentified() {
		return true;
	}
	
	
	@Override
	public String info() {
		String info = desc();
		info += "\n\n" + Messages.get(this,"charge",charge,fullCharge);
		return info;
	}

	 @Override
	 public String status() {
		 return Messages.format("%d", charge /120);
	 }

}