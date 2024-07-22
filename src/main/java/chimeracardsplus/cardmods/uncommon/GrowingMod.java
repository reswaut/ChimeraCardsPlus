package chimeracardsplus.cardmods.uncommon;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.TriggerOnObtainMod;
import chimeracardsplus.interfaces.UpdateObjectsMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static chimeracardsplus.util.CardCheckHelpers.doesntDowngradeMagicNoUseChecks;

public class GrowingMod extends AbstractAugment implements TriggerOnObtainMod, UpdateObjectsMod {
    public static final String ID = ChimeraCardsPlus.makeID(GrowingMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;
    public static final String UI_TEXT = CardCrawlGame.languagePack.getEventString("Upgrade Shrine").OPTIONS[2];
    private final Set<AbstractDungeon.CurrentScreen> VALID_SCREENS = new HashSet<AbstractDungeon.CurrentScreen>(Arrays.asList(
            AbstractDungeon.CurrentScreen.COMBAT_REWARD,
            AbstractDungeon.CurrentScreen.MAP,
            AbstractDungeon.CurrentScreen.NONE,
            AbstractDungeon.CurrentScreen.SHOP,
            AbstractDungeon.CurrentScreen.VICTORY
    ));
    private boolean pickup = false, cardsSelected = true;
    private CardGroup group;

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
    public void onObtain(AbstractCard card) {
        pickup = true;
    }

    @Override
    public boolean onUpdateObjects(AbstractCard card) {
        AbstractRoom.RoomPhase phase = AbstractDungeon.getCurrRoom().phase;
        if (cardsSelected && pickup && phase != AbstractRoom.RoomPhase.INCOMPLETE && phase != AbstractRoom.RoomPhase.COMBAT && VALID_SCREENS.contains(AbstractDungeon.screen)) {
            pickup = false;
            group = AbstractDungeon.player.masterDeck.getUpgradableCards();
            group.removeCard(card);
            AbstractDungeon.gridSelectScreen.open(group, 1, UI_TEXT, true, false, true, false);
            AbstractDungeon.dynamicBanner.hide();
            cardsSelected = false;
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.INCOMPLETE;
        }
        if (!cardsSelected && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            cardsSelected = true;
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            c.untip();
            c.unhover();

            AbstractDungeon.effectsQueue.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
            c.upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(c);

            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            return true;
        }
        if (!cardsSelected && (group != AbstractDungeon.gridSelectScreen.targetGroup || AbstractDungeon.screen == AbstractDungeon.CurrentScreen.COMBAT_REWARD)) {
            cardsSelected = true;
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
        }
        return false;
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
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new GrowingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}