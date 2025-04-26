package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.relics.PenNib;

public class PointyMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(PointyMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> (c.type == AbstractCard.CardType.ATTACK && c.baseDamage >= 1 && c.cost >= -1))
                && characterCheck((p) -> p.hasRelic(PenNib.ID));
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
    public float modifyDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic.relicId.equals("Pen Nib") && relic.counter == 9) {
                return damage * 2.0F;
            }
        }
        return damage;
    }

    @Override
    public Color getGlow(AbstractCard card) {
        for (AbstractRelic relic : AbstractDungeon.player.relics) {
            if (relic.relicId.equals("Pen Nib") && relic.counter == 9) {
                return Color.GOLD;
            }
        }
        return null;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PointyMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}