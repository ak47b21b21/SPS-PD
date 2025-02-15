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

import com.hmdzl.spspd.Dungeon;
import com.hmdzl.spspd.actors.hero.Hero;
import com.hmdzl.spspd.actors.hero.HeroClass;
import com.hmdzl.spspd.actors.mobs.npcs.Shopkeeper;
import com.hmdzl.spspd.items.ChallengeBook;
import com.hmdzl.spspd.items.DolyaSlate;
import com.hmdzl.spspd.items.EquipableItem;
import com.hmdzl.spspd.items.Gold;
import com.hmdzl.spspd.items.Heap;
import com.hmdzl.spspd.items.Item;
import com.hmdzl.spspd.items.bags.HeartOfScarecrow;
import com.hmdzl.spspd.items.bags.KeyRing;
import com.hmdzl.spspd.items.bags.PotionBandolier;
import com.hmdzl.spspd.items.bags.ScrollHolder;
import com.hmdzl.spspd.items.bags.SeedPouch;
import com.hmdzl.spspd.items.bags.ShoppingCart;
import com.hmdzl.spspd.items.bags.WandHolster;
import com.hmdzl.spspd.items.challengelists.ChallengeList;
import com.hmdzl.spspd.items.journalpages.JournalPage;
import com.hmdzl.spspd.messages.Messages;
import com.hmdzl.spspd.scenes.PixelScene;
import com.hmdzl.spspd.sprites.ItemSprite;
import com.hmdzl.spspd.ui.ItemSlot;
import com.hmdzl.spspd.ui.RedButton;
import com.hmdzl.spspd.ui.RenderedTextMultiline;
import com.hmdzl.spspd.ui.Window;
import com.hmdzl.spspd.utils.GLog;

public class WndTradeItem extends Window {

	private static final float GAP = 2;
	private static final int WIDTH = 120;
	private static final int BTN_HEIGHT = 16;

	private WndBag owner;

	public WndTradeItem(final Item item, WndBag owner) {

		super();

		this.owner = owner;

		float pos = createDescription(item, false);

		if (item.quantity() == 1) {

			RedButton btnSell = new RedButton(Messages.get(this, "sell", item.price())) {
				@Override
				protected void onClick() {
					sell(item);
					hide();
				}
			};
			btnSell.setRect(0, pos + GAP, WIDTH, BTN_HEIGHT);
			add(btnSell);

			pos = btnSell.bottom();

		} else {

			int priceAll = item.price();
			RedButton btnSell1 = new RedButton( Messages.get(this, "sell_1", priceAll / item.quantity()) ) {
				@Override
				protected void onClick() {
					sellOne(item);
					hide();
				}
			};
			btnSell1.setRect(0, pos + GAP, WIDTH, BTN_HEIGHT);
			add(btnSell1);
			RedButton btnSellAll = new RedButton( Messages.get(this, "sell_all", priceAll )) {
				@Override
				protected void onClick() {
					sell(item);
					hide();
				}
			};
			btnSellAll.setRect(0, btnSell1.bottom() + GAP, WIDTH, BTN_HEIGHT);
			add(btnSellAll);

			pos = btnSellAll.bottom();

		}

		RedButton btnCancel = new RedButton(Messages.get(this, "cancel")) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnCancel.setRect(0, pos + GAP, WIDTH, BTN_HEIGHT);
		add(btnCancel);

		resize(WIDTH, (int) btnCancel.bottom());
	}

