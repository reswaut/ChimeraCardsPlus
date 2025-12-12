package chimeracardsplus.cardmods.uncommon;

import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.actions.AdaptAction;
import chimeracardsplus.cardmods.AbstractAugmentPlus;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class AdaptiveMod extends AbstractAugmentPlus {
    public static final String ID = ChimeraCardsPlus.makeID(AdaptiveMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return abstractCard.cost >= -1 && (abstractCard.baseDamage >= 2 && abstractCard.type == CardType.ATTACK || abstractCard.baseBlock >= 2 && abstractCard.type == CardType.SKILL);
    }

    @Override
    public float modifyBaseDamage(float damage, DamageType type, AbstractCard card, AbstractMonster target) {
        if (card.type != CardType.ATTACK) {
            return damage;
        }
        return damage > 0.0F ? damage * 0.75F : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        if (card.type != CardType.SKILL) {
            return block;
        }
        return block > 0.0F ? block * 0.75F : block;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (card.type == CardType.ATTACK) {
            addToBot(new AdaptAction(CardType.SKILL));
        } else if (card.type == CardType.SKILL) {
            addToBot(new AdaptAction(CardType.ATTACK));
        }
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
        if (card.type == CardType.ATTACK) {
            return insertAfterText(rawDescription, CARD_TEXT[0]);
        }
        if (card.type == CardType.SKILL) {
            return insertAfterText(rawDescription, CARD_TEXT[1]);
        }
        return rawDescription;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new AdaptiveMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public AugmentBonusLevel getModBonusLevel() {
        return AugmentBonusLevel.NORMAL;
    }
}