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
import com.hmdzl.spspd.messages.Messages;import com.hmdzl.spspd.ResultDescriptions;
import com.hmdzl.spspd.sprites.TrapSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public abstract class Trap implements Bundlable {

	public String name = Messages.get(this, "name");

	public int color;
	public int shape;

	public int pos;

	public TrapSprite sprite;
	public boolean visible;
	public boolean active = true;

	public Trap set(int pos){
		this.pos = pos;
		return this;
	}

	public Trap reveal() {
		visible = true;
		if (sprite != null && sprite.visible == false) {
			sprite.visible = true;
			sprite.alpha( 0 );
			sprite.parent.add( new AlphaTweener( sprite, 1, 0.6f));
		}
		return this;
	}

	public Trap hide() {
		visible = false;
		if (sprite != null)
			sprite.visible = false;
		return this;
	}

	public void activate(Char ch) {
	
		if (Dungeon.visible[pos]) {
			Sample.INSTANCE.play(Assets.SND_TRAP);
		}
		disarm();
		reveal();
		
	}

	protected void disarm(){
		Dungeon.level.disarmTrap(pos);
		active = false;
		if (sprite != null) {
			sprite.reset( this );
		}
	}

	private static final String POS	= "pos";
	private static final String VISIBLE	= "visible";
	private static final String ACTIVE = "active";

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		pos = bundle.getInt( POS );
		visible = bundle.getBoolean( VISIBLE );
		if (bundle.contains(ACTIVE)){
			active = bundle.getBoolean(ACTIVE);
		}
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( POS, pos );
		bundle.put( VISIBLE, visible );
		bundle.put( ACTIVE, active );
	}

	public String desc() {
		return Messages.get(this, "desc");
	}
}
