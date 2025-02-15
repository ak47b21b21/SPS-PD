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
package com.hmdzl.spspd.items.armor.normalarmor;

import com.hmdzl.spspd.actors.Char;
import com.hmdzl.spspd.actors.buffs.Buff;
import com.hmdzl.spspd.actors.buffs.Charm;
import com.hmdzl.spspd.actors.buffs.Corruption;
import com.hmdzl.spspd.actors.hero.Hero;
import com.hmdzl.spspd.items.Item;
import com.hmdzl.spspd.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class CatSharkArmor extends NormalArmor {
 
    public Buff passiveBuff;
	{
		//name = "cat";
		image = ItemSpriteSheet.CATSHARK;
		MAX = 0;
		MIN = 0;
	}

	public CatSharkArmor() {
		super(1,2,5,3);
	}

	public int charge = 0;
	//public int time = 0;
	private static final String CHARGE = "charge";	
	//private static final String TIME = "time";
	
	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CHARGE, charge);
		//bundle.put(TIME, time);
	}
	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		//time = bundle.getInt(TIME);
	}

	@Override
	public boolean doUnequip(Hero hero, boolean collect, boolean single) {
		if (super.doUnequip(hero, collect, single)) {
			if (passiveBuff != null){
				passiveBuff.detach();
				passiveBuff = null;
			}
			hero.belongings.armor = null;
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public Item upgrade(boolean hasglyph) {

        MAX-=2;

		return super.upgrade(hasglyph);
	}


	@Override
	public void proc(Char attacker, Char defender, int damage) {

		if (Random.Int(8) == 0) {
			Buff.affect(attacker,Charm.class,6f).object=defender.id();
			charge ++;
		}

		if (charge > 15 && !(attacker.properties().contains(Char.Property.BOSS) || attacker.properties().contains(Char.Property.MINIBOSS))) {
			Buff.affect(attacker, Corruption.class);
			charge = 0;
		}
		if (glyph != null) {
			glyph.proc(this, attacker, defender, damage);
		}
    }
}
