/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2020 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FistSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public abstract class YogFist extends Mob {

	{
		HP = HT = 300;
		defenseSkill = 25;

		//for doomed resistance
		EXP = 25;
		maxLvl = -2;

		state = WANDERING;

		properties.add(Property.MINIBOSS);
		properties.add(Property.DEMONIC);
	}

	private float rangedCooldown;
	protected boolean canRangedInMelee = true;

	protected void incrementRangedCooldown(){
		rangedCooldown += Random.NormalFloat(6, 9);
	}

	@Override
	protected boolean act() {
		if (paralysed <= 0 && rangedCooldown > 0) rangedCooldown--;
		return super.act();
	}

	@Override
	protected boolean canAttack(Char enemy) {
		if (rangedCooldown <= 0){
			return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		} else {
			return super.canAttack(enemy);
		}
	}

	@Override
	protected boolean doAttack( Char enemy ) {

		if (Dungeon.level.adjacent( pos, enemy.pos ) && (!canRangedInMelee || rangedCooldown > 0)) {

			return super.doAttack( enemy );

		} else {

			incrementRangedCooldown();
			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap( enemy.pos );
				return false;
			} else {
				zap();
				return true;
			}
		}
	}

	protected abstract void zap();

	public void onZapComplete(){
		zap();
		next();
	}

	@Override
	public int attackSkill( Char target ) {
		return 36;
	}

	@Override
	//TODO different for some fists perhaps?
	public int damageRoll() {
		return Random.NormalIntRange( 26, 32 );
	}

	@Override
	public int drRoll() {
		return Random.NormalIntRange(0, 15);
	}

	public static class Burning extends YogFist {

		{
			spriteClass = FistSprite.Burning.class;

			properties.add(Property.FIERY);
		}

		@Override
		public boolean act() {

			boolean result = super.act();

			if (Dungeon.level.map[pos] == Terrain.WATER){
				Level.set( pos, Terrain.EMPTY);
				GameScene.updateMap( pos );
				CellEmitter.get( pos ).burst( Speck.factory( Speck.STEAM ), 10 );
			}

			for (int i = 0; i < 2; i++){
				int cell = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
				if (Dungeon.level.map[cell] == Terrain.WATER){
					Level.set( cell, Terrain.EMPTY);
					GameScene.updateMap( cell );
					CellEmitter.get( cell ).burst( Speck.factory( Speck.STEAM ), 10 );
				}
			}

			for (int i : PathFinder.NEIGHBOURS9) {
				int vol = Fire.volumeAt(pos+i, Fire.class);
				if (vol < 4 && !Dungeon.level.water[pos + i] && !Dungeon.level.solid[pos + i]){
					GameScene.add( Blob.seed( pos + i, 4 - vol, Fire.class ) );
				}
			}

			return result;
		}

		@Override
		protected void zap() {
			spend( 1f );

			if (hit( this, enemy, true )) {

				int dmg = damageRoll()/2;
				//TODO damage source logic
				enemy.damage( dmg, this );
				Buff.affect( enemy, com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning.class ).reignite( enemy );

				if (!enemy.isAlive() && enemy == Dungeon.hero) {
					Dungeon.fail( getClass() );
					GLog.n( Messages.get(Char.class, "kill", name()) );
				}

			} else {

				enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
			}

			if (Dungeon.level.map[enemy.pos] == Terrain.WATER){
				Level.set( enemy.pos, Terrain.EMPTY);
				GameScene.updateMap( enemy.pos );
				CellEmitter.get( enemy.pos ).burst( Speck.factory( Speck.STEAM ), 10 );
			}

			for (int i : PathFinder.NEIGHBOURS9){
				if (!Dungeon.level.water[enemy.pos+i] && !Dungeon.level.solid[enemy.pos+i]){
					int vol = Fire.volumeAt(enemy.pos+i, Fire.class);
					if (vol < 4){
						GameScene.add( Blob.seed( enemy.pos + i, 4 - vol, Fire.class ) );
					}
				}
			}

		}

	}

	public static class Soiled extends YogFist {

		{
			spriteClass = FistSprite.Soiled.class;
		}

		@Override
		public boolean act() {

			boolean result = super.act();

			for (int i = 0; i < 2; i++){
				int cell = pos + PathFinder.NEIGHBOURS9[Random.Int(9)];
				if (Dungeon.level.map[cell] == Terrain.GRASS){
					Level.set( cell, Terrain.FURROWED_GRASS);
					GameScene.updateMap( cell );
					CellEmitter.get( cell ).burst( LeafParticle.GENERAL, 10 );
				}
			}

			Dungeon.observe();

			for (int i : PathFinder.NEIGHBOURS9) {
				int cell = pos + i;
				if (canSpreadGrass(cell)){
					Level.set(pos+i, Terrain.GRASS);
					GameScene.updateMap( pos + i );
				}
			}

			return result;
		}

		@Override
		public void damage(int dmg, Object src) {
			int grassCells = 0;
			for (int i : PathFinder.NEIGHBOURS9) {
				if (Dungeon.level.map[pos+i] == Terrain.FURROWED_GRASS
				|| Dungeon.level.map[pos+i] == Terrain.HIGH_GRASS){
					grassCells++;
				}
			}
			if (grassCells > 0) dmg = Math.round(dmg * (9-grassCells)/9f);

			super.damage(dmg, src);
		}

		@Override
		protected void zap() {
			spend( 1f );

			if (hit( this, enemy, true )) {

				int dmg = damageRoll()/2;
				//TODO damage source logic
				enemy.damage( dmg, this );
				Buff.affect( enemy, Roots.class, 3f );

				if (!enemy.isAlive() && enemy == Dungeon.hero) {
					Dungeon.fail( getClass() );
					GLog.n( Messages.get(Char.class, "kill", name()) );
				}

			} else {

				enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
			}

			for (int i : PathFinder.NEIGHBOURS9){
				int cell = enemy.pos + i;
				if (canSpreadGrass(cell)){
					if (Random.Int(5) == 0){
						Level.set(cell, Terrain.FURROWED_GRASS);
						GameScene.updateMap( cell );
					} else {
						Level.set(cell, Terrain.GRASS);
						GameScene.updateMap( cell );
					}
					CellEmitter.get( cell ).burst( LeafParticle.GENERAL, 10 );
				}
			}
			Dungeon.observe();

		}

		private boolean canSpreadGrass(int cell){
			int yogPos = Dungeon.level.exit + Dungeon.level.width()*3;
			return Dungeon.level.distance(cell, yogPos) > 4 && !Dungeon.level.solid[cell]
					&& !(Dungeon.level.map[cell] == Terrain.FURROWED_GRASS || Dungeon.level.map[cell] == Terrain.HIGH_GRASS);
		}

	}

	public static class Rotting extends YogFist {

		{
			spriteClass = FistSprite.Rotting.class;

			properties.add(Property.ACIDIC);
		}

		@Override
		protected boolean act() {
			//ensures toxic gas acts at the appropriate time when added
			GameScene.add(Blob.seed(pos, 0, ToxicGas.class));

			if (Dungeon.level.water[pos] && HP < HT) {
				sprite.emitter().burst( Speck.factory(Speck.HEALING), 3 );
				HP += HT/50;
			}

			return super.act();
		}

		@Override
		public void damage(int dmg, Object src) {
			if (!(src instanceof Bleeding)){
				Bleeding b = buff(Bleeding.class);
				if (b == null){
					b = new Bleeding();
				}
				b.announced = false;
				b.set(dmg/3f);
				b.attachTo(this);
				sprite.showStatus(CharSprite.WARNING, b.toString() + " " + (int)b.level());
			} else{
				super.damage(dmg, src);
			}
		}

		@Override
		protected void zap() {
			spend( 1f );
			GameScene.add(Blob.seed(enemy.pos, 100, ToxicGas.class));
		}

		@Override
		public int attackProc( Char enemy, int damage ) {
			damage = super.attackProc( enemy, damage );

			if (Random.Int( 2 ) == 0) {
				Buff.affect( enemy, Ooze.class ).set( 20f );
				enemy.sprite.burst( 0xFF000000, 5 );
			}

			return damage;
		}

		{
			immunities.add(ToxicGas.class);
		}

	}

	public static class Rusted extends YogFist {

		{
			spriteClass = FistSprite.Rusted.class;

			properties.add(Property.LARGE);
			properties.add(Property.INORGANIC);
		}

		@Override
		public void damage(int dmg, Object src) {
			if (!(src instanceof Viscosity.DeferedDamage)){
				Buff.affect(this, Viscosity.DeferedDamage.class).prolong(dmg);
				sprite.showStatus( CharSprite.WARNING, Messages.get(Viscosity.class, "deferred", dmg) );
			} else{
				super.damage(dmg, src);
			}
		}

		@Override
		protected void zap() {
			spend( 1f );
			Buff.affect(enemy, Cripple.class, 5f);
		}

	}

	public static class Bright extends YogFist {

		{
			spriteClass = FistSprite.Bright.class;

			properties.add(Property.ELECTRIC);

			canRangedInMelee = false;
		}

		@Override
		protected void incrementRangedCooldown() {
			//ranged attack has no cooldown
		}

		@Override
		protected void zap() {
			spend( 1f );

			if (hit( this, enemy, true )) {

				int dmg = damageRoll()/2;
				//TODO damage source logic
				enemy.damage( dmg, this );
				Buff.prolong( enemy, Blindness.class, 5f );

				if (!enemy.isAlive() && enemy == Dungeon.hero) {
					Dungeon.fail( getClass() );
					GLog.n( Messages.get(Char.class, "kill", name()) );
				}

			} else {

				enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
			}

		}

		@Override
		public void damage(int dmg, Object src) {
			int beforeHP = HP;
			super.damage(dmg, src);
			if (beforeHP > HT/2 && HP < HT/2){
				HP = HT/2;
				Buff.prolong( enemy, Blindness.class, 50f );
				int i;
				do {
					i = Random.Int(Dungeon.level.length());
				} while (Dungeon.level.heroFOV[i] || Dungeon.level.solid[i] || Actor.findChar(i) != null);
				ScrollOfTeleportation.appear(this, i);
				state = WANDERING;
				GameScene.flash(0xFFFFFF);
			} else if (!isAlive()){
				Buff.prolong( enemy, Blindness.class, 50f );
				GameScene.flash(0xFFFFFF);
			}
		}

	}

	public static class Dark extends YogFist {

		{
			spriteClass = FistSprite.Dark.class;

			canRangedInMelee = false;
		}

		@Override
		protected void incrementRangedCooldown() {
			//ranged attack has no cooldown
		}

		@Override
		protected void zap() {
			spend( 1f );

			if (hit( this, enemy, true )) {

				int dmg = damageRoll()/2;
				//TODO damage source logic
				enemy.damage( dmg, this );

				Light l = enemy.buff(Light.class);
				if (l != null){
					l.weaken(50);
				}

				if (!enemy.isAlive() && enemy == Dungeon.hero) {
					Dungeon.fail( getClass() );
					GLog.n( Messages.get(Char.class, "kill", name()) );
				}

			} else {

				enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
			}

		}

		@Override
		public void damage(int dmg, Object src) {
			int beforeHP = HP;
			super.damage(dmg, src);
			if (beforeHP > HT/2 && HP < HT/2){
				HP = HT/2;
				Light l = Dungeon.hero.buff(Light.class);
				if (l != null){
					l.detach();
				}
				int i;
				do {
					i = Random.Int(Dungeon.level.length());
				} while (Dungeon.level.heroFOV[i] || Dungeon.level.solid[i] || Actor.findChar(i) != null);
				ScrollOfTeleportation.appear(this, i);
				state = WANDERING;
				GameScene.flash(0, false);
			} else if (!isAlive()){
				Light l = Dungeon.hero.buff(Light.class);
				if (l != null){
					l.detach();
				}
				GameScene.flash(0, false);
			}
		}

	}

}
