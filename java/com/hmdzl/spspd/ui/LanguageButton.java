/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2017 Evan Debenham
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

package com.hmdzl.spspd.ui;

import com.hmdzl.spspd.Assets;
import com.hmdzl.spspd.messages.Messages;import com.hmdzl.spspd.ResultDescriptions;
import com.hmdzl.spspd.windows.WndLangs;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;

public class LanguageButton extends Button {

	private Image image;

	public LanguageButton() {
		super();

		width = image.width;
		height = image.height;
	}

	@Override
	protected void createChildren() {
		super.createChildren();

		image = Icons.get(Icons.LAN);
		add( image );
		updateIcon();
	}

	private void updateIcon(){
		switch(Messages.lang().status()){
			case INCOMPLETE:
				image.tint(1, 0, 0, .5f);
				break;
			case UNREVIEWED:
				image.tint(1, .5f, 0, .5f);
				break;
		}
	}

	@Override
	protected void layout() {
		super.layout();

		image.x = x;
		image.y = y;
	}

	@Override
	protected void onTouchDown() {
		image.brightness( 1.5f );
		Sample.INSTANCE.play( Assets.SND_CLICK );
	}

	@Override
	protected void onTouchUp() {
		image.resetColor();
		updateIcon();
	}

	@Override
	protected void onClick() {
		parent.add(new WndLangs());
	}

}
