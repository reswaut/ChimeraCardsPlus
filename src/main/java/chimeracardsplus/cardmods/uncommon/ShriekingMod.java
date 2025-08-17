package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.green.PiercingWail;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;

import static com.megacrit.cardcrawl.actions.AbstractGameAction.ActionType.DEBUFF;

public class ShriekingMod extends AbstractAugment {
    public static final String ID = ChimeraCardsPlus.makeID(ShriekingMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private boolean addedExhaust = true;

    @Override
    public void onInitialApplication(AbstractCard card) {
        addedExhaust = !card.exhaust;
        card.exhaust = true;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> c.cost >= -1 && (c.type == AbstractCard.CardType.ATTACK || c.type == AbstractCard.CardType.SKILL) && doesntUpgradeExhaust());
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (PiercingWail.ID.equals(card.cardID)) {
            return magic + 3;
        }
        return magic;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        if (PiercingWail.ID.equals(card.cardID)) {
            return;
        }
        this.addToBot(new AbstractGameAction() {
            {
                this.actionType = DEBUFF;
            }

            @Override
            public void update() {
                for (int i = AbstractDungeon.getMonsters().monsters.size(); i-- > 0; ) {
                    AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                    if (!mo.hasPower("Artifact")) {
                        this.addToTop(new ApplyPowerAction(mo, AbstractDungeon.player, new GainStrengthPower(mo, 3), 3, true, AbstractGameAction.AttackEffect.NONE));
                    }
                }
                for (int i = AbstractDungeon.getMonsters().monsters.size(); i-- > 0; ) {
                    AbstractMonster mo = AbstractDungeon.getMonsters().monsters.get(i);
                    this.addToTop(new ApplyPowerAction(mo, AbstractDungeon.player, new StrengthPower(mo, -3), -3, true, AbstractGameAction.AttackEffect.NONE));
                }
                if (Settings.FAST_MODE) {
                    this.addToTop(new VFXAction(AbstractDungeon.player, new ShockWaveEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, Settings.GREEN_TEXT_COLOR, ShockWaveEffect.ShockWaveType.CHAOTIC), 0.3F));
                } else {
                    this.addToTop(new VFXAction(AbstractDungeon.player, new ShockWaveEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, Settings.GREEN_TEXT_COLOR, ShockWaveEffect.ShockWaveType.CHAOTIC), 1.5F));
                }
                this.addToTop(new SFXAction("ATTACK_PIERCING_WAIL"));
                this.isDone = true;
            }
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
        if (PiercingWail.ID.equals(card.cardID)) {
            return rawDescription;
        }
        return insertAfterText(rawDescription, addedExhaust ? CARD_TEXT[0] : CARD_TEXT[1]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ShriekingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}