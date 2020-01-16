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
package com.hmdzl.spspd.change.items.skills;

import com.hmdzl.spspd.change.Badges;
import com.hmdzl.spspd.change.Dungeon;
import com.hmdzl.spspd.change.Assets;
import com.hmdzl.spspd.change.actors.Actor;
import com.hmdzl.spspd.change.actors.Char;
import com.hmdzl.spspd.change.actors.buffs.AttackUp;
import com.hmdzl.spspd.change.actors.buffs.Blindness;
import com.hmdzl.spspd.change.actors.buffs.Buff;
import com.hmdzl.spspd.change.actors.buffs.DefenceUp;
import com.hmdzl.spspd.change.actors.buffs.Disarm;
import com.hmdzl.spspd.change.actors.buffs.Fury;
import com.hmdzl.spspd.change.actors.buffs.BloodImbue;
import com.hmdzl.spspd.change.actors.buffs.Haste;
import com.hmdzl.spspd.change.actors.buffs.Paralysis;
import com.hmdzl.spspd.change.actors.buffs.ParyAttack;
import com.hmdzl.spspd.change.actors.buffs.Silent;
import com.hmdzl.spspd.change.actors.buffs.SpAttack;
import com.hmdzl.spspd.change.actors.buffs.Terror;
import com.hmdzl.spspd.change.actors.hero.Hero;
import com.hmdzl.spspd.change.actors.hero.HeroClass;
import com.hmdzl.spspd.change.actors.hero.HeroSubClass;
import com.hmdzl.spspd.change.actors.mobs.Mob;
import com.hmdzl.spspd.change.actors.mobs.npcs.NPC;
import com.hmdzl.spspd.change.actors.mobs.pets.PET;
import com.hmdzl.spspd.change.effects.CellEmitter;
import com.hmdzl.spspd.change.effects.Speck;
import com.hmdzl.spspd.change.items.Item;
import com.hmdzl.spspd.change.items.scrolls.ScrollOfUpgrade;
import com.hmdzl.spspd.change.levels.Level;
import com.hmdzl.spspd.change.mechanics.Ballistica;
import com.hmdzl.spspd.change.scenes.CellSelector;
import com.hmdzl.spspd.change.scenes.GameScene;
import com.hmdzl.spspd.change.sprites.CharSprite;
import com.hmdzl.spspd.change.sprites.ItemSpriteSheet;
import com.hmdzl.spspd.change.utils.GLog;
import com.hmdzl.spspd.change.messages.Messages;
import com.hmdzl.spspd.change.windows.WndBag;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.noosa.audio.Sample;
import com.hmdzl.spspd.change.effects.particles.ElmoParticle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.HashMap;


public class FollowerSkill extends ClassSkill {
	private static final String TXT_VALUE = "%+d";
 private static int SKILL_TIME = 1;
	{
		//name = "follower cape";
		image = ItemSpriteSheet.ARMOR_FOLLOWER;
	}

	private HashMap<Callback, Mob> targets = new HashMap<Callback, Mob>();

	@Override
	public void doSpecial() {
		curUser.spend(SKILL_TIME);
		curUser.sprite.operate(curUser.pos);
		curUser.busy();
		curUser.sprite.centerEmitter().start(ElmoParticle.FACTORY, 0.15f, 4);
		Sample.INSTANCE.play(Assets.SND_READ);
		FollowerSkill.charge += 15;
		Buff.affect(curUser, ParyAttack.class);
	}

	@Override
	public void doSpecial2() {
	
		curUser.spend(SKILL_TIME);
		curUser.sprite.operate(curUser.pos);
		curUser.busy();
		curUser.sprite.centerEmitter().start(ElmoParticle.FACTORY, 0.15f, 4);
		Sample.INSTANCE.play(Assets.SND_READ);
		FollowerSkill.charge += 20;
		int people = 0;
		for (Mob mob : Dungeon.level.mobs) {
			mob.beckon(curUser.pos);
			if (mob instanceof Mob && !(mob instanceof NPC)){
				people++;
			}
		}
		int goldearn = (int)(people*(Dungeon.hero.lvl/10+100));
		Dungeon.gold+= goldearn;
		Dungeon.hero.sprite.showStatus(CharSprite.NEUTRAL, TXT_VALUE, goldearn);
	}

	@Override
	public void doSpecial3() {

		for (Mob mob : Dungeon.level.mobs) {
			if (Level.fieldOfView[mob.pos] && (Dungeon.level.distance(curUser.pos, mob.pos) <= 10)) {
				Buff.affect(mob, Terror.class, 10f).object = curUser.id();
				Buff.prolong(mob, Blindness.class, 10f);
			}
		}	
	    Buff.affect(curUser, Haste.class,20f);
		curUser.spend(SKILL_TIME);
		curUser.sprite.operate(curUser.pos);
		curUser.busy();
		curUser.sprite.centerEmitter().start(ElmoParticle.FACTORY, 0.15f, 4);
		Sample.INSTANCE.play(Assets.SND_READ);
		FollowerSkill.charge += 10;
	}

	@Override
	public void doSpecial4() {
		GameScene.selectItem(itemSelector, WndBag.Mode.UPGRADEABLE, Messages.get(ScrollOfUpgrade.class,"prompt"));
		
	}

	private final WndBag.Listener itemSelector = new WndBag.Listener() {
		@Override
		public void onSelect(Item item) {
			if (item != null) {
				FollowerSkill.this.upgrade(item);
				FollowerSkill.charge += 15;
			}
		}
	};
	private void upgrade(Item item) {

		GLog.w(Messages.get(ScrollOfUpgrade.class,"looks_better", item.name()));

		item.upgrade(1);
        item.uncurse();
		
		curUser.sprite.operate(curUser.pos);
		curUser.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
		Badges.validateItemLevelAquired(item);
		
		curUser.spend(1f);
		curUser.busy();
		
	}
}