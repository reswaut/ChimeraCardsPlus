package chimeracardsplus.cardmods.common;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.HealingMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import java.util.ArrayList;

import static chimeracardsplus.util.CardCheckHelpers.doesntDowngradeMagicNoUseChecks;

public class FadingMod extends AbstractAugment implements HealingMod {
    public static final String ID = ChimeraCardsPlus.makeID(FadingMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return card.rarity != AbstractCard.CardRarity.BASIC && isNormalCard(card) &&
                (card.baseDamage > 0 || card.baseBlock > 0 || (card.baseMagicNumber > 0 && doesntDowngradeMagicNoUseChecks(card)));
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return (damage > 0.0F) ? (damage * 2.0F) : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return (block > 0.0F) ? (block * 2.0F) : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return (magic > 0.0F && doesntDowngradeMagicNoUseChecks(card)) ? (magic * 2.0F) : magic;
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
        return new FadingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @SpirePatch(
            clz = AbstractMonster.class,
            method = "onBossVictoryLogic"
    )
    public static class RemoveFadingCardOnBossVictoryPatch {
        @SpirePostfixPatch
        public static void Postfix() {
            ArrayList<AbstractCard> targetCards = new ArrayList<>();
            for (AbstractCard card : AbstractDungeon.player.masterDeck.group) {
                if (CardModifierManager.hasModifier(card, FadingMod.ID)) {
                    targetCards.add(card);
                }
            }
            for (AbstractCard card : targetCards) {
                AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card));
                AbstractDungeon.player.masterDeck.removeCard(card);
            }
        }
    }
}