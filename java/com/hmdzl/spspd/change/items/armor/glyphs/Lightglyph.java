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
package com.hmdzl.spspd.change.items.armor.glyphs;

import com.hmdzl.spspd.change.actors.Char;
import com.hmdzl.spspd.change.actors.buffs.Amok;
import com.hmdzl.spspd.change.actors.buffs.Buff;
import com.hmdzl.spspd.change.actors.buffs.Charm;
import com.hmdzl.spspd.change.actors.buffs.Frost;
import com.hmdzl.spspd.change.actors.buffs.Terror;
import com.hmdzl.spspd.change.actors.mobs.SewerHeart;
import com.hmdzl.spspd.change.effects.CellEmitter;
import com.hmdzl.spspd.change.effects.Speck;
import com.hmdzl.spspd.change.effects.particles.SnowParticle;
import com.hmdzl.spspd.change.items.armor.Armor;
import com.hmdzl.spspd.change.items.armor.Armor.Glyph;
import com.hmdzl.spspd.change.items.misc.FourClover;
import com.hmdzl.spspd.change.levels.Level;
import com.hmdzl.spspd.change.sprites.ItemSprite;
import com.hmdzl.spspd.change.sprites.ItemSprite.Glowing;
import com.hmdzl.spspd.change.actors.buffs.armorbuff.GlyphDark;
import com.hmdzl.spspd.change.actors.buffs.armorbuff.GlyphEarth;
import com.hmdzl.spspd.change.actors.buffs.armorbuff.GlyphElectricity;
import com.hmdzl.spspd.change.actors.buffs.armorbuff.GlyphFire;
import com.hmdzl.spspd.change.actors.buffs.armorbuff.GlyphIce;
import com.hmdzl.spspd.change.actors.buffs.armorbuff.GlyphLight;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Lightglyph extends Glyph {

	private static ItemSprite.Glowing YELLOW = new ItemSprite.Glowing( 0xFFFF44 );

	@Override
	public int proc(Armor armor, Char attacker, Char defender, int damage) {

		GlyphDark gdark = defender.buff(GlyphDark.class);
		GlyphIce gice = defender.buff(GlyphIce.class);
	    GlyphLight glight = defender.buff(GlyphLight.class);
	    GlyphFire gfire = defender.buff(GlyphFire.class);
		GlyphEarth gearth = defender.buff(GlyphEarth.class);
		GlyphElectricity gelect = defender.buff(GlyphElectricity.class);
		FourClover.FourCloverBless fcb = defender.buff(FourClover.FourCloverBless.class);
	
		if (defender.isAlive() && glight == null)
		{
			Buff.detach(defender,GlyphIce.class);
			Buff.detach(defender,GlyphDark.class);
			Buff.detach(defender,GlyphFire.class);
			Buff.detach(defender,GlyphEarth.class);
			Buff.detach(defender,GlyphElectricity.class);
			Buff.affect(defender,GlyphLight.class);
		}	
	
		int level = (int) GameMath.gate(0, armor.level, 6);

		if (Random.Int(level / 2 + 5) >= 4) {

			int duration = Random.IntRange(4, 7);
			Buff.affect(attacker, Charm.class, Charm.durationFactor(attacker)
					* duration).object = defender.id();
		    Buff.affect(attacker, Amok.class,10f);
			attacker.sprite.centerEmitter().start(Speck.factory(Speck.HEART),0.2f, 5);
		} 
		
		else if (Random.Int(level / 2 + 5) >= 3 || (fcb != null && Random.Int(level/2 + 5) >= 1)) {
			Buff.affect(attacker, Terror.class, 10f).object = defender.id();
		} 		

		return damage;
	}

	@Override
	public Glowing glowing() {
		return YELLOW;
	}

}
