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
import com.hmdzl.spspd.actors.Char;
import com.hmdzl.spspd.actors.buffs.Buff;
import com.hmdzl.spspd.actors.buffs.Paralysis;
import com.hmdzl.spspd.effects.CellEmitter;
import com.hmdzl.spspd.effects.Speck;
import com.hmdzl.spspd.levels.Level;
import com.hmdzl.spspd.messages.Messages;import com.hmdzl.spspd.ResultDescriptions;
import com.hmdzl.spspd.sprites.TrapSprite;
import com.hmdzl.spspd.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class RockfallTrap extends Trap {

	{
		color = TrapSprite.GREY;
		shape = TrapSprite.DIAMOND;
	}

	@Override
	public void activate(Char ch) {
		super.activate(ch);

		boolean seen = false;

		for (int i : Level.NEIGHBOURS9){

			if (Level.solid[pos+i])
				continue;

			if (Dungeon.visible[ pos+i ]){
				CellEmitter.get( pos + i - Level.WIDTH ).start(Speck.factory(Speck.ROCK), 0.07f, 10);
				if (!seen) {
					Camera.main.shake(3, 0.7f);
					Sample.INSTANCE.play(Assets.SND_ROCKS);
					seen = true;
				}
			}

			//Char ch = Actor.findChar( pos+i );

			if (ch != null){
				int damage = Random.NormalIntRange(Dungeon.depth, Dungeon.depth*2);
				damage -= Random.IntRange( 0, ch.drRoll());
				ch.damage( Math.max(damage, 0) , this);

				Buff.prolong( ch, Paralysis.class, Paralysis.duration(ch)/2);

				if (!ch.isAlive() && ch == Dungeon.hero){
					Dungeon.fail(Messages.format(ResultDescriptions.LOSE));
					GLog.n( Messages.get(this, "ondeath") );
				}
			}
		}

	}
}
