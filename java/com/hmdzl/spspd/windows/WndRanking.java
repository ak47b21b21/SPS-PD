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
package com.hmdzl.spspd.windows;

import com.hmdzl.spspd.Assets;
import com.hmdzl.spspd.Badges;
import com.hmdzl.spspd.Dungeon;
import com.hmdzl.spspd.Statistics;
import com.hmdzl.spspd.actors.hero.Belongings;
import com.hmdzl.spspd.items.Item;
import com.hmdzl.spspd.messages.Messages;
import com.hmdzl.spspd.scenes.PixelScene;
import com.hmdzl.spspd.sprites.HeroSprite;
import com.hmdzl.spspd.ui.BadgesList;
import com.hmdzl.spspd.ui.Icons;
import com.hmdzl.spspd.ui.ItemSlot;
import com.hmdzl.spspd.ui.RedButton;
import com.hmdzl.spspd.ui.ScrollPane;
import com.hmdzl.spspd.ui.Window;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;

import java.util.Locale;

public class WndRanking extends WndTabbed {

	private static final String TXT_ERROR = "Unable to load additional information";

	private static final String TXT_STATS = "Stats";
	private static final String TXT_ITEMS = "Items";
	private static final String TXT_BADGES = "Badges";

	private static final int WIDTH = 115;
	private static final int HEIGHT = 180;

	private static final int TAB_WIDTH = 40;

	private Thread thread;
	private String error = null;

	private Image busy;

	public WndRanking(final String gameFile) {

		super();
		resize(WIDTH, HEIGHT);

		thread = new Thread() {
			@Override
			public void run() {
				try {
					Badges.loadGlobal();
					Dungeon.loadGame(gameFile);
				} catch (Exception e) {
					error = Messages.get(WndRanking.class, "error");
				}
			}
		};
		thread.start();

		busy = Icons.BUSY.get();
		busy.origin.set(busy.width / 2, busy.height / 2);
		busy.angularSpeed = 720;
		busy.x = (WIDTH - busy.width) / 2;
		busy.y = (HEIGHT - busy.height) / 2;
		add(busy);
	}

	@Override
	public void update() {
		super.update();

		if (thread != null && !thread.isAlive()) {
			thread = null;
			if (error == null) {
				remove(busy);
				createControls();
			} else {
				hide();
				Game.scene().add(new WndError(error));
			}
		}
	}

	private void createControls() {

		String[] labels = 
		{Messages.get(this, "stats"), Messages.get(this, "items"), Messages.get(this, "badges")};
		Group[] pages = 
		{ new StatsTab(), new ItemsTab(), new BadgesTab() };

		for (int i = 0; i < pages.length; i++) {

			add(pages[i]);

			Tab tab = new RankingTab(labels[i], pages[i]);
			add(tab);
		}

		layoutTabs();

		select(0);
	}

	private class RankingTab extends LabeledTab {

		private Group page;

		public RankingTab(String label, Group page) {
			super(label);
			this.page = page;
		}

		@Override
		protected void select(boolean value) {
			super.select(value);
			if (page != null) {
				page.visible = page.active = selected;
			}
		}
	}

	private class StatsTab extends Group {

		private static final int GAP = 4;

		public StatsTab() {
			super();

			String heroClass = Dungeon.hero.className();

			IconTitle title = new IconTitle();
			title.icon(HeroSprite.avatar(Dungeon.hero.heroClass,
					Dungeon.hero.useskin()));
			title.label( Messages.get(this, "title", Dungeon.hero.lvl, heroClass ).toUpperCase( Locale.ENGLISH ) );
			title.color(Window.SHPX_COLOR);
			title.setRect(0, 0, WIDTH, 0);
			add(title);

			float pos = title.bottom();

			if (Dungeon.challenges > 0) {
				RedButton btnCatalogus = new RedButton( Messages.get(this, "challenges")) {
					@Override
					protected void onClick() {
						Game.scene().add(
								new WndChallenges(Dungeon.challenges, false));
					}
				};
				btnCatalogus.setRect(0, pos + GAP, btnCatalogus.reqWidth() + 2,
						btnCatalogus.reqHeight() + 2);
				add(btnCatalogus);

				pos = btnCatalogus.bottom();
			}

			pos += GAP + GAP;

			pos = statSlot(this, Messages.get(this, "str"), Integer.toString(Dungeon.hero.STR),
					pos);
			pos = statSlot(this, Messages.get(this, "health"), Integer.toString(Dungeon.hero.HT),
					pos);

			pos += GAP;

			pos = statSlot(this, (Messages.get(this, "duration")),Integer.toString((int) Statistics.duration), pos);

			pos += GAP;

			//pos = statSlot(this, Messages.get(this, "depth"),
					//Integer.toString(Statistics.deepestFloor), pos);
			pos = statSlot(this, Messages.get(this, "enemies"),
					Integer.toString(Statistics.enemiesSlain), pos);
			pos = statSlot(this, Messages.get(this, "gold"),
					Integer.toString(Statistics.goldCollected), pos);

			pos += GAP;

			pos = statSlot(this, Messages.get(this, "food"),
					Integer.toString(Statistics.foodEaten), pos);
			pos = statSlot(this, Messages.get(this, "alchemy"),
					Integer.toString(Statistics.potionsCooked), pos);
			pos = statSlot(this, Messages.get(this, "ankhs"),
					Integer.toString(Statistics.ankhsUsed), pos);
			
			pos += GAP;
			//pos = statSlot(this, Messages.get(this, "water"),
					//Integer.toString(Statistics.waters), pos);
			//pos = statSlot(this, TXT_SHADOW,
					//Integer.toString(Statistics.shadowYogsKilled), pos);
		}

