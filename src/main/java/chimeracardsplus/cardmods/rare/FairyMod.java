package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.TriggerPreDeathMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;

import static chimeracardsplus.util.CardCheckHelpers.doesntDowngradeMagicNoUseChecks;

public class FairyMod extends AbstractAugment implements TriggerPreDeathMod {
    public static final String ID = ChimeraCardsPlus.makeID(FairyMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return (damage > 1) ? (damage * 2.0F / 3.0F) : damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        return (block > 1) ? (block * 2.0F / 3.0F) : block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        return (magic > 1 && doesntDowngradeMagicNoUseChecks(card)) ? (magic * 2.0F / 3.0F) : magic;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> isNormalCard(c) && c.rarity != AbstractCard.CardRarity.BASIC);
    }

    @Override
    public boolean preDeath(AbstractCard card, AbstractPlayer player) {
        if (player.currentHealth > 0) {
            return false;
        }
        player.currentHealth = 0;
        int healAmt = (int) (player.maxHealth / 10.0F);
        if (healAmt < 1) {
            healAmt = 1;
        }
        player.heal(healAmt, true);

        AbstractDungeon.topLevelEffects.add(new PurgeCardEffect(card, (float) (Settings.WIDTH / 2), (float) (Settings.HEIGHT / 2)));
        AbstractDungeon.player.masterDeck.removeCard(card);

        return true;
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
        return new FairyMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}