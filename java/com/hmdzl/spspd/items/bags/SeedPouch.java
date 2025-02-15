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
package com.hmdzl.spspd.items.bags;

import com.hmdzl.spspd.actors.hero.Hero;
import com.hmdzl.spspd.items.Item;

import com.hmdzl.spspd.items.StoneOre;
import com.hmdzl.spspd.items.nornstone.NornStone;
import com.hmdzl.spspd.plants.Plant;
import com.hmdzl.spspd.sprites.ItemSpriteSheet;

public class SeedPouch extends Bag {

	{
		//name = "seed pouch";
		image = ItemSpriteSheet.POUCH;

		size = 25;
	}

	@Override
	public boolean grab(Item item) {
        return item instanceof Plant.Seed
                || item instanceof StoneOre
                || item instanceof NornStone;
	}
	
	@Override
	public int price() {

	    return 50 * quantity;
	}

	@Override
	public boolean doPickUp( Hero hero ) {

		return hero.belongings.getItem( SeedPouch.class ) == null && super.doPickUp( hero ) ;

	}
}