	public WndTradeItem(final Heap heap, boolean canBuy) {

		super();

		Item item = heap.peek();

		float pos = createDescription(item, true);

		final int price = price(item);

		if (canBuy) {

			RedButton btnBuy = new RedButton( Messages.get(this, "buy", price)) {
				@Override
				protected void onClick() {
					hide();
					buy(heap);
				}
			};
			btnBuy.setRect(0, pos + GAP, WIDTH, BTN_HEIGHT);
			btnBuy.enable(price <= Dungeon.gold);
			add(btnBuy);

			RedButton btnCancel = new RedButton(Messages.get(this, "cancel")) {
				@Override
				protected void onClick() {
					hide();
				}
			};

			
			//final MasterThievesArmband.Thievery thievery = Dungeon.hero
				//	.buff(MasterThievesArmband.Thievery.class);
			//if (thievery != null) {
			//	final float chance = thievery.stealChance(price);
				//RedButton btnSteal = new RedButton( Messages.get(this, "steal", Math.min(100, (int)(chance*100)))) {
				//	@Override
				//	protected void onClick() {
				//		if (thievery.steal(price)) {
				///			Hero hero = Dungeon.hero;
				//			Item item = heap.pickUp();
					//		int price = (int)Math.max((1 - chance) * price(item) , 0);
				//			Dungeon.gold -= price;
				//			hide();
				//			if (!item.doPickUp(hero)) {
				//				Dungeon.level.drop(item, heap.pos).sprite.drop();
				//			}
				//		} else {
				//			for (Mob mob : Dungeon.level.mobs) {
				//			}
				//			hide();
			//			}
			//		}
			//	};
				//btnSteal.setRect(0, btnBuy.bottom() + GAP, WIDTH, BTN_HEIGHT);
				//btnSteal.enable(price <= Dungeon.gold);
			//	add(btnSteal);

				//btnCancel
						//.setRect(0, btnSteal.bottom() + GAP, WIDTH, BTN_HEIGHT);
		//	} else
				btnCancel.setRect(0, btnBuy.bottom() + GAP, WIDTH, BTN_HEIGHT);

			add(btnCancel);

			resize(WIDTH, (int) btnCancel.bottom());

		} else {

			resize(WIDTH, (int) pos);

		}
	}

	@Override
	public void hide() {

		super.hide();

		if (owner != null) {
			owner.hide();
			Shopkeeper.sell();
		}
	}

	private float createDescription(Item item, boolean forSale) {

		// Title
		IconTitle titlebar = new IconTitle();
		titlebar.icon(new ItemSprite(item.image(), item.glowing()));
		titlebar.label(forSale ? 
		Messages.get(this, "sale", item.toString(), price( item ) ) :
		Messages.titleCase( item.toString() ) );
		titlebar.setRect(0, 0, WIDTH, 0);
		add(titlebar);

		// Upgraded / degraded
		if (item.levelKnown && item.level > 0) {
			titlebar.color(ItemSlot.UPGRADED);
		} else if (item.levelKnown && item.level < 0) {
			titlebar.color(ItemSlot.DEGRADED);
		}

		// Description
		RenderedTextMultiline info = PixelScene.renderMultiline( item.info(), 6 );
		info.maxWidth(WIDTH);
		info.setPos(titlebar.left(), titlebar.bottom() + GAP);
		add( info );

		return info.bottom();
	}

	private void sell(Item item) {

		Hero hero = Dungeon.hero;

		if (item instanceof SeedPouch || item instanceof KeyRing || item instanceof ShoppingCart || item instanceof HeartOfScarecrow
		|| item instanceof PotionBandolier || item instanceof WandHolster || item instanceof ScrollHolder
		|| item instanceof JournalPage || item instanceof DolyaSlate || item instanceof ChallengeList || item instanceof ChallengeBook){
			GLog.w( Messages.get(this, "donot") );
			return ;
		} else if (item.isEquipped(hero)
				&& !((EquipableItem) item).doUnequip(hero, false)) {
			return;
		}
		
		item.detachAll(hero.belongings.backpack);

		new Gold(item.price()).doPickUp(hero);
		hero.spend(-hero.cooldown());
	
	}

	private void sellOne(Item item) {

		if (item.quantity() <= 1) {
			sell(item);
		} else {

			Hero hero = Dungeon.hero;
			item = item.detach( hero.belongings.backpack );
			
			new Gold(item.price()).doPickUp(hero);
			hero.spend(-hero.cooldown());
		}
	}

	private int price(Item item) {
		int price = Dungeon.hero.heroClass == HeroClass.FOLLOWER ?
				Math.min(item.price() * 4 * (Dungeon.depth / 5 + 1), item.price() * 20) :
				Math.min(item.price() * 5 * (Dungeon.depth / 5 + 1), item.price() * 25);
		return price;
	}

	private void buy(Heap heap) {

		Hero hero = Dungeon.hero;
		Item item = heap.pickUp();

		int price = price(item);
		Dungeon.gold -= price;

		if (!item.doPickUp(hero)) {
			Dungeon.level.drop(item, heap.pos).sprite.drop();
		}
	}
}
