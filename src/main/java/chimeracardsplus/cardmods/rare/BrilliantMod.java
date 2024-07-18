package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.purple.Brilliance;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class BrilliantMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(BrilliantMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage + AbstractDungeon.actionManager.mantraGained;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, c -> c.baseDamage >= 1) && characterCheck(p -> {
            ArrayList<AbstractCard> deck = p.masterDeck.group;
            for (AbstractCard c : deck) {
                if (c.rawDescription.contains(CARD_TEXT[3])) {
                    return true;
                }
            }
            return false;
        });
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
        if (card instanceof Brilliance) {
            return rawDescription.replace(CARD_TEXT[1], CARD_TEXT[2]);
        }
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new BrilliantMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}