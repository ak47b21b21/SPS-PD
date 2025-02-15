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
package com.hmdzl.spspd.items.scrolls;

import com.hmdzl.spspd.Assets;
import com.hmdzl.spspd.Dungeon;
import com.hmdzl.spspd.actors.buffs.Buff;
import com.hmdzl.spspd.actors.buffs.Invisibility;
import com.hmdzl.spspd.actors.buffs.SuperArcane;
import com.hmdzl.spspd.actors.mobs.Mob;
import com.hmdzl.spspd.actors.mobs.npcs.NPC;
import com.hmdzl.spspd.levels.Level;
import com.hmdzl.spspd.scenes.GameScene;
import com.watabou.noosa.audio.Sample;

public class ScrollOfPsionicBlast extends Scroll {

	{
		//name = "Scroll of Psionic Blast";
		consumedValue = 10;
		initials = 7;

		 
	}

	@Override
	public void doRead() {

		GameScene.flash(0xFFFFFF);

		Sample.INSTANCE.play(Assets.SND_BLAST);
		Invisibility.dispel();

		int people = 0;
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			mob.beckon(curUser.pos);
			if (mob instanceof Mob && !(mob instanceof NPC)){
				people++;
			}
		}
		Buff.prolong(curUser, SuperArcane.class, 30f).level(people);

		//for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
		//	if (Level.fieldOfView[mob.pos]) {
		//		mob.damage(mob.HT, this );
		//	}
		//}

		Dungeon.observe();

		setKnown();

		readAnimation();
	
	}
	
	@Override
	public void empoweredRead() {
		GameScene.flash( 0xFFFFFF );
		
		Sample.INSTANCE.play( Assets.SND_BLAST );
		Invisibility.dispel();
		
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (Level.fieldOfView[mob.pos]) {
				mob.damage(mob.HT, this );
			}
		}
		
		setKnown();

		curUser.spendAndNext(TIME_TO_READ);
	}	
	
	public boolean checkOriginalGenMobs (){
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (mob.originalgen){return true;}
		 }	
		return false;
	}

	@Override
	public int price() {
		return isKnown() ? 80 * quantity : super.price();
	}
}
