package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class AkabekoMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(AkabekoMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        int count = 0;
        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisCombat) {
            if (c.type == AbstractCard.CardType.ATTACK) {
                count += 1;
                if (count >= 2) {
                    return damage;
                }
            }
        }
        return damage + 8;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, c -> (c.baseDamage >= 1 && c.type == AbstractCard.CardType.ATTACK))
                && characterCheck((p) -> p.hasRelic("Akabeko"));
    }

    @Override
    public Color getGlow(AbstractCard card) {
        for (AbstractCard c : AbstractDungeon.actionManager.cardsPlayedThisCombat) {
            if (c.type == AbstractCard.CardType.ATTACK) {
                return null;
            }
        }
        return Color.GOLD;
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
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new AkabekoMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}