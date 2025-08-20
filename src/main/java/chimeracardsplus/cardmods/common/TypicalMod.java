package chimeracardsplus.cardmods.common;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.HashSet;
import java.util.stream.Collectors;

public class TypicalMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(TypicalMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return damage > 0.0F ? damage * 0.6F : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block > 0.0F ? block * 0.6F : block;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> noShenanigans(c)
                && c.cost >= 0
                && (c.baseDamage >= 2 || c.baseBlock >= 2)
                && customCheck(c, check ->
                noCardModDescriptionChanges(check)
                        && check.rawDescription.chars().filter(ch -> ch == '.' || ch == '。').count() == 1L));
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
        return rawDescription.replaceFirst("[.。]", CARD_TEXT[0]);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new UseCardPerTypeAction(card, target));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new TypicalMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.NORMAL;
    }

    private static class UseCardPerTypeAction extends AbstractGameAction {
        private final AbstractCard card;
        private final AbstractCreature cardTarget;

        private UseCardPerTypeAction(AbstractCard card, AbstractCreature cardTarget) {
            this.card = card;
            this.cardTarget = cardTarget;
        }

        @Override
        public void update() {
            int hits = AbstractDungeon.actionManager.cardsPlayedThisTurn.stream().map(c -> c.type).collect(Collectors.toCollection(HashSet<CardType>::new)).size();
            for (int i = 0; i < hits - 1; ++i) {
                card.use(AbstractDungeon.player, (AbstractMonster) cardTarget);
            }
            isDone = true;
        }
    }
}