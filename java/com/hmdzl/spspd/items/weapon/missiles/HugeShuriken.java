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
package com.hmdzl.spspd.items.weapon.missiles;

import com.hmdzl.spspd.actors.Char;
import com.hmdzl.spspd.actors.buffs.AttackDown;
import com.hmdzl.spspd.actors.buffs.BeOld;
import com.hmdzl.spspd.actors.buffs.Buff;
import com.hmdzl.spspd.items.Item;
import com.hmdzl.spspd.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class HugeShuriken extends MissileWeapon {

	{
		//name = "Huge shuriken";
		image = ItemSpriteSheet.HUGESHURIKEN;

		STR = 10;

		MIN = 2;
		MAX = 6;
	}

	public HugeShuriken() {
		this(1);
	}

	public HugeShuriken(int number) {
		super();
		quantity = number;
	}

	@Override
	public void proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, AttackDown.class,10f).level(70);
		Buff.affect(defender, BeOld.class).set(10f);
		super.proc(attacker, defender, damage);
	}
	@Override
	public Item random() {
		quantity = Random.Int(3, 7);
		return this;
	}

	@Override
	public int price() {
		return 10 * quantity;
	}
}
