package chimeracardsplus.cardmods.rare;

import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import chimeracardsplus.ChimeraCardsPlus;
import chimeracardsplus.interfaces.TriggerOnObtainMod;
import chimeracardsplus.interfaces.TriggerOnUpdateObjectsMod;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon.CurrentScreen;
import com.megacrit.cardcrawl.events.shrines.Transmogrifier;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import com.megacrit.cardcrawl.rooms.MonsterRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

public class ChangingMod extends AbstractAugment implements TriggerOnObtainMod, TriggerOnUpdateObjectsMod {
    public static final String ID = ChimeraCardsPlus.makeID(ChangingMod.class.getSimpleName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(ID);
    private static final String[] TEXT = uiStrings.TEXT;
    private static final String[] CARD_TEXT = uiStrings.EXTRA_TEXT;
    public static final String UI_TEXT = CardCrawlGame.languagePack.getEventString(Transmogrifier.ID).OPTIONS[2];
    private static final Collection<CurrentScreen> VALID_SCREENS = EnumSet.copyOf(Arrays.asList(
            CurrentScreen.COMBAT_REWARD,
            CurrentScreen.MAP,
            CurrentScreen.NONE,
            CurrentScreen.SHOP,
            CurrentScreen.VICTORY
    ));
    private CurrentScreen prevScreen = null;
    private boolean pickup, cardsSelected;

    public ChangingMod() {
        pickup = false;
        cardsSelected = true;
    }
    public ChangingMod(boolean pickup) {
        this.pickup = pickup;
        cardsSelected = true;
    }

    @Override
    public boolean validCard(AbstractCard abstractCard) {
        return isNormalCard(abstractCard) && abstractCard.rarity != CardRarity.BASIC && (!CardCrawlGame.isInARun() || AbstractDungeon.getCurrMapNode() != null && AbstractDungeon.getCurrRoom() instanceof MonsterRoom);
    }

    @Override
    public void onObtain(AbstractCard card) {
        pickup = true;
    }

    @Override
    public boolean onUpdateObjects(AbstractCard card) {
        if (AbstractDungeon.getCurrMapNode() == null) {
            return false;
        }
        RoomPhase phase = AbstractDungeon.getCurrRoom().phase;
        if (cardsSelected && pickup && phase != RoomPhase.INCOMPLETE && phase != RoomPhase.COMBAT && VALID_SCREENS.contains(AbstractDungeon.screen)) {
            prevScreen = AbstractDungeon.screen;
            pickup = false;
            CardGroup group = CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck.getPurgeableCards());
            group.removeCard(card);
            AbstractDungeon.gridSelectScreen.open(group, 1, UI_TEXT, false, true, true, false);
            AbstractDungeon.dynamicBanner.hide();
            cardsSelected = false;
            AbstractDungeon.getCurrRoom().phase = RoomPhase.INCOMPLETE;
        }
        if (!cardsSelected && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
            cardsSelected = true;
            AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
            c.untip();
            c.unhover();

            AbstractDungeon.player.masterDeck.removeCard(c);
            AbstractDungeon.transformCard(c, false, AbstractDungeon.miscRng);
            AbstractCard transCard = AbstractDungeon.getTransformedCard();
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(transCard, c.current_x, c.current_y));

            AbstractDungeon.getCurrRoom().phase = RoomPhase.COMPLETE;
            AbstractDungeon.gridSelectScreen.selectedCards.clear();
            return true;
        }
        if (!cardsSelected && AbstractDungeon.screen == prevScreen) {
            cardsSelected = true;
            AbstractDungeon.getCurrRoom().phase = RoomPhase.COMPLETE;
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
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ChangingMod(pickup);
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}