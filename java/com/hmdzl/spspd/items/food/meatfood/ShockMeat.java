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
package com.hmdzl.spspd.items.food.meatfood;

import com.hmdzl.spspd.actors.buffs.Bleeding;
import com.hmdzl.spspd.actors.buffs.Buff;
import com.hmdzl.spspd.actors.buffs.Cripple;
import com.hmdzl.spspd.actors.buffs.Drowsy;
import com.hmdzl.spspd.actors.buffs.Poison;
import com.hmdzl.spspd.actors.buffs.STRdown;
import com.hmdzl.spspd.actors.buffs.Slow;
import com.hmdzl.spspd.actors.buffs.Vertigo;
import com.hmdzl.spspd.actors.hero.Hero;
import com.hmdzl.spspd.items.food.Food;
import com.hmdzl.spspd.messages.Messages;
import com.hmdzl.spspd.sprites.ItemSprite;
import com.hmdzl.spspd.sprites.ItemSpriteSheet;
import com.hmdzl.spspd.utils.GLog;

public class ShockMeat extends MeatFood {

    private static ItemSprite.Glowing GREEN = new ItemSprite.Glowing( 0x00FF00 );
 	@Override
	public ItemSprite.Glowing glowing() {
		return GREEN;
	}
	{
		//name = "frozen carpaccio";
		image = ItemSpriteSheet.MEAT;
		energy = 85;
		hornValue = 1;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_EAT)) {
            effect(hero);
		}
	}

	@Override
	public int price() {
		return 3 * quantity;
	}

    public static void effect(Hero hero){
		GLog.i( Messages.get(Meat.class, "refresh") );
		Buff.detach( hero, Poison.class );
		Buff.detach( hero, Cripple.class );
		Buff.detach( hero, STRdown.class );
		Buff.detach( hero, Bleeding.class );
		Buff.detach( hero, Drowsy.class );
		Buff.detach( hero, Slow.class );
		Buff.detach( hero, Vertigo.class);
	}
	
	public static Food cook(MysteryMeat ingredient) {
		ShockMeat result = new ShockMeat();
		result.quantity = ingredient.quantity();
		return result;
	}
	public static Food cook(Meat ingredient) {
		ShockMeat result = new ShockMeat();
		result.quantity = ingredient.quantity();
		return result;
	}
}
