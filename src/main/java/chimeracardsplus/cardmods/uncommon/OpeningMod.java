package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class OpeningMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(OpeningMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return card.cost >= -1 && (card.baseDamage >= 1 && card.type == AbstractCard.CardType.ATTACK) || (card.baseBlock >= 1 && card.type == AbstractCard.CardType.SKILL);
    }

    @Override
    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        if (card.type != AbstractCard.CardType.ATTACK) {
            return damage;
        }
        if (AbstractDungeon.actionManager.cardsPlayedThisTurn.stream().filter((c) -> c != null && c.type == AbstractCard.CardType.ATTACK).count() == 1) {
            return damage + 3;
        }
        return damage;
    }

    @Override
    public float modifyBlock(float block, AbstractCard card) {
        if (card.type != AbstractCard.CardType.SKILL) {
            return block;
        }
        if (AbstractDungeon.actionManager.cardsPlayedThisTurn.stream().filter((c) -> c != null && c.type == AbstractCard.CardType.SKILL).count() == 1) {
            return block + 3;
        }
        return block;
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
        if (card.type == AbstractCard.CardType.ATTACK) {
            return insertAfterText(rawDescription, CARD_TEXT[0]);
        }
        if (card.type == AbstractCard.CardType.SKILL) {
            return insertAfterText(rawDescription, CARD_TEXT[1]);
        }
        return rawDescription;
    }

    @Override
    public Color getGlow(AbstractCard card) {
        if (AbstractDungeon.actionManager.cardsPlayedThisTurn.stream().noneMatch((c) -> c.type == card.type)) {
            return Color.GOLD.cpy();
        }
        return null;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new OpeningMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}