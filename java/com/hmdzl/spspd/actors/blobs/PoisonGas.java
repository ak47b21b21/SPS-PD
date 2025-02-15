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
package com.hmdzl.spspd.actors.blobs;

import com.hmdzl.spspd.actors.Actor;
import com.hmdzl.spspd.actors.Char;
import com.hmdzl.spspd.actors.buffs.ArmorBreak;
import com.hmdzl.spspd.actors.buffs.Buff;
import com.hmdzl.spspd.actors.buffs.Burning;
import com.hmdzl.spspd.actors.buffs.Roots;
import com.hmdzl.spspd.actors.buffs.Slow;
import com.hmdzl.spspd.effects.BlobEmitter;
import com.hmdzl.spspd.effects.Speck;
import com.hmdzl.spspd.Dungeon;
import com.hmdzl.spspd.actors.buffs.Tar;
import com.hmdzl.spspd.messages.Messages;import com.hmdzl.spspd.ResultDescriptions;
import com.watabou.utils.Random;
import com.hmdzl.spspd.levels.Level;
import com.hmdzl.spspd.scenes.GameScene;


public class PoisonGas extends Blob {

	@Override
	protected void evolve() {
		super.evolve();
		
		Char ch;
		for (int i = 0; i < LENGTH; i++) {
			if (cur[i] > 0 && (ch = Actor.findChar(i)) != null) {
				if (!ch.isImmune(this.getClass())){          
					Buff.affect(ch,Slow.class,3f);
					Buff.affect(ch,Roots.class,3f);
					Buff.affect(ch,ArmorBreak.class,3f).level(20);
                }	
			}				
        }
		
        Blob blob = Dungeon.level.blobs.get( Fire.class );
        if (blob != null) {

            for (int pos=0; pos < LENGTH; pos++) {

                if ( cur[pos] > 0 && blob.cur[ pos ] < 2 ) {

                    int flammability = 0;

                    for (int n : Level.NEIGHBOURS8) {
                        if ( blob.cur[ pos + n ] > 0 ) {
                            flammability++;
                        }
                    }
                    if( Random.Int( 4 ) < flammability ) {
                        blob.volume += ( blob.cur[ pos ] = 2 );
                        volume -= ( cur[pos] / 2 );
                        cur[pos] -=( cur[pos] / 2 );
                    }
                }
            }
        }			
	}

	@Override
	public void use(BlobEmitter emitter) {
		super.use(emitter);

		emitter.pour(Speck.factory(Speck.TARGAS,true ), 0.6f);
	}

	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}