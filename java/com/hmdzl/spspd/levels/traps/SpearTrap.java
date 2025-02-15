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

import com.hmdzl.spspd.Assets;
import com.hmdzl.spspd.Dungeon;
import com.hmdzl.spspd.ResultDescriptions;
import com.hmdzl.spspd.actors.Char;
import com.hmdzl.spspd.effects.Wound;
import com.hmdzl.spspd.levels.Level;
import com.hmdzl.spspd.levels.Terrain;
import com.hmdzl.spspd.messages.Messages;
import com.hmdzl.spspd.sprites.TrapSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class SpearTrap extends Trap {

	{
		color = TrapSprite.GREY;
		shape = TrapSprite.DOTS;
	}

	public void trigger() {
		if (Dungeon.visible[pos]){
			Sample.INSTANCE.play(Assets.SND_TRAP);
		}
		//this trap is not disarmed by being triggered
		reveal();
		Level.set(pos, Terrain.TRAP);
	}

	@Override
	public void activate(Char ch) {
		//super.activate(ch);
		if (Dungeon.visible[pos]){
			Sample.INSTANCE.play(Assets.SND_HIT);
			Wound.hit(pos);
		}

		//Char ch = Actor.findChar( pos);
		if (ch != null){
			int damage = Random.NormalIntRange(Dungeon.depth, Dungeon.depth*2);
			damage -= Random.IntRange( 0, ch.drRoll());
			ch.damage( Math.max(damage, 0) , this);
			if (!ch.isAlive() && ch == Dungeon.hero){
				Dungeon.fail(Messages.format(ResultDescriptions.LOSE));
				//GLog.n( Messages.get(this, "ondeath") );
			}
		}
	}
}
