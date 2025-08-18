package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.actions.UnloadCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.cards.green.Unload;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.stream.Stream;

public class UnstableMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(UnstableMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean modMagic = false;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (cardCheck(card, c -> c.baseMagicNumber >= 1 && doesntDowngradeMagic())) {
            modMagic = true;
        }
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= -1 && (c.baseDamage >= 4 || c.baseBlock >= 4 || c.baseMagicNumber >= 4 && doesntDowngradeMagic()) && !Unload.ID.equals(c.cardID) && Stream.of(CardType.ATTACK, CardType.SKILL, CardType.POWER).anyMatch(type -> c.type == type));
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        return damage > 0.0F ? damage * 1.25F : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return block > 0.0F ? block * 1.25F : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return modMagic ? magic * 1.25F : magic;
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
        String text = "";
        if (card.type == CardType.ATTACK) {
            text = CARD_TEXT[0];
        } else if (card.type == CardType.SKILL) {
            text = CARD_TEXT[1];
        } else if (card.type == CardType.POWER) {
            text = CARD_TEXT[2];
        }
        return insertAfterText(rawDescription, text);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new UnloadCardAction(card.type, AbstractDungeon.player));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new UnstableMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}