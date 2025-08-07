package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.DynvarCarrier;
import CardAugments.patches.InterruptUseCardFieldPatches;
import CardAugments.util.CalcHelper;
import CardAugments.util.Wiz;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardBorderGlowManager;
import chimeracardsplus.ChimeraCardsPlus;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.GameDictionary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class PunishingMod extends AbstractAugment implements DynvarCarrier {
    public static final String ID = ChimeraCardsPlus.makeID(PunishingMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    public static final String DESCRIPTION_KEY = "!" + ID + "!";
    public int val;
    private boolean upgraded, modified;

    @Override
    public boolean validCard(AbstractCard card) {
        return cardCheck(card, (c) -> c.cost > 0 && c.baseDamage > 0 && doesntUpgradeCost() && usesVanillaTargeting(c) && c.type == AbstractCard.CardType.ATTACK && customCheck(c, (check) -> check.rawDescription.chars().filter((ch) -> ch == '.' || ch == '。').count() == 1L));
    }

    public void onInitialApplication(AbstractCard card) {
        this.val = this.getBaseVal(card);
        --card.cost;
        card.costForTurn = card.cost;
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
        if (card.target != AbstractCard.CardTarget.SELF_AND_ENEMY && card.target != AbstractCard.CardTarget.ENEMY) {
            card.target = AbstractCard.CardTarget.ENEMY;
        }
    }

    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        return damage * 0.5F;
    }

    public void updateDynvar(AbstractCard card) {
        this.val = this.getBaseVal(card);
        this.modified = false;
    }

    public void onApplyPowers(AbstractCard card) {
        this.val = CalcHelper.applyPowers(this.getBaseVal(card));
        this.modified = this.val != this.getBaseVal(card);
    }

    public void onCalculateCardDamage(AbstractCard card, AbstractMonster mo) {
        this.val = CalcHelper.calculateCardDamage(this.getBaseVal(card), mo);
        this.modified = this.val != this.getBaseVal(card);
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
        if (Character.isAlphabetic(rawDescription.charAt(0))) {
            String word = rawDescription.split(" ")[0].replaceAll("[^a-zA-Z0-9]", "");
            if (!GameDictionary.keywords.containsKey(word.toLowerCase())) {
                char[] c = rawDescription.toCharArray();
                c[0] = Character.toLowerCase(c[0]);
                rawDescription = new String(c);
            }
        }

        return String.format(CARD_TEXT[0], DESCRIPTION_KEY) + rawDescription;
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        this.addToBot(new DamageAction(target, new DamageInfo(AbstractDungeon.player, this.val, card.damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
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

    public CardBorderGlowManager.GlowInfo getGlowInfo() {
        return new CardBorderGlowManager.GlowInfo() {
            public boolean test(AbstractCard abstractCard) {
                return PunishingMod.this.hasThisMod(abstractCard) && Wiz.isInCombat() && AbstractDungeon.getMonsters().monsters.stream().anyMatch((m) -> !m.isDeadOrEscaped() && m.hasPower(VulnerablePower.POWER_ID));
            }

            public Color getColor(AbstractCard abstractCard) {
                return Color.GOLD.cpy();
            }

            public String glowID() {
                return PunishingMod.ID + "Glow";
            }
        };
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

    @Override
    public String key() {
        return ID;
    }

    public int getBaseVal(AbstractCard card) {
        return 5 + this.getEffectiveUpgrades(card) * 2;
    }

    @Override
    public int val(AbstractCard card) {
        return this.val;
    }

    @Override
    public int baseVal(AbstractCard card) {
        return this.getBaseVal(card);
    }

    @Override
    public boolean modified(AbstractCard card) {
        return this.modified;
    }

    @Override
    public boolean upgraded(AbstractCard card) {
        this.val = this.getBaseVal(card);
        this.modified = card.timesUpgraded != 0 || card.upgraded;
        this.upgraded = card.timesUpgraded != 0 || card.upgraded;
        return this.upgraded;
    }
}