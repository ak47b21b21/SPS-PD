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
package com.hmdzl.spspd.levels.traps;

import com.hmdzl.spspd.Dungeon;
import com.hmdzl.spspd.actors.Char;
import com.hmdzl.spspd.actors.buffs.Buff;
import com.hmdzl.spspd.actors.buffs.Poison;
import com.hmdzl.spspd.effects.CellEmitter;
import com.hmdzl.spspd.effects.particles.PoisonParticle;
import com.hmdzl.spspd.items.Heap;
import com.hmdzl.spspd.sprites.TrapSprite;

public class PoisonTrap extends Trap{

	{
		color = TrapSprite.VIOLET;
		shape = TrapSprite.CROSSHAIR;
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);

		//Char ch = Actor.findChar( pos );

		if (ch != null) {
			Buff.affect( ch, Poison.class ).set(4 + Dungeon.depth / 2 );
		}

		CellEmitter.center( pos ).burst( PoisonParticle.SPLASH, 3 );
		Heap heap = Dungeon.level.heaps.get(pos);
		if (heap != null) {heap.earthhit();}

	}
}
