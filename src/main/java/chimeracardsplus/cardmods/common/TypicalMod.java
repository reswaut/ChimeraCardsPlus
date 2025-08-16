package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.HashSet;
import java.util.stream.Collectors;

public class TypicalMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(TypicalMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage > 0.0F ? damage * 0.6F : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block > 0.0F ? block * 0.6F : block;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (noShenanigans(c)
                && c.cost >= 0
                && (c.baseDamage >= 2 || c.baseBlock >= 2)
                && customCheck(c, (check) ->
                noCardModDescriptionChanges(check)
                        && check.rawDescription.chars().filter((ch) -> ch == '.' || ch == '。').count() == 1)
        ));
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
    public void onUse(AbstractCard card, AbstractCreature cardTarget, UseCardAction action) {
        addToBot(new AbstractGameAction() {
            @Override
            public void update() {
                int hits = AbstractDungeon.actionManager.cardsPlayedThisTurn.stream().map((c) -> c.type).collect(Collectors.toCollection(HashSet<AbstractCard.CardType>::new)).size();
                for (int i = 0; i < hits - 1; ++i) {
                    card.use(AbstractDungeon.player, (AbstractMonster) cardTarget);
                }
                this.isDone = true;
            }
        });
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
}