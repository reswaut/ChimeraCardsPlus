package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import CardAugments.patches.InterruptUseCardFieldPatches.InterceptUseField;
import CardAugments.util.CalcHelper;
import CardAugments.util.Wiz;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardTarget;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;

import java.util.Locale;

public class PunishingMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(PunishingMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    private static final String DESCRIPTION_KEY = '!' + ID + '!';
    private int val = 0;
    private boolean modified = false;

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return cardCheck(abstractCard, c -> c.cost >= 1 && usesVanillaTargeting(c) && c.type == CardType.ATTACK && customCheck(c, check -> check.rawDescription.chars().filter(ch -> ch == '.' || ch == '。').count() == 1L));
    }

    @Override
    public void onInitialApplication(AbstractCard card) {
        val = baseVal(card);
        InterceptUseField.interceptUse.set(card, Boolean.TRUE);
        if (card.target != CardTarget.SELF_AND_ENEMY && card.target != CardTarget.ENEMY) {
            card.target = CardTarget.ENEMY;
        }
    }

    @Override
    public void updateDynvar(AbstractCard card) {
        val = baseVal(card);
        modified = false;
    }

    @Override
    public void onApplyPowers(AbstractCard card) {
        val = CalcHelper.applyPowers(baseVal(card));
        modified = val != baseVal(card);
    }

    @Override
    public void onCalculateCardDamage(AbstractCard card, AbstractMonster mo) {
        val = CalcHelper.calculateCardDamage(baseVal(card), mo);
        modified = val != baseVal(card);
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
        String description = rawDescription;
        if (Character.isAlphabetic(description.charAt(0))) {
            String word = description.split(" ")[0].replaceAll("[^a-zA-Z0-9]", "");
            if (!GameDictionary.keywords.containsKey(word.toLowerCase(Locale.ROOT))) {
                char[] c = description.toCharArray();
                c[0] = Character.toLowerCase(c[0]);
                description = new String(c);
            }
        }

        return String.format(CARD_TEXT[0], DESCRIPTION_KEY) + description;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new DamageAction(target, new DamageInfo(AbstractDungeon.player, val, card.damageTypeForTurn), AttackEffect.BLUNT_HEAVY));
        if (target instanceof AbstractMonster && target.hasPower(VulnerablePower.POWER_ID)) {
            card.use(AbstractDungeon.player, (AbstractMonster) target);
        }
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new PunishingMod();
    }

    @Override
    public Color getGlow(AbstractCard card) {
        if (hasThisMod(card) && Wiz.isInCombat() && AbstractDungeon.getMonsters().monsters.stream().anyMatch(m -> !m.isDeadOrEscaped() && m.hasPower(VulnerablePower.POWER_ID))) {
            return Color.GOLD.cpy();
        }
        return null;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String key() {
        return ID;
    }

    @Override
    public int val(AbstractCard abstractCard) {
        return val;
    }

    @Override
    public int baseVal(AbstractCard abstractCard) {
        return 5 + getEffectiveUpgrades(abstractCard) * 2;
    }

    @Override
    public boolean modified(AbstractCard abstractCard) {
        return modified;
    }

    @Override
    public boolean upgraded(AbstractCard abstractCard) {
        val = baseVal(abstractCard);
        modified = abstractCard.timesUpgraded != 0 || abstractCard.upgraded;
        return modified;
    }
}