		private float statSlot(Group parent, String label, String value,
				float pos) {

			RenderedText txt = PixelScene.renderText( label, 7 );
			txt.y = pos;
			parent.add( txt );
			
			txt = PixelScene.renderText( value, 7 );
			txt.x = WIDTH * 0.65f;
			txt.y = pos;
			PixelScene.align(txt);
			parent.add( txt );
			
			return pos + GAP + txt.baseLine();
		}
	}

	private class ItemsTab extends Group {

		private float pos;

		public ItemsTab() {
			super();

			Belongings stuff = Dungeon.hero.belongings;
			if (stuff.weapon != null) {
				addItem(stuff.weapon);
			}
			if (stuff.armor != null) {
				addItem(stuff.armor);
			}
			if (stuff.misc1 != null) {
				addItem(stuff.misc1);
			}
			if (stuff.misc2 != null) {
				addItem(stuff.misc2);
			}
			if (stuff.misc3 != null) {
				addItem(stuff.misc3);
			}
		}

		private void addItem( Item item ) {
			ItemButton slot = new ItemButton( item );
			slot.setRect( 0, pos, width, ItemButton.HEIGHT );
			add( slot );
			
			pos += slot.height() + 1;
		}
	}

	private class BadgesTab extends Group {

		public BadgesTab() {
			super();

			camera = WndRanking.this.camera;

			ScrollPane list = new BadgesList(false);
			add(list);

			list.setSize(WIDTH, HEIGHT);
		}
	}

	private class ItemButton extends Button {

		public static final int HEIGHT = 28;

		private Item item;

		private ItemSlot slot;
		private ColorBlock bg;
		private RenderedText name;

		public ItemButton(Item item) {

			super();

			this.item = item;

			slot.item(item);
			if (item.cursed && item.cursedKnown) {
				bg.ra = +0.2f;
				bg.ga = -0.1f;
			} else if (!item.isIdentified()) {
				bg.ra = 0.1f;
				bg.ba = 0.1f;
			}
		}

		@Override
		protected void createChildren() {

			bg = new ColorBlock(HEIGHT, HEIGHT, 0xFF4A4D44);
			add(bg);

			slot = new ItemSlot();
			add(slot);

			name = PixelScene.renderText("?", 7);
			add(name);

			super.createChildren();
		}

		@Override
		protected void layout() {
			bg.x = x;
			bg.y = y;

			slot.setRect(x, y, HEIGHT, HEIGHT);

			name.x = slot.right() + 2;
			name.y = y + (height - name.baseLine()) / 2;

			String str = Messages.titleCase( item.name() );
			name.text(str);
			if (name.width() > width - name.x) {
				do {
					str = str.substring(0, str.length() - 1);
					name.text(str + "...");
				} while (name.width() > width - name.x);
			}

			super.layout();
		}

		@Override
		protected void onTouchDown() {
			bg.brightness(1.5f);
			Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f);
		}

		@Override
		protected void onTouchUp() {
			bg.brightness(1.0f);
		}

		@Override
		protected void onClick() {
			Game.scene().add(new WndItem(null, item));
		}
	}

	private class QuickSlotButton extends ItemSlot {

		public static final int HEIGHT = 28;

		private Item item;
		private ColorBlock bg;

		QuickSlotButton(Item item) {
			super(item);
			this.item = item;
		}

		@Override
		protected void createChildren() {
			bg = new ColorBlock(HEIGHT, HEIGHT, 0xFF4A4D44);
			add(bg);

			super.createChildren();
		}

		@Override
		protected void layout() {
			bg.x = x;
			bg.y = y;

			super.layout();
		}

		@Override
		protected void onTouchDown() {
			bg.brightness(1.5f);
			Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f);
		}

		@Override
		protected void onTouchUp() {
			bg.brightness(1.0f);
		}

		@Override
		protected void onClick() {
			Game.scene().add(new WndItem(null, item));
		}
	}
}
