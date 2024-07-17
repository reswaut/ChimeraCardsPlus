package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

public class CalendarMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(CalendarMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    private boolean used = false;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> c.cost >= -1) && characterCheck((p) -> p.hasRelic("StoneCalendar"));
    }

    @Override
    public void atEndOfTurn(AbstractCard card, CardGroup group) {
        used = GameActionManager.turn != 7;
    }

    @Override
    public boolean onBattleStart(AbstractCard card) {
        used = true;
        return false;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (!used) {
            AbstractRelic relic = AbstractDungeon.player.getRelic("StoneCalendar");
            if (relic != null) {
                relic.flash();
                this.addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, relic));
            }
            this.addToBot(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(52, true), DamageInfo.DamageType.THORNS, AbstractGameAction.AttackEffect.BLUNT_HEAVY));
            used = true;
        }
    }

    @Override
    public Color getGlow(AbstractCard card) {
        return !used ? Color.GOLD : null;
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSuffix() {
        return TEXT[1];
    }

    @Override
    public String getAugmentDescription() {
        return TEXT[2];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new CalendarMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}