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
package com.hmdzl.spspd.sprites;

import com.hmdzl.spspd.Assets;
import com.hmdzl.spspd.actors.mobs.pets.GreenDragon;
import com.hmdzl.spspd.effects.Lightning;
import com.watabou.noosa.TextureFilm;

import java.util.Calendar;

public class GreenDragonSprite extends MobSprite {
	
	//Frames 1-4 are idle, 5-8 are moving, 9-12 are attack and the last are for death RBVG
		public boolean festive;
	private int[] points = new int[2];

	public GreenDragonSprite() {
		super();

		final Calendar calendar = Calendar.getInstance();
		festive = (calendar.get(Calendar.MONTH) == 10 && calendar
				.get(Calendar.WEEK_OF_MONTH) > 2);		
		
		final int c = festive ? 0 : 16;
		
		texture(Assets.GREENDRAGON);
		

		TextureFilm frames = new TextureFilm(texture, 16, 16);

		idle = new Animation(2, true);
		idle.frames(frames, c + 0, c + 1, c + 2, c + 3);

		run = new Animation(8, true);
		run.frames(frames, c + 4, c + 5, c + 6,c +  7);

		attack = new Animation(8, false);
		attack.frames(frames, c + 8, c + 9, c + 10, c + 11);

		zap = attack.clone();
		
		die = new Animation(8, false);
		die.frames(frames, c + 12, c + 13, c + 14, c + 15);

		play(idle);
	}

	@Override
	public void zap(int pos) {

		parent.add( new Lightning( ch.pos, pos, (GreenDragon) ch));

		turnTo(ch.pos, pos);
		play(zap);
	}
	
	@Override
	public int blood() {
		return 0xFFcdcdb7;
	}
}
