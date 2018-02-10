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
package com.hmdzl.spspd.change.actors.mobs.npcs;


import com.hmdzl.spspd.change.Dungeon;
import com.hmdzl.spspd.change.Statistics;
import com.hmdzl.spspd.change.actors.Char;
import com.hmdzl.spspd.change.actors.buffs.Buff;
import com.hmdzl.spspd.change.items.Heap;
import com.hmdzl.spspd.change.items.weapon.melee.special.Spork;
import com.hmdzl.spspd.change.levels.Level;
import com.hmdzl.spspd.change.messages.Messages;
import com.hmdzl.spspd.change.sprites.RatKingSprite;

public class RatKing extends NPC {

	{
		//name = "rat king";
		spriteClass = RatKingSprite.class;

		state = SLEEPING;
		properties.add(Property.BEAST);
		properties.add(Property.BOSS);
	}

	@Override
	public int defenseSkill(Char enemy) {
		return 1000;
	}

	@Override
	public float speed() {
		return 2f;
	}

	@Override
	protected Char chooseEnemy() {
		return null;
	}

	@Override
	public void damage(int dmg, Object src) {
	}

	@Override
	public void add(Buff buff) {
	}

	@Override
	public boolean reset() {
		return true;
	}

   
	
	@Override
	public void interact() {
		
		int checkChests = 0;
		int length = Level.getLength();
		for (int i = 0; i < length; i++) {
			Heap chest = Dungeon.level.heaps.get(i);
			if(chest != null && chest.chestCheck()){checkChests++;}
		}
		
		Spork spork = Dungeon.hero.belongings.getItem(Spork.class);
		//RunicBlade runicblade = Dungeon.hero.belongings.getItem(RunicBlade.class);
		
		sprite.turnTo(pos, Dungeon.hero.pos);
		if (state == SLEEPING) {
			notice();
			yell(Messages.get(this, "not_sleeping"));
			yell(Messages.get(this, "not_takeing"));
			state = WANDERING;
		//} else if (Statistics.deepestFloor>9 && checkChests >= Dungeon.ratChests && spork==null && runicblade==null){ 
		} else if (Statistics.deepestFloor>10 && checkChests >= Dungeon.ratChests && spork==null){ 
			yell(Messages.get(this, "thanks"));
			Dungeon.sporkAvail = true;
		} else if (checkChests < Dungeon.ratChests){
			Dungeon.sporkAvail = false;
			yell(Messages.get(this, "why"));
		} else if (spork!=null) {
			//yell("You found my spork! Here, trade me for this old one.");
			yell(Messages.get(this, "havefun"));
			//if (spork.isEquipped(Dungeon.hero)) {
			//	spork.doUnequip(Dungeon.hero, false);
			//}
			//spork.detach(Dungeon.hero.belongings.backpack);
			//Dungeon.level.drop(new RunicBlade().enchantNom(), pos).sprite.drop();
			//Dungeon.limitedDrops.runicblade.drop();
			
		} else {
			yell(Messages.get(this, "what_is_it"));
		}
	}

	@Override
	public String description() {
		return ((RatKingSprite) sprite).festive ? 
		Messages.get(this, "desc_festive") : super.description();
	}
}
