package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.unique.BaneAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.green.Bane;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class BanefulMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(BanefulMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * 0.75F;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, c -> (c.cost >= -1 && c.baseDamage >= 3 && usesEnemyTargeting())) && characterCheck(p -> {
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
        if (card instanceof Bane) {
            return rawDescription.replace(CARD_TEXT[1], CARD_TEXT[2]);
        }
        return insertAfterText(rawDescription, CARD_TEXT[0]);
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (target != null) {
            this.addToBot(new WaitAction(0.01F));
            this.addToBot(new BaneAction((AbstractMonster) target, new DamageInfo(AbstractDungeon.player, card.damage, card.damageTypeForTurn)));
        }
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.COMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new BanefulMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}